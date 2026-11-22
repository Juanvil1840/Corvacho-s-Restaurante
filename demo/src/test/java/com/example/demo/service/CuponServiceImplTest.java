package com.example.demo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Cupon;
import com.example.demo.repository.CuponRepository;
import com.example.demo.repository.PedidoRepository;

// Pruebas unitarias de las reglas de los cupones (con mocks, sin base de datos)
@ExtendWith(MockitoExtension.class)
class CuponServiceImplTest {

    private static final LocalDate HOY = LocalDate.of(2026, 11, 22);

    @Mock
    private CuponRepository cuponRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private CuponServiceImpl cuponService;

    private Cupon cupon(String tipo, double valor, double minima, Integer maximos, int usos, LocalDate vence, boolean activo) {
        Cupon cupon = new Cupon("PRUEBA", null, tipo, valor, minima, maximos, vence, activo);
        cupon.setId_cupon(1L);
        cupon.setUsos(usos);
        return cupon;
    }

    private void existe(Cupon cupon) {
        when(cuponRepository.findByCodigoIgnoreCase("PRUEBA")).thenReturn(Optional.of(cupon));
    }

    @Test
    void validar_cuponValido_loDevuelveSinImportarMayusculasNiEspacios() {
        Cupon cupon = cupon(Cupon.PORCENTAJE, 10, 20000, 5, 4, HOY, true);
        existe(cupon);

        assertThat(cuponService.validar("  prueba ", 20000, HOY)).isSameAs(cupon);
    }

    @Test
    void validar_noExiste_error400() {
        when(cuponRepository.findByCodigoIgnoreCase("NOEXISTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cuponService.validar("noexiste", 50000, HOY))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST))
                .hasMessageContaining("no existe");
    }

    @Test
    void validar_vencido_error() {
        existe(cupon(Cupon.PORCENTAJE, 10, 0, null, 0, HOY.minusDays(1), true));

        assertThatThrownBy(() -> cuponService.validar("PRUEBA", 50000, HOY)).hasMessageContaining("venció el 21/11/2026");
    }

    @Test
    void validar_agotado_error() {
        existe(cupon(Cupon.VALOR_FIJO, 5000, 0, 3, 3, null, true));

        assertThatThrownBy(() -> cuponService.validar("PRUEBA", 50000, HOY)).hasMessageContaining("límite de usos");
    }

    @Test
    void validar_inactivo_error() {
        existe(cupon(Cupon.VALOR_FIJO, 5000, 0, null, 0, null, false));

        assertThatThrownBy(() -> cuponService.validar("PRUEBA", 50000, HOY)).hasMessageContaining("ya no está disponible");
    }

    @Test
    void validar_noAlcanzaLaCompraMinima_errorConElValor() {
        existe(cupon(Cupon.VALOR_FIJO, 5000, 40000, null, 0, null, true));

        assertThatThrownBy(() -> cuponService.validar("PRUEBA", 39999, HOY)).hasMessageContaining("40.000");
    }

    @Test
    void calcularDescuento_porcentajeRedondeadoYValorFijoNuncaMayorQueLaCompra() {
        assertThat(cuponService.calcularDescuento(cupon(Cupon.PORCENTAJE, 15, 0, null, 0, null, true), 33333))
                .isEqualTo(5000);
        assertThat(cuponService.calcularDescuento(cupon(Cupon.VALOR_FIJO, 10000, 0, null, 0, null, true), 8000))
                .isEqualTo(8000);
        assertThat(cuponService.calcularDescuento(null, 8000)).isZero();
    }

    @Test
    void crear_porcentajeMayorA100_error() {
        Cupon nuevo = new Cupon("MITAD", null, Cupon.PORCENTAJE, 150, 0, null, null, true);

        assertThatThrownBy(() -> cuponService.crear(nuevo)).hasMessageContaining("100");
        verify(cuponRepository, never()).save(any());
    }

    @Test
    void crear_codigoRepetido_error409() {
        Cupon existente = cupon(Cupon.PORCENTAJE, 10, 0, null, 0, null, true);
        when(cuponRepository.findByCodigoIgnoreCase("PRUEBA")).thenReturn(Optional.of(existente));
        Cupon nuevo = new Cupon("prueba", null, Cupon.PORCENTAJE, 10, 0, null, null, true);

        assertThatThrownBy(() -> cuponService.crear(nuevo))
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void crear_normalizaElCodigoYEmpiezaSinUsos() {
        when(cuponRepository.findByCodigoIgnoreCase("VERANO")).thenReturn(Optional.empty());
        when(cuponRepository.save(any(Cupon.class))).thenAnswer(inv -> inv.getArgument(0));
        Cupon nuevo = new Cupon(" verano ", "  ", Cupon.VALOR_FIJO, 3000, 0, null, null, true);
        nuevo.setUsos(99);

        Cupon guardado = cuponService.crear(nuevo);

        assertThat(guardado.getCodigo()).isEqualTo("VERANO");
        assertThat(guardado.getUsos()).isZero();
        assertThat(guardado.getDescripcion()).isNull();
    }

    @Test
    void eliminar_cuponUsadoEnPedidos_error409YNoBorra() {
        when(pedidoRepository.existsConCupon(1L)).thenReturn(true);

        assertThatThrownBy(() -> cuponService.eliminar(1L))
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        verify(cuponRepository, never()).deleteById(any());
    }

    @Test
    void estado_muestraInactivoVencidoAgotadoOActivo() {
        assertThat(CuponServiceImpl.estado(cupon(Cupon.PORCENTAJE, 10, 0, null, 0, null, false), HOY)).isEqualTo("Inactivo");
        assertThat(CuponServiceImpl.estado(cupon(Cupon.PORCENTAJE, 10, 0, null, 0, HOY.minusDays(1), true), HOY)).isEqualTo("Vencido");
        assertThat(CuponServiceImpl.estado(cupon(Cupon.PORCENTAJE, 10, 0, 2, 2, null, true), HOY)).isEqualTo("Agotado");
        assertThat(CuponServiceImpl.estado(cupon(Cupon.PORCENTAJE, 10, 0, 2, 1, HOY, true), HOY)).isEqualTo("Activo");
    }
}
