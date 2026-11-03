# Corvacho's Restaurante

## Historia

Corvacho's nacio en el corazon de Bogota en la Carrera 7 con Calle 40, fundado por la familia Corvacho, apasionada por la gastronomia y las tradiciones culinarias. Se convirtió en un referente de la alta cocina en la ciudad.

El nombre "Corvacho" proviene del apellido de la familia, que durante generaciones ha transmitido recetas secretas y tecnicas de cocina heredadas de sus antepasados europeos. Hoy, Corvacho's es un espacio donde la tradicion se encuentra con la innovacion, ofreciendo a sus comensales una experiencia que despierta todos los sentidos.

## Descripcion del Negocio

Corvacho's es un restaurante de alta cocina ubicado en la ciudad de Bogota, Colombia. Ofrece una experiencia gastronomica unica, combinando ingredientes frescos y tecnicas tradicionales con un toque contemporaneo.

Nuestra carta esta compuesta por platos gourmet que resaltan los sabores de la cocina internacional, con especial enfasis en pastas, carnes, mariscos y pizzas artesanales. Cada plato es preparado con la mejor calidad y presentado con elegancia.

## Mision

Brindar a nuestros clientes una experiencia culinaria inolvidable, superando sus expectativas con sabores autenticos, ingredientes de primera calidad y un servicio excepcional.

## Vision

Ser reconocidos como el mejor restaurante de alta cocina en Bogota, expandiendo nuestra presencia a nivel nacional y siendo un referente de innovacion gastronomica.

## Servicios

- **Reservas:** Aceptamos reservas para grupos y ocasiones especiales.
- **Pedidos en línea y a domicilio:** Selección de platos del menú con adicionales personalizados por categoría, carrito de compras persistente y seguimiento de estados en tiempo real (`Recibido` -> `Cocinando` -> `Enviado` -> `Entregado`).

## Tecnologias Utilizadas

- **Backend:** Spring Boot 3 (Java 17, Spring Data JPA, Spring Web, Validation, Lombok)
- **Frontend:** Angular 19 (Standalone Components, RxJS, FormsModule, Router)
- **Base de datos:** H2 Database (persistencia en archivo y memoria para pruebas)

---

## Arquitectura del Proyecto

El proyecto está dividido en dos módulos principales:

1. **`demo/` (Backend - Spring Boot REST API):**
   - **`entities/`**: Modelos JPA (`Producto`, `Categoria`, `Adicional`, `AdicionalSeleccionado`, `Cliente`, `Carrito`, `ProductoCarrito`, `Pedido`, `DetallePedido`, `Operador`, `Domiciliario`, `Administrador`).
   - **`repository/`**: Interfaces Spring Data JPA con consultas personalizadas para filtrado de productos/adicionales disponibles, validación de integridad referencial y búsqueda de pedidos activos.
   - **`service/`**: Lógica de negocio, validaciones de estados de pedidos, disponibilidad automática de domiciliarios, persistencia de carritos por cliente y carga inicial de datos (`DataLoader`).
   - **`controller/`**: Controladores REST bajo el prefijo `/api/*` con soporte CORS para `http://localhost:4200`.
   - **`dto/`**: Objetos de transferencia de datos para desacoplar las entidades JPA de las respuestas JSON y evitar referencias circulares.
   - **`errors/`**: Manejo global de excepciones (`RestGlobalExceptionHandler`) que devuelve respuestas estructuradas con `MensajeDTO`.

2. **`Corvachos-Restaurante-Angular/` (Frontend - SPA en Angular):**
   - **`models/`**: Interfaces TypeScript que representan los contratos de datos con el backend.
   - **`service/`**: Servicios inyectables (`HttpClient` y estado reactivo con `BehaviorSubject` en `CarritoService`).
   - **`shared/`**: Barra de navegación dinámica (`NavbarComponent`) con panel lateral de carrito y pie de página (`FooterComponent`).
   - **`pages/`**: Páginas organizadas en contenedores y subcomponentes presentacionales (menú, detalle de producto, logins por rol, portales de administrador y operario, perfil del cliente y CRUDs completos).

---

## Reglas de Negocio Principales

1. **Flujo de Estados del Pedido:**
   - Un pedido nuevo inicia obligatoriamente en estado **`Recibido`**.
   - Los estados solo pueden avanzar de forma secuencial: **`Recibido` -> `Cocinando` -> `Enviado` -> `Entregado`**.
   - una vez que el pedido sale de `Recibido`, no se pueden alterar sus comidas ni el cliente asociado. Un pedido en `Entregado` es final y no admite más cambios.

2. **Gestión Automática de Domiciliarios:**
   - Para pasar un pedido a **`Enviado`**, es obligatorio asignar un domiciliario activo y disponible (`informaciondisp = true`).
   - Mientras el pedido está en **`Enviado`**, el domiciliario pasa automáticamente a ocupado (`informaciondisp = false`).
   - Cuando el pedido pasa a **`Entregado`** (o si se elimina estando en `Enviado`), el domiciliario vuelve a quedar disponible automáticamente.

3. **Carrito de Compras Persistente:**
   - El cliente puede armar su carrito antes o después de iniciar sesión; al autenticarse, los productos temporales se fusionan con el carrito guardado en el servidor (`/api/carritos/cliente/{clienteId}`).
   - Solo se permiten productos y adicionales disponibles y cuya categoría coincida con la del plato.

4. **Protección de Integridad al Eliminar:**
   - No se permite eliminar una **Categoría** que tenga productos o adicionales asociados (`409 Conflict`).
   - No se permite eliminar un **Producto** o **Adicional** que esté presente en un pedido o carrito (`409 Conflict`).
   - Al eliminar un **Operador** o **Domiciliario**, sus pedidos asociados se desvinculan (`null`) sin perder el historial de pedidos.

---

## Cómo Ejecutar el Proyecto

### 1. Iniciar el Backend (Spring Boot)

```bash
cd demo
./mvnw spring-boot:run
```

El servidor iniciará en `http://localhost:8080`.

### 2. Iniciar el Frontend (Angular)

```bash
cd Corvachos-Restaurante-Angular
npm install
npm start
```

La aplicación web estará disponible en `http://localhost:4200`.
