package com.example.demo.system;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Caso 2: Flujo End-to-End (E2E) con Selenium WebDriver.
 *
 * Requerimiento:
 * 1. Llega un usuario ya registrado a la plataforma, ingresa sus credenciales y hace login.
 * 2. Va a la sección de menú y agrega 2 comidas, cada una con 2 adicionales.
 * 3. Antes de realizar el pedido verifica que el carrito esté correcto (comidas, adicionales y total).
 * 4. Confirma el envío y espera la respuesta del servidor.
 * 5. En otra pestaña un operador inicia sesión, va a pedidos, selecciona el nuevo pedido y cambia su estado.
 *    En la pestaña del usuario se verifica que aparezca el cambio de estado.
 * 6. El operador sigue cambiando el estado, asigna el domiciliario y completa el pedido.
 * 7. El usuario entra a su perfil y en el historial revisa que el pedido esté entregado,
 *    con todas las comidas y adicionales elegidos, y que el total corresponda a los productos.
 *
 * El total esperado NO está quemado: se calcula con los precios que la prueba lee en pantalla
 * (precio de cada comida y de cada adicional en la página de detalle del producto).
 *
 * Para correrla:
 *  - Levantar el frontend con "ng serve" (http://localhost:4200).
 *  - NO tener el backend corriendo: esta prueba levanta su propio Spring Boot en el puerto 8090
 *    con el perfil "test" (base H2 en memoria, así no se ensucia mydatabase).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class Caso2PedidoClienteOperadorTest {

    private WebDriver driver;
    private WebDriverWait wait;

    // URL base de la aplicación frontend en Angular (puerto 4200 por defecto)
    private static final String BASE_URL = System.getProperty("frontend.url", "http://localhost:4200");

    // Pausa entre pasos para poder ver la prueba (se puede quitar con -De2e.pausa=0)
    private static final int PAUSA_MS = Integer.parseInt(System.getProperty("e2e.pausa", "800"));

    // Cliente ya registrado por el DataLoader (no tiene pedidos previos)
    private static final String CORREO_CLIENTE = "diegui200@gmail.com";
    private static final String CONTRASENA_CLIENTE = "1233377";
    private static final String NOMBRE_CLIENTE = "Diego";

    // Operador registrado por el DataLoader
    private static final String USUARIO_OPERADOR = "KikaNieto_";
    private static final String CONTRASENA_OPERADOR = "1234";

    // Comidas que el cliente va a pedir (las dos son de "Pastas", que tiene varios adicionales)
    private static final String COMIDA_1 = "pasta gratinada del chef";
    private static final String COMIDA_2 = "Carbonara di mare";
    private static final int ADICIONALES_POR_COMIDA = 2;

    // Lo que el cliente eligió, con los precios leídos de la pantalla
    record AdicionalElegido(String nombre, long precio) {}

    record ComidaElegida(String nombre, long precio, List<AdicionalElegido> adicionales) {
        // Precio de la comida más sus adicionales (1 unidad de cada uno)
        long subtotal() {
            long total = precio;
            for (AdicionalElegido adicional : adicionales) {
                total += adicional.precio();
            }
            return total;
        }
    }

    @BeforeEach
    void setUp() {
        // WebDriverManager descarga el ChromeDriver que corresponde a tu Chrome.
        // Si ya tienes uno, se puede indicar con -Dwebdriver.chrome.driver=ruta/al/chromedriver
        if (System.getProperty("webdriver.chrome.driver") == null) {
            WebDriverManager.chromedriver().setup();
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.addArguments("--window-size=1920,1080");
        // Para correr sin interfaz visual: mvn test -De2e.headless=true
        if (Boolean.parseBoolean(System.getProperty("e2e.headless", "false"))) {
            options.addArguments("--headless=new");
        }

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Caso 2: Cliente hace un pedido con 2 comidas y 2 adicionales c/u, el operador lo lleva hasta Entregado y el cliente revisa su historial")
    void testCaso2PedidoClienteYOperador() {

        // =========================================================================
        // PASO 1: El cliente llega a la plataforma e inicia sesión
        // =========================================================================
        driver.get(BASE_URL);
        pausar(PAUSA_MS);

        scrollYClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("navLogin"))));
        wait.until(ExpectedConditions.urlContains("/login"));

        limpiarYEscribir(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("correo"))), CORREO_CLIENTE);
        limpiarYEscribir(driver.findElement(By.id("contrasena")), CONTRASENA_CLIENTE);
        pausar(PAUSA_MS);
        scrollYClick(driver.findElement(By.id("btnIngresar")));

        // Al iniciar sesión el sistema lo lleva a su perfil
        wait.until(ExpectedConditions.urlContains("/clientes/perfil/"));
        wait.until(textoPresente(By.className("perfil-nombre"), NOMBRE_CLIENTE));
        String urlPerfil = driver.getCurrentUrl();
        pausar(PAUSA_MS);

        // =========================================================================
        // PASO 2: Va al menú y agrega 2 comidas con 2 adicionales cada una
        // =========================================================================
        List<ComidaElegida> comidas = new ArrayList<>();
        comidas.add(agregarComidaConAdicionales(COMIDA_1, ADICIONALES_POR_COMIDA));
        comidas.add(agregarComidaConAdicionales(COMIDA_2, ADICIONALES_POR_COMIDA));

        // Total esperado calculado con los precios que se leyeron en pantalla
        long totalEsperado = 0;
        for (ComidaElegida comida : comidas) {
            totalEsperado += comida.subtotal();
        }

        // =========================================================================
        // PASO 3: Antes de pedir, verifica que el carrito esté correcto
        // =========================================================================
        scrollYClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("btnCarrito"))));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("carrito-drawer")));

        List<WebElement> itemsCarrito = wait.until(
                ExpectedConditions.numberOfElementsToBe(By.className("carrito-item"), comidas.size()));
        assertThat(itemsCarrito).hasSize(comidas.size());

        for (ComidaElegida comida : comidas) {
            WebElement item = buscarItemCarrito(itemsCarrito, comida.nombre());
            String textoAdicionales = textoDe(item.findElements(By.className("carrito-item-adicional")));
            for (AdicionalElegido adicional : comida.adicionales()) {
                assertThat(textoAdicionales)
                        .as("Adicional %s de %s en el carrito", adicional.nombre(), comida.nombre())
                        .containsIgnoringCase("1x " + adicional.nombre());
            }
            assertThat(soloNumero(item.findElement(By.className("carrito-item-subtotal")).getText()))
                    .as("Subtotal de %s en el carrito", comida.nombre())
                    .isEqualTo(comida.subtotal());
        }
        assertThat(soloNumero(driver.findElement(By.id("carritoTotal")).getText()))
                .as("Total del carrito")
                .isEqualTo(totalEsperado);
        pausar(PAUSA_MS * 2);

        // =========================================================================
        // PASO 4: Confirma el pedido y espera la respuesta del servicio REST
        // =========================================================================
        scrollYClick(driver.findElement(By.id("btnConfirmarPedido")));

        // El mensaje de éxito se muestra poco tiempo, por eso se revisa cada 100 ms
        WebDriverWait esperaRapida = new WebDriverWait(driver, Duration.ofSeconds(12), Duration.ofMillis(100));
        String textoExito = esperaRapida.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("pedidoExito"))).getText();
        assertThat(textoExito).containsIgnoringCase("realizado con éxito");

        Matcher matcher = Pattern.compile("#(\\d+)").matcher(textoExito);
        assertThat(matcher.find()).as("El mensaje de éxito trae el número del pedido").isTrue();
        String idPedido = matcher.group(1);

        // Después de pedir, el sistema lo devuelve a su perfil con el pedido en "Recibido"
        wait.until(ExpectedConditions.urlContains("/clientes/perfil/"));
        By estadoPedidoCliente = By.cssSelector("#pedido-" + idPedido + " .pedido-estado");
        wait.until(textoPresente(estadoPedidoCliente, "Recibido"));
        centrarEnPantalla(driver.findElement(By.id("pedido-" + idPedido)));
        pausar(PAUSA_MS * 2);

        String pestanaCliente = driver.getWindowHandle();

        // =========================================================================
        // PASO 5: En otra pestaña un operador inicia sesión y va a la sección de pedidos
        // =========================================================================
        driver.switchTo().newWindow(WindowType.TAB);
        String pestanaOperador = driver.getWindowHandle();

        driver.get(BASE_URL + "/login-operario");
        limpiarYEscribir(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usuario"))), USUARIO_OPERADOR);
        limpiarYEscribir(driver.findElement(By.id("contrasena")), CONTRASENA_OPERADOR);
        pausar(PAUSA_MS);
        scrollYClick(driver.findElement(By.id("btnIngresarOperador")));

        wait.until(ExpectedConditions.urlContains("/portal-operario"));
        pausar(PAUSA_MS);
        scrollYClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("btnGestionarPedidos"))));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fila-pedido-" + idPedido)));
        verificarEstadoEnFila(idPedido, "Recibido");
        pausar(PAUSA_MS);

        // Selecciona el nuevo pedido y lo pasa a "Cocinando"
        avanzarEstado(idPedido, comidas.size(), "Recibido", "Cocinando", false);
        verificarEstadoEnFila(idPedido, "Cocinando");

        // En la pestaña del usuario debe aparecer el cambio de estado
        driver.switchTo().window(pestanaCliente);
        driver.navigate().refresh();
        wait.until(textoPresente(estadoPedidoCliente, "Cocinando"));
        centrarEnPantalla(driver.findElement(By.id("pedido-" + idPedido)));
        pausar(PAUSA_MS * 2);

        // =========================================================================
        // PASO 6: El operador lo pasa a "Enviado" asignando domiciliario y luego a "Entregado"
        // =========================================================================
        driver.switchTo().window(pestanaOperador);
        String domiciliario = avanzarEstado(idPedido, comidas.size(), "Cocinando", "Enviado", true);
        verificarEstadoEnFila(idPedido, "Enviado");
        assertThat(driver.findElement(By.cssSelector("#fila-pedido-" + idPedido + " .pedido-domiciliario")).getText())
                .as("Domiciliario asignado al pedido")
                .isEqualToIgnoringCase(domiciliario);

        driver.switchTo().window(pestanaCliente);
        driver.navigate().refresh();
        wait.until(textoPresente(estadoPedidoCliente, "Enviado"));
        pausar(PAUSA_MS);

        driver.switchTo().window(pestanaOperador);
        avanzarEstado(idPedido, comidas.size(), "Enviado", "Entregado", false);
        // Un pedido entregado sale de la lista de pedidos activos del operador
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("fila-pedido-" + idPedido)));
        pausar(PAUSA_MS);

        // El operador cierra su sesión y cierra su pestaña
        driver.get(BASE_URL + "/portal-operario");
        scrollYClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("btnCerrarSesionOperador"))));
        wait.until(ExpectedConditions.urlContains("/login-operario"));
        driver.close();

        // =========================================================================
        // PASO 7: El usuario entra a su perfil y revisa el pedido en el historial
        // =========================================================================
        driver.switchTo().window(pestanaCliente);
        scrollYClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("navMenu"))));
        wait.until(ExpectedConditions.urlContains("/menu"));
        scrollYClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("navPerfilCliente"))));
        wait.until(ExpectedConditions.urlToBe(urlPerfil));

        wait.until(textoPresente(estadoPedidoCliente, "Entregado"));
        WebElement pedido = driver.findElement(By.id("pedido-" + idPedido));
        centrarEnPantalla(pedido);

        List<WebElement> lineasPedido = pedido.findElements(By.className("pedido-producto"));
        assertThat(lineasPedido).as("Comidas del pedido en el historial").hasSize(comidas.size());

        for (ComidaElegida comida : comidas) {
            String texto = buscarLineaHistorial(lineasPedido, comida.nombre()).getText();
            for (AdicionalElegido adicional : comida.adicionales()) {
                assertThat(texto)
                        .as("Adicional %s de %s en el historial", adicional.nombre(), comida.nombre())
                        .containsIgnoringCase("1x " + adicional.nombre());
            }
            // El subtotal de la línea es lo que va después del último "$"
            assertThat(soloNumero(texto.substring(texto.lastIndexOf('$'))))
                    .as("Subtotal de %s en el historial", comida.nombre())
                    .isEqualTo(comida.subtotal());
        }

        assertThat(soloNumero(pedido.findElement(By.className("pedido-total")).getText()))
                .as("Total a pagar del pedido")
                .isEqualTo(totalEsperado);
        pausar(PAUSA_MS * 3);
    }

    // Desde el menú abre el detalle de una comida, elige sus primeros adicionales y la agrega al carrito.
    // Devuelve la comida con los precios leídos en pantalla.
    private ComidaElegida agregarComidaConAdicionales(String nombreComida, int cantidadAdicionales) {
        scrollYClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("navMenu"))));
        wait.until(ExpectedConditions.urlContains("/menu"));

        // Busca la tarjeta de la comida por su título y entra a "Ver detalle / Adicionales"
        By botonDetalle = By.xpath("//app-producto-card[.//h5[normalize-space()=\"" + nombreComida
                + "\"]]//a[contains(@class, 'btn-detalle-producto')]");
        WebElement verDetalle = wait.until(ExpectedConditions.elementToBeClickable(botonDetalle));
        centrarEnPantalla(verDetalle);
        pausar(PAUSA_MS);
        scrollYClick(verDetalle);

        wait.until(textoPresente(By.id("detalleNombre"), nombreComida));
        long precioComida = soloNumero(driver.findElement(By.id("detallePrecio")).getText());

        // Espera a que el servicio REST traiga los adicionales de la categoría
        List<WebElement> opciones = wait.until(ExpectedConditions
                .numberOfElementsToBeMoreThan(By.className("adicional-opcion"), cantidadAdicionales - 1));
        List<String> idsOpciones = new ArrayList<>();
        for (int i = 0; i < cantidadAdicionales; i++) {
            idsOpciones.add(opciones.get(i).getAttribute("id"));
        }

        List<AdicionalElegido> adicionales = new ArrayList<>();
        for (String idOpcion : idsOpciones) {
            WebElement opcion = driver.findElement(By.id(idOpcion));
            String nombre = opcion.findElement(By.className("adicional-nombre")).getText().trim();
            long precio = soloNumero(opcion.findElement(By.className("adicional-precio")).getText());
            scrollYClick(opcion);
            wait.until(ExpectedConditions.attributeContains(By.id(idOpcion), "class", "adicional-activo"));
            adicionales.add(new AdicionalElegido(nombre, precio));
            pausar(PAUSA_MS);
        }

        scrollYClick(driver.findElement(By.id("btnAgregarCarrito")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("alertaAgregado")));
        pausar(PAUSA_MS);

        return new ComidaElegida(nombreComida, precioComida, adicionales);
    }

    // Desde la lista de pedidos del operador: abre el pedido, lo pasa al siguiente estado y guarda.
    // Si se pide, asigna el primer domiciliario disponible y devuelve su nombre.
    private String avanzarEstado(String idPedido, int cantidadComidas, String estadoActual,
            String estadoSiguiente, boolean asignarDomiciliario) {
        scrollYClick(wait.until(ExpectedConditions.elementToBeClickable(By.id("editar-pedido-" + idPedido))));
        wait.until(ExpectedConditions.urlContains("/pedidos/editar/" + idPedido));

        // Espera a que el pedido cargue desde el backend (sus comidas y su estado real)
        wait.until(ExpectedConditions.numberOfElementsToBe(By.className("pedido-comida-item"), cantidadComidas));
        wait.until(textoPresente(By.id("estadoActual"), estadoActual));
        pausar(PAUSA_MS);

        WebElement botonAvanzar = wait.until(ExpectedConditions.elementToBeClickable(By.id("btnAvanzarEstado")));
        assertThat(botonAvanzar.getText()).containsIgnoringCase(estadoSiguiente);
        scrollYClick(botonAvanzar);
        wait.until(textoPresente(By.className("estado-avance-confirmado"), estadoSiguiente));

        String domiciliario = null;
        if (asignarDomiciliario) {
            // Espera a que lleguen los domiciliarios disponibles y elige el primero
            wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("#domiciliario option"), 1));
            WebElement selectElement = driver.findElement(By.id("domiciliario"));
            centrarEnPantalla(selectElement);
            Select select = new Select(selectElement);
            select.selectByIndex(1);
            domiciliario = select.getFirstSelectedOption().getText().trim();
        }
        pausar(PAUSA_MS);

        scrollYClick(driver.findElement(By.id("btnGuardarPedido")));
        wait.until(ExpectedConditions.urlMatches("/pedidos$"));
        pausar(PAUSA_MS);
        return domiciliario;
    }

    // Verifica el estado que muestra la tabla de pedidos del operador
    private void verificarEstadoEnFila(String idPedido, String estado) {
        wait.until(textoPresente(By.cssSelector("#fila-pedido-" + idPedido + " .pedido-estado-badge"), estado));
    }

    // Espera a que el elemento contenga el texto, sin importar mayúsculas (el CSS puede capitalizar)
    private ExpectedCondition<Boolean> textoPresente(By localizador, String texto) {
        return d -> d.findElement(localizador).getText().toLowerCase().contains(texto.toLowerCase());
    }

    // Convierte textos como "$ 50,000" o "+ $ 5.500" en 50000 y 5500
    private long soloNumero(String texto) {
        String digitos = texto.replaceAll("[^0-9]", "");
        assertThat(digitos).as("Valor numérico en \"%s\"", texto).isNotEmpty();
        return Long.parseLong(digitos);
    }

    private String textoDe(List<WebElement> elementos) {
        StringBuilder texto = new StringBuilder();
        for (WebElement elemento : elementos) {
            texto.append(elemento.getText()).append(' ');
        }
        return texto.toString();
    }

    private WebElement buscarItemCarrito(List<WebElement> items, String nombreComida) {
        for (WebElement item : items) {
            if (item.findElement(By.className("carrito-item-nombre")).getText().trim().equalsIgnoreCase(nombreComida)) {
                return item;
            }
        }
        throw new AssertionError("No se encontró \"" + nombreComida + "\" en el carrito");
    }

    private WebElement buscarLineaHistorial(List<WebElement> lineas, String nombreComida) {
        for (WebElement linea : lineas) {
            if (linea.getText().toLowerCase().contains(nombreComida.toLowerCase())) {
                return linea;
            }
        }
        throw new AssertionError("No se encontró \"" + nombreComida + "\" en el historial del pedido");
    }

    private void centrarEnPantalla(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
    }

    private void scrollYClick(WebElement element) {
        centrarEnPantalla(element);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element)).click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    private void limpiarYEscribir(WebElement input, String texto) {
        centrarEnPantalla(input);
        input.click();
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        input.sendKeys(Keys.BACK_SPACE);
        input.sendKeys(texto);
    }

    private void pausar(int milisegundos) {
        if (milisegundos <= 0) {
            return;
        }
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
