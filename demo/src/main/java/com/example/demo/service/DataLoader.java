package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.ProductoRepository;

import jakarta.transaction.Transactional;

import com.example.demo.entities.Categoria;
import com.example.demo.entities.Cliente;
import com.example.demo.entities.Producto;

@Component
@Transactional 

public class DataLoader implements CommandLineRunner{

    @Autowired
    private CategoriaRepository categoriaRepository;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired 
    private ClienteRepository clienteRepository;
    
    @Override
    public void run(String... args) throws Exception {

        /* Categorias */

        Categoria entradas = categoriaRepository.save(new Categoria("Entradas"));
        Categoria pastas = categoriaRepository.save(new Categoria("Pastas"));
        Categoria pescados_y_Mariscos = categoriaRepository.save(new Categoria("Pescados y Mariscos"));
        Categoria carnes = categoriaRepository.save(new Categoria("Carnes"));
        Categoria ensaladas = categoriaRepository.save(new Categoria("Ensaladas"));
        Categoria pizzas = categoriaRepository.save(new Categoria("Pizzas"));
        Categoria bebidas = categoriaRepository.save(new Categoria("Bebidas"));
        Categoria postres = categoriaRepository.save(new Categoria("Postres"));

        /* Clientes */

        clienteRepository.save(new Cliente("Daniel","Cedeño","cedeno.danielc@javeriana.edu.co","1234567","3106699555","Calle 45 #7-95",true));
        clienteRepository.save(new Cliente("Jose","Pulido","jose.p@javeriana.edu.co","5678923","3113479585","Calle 98 #8-45",true));
        clienteRepository.save(new Cliente("Sebastián","Rincón","rincon.sebas@javeriana.edu.co","1230987","3112549855","Calle 100 #4-35",true));
        clienteRepository.save(new Cliente("Eileen","Rodriguez","eileen.rodriguez@javeriana.edu.co","1234900","3100076895","Calle 129 #9-42",true));
        clienteRepository.save(new Cliente("Giovanny","Durán","gio.duran_@javeriana.edu.co","1236547","3057789635","Calle 40 #7-90",true));
        clienteRepository.save(new Cliente("Diego","Melgarejo","diegui200@gmail.com","1233377","3177799950","Calle 80 #13-43",true));
        clienteRepository.save(new Cliente("Karen","Colmenares","karencol07_@hotmail.com","7843567","3195054045","Calle 127 #5-35",true));
        clienteRepository.save(new Cliente("Laura","Corvacho","corvacho.laura_xx@gmail.com","9876543","3096789500","Calle 200 #15-90",true));
        clienteRepository.save(new Cliente("Juan","vil","vil_juan_2005@gmail.com","7799881","3145533789","Calle 150 #9-50",true));
        clienteRepository.save(new Cliente("Sebastián","Angarita","angarita.sebastian@yahoo.com","5554445","3143224455","Calle 24b #5-75",true));

        /* Productos */

        productoRepository.save(new Producto("berenjenas a la parmesana",40000,"capas de rodajas de berenjena fritas o asadas, alternadas con salsa de tomate, hojas de albahaca fresca, queso mozzarella y queso parmesano","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/2c7fa867-64f4-4fea-af73-98ac6129b3c6.webp",true,entradas));

        productoRepository.save(new Producto("Burrata",45000,"Saco exterior de queso mozzarella bañado en su propio suero para conservar la frescura, el cual contiene un relleno de hilos de queso stracciatella mezclados con crema de leche fresca, cortado al momento de servir y presentado sobre una base de hojas verdes con tomates frescos.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/3a57d222-27b7-4a96-b6e7-e30ae4839110.webp",true,entradas));

        productoRepository.save(new Producto("carpaccio di salmone",60000,"Láminas finas de salmón fresco crudo dispuestas en una base circular, bañadas con una emulsión de jugo de limón y aceite de oliva virgen extra","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/0ce8ec33-3420-48cd-9b57-e5a58ddc0765.webp",true,pescados_y_Mariscos));

        productoRepository.save(new Producto("papas a la Francesa",25000,"Bowl de papas a la francesa cubiertas de queso parmesano, champiñones y salsa de la casa","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/73a7ec70-a935-4173-96d8-33c1f7482eda.webp",true,entradas));

        productoRepository.save(new Producto("pasta gratinada del chef",50000,"Pasta con queso gratinado mozzarella y acompañada de un pan en forma de estrella bañado en salsa","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/579c0e8e-d7e1-4b91-8874-c09b9966d142.webp",true,pastas));

        productoRepository.save(new Producto("polpo alla griglia",50000,"Tentáculo de pulpo cocido y sellado a la parrilla, bañado en un aderezo de aceite de oliva con ajo y perejil, el cual contiene un toque de pimentón ahumado y sal marina, cortado en rodajas gruesas y servido sobre una cama de papas rústicas con una guarnición de vegetales asados.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/a3263a03-04f8-42c0-8b09-c150f1fef7dd.webp",true,pescados_y_Mariscos));

        productoRepository.save(new Producto("Fiori di Zucca",32000, "Flores de calabacín rellenas de queso ricota, fritas y servidas crujientes.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/6bb40637-870d-4e7e-923f-19e03a950a50.webp",true,entradas));

        productoRepository.save(new Producto("Crema di pomodoro", 24000, "Sopa tersa de tomates maduros, sazonada con albahaca y crema fresca.", "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/927627df-b178-41f8-a7de-edeb7192d7ed.webp", true, entradas));

        productoRepository.save(new Producto("Ensalada mediterranea",28000, "Mezcla fresca de vegetales, aceitunas y queso feta con aderezo de oliva.", "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/a20878dc-3bd0-47a8-a694-fff42b7f8416.webp", true,ensaladas));

        productoRepository.save(new Producto("Carbonara di mare", 48000, "Pasta con salsa cremosa de yemas, mariscos salteados y pimienta negra.", "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/d1a66561-4ffb-4c22-bc23-088e401c8fb1.webp", true, pastas));

        productoRepository.save(new Producto("Alfredo con camarones",46000,"Pasta en salsa cremosa de parmesano y mantequilla, coronada con camarones salteados.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/5f6626fd-541c-4ac9-abc5-5542ec43c934.webp",true,pastas));

        productoRepository.save(new Producto("Pasta sofia loren",38000,"Pasta salteada en salsa de tomate fresco, ajo, albahaca y mozzarella derretida.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/563ed4f5-2302-4c44-a1ca-e5cbe1e94be2.webp",true,pastas));

        productoRepository.save(new Producto("Arrabiata",34000,"Pasta corta bañada en salsa picante de tomate, ajo y chile picante.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/b53768a0-7121-4d5b-8207-04ae94af146a.webp",true,pastas));

        productoRepository.save(new Producto("Pesto e pistacchio",42000,"Pasta bañada en salsa cremosa de albahaca, queso y crocante de pistachos.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/177d60bb-6253-4f06-a3cc-6ec3f262cfc7.webp",true,pastas));

        productoRepository.save(new Producto("Salmone alla griglia",58000,"Filete de salmón parrillado al término, bañado en finas hierbas y limón.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/21b7d9db-7032-40ea-9f14-8c3f8d72b33c.webp",true,pescados_y_Mariscos));

        productoRepository.save(new Producto("Atún speciale",56000,"Medallón de atún sellado a la parrilla, costra de semillas y aderezo especial.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/1a785034-0061-414d-ae92-a3cfcf1e82f0.webp",true,pescados_y_Mariscos));

        productoRepository.save(new Producto("Milanesa gratinada",44000,"Milanesa de carne crujiente, cubierta con salsa de tomate y queso mozzarella derretido.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/b1213c5b-3392-49f4-9bbb-92ce62f76fa9.webp",true,carnes));

        productoRepository.save(new Producto("Lomo a la parrilla",62000,"Corte de lomo tierno asado al fuego, sazonado con sal marina y hierbas.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/58c9310d-98ff-4fa4-82c2-f2d16105d2e0.webp",true,carnes));

        productoRepository.save(new Producto("Pepperoni",38000,"Pizza crujiente cubierta de salsa de tomate, mozzarella y rodajas de pepperoni dorado.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/7332ac88-b5da-4eea-b349-91b49579d9dd.webp",true,pizzas));

        productoRepository.save(new Producto("Quatro Formaggi E Mirtilli",44000,"Pizza con cuatro quesos derretidos, contrastada con arándanos dulces y un toque herbal.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/82bad0bb-fcfd-4428-a6b9-653515a0c785.webp",true,pizzas));

        productoRepository.save(new Producto("Polpete D'amore",42000,"Albóndigas italianas elaboradas con carne seleccionada, acompañadas de salsa de tomate tradicional y sabores mediterráneos.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/88ee35c5-6c0b-4b91-aecf-2a2a08843e8a.webp",true,entradas));

        productoRepository.save(new Producto("Carpaccio di Manzo Tartufato",58000,"Finas láminas de res acompañadas de aceite de oliva, queso parmesano y un delicado toque de trufa.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/85176223-2fe4-441b-89fa-c1f876ed23ce.webp",true,entradas));

        productoRepository.save(new Producto("Prosciutto Di Parma",52000,"Jamón italiano tradicional servido con acompañamientos frescos y una presentación clásica mediterránea.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/fb2f19e0-5ae0-4cf3-9b99-e47b79118ca8.webp",true,entradas));

        productoRepository.save(new Producto("Kale Cesare",32000,"Ensalada fresca de kale con aderezo César, queso parmesano y complementos seleccionados.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/113b3503-604f-4706-a9aa-fe59a1829d53.webp",true,ensaladas));

        productoRepository.save(new Producto("Gamberi Alla Vodka",56000,"Camarones preparados en una cremosa salsa de vodka con sabores italianos y acompañamiento especial.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/2768426a-3626-44bd-86f8-cd68df1d433b.webp",true,pescados_y_Mariscos));

        productoRepository.save(new Producto("Besos al chef",45000,"Preparación especial de la casa creada con una combinación de sabores dulces y presentación elegante.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/d60f4ece-0e17-428f-8666-cd4d564bd973.webp",true,postres));

        productoRepository.save(new Producto("New York Funghi",46000,"Pizza con variedad de hongos, queso mozzarella y sabores intensos inspirados en la cocina italiana.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/18442977-6b08-49ff-bf0b-0da9e4cc5dca.webp",true,pizzas));

        productoRepository.save(new Producto("Ragú Toscano",52000,"Pasta tradicional con salsa ragú preparada al estilo toscano con carne y especias italianas.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/321935df-17e1-4dcb-ab49-0b53cc217e51.webp",true,pastas));

        productoRepository.save(new Producto("Asado de Tira Alla Toscana",75000,"Corte de carne cocinado lentamente al estilo italiano, acompañado de sabores tradicionales de la Toscana.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/9e8b4993-29ef-48d9-a573-a679811dd80e.webp",true,carnes));

        productoRepository.save(new Producto("Ragú de Ossobuco",68000,"Preparación italiana de ossobuco cocinado lentamente con salsa rica en aromas y sabores profundos.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/58ad402b-361e-45e9-9dce-742b3808626f.webp",true,carnes));

        productoRepository.save(new Producto("Picanha Steak",79000,"Corte de picanha preparado a la parrilla, conservando su jugosidad y sabor característico.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/d62c4945-743e-41da-a4c9-b9b99bbfe968.webp",true,carnes));

        productoRepository.save(new Producto("Besos con Propósito",42000,"Postre especial de la casa con combinación de texturas dulces y una presentación sofisticada.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/aea0a96b-a4b8-445f-b1b3-543aed34640b.webp",true,postres));

        productoRepository.save(new Producto("Tiramisú Tradicional",30000,"Postre italiano clásico preparado con café, crema de mascarpone y cacao.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/e6e5007d-cca0-44d5-b568-8677ed6863f1.webp",true,postres));

        productoRepository.save(new Producto("Brownie con Gelato",32000,"Brownie de chocolate acompañado con helado, creando una combinación de sabores y temperaturas.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/2f7b1089-575e-4dcc-b7a4-c788b4da6ba1.webp",true,postres));

        productoRepository.save(new Producto("Dolce Storia D'Amore",38000,"Postre insignia con una mezcla de sabores dulces y una presentación inspirada en la cocina italiana.","https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/c823653f-d363-4c8f-844e-a26365786a10.webp",true,postres));

        productoRepository.save(new Producto("Coca Cola",8000,"Bebida gaseosa fría ideal para acompañar cualquier plato del menú.","https://i.pinimg.com/736x/b8/c9/35/b8c93542014b2d12d4f778ef312e2bf3.jpg",true,bebidas));

        productoRepository.save(new Producto("Jarra de limonada",18000,"Limonada fresca preparada con limón natural, hielo y un toque refrescante.","https://thumbs.dreamstime.com/b/jarra-de-limonada-con-rodajas-lim%C3%B3n-primer-plano-y-menta-sobre-una-mesa-madera-fondo-rural-escena-218289142.jpg",true,bebidas));

        productoRepository.save(new Producto("Cerveza",12000,"Cerveza fría perfecta para acompañar comidas y momentos especiales.","https://cdn.pixabay.com/photo/2018/11/08/22/12/beer-3803425_640.jpg",true,bebidas));

        productoRepository.save(new Producto("Jugo de fresa",14000,"Jugo natural de fresa preparado con fruta fresca y servido frío.","https://images.pexels.com/photos/31578583/pexels-photo-31578583/free-photo-of-coctel-refrescante-de-fresa-con-cubitos-de-hielo.jpeg?cs=tinysrgb&dpr=1&w=500",true,bebidas));

        productoRepository.save(new Producto("Jugo de lulo",14000,"Jugo natural de lulo con sabor tropical y refrescante.","https://img.postershop.me/22622/a972bd6a-1273-4ae2-9850-d40848dda99a_image.jpeg",true,bebidas));
    }
    
}
 