package com.example.demo.system;

import java.time.Duration;
import java.util.List;

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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.example.demo.entities.Adicional;
import com.example.demo.entities.Administrador;
import com.example.demo.entities.Categoria;
import com.example.demo.entities.Producto;
import com.example.demo.repository.AdicionalRepository;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ProductoRepository;

import io.github.bonigarcia.wdm.WebDriverManager;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Caso 1: Flujo End-to-End (E2E) con Selenium WebDriver.
 * 
 * Requerimiento:
 * 1. El administrador comienza en el landing page y va a su página de login.
 * 2. Trata de ingresar, sin embargo se equivoca la primera vez en sus credenciales. Verificar el error.
 * 3. Posteriormente ingresa de manera correcta, y va a la sección de productos.
 * 4. Crea un nuevo producto en la categoría "Pescados y Mariscos", regresa al menú de admin,
 *    entra a la sección de adicionales y agrega 2 adicionales a esa categoría.
 * 5. Verifica en otra pestaña que el producto aparezca en la sección de menú con los 2 adicionales correspondientes.
 * 6. Se da cuenta que falta un adicional, vuelve a su portal de admin, entra a adicionales y agrega el 3er adicional a la categoría.
 * 7. Finalmente verifica en el menú que esté el producto con los 3 adicionales y los selecciona.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class Caso1RegistroProductoTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    private static final String CATEGORIA_OBJETIVO = "Pescados y Mariscos";
    private static final String NOMBRE_PRODUCTO = "Cazuela de Mariscos";

    // URL base de la aplicación frontend en Angular (puerto 4200 por defecto)
    private static final String BASE_URL = System.getProperty("frontend.url", "http://localhost:4200");

    @BeforeEach
    void setUp() {
        // 1. Asegurar que exista el administrador por defecto en la base de datos
        if (administradorRepository.findByUsuario("admin1") == null) {
            administradorRepository.save(new Administrador("Daniel Cedeño", "admin1", "1234"));
        }

        // 2. Limpiar productos de ejecuciones anteriores para que no aparezcan duplicados ni códigos en el menú
        List<Producto> todosLosProductos = productoRepository.findAll();
        for (Producto p : todosLosProductos) {
            String nombre = p.getNombre() != null ? p.getNombre().trim() : "";
            if (nombre.equalsIgnoreCase(NOMBRE_PRODUCTO)
                    || nombre.toUpperCase().contains("E2E")
                    || nombre.toLowerCase().contains("cazuela de mariscos")
                    || nombre.toLowerCase().contains("bruschetta especial")) {
                try {
                    productoRepository.delete(p);
                } catch (Exception e) {
                    p.setDisponible(false);
                    productoRepository.save(p);
                }
            }
        }

        // 3. Limpiar adicionales sobrantes con sufijos antiguos y dejar "Pescados y Mariscos" con 0 adicionales
        Categoria categoriaPescados = categoriaRepository.findAll().stream()
                .filter(c -> c.getNombre() != null && c.getNombre().trim().equalsIgnoreCase(CATEGORIA_OBJETIVO))
                .findFirst()
                .orElseGet(() -> categoriaRepository.save(new Categoria(CATEGORIA_OBJETIVO)));

        for (Adicional ad : adicionalRepository.findAll()) {
            boolean esDePescados = ad.getCategoria() != null
                    && ad.getCategoria().getId().equals(categoriaPescados.getId());
            boolean esDePruebaAnterior = ad.getNombre() != null
                    && (ad.getNombre().toUpperCase().contains("E2E")
                        || ad.getNombre().toLowerCase().contains("salsa tártara artesanal"));

            if (esDePescados || esDePruebaAnterior) {
                try {
                    if (!adicionalRepository.existsEnDetallePedido(ad.getId_adicional())
                            && !adicionalRepository.existsEnCarrito(ad.getId_adicional())) {
                        adicionalRepository.delete(ad);
                    } else {
                        ad.setDisponible(false);
                        adicionalRepository.save(ad);
                    }
                } catch (Exception e) {
                    ad.setDisponible(false);
                    adicionalRepository.save(ad);
                }
            }
        }

        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.addArguments("--start-maximized");
        options.addArguments("--window-size=1920,1080");
        // Descomenta la siguiente línea si deseas ejecutar las pruebas sin interfaz visual (Headless):
        // options.addArguments("--headless");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    private void centrarEnPantalla(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'});",
            element
        );
        pausar(600);
    }

    private void scrollYClick(WebElement element) {
        centrarEnPantalla(element);
        try {
            element.click();
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

    private void seleccionarOpcionPorTexto(By selectLocator, String textoOpcion) {
        // Esperar a que Angular cargue las opciones dentro del <select>
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(
            selectLocator,
            By.xpath(".//option[contains(normalize-space(), '" + textoOpcion + "')]")
        ));
        WebElement selectElement = driver.findElement(selectLocator);
        centrarEnPantalla(selectElement);
        Select select = new Select(selectElement);
        for (WebElement opt : select.getOptions()) {
            if (opt.getText().trim().equalsIgnoreCase(textoOpcion)) {
                select.selectByVisibleText(opt.getText());
                break;
            }
        }
    }

    private void regresarAPortalAdmin() {
        // Hacer clic en el botón 'PERFIL' de la barra de navegación superior para regresar a las opciones de admin
        try {
            WebElement perfilNav = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[routerLink='/portal-admin']")
            ));
            scrollYClick(perfilNav);
            new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.urlContains("/portal-admin"));
        } catch (Exception e) {
            driver.get(BASE_URL + "/portal-admin");
        }
        wait.until(ExpectedConditions.urlContains("/portal-admin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("dashboard-contenedor")));
        pausar(2000);
    }

    private void irAGestionarAdicionalesDesdePortal() {
        WebElement gestionarAdicionalesBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//h3[contains(text(), 'Gestionar Adicionales')]/ancestor::a | //a[contains(@href, '/adicionales')]")
        ));
        centrarEnPantalla(gestionarAdicionalesBtn);
        pausar(1200);
        scrollYClick(gestionarAdicionalesBtn);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.urlContains("/adicionales"));
        } catch (Exception e) {
            driver.get(BASE_URL + "/adicionales");
        }
        wait.until(ExpectedConditions.urlContains("/adicionales"));
        pausar(2000);
    }

    private void crearAdicionalEnUI(String nombreAdicional, String precio, String nombreCategoria) {
        // Hacer clic en '+ Nuevo Adicional'
        WebElement nuevoAdicionalBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//a[contains(@href, '/adicionales/nuevo') or contains(text(), 'Nuevo Adicional')]")
        ));
        scrollYClick(nuevoAdicionalBtn);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.urlContains("/adicionales/nuevo"));
        } catch (Exception e) {
            driver.get(BASE_URL + "/adicionales/nuevo");
        }
        wait.until(ExpectedConditions.urlContains("/adicionales/nuevo"));
        pausar(1500);

        // Llenar nombre del adicional
        WebElement nombreAdicionalInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nombre")));
        limpiarYEscribir(nombreAdicionalInput, nombreAdicional);
        pausar(1000);

        // Llenar precio del adicional
        WebElement precioAdicionalInput = driver.findElement(By.id("precio"));
        limpiarYEscribir(precioAdicionalInput, precio);
        pausar(1000);

        // Seleccionar la categoría correspondiente (ej. "Pescados y Mariscos")
        seleccionarOpcionPorTexto(By.id("categoria"), nombreCategoria);
        pausar(1200);

        // Asegurar que esté marcado como Disponible
        WebElement dispAdicionalCheck = driver.findElement(By.id("disponible"));
        if (!dispAdicionalCheck.isSelected()) {
            scrollYClick(dispAdicionalCheck);
        }
        pausar(1800); // Pausa para ver el formulario del adicional lleno antes de guardar

        // Guardar el adicional y esperar a que termine la petición y regrese a /adicionales
        WebElement btnGuardarAdicional = driver.findElement(By.cssSelector("button.adicional-form-btn-guardar"));
        scrollYClick(btnGuardarAdicional);

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/nuevo")));
        wait.until(ExpectedConditions.urlContains("/adicionales"));
        pausar(2200); // Pausa para ver la lista de adicionales tras guardar
    }

    private void pausar(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException ignored) {}
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Caso 1: Registro de producto en Pescados y Mariscos, creación de 2 adicionales, verificación en menú y adición del 3er adicional")
    void testCaso1RegistroProductoYAdicionales() {

        // =========================================================================
        // PASO 1: Comenzar en el Landing Page y navegar a la página de Login Admin
        // =========================================================================
        driver.get(BASE_URL);
        pausar(2500);

        // En el navbar, hacer clic en el enlace 'LOG IN'
        WebElement loginNavBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("a.nav-link.login")
        ));
        loginNavBtn.click();
        pausar(1800);

        // Al estar en /login, la barra de navegación muestra el acceso para 'ADMIN'
        WebElement adminLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("a[routerLink='/login-admin']")
        ));
        adminLink.click();

        // Verificar que estamos en la pantalla de login del administrador
        wait.until(ExpectedConditions.urlContains("/login-admin"));
        WebElement usuarioInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("usuario")));
        WebElement contrasenaInput = driver.findElement(By.id("contrasena"));
        WebElement submitLoginBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        pausar(1500);

        // =========================================================================
        // PASO 2: Intentar ingresar con credenciales incorrectas y verificar error
        // =========================================================================
        usuarioInput.sendKeys("admin_invalido");
        pausar(800);
        contrasenaInput.sendKeys("clave_erronea_123");
        pausar(1200);
        submitLoginBtn.click();

        // Esperar el mensaje de error en pantalla y verificar su contenido
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.className("admin-form-error")
        ));
        assertThat(errorMsg.getText()).contains("Usuario o contraseña incorrectos");
        pausar(3000); // Pausa amplia para apreciar el mensaje de error en pantalla

        // =========================================================================
        // PASO 3: Ingresar con credenciales correctas e ir a la sección de productos
        // =========================================================================
        WebElement userInput = wait.until(ExpectedConditions.elementToBeClickable(By.id("usuario")));
        limpiarYEscribir(userInput, "admin1");
        pausar(1000);

        WebElement passInput = driver.findElement(By.id("contrasena"));
        limpiarYEscribir(passInput, "1234");
        pausar(1200);

        WebElement loginBtn = driver.findElement(By.cssSelector("button[type='submit']"));
        loginBtn.click();

        // Esperar llegada al panel de control del administrador (/portal-admin)
        wait.until(ExpectedConditions.urlContains("/portal-admin"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("dashboard-contenedor")));
        pausar(2500);

        // Ir a la sección 'Gestionar Comidas' (/productos)
        WebElement irAProductosBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//h3[contains(text(), 'Gestionar Comidas')]/ancestor::a | //a[contains(@href, '/productos')]")
        ));
        centrarEnPantalla(irAProductosBtn);
        pausar(1200);
        scrollYClick(irAProductosBtn);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.urlContains("/productos"));
        } catch (Exception e) {
            driver.get(BASE_URL + "/productos");
        }
        wait.until(ExpectedConditions.urlContains("/productos"));
        pausar(2500);

        // =========================================================================
        // PASO 4: Crear un nuevo producto en la categoría 'Pescados y Mariscos'
        //         y luego ir a Adicionales para agregarle los 2 adicionales iniciales
        // =========================================================================
        WebElement nuevoProductoBtn = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//a[contains(@href, '/productos/nuevo') or contains(text(), 'Nuevo Producto')]")
        ));
        scrollYClick(nuevoProductoBtn);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.urlContains("/productos/nuevo"));
        } catch (Exception e) {
            driver.get(BASE_URL + "/productos/nuevo");
        }
        wait.until(ExpectedConditions.urlContains("/productos/nuevo"));
        pausar(1800);

        // Llenar campos del nuevo producto (sin códigos numéricos)
        WebElement nombreInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("nombre")));
        limpiarYEscribir(nombreInput, NOMBRE_PRODUCTO);
        pausar(1000);

        WebElement precioInput = driver.findElement(By.name("precio"));
        limpiarYEscribir(precioInput, "54000");
        pausar(1000);

        WebElement descInput = driver.findElement(By.name("descripcion"));
        limpiarYEscribir(descInput, "Cazuela cremosa de mariscos frescos al estilo mediterráneo con toque de ajo y vino blanco");
        pausar(1000);

        WebElement imgInput = driver.findElement(By.name("imagen"));
        limpiarYEscribir(imgInput, "https://images.unsplash.com/photo-1534422298391-e4f8c172dddb");
        pausar(1000);

        // Seleccionar la categoría 'Pescados y Mariscos'
        seleccionarOpcionPorTexto(By.name("categoriaId"), CATEGORIA_OBJETIVO);
        pausar(1200);

        // Asegurar que el producto esté marcado como disponible
        WebElement disponibleCheck = driver.findElement(By.name("disponible"));
        if (!disponibleCheck.isSelected()) {
            scrollYClick(disponibleCheck);
        }
        pausar(2200); // Pausa para ver el formulario de la comida antes de guardar

        // Guardar el nuevo producto y esperar a que regrese a la tabla /productos (sin /nuevo)
        WebElement btnGuardarProducto = driver.findElement(By.cssSelector("button.producto-form-btn-guardar"));
        scrollYClick(btnGuardarProducto);

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/nuevo")));
        wait.until(ExpectedConditions.urlContains("/productos"));
        pausar(2500);

        // Regresar al menú de opciones de Administrador (/portal-admin) y entrar a Gestionar Adicionales
        regresarAPortalAdmin();
        irAGestionarAdicionalesDesdePortal();

        // Crear los 2 adicionales iniciales para la categoría 'Pescados y Mariscos' (sin códigos)
        crearAdicionalEnUI("Patacones Crocantes", "4500", CATEGORIA_OBJETIVO);
        crearAdicionalEnUI("Arroz con Coco", "5500", CATEGORIA_OBJETIVO);

        // =========================================================================
        // PASO 5: Verificar en otra pestaña que aparezca en el menú con los 2 adicionales
        // =========================================================================
        String adminTabHandle = driver.getWindowHandle();

        // Abrir nueva pestaña en el navegador
        driver.switchTo().newWindow(WindowType.TAB);
        String menuTabHandle = driver.getWindowHandle();

        // Navegar a la sección del menú público
        driver.get(BASE_URL + "/menu");
        pausar(3000);

        // Esperar a que rendericen las tarjetas de comida en el menú
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("app-producto-card")));

        // Buscar la única tarjeta del producto recién creado por su título y centrarla en pantalla
        WebElement cardTitulo = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//h5[contains(@class, 'card-title') and normalize-space()='" + NOMBRE_PRODUCTO + "']")
        ));
        WebElement cardContenedor = cardTitulo.findElement(By.xpath("./ancestor::article[contains(@class, 'card')]"));
        centrarEnPantalla(cardContenedor);
        pausar(3000); // Pausa para ver la tarjeta de la nueva comida en la sección 'Pescados y Mariscos' del menú

        // Hacer clic en 'Ver detalle / Adicionales' para ingresar al detalle del producto
        WebElement verDetalleBtn = cardContenedor.findElement(By.xpath(".//a[contains(text(), 'Ver detalle')]"));
        String hrefDetalle = verDetalleBtn.getAttribute("href");
        scrollYClick(verDetalleBtn);

        try {
            new WebDriverWait(driver, Duration.ofSeconds(2)).until(ExpectedConditions.urlContains("/menu/detalle"));
        } catch (Exception e) {
            if (hrefDetalle != null && !hrefDetalle.isEmpty()) {
                driver.get(hrefDetalle);
            }
        }

        // Verificar que estamos en la vista de detalle del producto creado
        // (Se usa containsIgnoringCase porque el CSS aplica text-transform: capitalize al título)
        WebElement detalleNombre = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("detalle-nombre")));
        assertThat(detalleNombre.getText()).containsIgnoringCase(NOMBRE_PRODUCTO);

        // Esperar a que cargue la lista de adicionales y verificar que haya exactamente 2 adicionales
        WebElement listaAdicionalesElem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("adicionales-lista")));
        centrarEnPantalla(listaAdicionalesElem);
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".adicionales-lista .adicional-derecha"), 2));

        List<WebElement> adicionalesIniciales = driver.findElements(By.cssSelector(".adicionales-lista .adicional-derecha"));
        assertThat(adicionalesIniciales).hasSize(2);
        pausar(4500); // Pausa amplia para observar claramente el plato con sus 2 adicionales iniciales

        // =========================================================================
        // PASO 6: Se da cuenta que falta un adicional, vuelve al portal admin,
        //         regresa al menú de opciones, entra a adicionales y agrega el 3ro
        // =========================================================================
        driver.switchTo().window(adminTabHandle);
        pausar(2000);

        // Regresar al menú de opciones de Administrador y volver a seleccionar Gestionar Adicionales
        regresarAPortalAdmin();
        irAGestionarAdicionalesDesdePortal();

        // Crear el 3er adicional en la categoría 'Pescados y Mariscos' (sin código)
        crearAdicionalEnUI("Suero Costeño", "3500", CATEGORIA_OBJETIVO);

        // =========================================================================
        // PASO 7: Volver a la pestaña del menú y verificar el producto con los 3 adicionales
        // =========================================================================
        driver.switchTo().window(menuTabHandle);
        pausar(2000);

        // Recargar la página del detalle del producto para ver reflejado el 3er adicional
        driver.navigate().refresh();

        // Esperar a que la lista de adicionales contenga ahora los 3 adicionales
        WebElement listaActualizadaElem = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("adicionales-lista")));
        centrarEnPantalla(listaActualizadaElem);
        wait.until(ExpectedConditions.numberOfElementsToBe(By.cssSelector(".adicionales-lista .adicional-derecha"), 3));

        List<WebElement> adicionalesActualizados = driver.findElements(By.cssSelector(".adicionales-lista .adicional-derecha"));
        assertThat(adicionalesActualizados).hasSize(3);
        pausar(3500); // Pausa para observar que ahora aparecen los 3 adicionales en la lista

        // Seleccionar los 3 adicionales haciendo clic en cada uno
        List<WebElement> itemsAdicionales = driver.findElements(
            By.xpath("//li[contains(@class, 'adicional-item')][not(contains(., 'Sin adicional'))]")
        );
        for (WebElement item : itemsAdicionales) {
            scrollYClick(item);
            pausar(1500); // Pausa de 1.5s entre cada clic para ver cómo se marca cada uno de los 3 adicionales
        }

        // Verificar que los 3 adicionales queden con la clase 'adicional-activo' (seleccionados)
        List<WebElement> adicionalesSeleccionados = driver.findElements(
            By.cssSelector("li.adicional-item.adicional-activo:not(:first-child)")
        );
        assertThat(adicionalesSeleccionados).hasSize(3);

        pausar(6000); // Pausa final de 6 segundos para apreciar el producto con los 3 adicionales seleccionados
    }
}
