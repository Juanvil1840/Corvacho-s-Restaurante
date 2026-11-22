import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { EventoSeguimiento, SeguimientoPedido } from '../models/seguimiento.model';
import { obtenerTokenValido } from './token.util';

// Espera máxima entre reintentos cuando se cae la conexión
const ESPERA_MAXIMA_MS = 15_000;

// Error del servidor que no se arregla reintentando (sin sesión, sin permiso o pedido inexistente)
export class SeguimientoNoDisponible extends Error {
  constructor(public readonly status: number) {
    super(`Seguimiento no disponible (HTTP ${status})`);
  }
}

// Seguimiento en vivo de un pedido usando Server-Sent Events (SSE).
// Se usa fetch y no EventSource porque EventSource no permite enviar el header Authorization con el JWT.
@Injectable({
  providedIn: 'root'
})
export class SeguimientoService {

  private apiUrl = `${environment.apiUrl}/pedidos`;

  // Emite el estado actual del pedido y después cada cambio. Si la conexión se cae,
  // se reconecta solo esperando cada vez un poco más (1s, 2s, 4s... hasta 15s).
  // Al cancelar la suscripción se cierra la conexión.
  seguir(pedidoId: number): Observable<EventoSeguimiento> {
    return new Observable<EventoSeguimiento>(suscriptor => {
      let controlador: AbortController | null = null;
      let temporizador: ReturnType<typeof setTimeout> | undefined;
      let intentos = 0;
      let cerrado = false;

      const conectar = async (): Promise<void> => {
        const token = obtenerTokenValido();
        if (!token) {
          suscriptor.error(new SeguimientoNoDisponible(401));
          return;
        }
        controlador = new AbortController();
        suscriptor.next({ tipo: 'conexion', estado: intentos === 0 ? 'conectando' : 'reconectando' });

        try {
          const respuesta = await fetch(`${this.apiUrl}/${pedidoId}/seguimiento`, {
            headers: { Authorization: `Bearer ${token}`, Accept: 'text/event-stream' },
            signal: controlador.signal
          });
          if ([401, 403, 404].includes(respuesta.status)) {
            suscriptor.error(new SeguimientoNoDisponible(respuesta.status));
            return;
          }
          if (!respuesta.ok || !respuesta.body) {
            throw new Error(`HTTP ${respuesta.status}`);
          }

          intentos = 0;
          suscriptor.next({ tipo: 'conexion', estado: 'en-vivo' });
          await this.leerEventos(respuesta.body, datos => suscriptor.next({ tipo: 'estado', datos }));
        } catch (error) {
          if (cerrado) {
            return;
          }
          console.warn('Se perdió la conexión del seguimiento, reintentando…', error);
        }

        // El servidor cerró el canal (por ejemplo a los 30 minutos) o se cayó la red: se reintenta
        if (!cerrado) {
          intentos++;
          suscriptor.next({ tipo: 'conexion', estado: 'reconectando' });
          temporizador = setTimeout(conectar, Math.min(1000 * 2 ** (intentos - 1), ESPERA_MAXIMA_MS));
        }
      };

      conectar();

      return () => {
        cerrado = true;
        clearTimeout(temporizador);
        controlador?.abort();
      };
    });
  }

  // Lee el cuerpo de la respuesta a medida que llega. Cada evento SSE termina con una línea en blanco:
  //   event:estado
  //   data:{...json...}
  private async leerEventos(cuerpo: ReadableStream<Uint8Array>, alRecibir: (datos: SeguimientoPedido) => void): Promise<void> {
    const lector = cuerpo.pipeThrough(new TextDecoderStream()).getReader();
    let pendiente = '';
    while (true) {
      const { value, done } = await lector.read();
      if (done) {
        return;
      }
      pendiente += value;
      const bloques = pendiente.split(/\r?\n\r?\n/);
      pendiente = bloques.pop() ?? '';
      for (const bloque of bloques) {
        const datos = this.leerBloque(bloque);
        if (datos) {
          alRecibir(datos);
        }
      }
    }
  }

  // Convierte un bloque SSE en datos; ignora los "ping" (líneas que empiezan con ":")
  private leerBloque(bloque: string): SeguimientoPedido | null {
    let evento = 'message';
    const lineasDatos: string[] = [];
    for (const linea of bloque.split(/\r?\n/)) {
      if (linea.startsWith('event:')) {
        evento = linea.slice(6).trim();
      } else if (linea.startsWith('data:')) {
        lineasDatos.push(linea.slice(5).trimStart());
      }
    }
    if (evento !== 'estado' || lineasDatos.length === 0) {
      return null;
    }
    try {
      return JSON.parse(lineasDatos.join('\n')) as SeguimientoPedido;
    } catch {
      return null;
    }
  }
}
