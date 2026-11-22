package com.example.demo.service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.entities.Cupon;
import com.example.demo.repository.CuponRepository;
import com.example.demo.repository.PedidoRepository;

// Implementacion del servicio de cupones
@Service
public class CuponServiceImpl implements CuponService {

    // Letras, numeros, guion y guion bajo; entre 3 y 20 caracteres
    private static final Pattern FORMATO_CODIGO = Pattern.compile("^[A-Z0-9_-]{3,20}$");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CuponRepository cuponRepository;
    private final PedidoRepository pedidoRepository;

    public CuponServiceImpl(CuponRepository cuponRepository, PedidoRepository pedidoRepository) {
        this.cuponRepository = cuponRepository;
        this.pedidoRepository = pedidoRepository;
    }

    // Estado del cupon para mostrarlo en la lista del administrador
    public static String estado(Cupon cupon, LocalDate hoy) {
        if (!cupon.isActivo()) {
            return "Inactivo";
        }
        if (cupon.getFechaVencimiento() != null && cupon.getFechaVencimiento().isBefore(hoy)) {
            return "Vencido";
        }
        if (agotado(cupon)) {
            return "Agotado";
        }
        return "Activo";
    }

    private static boolean agotado(Cupon cupon) {
        return cupon.getUsosMaximos() != null && cupon.getUsos() >= cupon.getUsosMaximos();
    }

    @Override
    public List<Cupon> findAll() {
        return cuponRepository.findAll().stream()
                .sorted(Comparator.comparing(Cupon::getId_cupon).reversed())
                .toList();
    }

    @Override
    public Cupon findById(Long id) {
        return cuponRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Cupon crear(Cupon cupon) {
        cupon.setId_cupon(null);
        cupon.setUsos(0);
        validarDatos(cupon, null);
        return cuponRepository.save(cupon);
    }

    @Override
    @Transactional
    public Cupon actualizar(Long id, Cupon cambios) {
        Cupon existente = findById(id);
        if (existente == null) {
            return null;
        }
        validarDatos(cambios, id);
        existente.setCodigo(cambios.getCodigo());
        existente.setDescripcion(cambios.getDescripcion());
        existente.setTipo(cambios.getTipo());
        existente.setValor(cambios.getValor());
        existente.setCompraMinima(cambios.getCompraMinima());
        existente.setUsosMaximos(cambios.getUsosMaximos());
        existente.setFechaVencimiento(cambios.getFechaVencimiento());
        existente.setActivo(cambios.isActivo());
        // Los usos no se cambian desde el formulario: solo cuentan los pedidos
        return cuponRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (pedidoRepository.existsConCupon(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar el cupón porque ya se usó en pedidos. Puedes desactivarlo.");
        }
        cuponRepository.deleteById(id);
    }

    @Override
    public Cupon validar(String codigo, double subtotal) {
        return validar(codigo, subtotal, LocalDate.now());
    }

    // Igual que validar(codigo, subtotal) pero con la fecha de hoy como parametro (para las pruebas)
    Cupon validar(String codigo, double subtotal, LocalDate hoy) {
        String limpio = normalizarCodigo(codigo);
        if (limpio.isEmpty()) {
            throw error("Escribe el código del cupón");
        }
        Cupon cupon = cuponRepository.findByCodigoIgnoreCase(limpio)
                .orElseThrow(() -> error("El cupón " + limpio + " no existe"));
        if (!cupon.isActivo()) {
            throw error("El cupón " + limpio + " ya no está disponible");
        }
        if (cupon.getFechaVencimiento() != null && cupon.getFechaVencimiento().isBefore(hoy)) {
            throw error("El cupón " + limpio + " venció el " + cupon.getFechaVencimiento().format(FORMATO_FECHA));
        }
        if (agotado(cupon)) {
            throw error("El cupón " + limpio + " ya alcanzó su límite de usos");
        }
        if (subtotal < cupon.getCompraMinima()) {
            throw error("El cupón " + limpio + " aplica para compras desde " + pesos(cupon.getCompraMinima()));
        }
        return cupon;
    }

    @Override
    public double calcularDescuento(Cupon cupon, double subtotal) {
        if (cupon == null || subtotal <= 0) {
            return 0;
        }
        double descuento = Cupon.PORCENTAJE.equals(cupon.getTipo())
                ? subtotal * cupon.getValor() / 100
                : cupon.getValor();
        // En pesos enteros y nunca mas que la compra
        return Math.min(Math.round(descuento), subtotal);
    }

    @Override
    @Transactional
    public void registrarUso(Cupon cupon) {
        cupon.setUsos(cupon.getUsos() + 1);
        cuponRepository.save(cupon);
    }

    // ---------------------------------------------------------------- validacion del formulario

    private void validarDatos(Cupon cupon, Long idActual) {
        String codigo = normalizarCodigo(cupon.getCodigo());
        if (!FORMATO_CODIGO.matcher(codigo).matches()) {
            throw error("El código debe tener entre 3 y 20 letras o números, sin espacios");
        }
        cuponRepository.findByCodigoIgnoreCase(codigo)
                .filter(otro -> !otro.getId_cupon().equals(idActual))
                .ifPresent(otro -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cupón con el código " + codigo);
                });
        cupon.setCodigo(codigo);

        if (!Cupon.PORCENTAJE.equals(cupon.getTipo()) && !Cupon.VALOR_FIJO.equals(cupon.getTipo())) {
            throw error("El tipo de descuento debe ser porcentaje o valor fijo");
        }
        if (cupon.getValor() <= 0) {
            throw error("El valor del descuento debe ser mayor que 0");
        }
        if (Cupon.PORCENTAJE.equals(cupon.getTipo()) && cupon.getValor() > 100) {
            throw error("El porcentaje no puede ser mayor que 100");
        }
        if (cupon.getCompraMinima() < 0) {
            throw error("La compra mínima no puede ser negativa");
        }
        if (cupon.getUsosMaximos() != null && cupon.getUsosMaximos() < 1) {
            throw error("El máximo de usos debe ser al menos 1 (o vacío para usos ilimitados)");
        }
        if (cupon.getDescripcion() != null) {
            String descripcion = cupon.getDescripcion().trim();
            if (descripcion.length() > 120) {
                throw error("La descripción no puede tener más de 120 caracteres");
            }
            cupon.setDescripcion(descripcion.isEmpty() ? null : descripcion);
        }
    }

    private static String normalizarCodigo(String codigo) {
        return codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT);
    }

    private static ResponseStatusException error(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private static String pesos(double valor) {
        NumberFormat formato = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO"));
        return "$" + formato.format(valor);
    }
}
