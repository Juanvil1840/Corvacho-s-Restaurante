import { Injectable } from '@angular/core';
import { Producto } from '../models/producto.model';

@Injectable({
  providedIn: 'root'
})
export class ProductoServiceService {

  private productos: Producto[] = [
    {
    id: 1,
    nombre: "berenjenas a la parmesana",
    precio: 40000,
    descripcion: "capas de rodajas de berenjena fritas o asadas, alternadas con salsa de tomate, hojas de albahaca fresca, queso mozzarella y queso parmesano",
    imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/2c7fa867-64f4-4fea-af73-98ac6129b3c6.webp",
    disponible: true,
    categoria: {
      id: 1,
      nombre: "Entradas"
    }
    },
    {
      id: 2,
      nombre: "Burrata",
      precio: 45000,
      descripcion: "Saco exterior de queso mozzarella bañado en su propio suero para conservar la frescura, el cual contiene un relleno de hilos de queso stracciatella mezclados con crema de leche fresca, cortado al momento de servir y presentado sobre una base de hojas verdes con tomates frescos.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/3a57d222-27b7-4a96-b6e7-e30ae4839110.webp",
      disponible: true,
      categoria: {
      id: 1,
      nombre: "Entradas"
    }
    },
    {
      id: 3,
      nombre: "carpaccio di salmone",
      precio: 60000,
      descripcion: "Láminas finas de salmón fresco crudo dispuestas en una base circular, bañadas con una emulsión de jugo de limón y aceite de oliva virgen extra",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/0ce8ec33-3420-48cd-9b57-e5a58ddc0765.webp",
      disponible: true,
      categoria: {
      id: 3,
      nombre: "Pescados y Mariscos"
    }
    },
    {
      id: 4,
      nombre: "papas a la Francesa",
      precio: 25000,
      descripcion: "Bowl de papas a la francesa cubiertas de queso parmesano, champiñones y salsa de la casa",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/73a7ec70-a935-4173-96d8-33c1f7482eda.webp",
      disponible: true,
      categoria: {
      id: 1,
      nombre: "Entradas"
    }
    },
    {
      id: 5,
      nombre: "pasta gratinada del chef",
      precio: 50000,
      descripcion: "Pasta con queso gratinado mozzarella y acompañada de un pan en forma de estrella bañado en salsa",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/579c0e8e-d7e1-4b91-8874-c09b9966d142.webp",
      disponible: true,
      categoria: {
      id: 2,
      nombre: "Pastas"
    }
    },
    {
      id: 6,
      nombre: "polpo alla griglia",
      precio: 50000,
      descripcion: "Tentáculo de pulpo cocido y sellado a la parrilla, bañado en un aderezo de aceite de oliva con ajo y perejil, el cual contiene un toque de pimentón ahumado y sal marina, cortado en rodajas gruesas y servido sobre una cama de papas rústicas con una guarnición de vegetales asados.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/a3263a03-04f8-42c0-8b09-c150f1fef7dd.webp",
      disponible: true,
      categoria: {
      id: 3,
      nombre: "Pescados y Mariscos"
    }
    },
    {
    id: 7,
    nombre: "Fiori di Zucca",
    precio: 32000,
    descripcion: "Flores de calabacín rellenas y preparadas al estilo de la casa",
    imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/6bb40637-870d-4e7e-923f-19e03a950a50.webp",
    disponible: true,
    categoria: {
      id: 1,
      nombre: "Entradas"
    }
    },
    {
      id: 8,
      nombre: "Crema di pomodoro",
      precio: 24000,
      descripcion: "Crema de tomate preparada al estilo de la casa",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/927627df-b178-41f8-a7de-edeb7192d7ed.webp",
      disponible: true,
      categoria: {
      id: 1,
      nombre: "Entradas"
    }
    },
    {
      id: 9,
      nombre: "Ensalada mediterranea",
      precio: 28000,
      descripcion: "Ensalada mediterránea fresca",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/a20878dc-3bd0-47a8-a694-fff42b7f8416.webp",
      disponible: true,
      categoria: {
      id: 5,
      nombre: "Ensaladas"
    }
    },
    {
      id: 10,
      nombre: "Carbonara di mare",
      precio: 48000,
      descripcion: "Pasta preparada con sabores del mar",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/d1a66561-4ffb-4c22-bc23-088e401c8fb1.webp",
      disponible: true,
      categoria: {
      id: 2,
      nombre: "Pastas"
    }
    },
    {
      id: 11,
      nombre: "Alfredo con camarones",
      precio: 46000,
      descripcion: "Pasta Alfredo acompañada de camarones",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/5f6626fd-541c-4ac9-abc5-5542ec43c934.webp",
      disponible: true,
      categoria: {
      id: 2,
      nombre: "Pastas"
    }
    },
    {
      id: 12,
      nombre: "Pasta sofia loren",
      precio: 38000,
      descripcion: "Pasta preparada al estilo Sofia Loren",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/563ed4f5-2302-4c44-a1ca-e5cbe1e94be2.webp",
      disponible: true,
      categoria: {
      id: 2,
      nombre: "Pastas"
    }
    },
    {
      id: 13,
      nombre: "Arrabiata",
      precio: 34000,
      descripcion: "Pasta con salsa arrabiata",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/b53768a0-7121-4d5b-8207-04ae94af146a.webp",
      disponible: true,
      categoria: {
      id: 2,
      nombre: "Pastas"
    }
    },
    {
      id: 14,
      nombre: "Prosciutto Di Parma",
      precio: 52000,
      descripcion: "Jamón italiano tradicional servido con acompañamientos frescos y una presentación clásica mediterránea.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/fb2f19e0-5ae0-4cf3-9b99-e47b79118ca8.webp",
      disponible: true,
      categoria: {
      id: 1,
      nombre: "Entradas"
    }
    },
    {
      id: 15,
      nombre: "Pesto e pistacchio",
      precio: 42000,
      descripcion: "Pasta bañada en salsa cremosa de albahaca, queso y crocante de pistachos.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/177d60bb-6253-4f06-a3cc-6ec3f262cfc7.webp",
      disponible: true,
      categoria: {
      id: 2,
      nombre: "Pastas"
    }
    },
    {
      id: 16,
      nombre: "Salmone alla griglia",
      precio: 58000,
      descripcion: "Filete de salmón parrillado al término, bañado en finas hierbas y limón.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/21b7d9db-7032-40ea-9f14-8c3f8d72b33c.webp",
      disponible: true,
      categoria: {
      id: 3,
      nombre: "Pescados y Mariscos"
    }
    },
    {
      id: 17,
      nombre: "Atún speciale",
      precio: 56000,
      descripcion: "Medallón de atún sellado a la parrilla, costra de semillas y aderezo especial.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/1a785034-0061-414d-ae92-a3cfcf1e82f0.webp",
      disponible: true,
      categoria: {
      id: 3,
      nombre: "Pescados y Mariscos"
    }
    },
    {
      id: 18,
      nombre: "Milanesa gratinada",
      precio: 44000,
      descripcion: "Milanesa de carne crujiente, cubierta con salsa de tomate y queso mozzarella derretido.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/b1213c5b-3392-49f4-9bbb-92ce62f76fa9.webp",
      disponible: true,
      categoria: {
      id: 4,
      nombre: "Carnes"
    }
    },
    {
      id: 19,
      nombre: "Lomo a la parrilla",
      precio: 62000,
      descripcion: "Corte de lomo tierno asado al fuego, sazonado con sal marina y hierbas.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/58c9310d-98ff-4fa4-82c2-f2d16105d2e0.webp",
      disponible: true,
      categoria: {
      id: 4,
      nombre: "Carnes"
    }
    },
    {
      id: 20,
      nombre: "Pepperoni",
      precio: 38000,
      descripcion: "Pizza crujiente cubierta de salsa de tomate, mozzarella y rodajas de pepperoni dorado.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/7332ac88-b5da-4eea-b349-91b49579d9dd.webp",
      disponible: true,
      categoria: {
      id: 6,
      nombre: "Pizzas"
    }
    },
    {
      id: 21,
      nombre: "Quatro Formaggi E Mirtilli",
      precio: 44000,
      descripcion: "Pizza con cuatro quesos derretidos, contrastada con arándanos dulces y un toque herbal.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/82bad0bb-fcfd-4428-a6b9-653515a0c785.webp",
      disponible: true,
      categoria: {
      id: 6,
      nombre: "Pizzas"
    }
    },
    {
      id: 22,
      nombre: "Polpete D'amore",
      precio: 42000,
      descripcion: "Albóndigas italianas elaboradas con carne seleccionada, acompañadas de salsa de tomate tradicional y sabores mediterráneos.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/88ee35c5-6c0b-4b91-aecf-2a2a08843e8a.webp",
      disponible: true,
      categoria: {
      id: 1,
      nombre: "Entradas"
    }
    },
    {
      id: 23,
      nombre: "Carpaccio di Manzo Tartufato",
      precio: 58000,
      descripcion: "Finas láminas de res acompañadas de aceite de oliva, queso parmesano y un delicado toque de trufa.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/85176223-2fe4-441b-89fa-c1f876ed23ce.webp",
      disponible: true,
      categoria: {
      id: 1,
      nombre: "Entradas"
    }
    },
    {
      id: 24,
      nombre: "Kale Cesare",
      precio: 32000,
      descripcion: "Ensalada fresca de kale con aderezo César, queso parmesano y complementos seleccionados.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/113b3503-604f-4706-a9aa-fe59a1829d53.webp",
      disponible: true,
      categoria: {
      id: 5,
      nombre: "Ensaladas"
    }
    },
    {
      id: 25,
      nombre: "Gamberi Alla Vodka",
      precio: 56000,
      descripcion: "Camarones preparados en una cremosa salsa de vodka con sabores italianos y acompañamiento especial.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/2768426a-3626-44bd-86f8-cd68df1d433b.webp",
      disponible: true,
      categoria: {
      id: 3,
      nombre: "Pescados y Mariscos"
    }
    },
    {
      id: 26,
      nombre: "Besos al chef",
      precio: 45000,
      descripcion: "Preparación especial de la casa creada con una combinación de sabores dulces y presentación elegante.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/d60f4ece-0e17-428f-8666-cd4d564bd973.webp",
      disponible: true,
      categoria: {
      id: 8,
      nombre: "Postres"
    }
    },
    {
      id: 27,
      nombre: "New York Funghi",
      precio: 46000,
      descripcion: "Pizza con variedad de hongos, queso mozzarella y sabores intensos inspirados en la cocina italiana.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/18442977-6b08-49ff-bf0b-0da9e4cc5dca.webp",
      disponible: true,
      categoria: {
      id: 6,
      nombre: "Pizzas"
    }
    },
    {
      id: 28,
      nombre: "Ragú Toscano",
      precio: 52000,
      descripcion: "Pasta tradicional con salsa ragú preparada al estilo toscano con carne y especias italianas.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/321935df-17e1-4dcb-ab49-0b53cc217e51.webp",
      disponible: true,
      categoria: {
      id: 2,
      nombre: "Pastas"
    }
    },
    {
      id: 29,
      nombre: "Asado de Tira Alla Toscana",
      precio: 75000,
      descripcion: "Corte de carne cocinado lentamente al estilo italiano, acompañado de sabores tradicionales de la Toscana.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/9e8b4993-29ef-48d9-a573-a679811dd80e.webp",
      disponible: true,
      categoria: {
      id: 4,
      nombre: "Carnes"
    }
    },
    {
      id: 30,
      nombre: "Ragú de Ossobuco",
      precio: 68000,
      descripcion: "Preparación italiana de ossobuco cocinado lentamente con salsa rica en aromas y sabores profundos.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/58ad402b-361e-45e9-9dce-742b3808626f.webp",
      disponible: true,
      categoria: {
      id: 4,
      nombre: "Carnes"
    }
    },
    {
      id: 31,
      nombre: "Picanha Steak",
      precio: 79000,
      descripcion: "Corte de picanha preparado a la parrilla, conservando su jugosidad y sabor característico.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/d62c4945-743e-41da-a4c9-b9b99bbfe968.webp",
      disponible: true,
      categoria: {
      id: 4,
      nombre: "Carnes"
    }
    },
    {
      id: 32,
      nombre: "Besos con Propósito",
      precio: 42000,
      descripcion: "Postre especial de la casa con combinación de texturas dulces y una presentación sofisticada.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/aea0a96b-a4b8-445f-b1b3-543aed34640b.webp",
      disponible: true,
      categoria: {
      id: 8,
      nombre: "Postres"
    }
    },
    {
      id: 33,
      nombre: "Tiramisú Tradicional",
      precio: 30000,
      descripcion: "Postre italiano clásico preparado con café, crema de mascarpone y cacao.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/e6e5007d-cca0-44d5-b568-8677ed6863f1.webp",
      disponible: true,
      categoria: {
      id: 8,
      nombre: "Postres"
    }
    },
    {
      id: 34,
      nombre: "Brownie con Gelato",
      precio: 32000,
      descripcion: "Brownie de chocolate acompañado con helado, creando una combinación de sabores y temperaturas.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/2f7b1089-575e-4dcc-b7a4-c788b4da6ba1.webp",
      disponible: true,
      categoria: {
      id: 8,
      nombre: "Postres"
    }
    },
    {
      id: 35,
      nombre: "Dolce Storia D'Amore",
      precio: 38000,
      descripcion: "Postre insignia con una mezcla de sabores dulces y una presentación inspirada en la cocina italiana.",
      imagen: "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/c823653f-d363-4c8f-844e-a26365786a10.webp",
      disponible: true,
      categoria: {
      id: 8,
      nombre: "Postres"
    }
    },
    {
      id: 36,
      nombre: "Coca Cola",
      precio: 8000,
      descripcion: "Bebida gaseosa fría ideal para acompañar cualquier plato del menú.",
      imagen: "https://i.pinimg.com/736x/b8/c9/35/b8c93542014b2d12d4f778ef312e2bf3.jpg",
      disponible: true,
      categoria: {
      id: 7,
      nombre: "Bebidas"
    }
    },
    {
      id: 37,
      nombre: "Jarra de limonada",
      precio: 18000,
      descripcion: "Limonada fresca preparada con limón natural, hielo y un toque refrescante.",
      imagen: "https://thumbs.dreamstime.com/b/jarra-de-limonada-con-rodajas-lim%C3%B3n-primer-plano-y-menta-sobre-una-mesa-madera-fondo-rural-escena-218289142.jpg",
      disponible: true,
      categoria: {
      id: 7,
      nombre: "Bebidas"
    }
    },
    {
      id: 38,
      nombre: "Cerveza",
      precio: 12000,
      descripcion: "Cerveza fría perfecta para acompañar comidas y momentos especiales.",
      imagen: "https://cdn.pixabay.com/photo/2018/11/08/22/12/beer-3803425_640.jpg",
      disponible: true,
      categoria: {
      id: 7,
      nombre: "Bebidas"
    }
    },
    {
      id: 39,
      nombre: "Jugo de fresa",
      precio: 14000,
      descripcion: "Jugo natural de fresa preparado con fruta fresca y servido frío.",
      imagen: "https://images.pexels.com/photos/31578583/pexels-photo-31578583/free-photo-of-coctel-refrescante-de-fresa-con-cubitos-de-hielo.jpeg?cs=tinysrgb&dpr=1&w=500",
      disponible: true,
      categoria: {
      id: 7,
      nombre: "Bebidas"
    }
    },
    {
      id: 40,
      nombre: "Jugo de lulo",
      precio: 14000,
      descripcion: "Jugo natural de lulo con sabor tropical y refrescante.",
      imagen: "https://img.postershop.me/22622/a972bd6a-1273-4ae2-9850-d40848dda99a_image.jpeg",
      disponible: true,
      categoria: {
      id: 7,
      nombre: "Bebidas"
    }
    },
  ];

  constructor() { }

  getProductos(): Producto[] {
    return this.productos;
  }
}
