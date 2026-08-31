package com.example.demo.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Repository;

import com.example.demo.entities.Producto;

@Repository
public class ProductoRepository {

    private Map<Integer, Producto> data = new HashMap<>();
    private AtomicInteger idGenerator = new AtomicInteger(21);

    public ProductoRepository() {
        data.put(1, new Producto(1, "berenjenas a la parmesana", 40000,
                "capas de rodajas de berenjena fritas o asadas, alternadas con salsa de tomate, hojas de albahaca fresca, queso mozzarella y queso parmesano",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/2c7fa867-64f4-4fea-af73-98ac6129b3c6.webp",
                true,
                List.of("Berenjena", "Salsa de tomate", "Albahaca fresca", "Queso mozzarella",
                        "Queso parmesano")));

        data.put(2, new Producto(2, "Burrata", 45000,
                "Saco exterior de queso mozzarella bañado en su propio suero para conservar la frescura, el cual contiene un relleno de hilos de queso stracciatella mezclados con crema de leche fresca, cortado al momento de servir y presentado sobre una base de hojas verdes con tomates frescos.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/3a57d222-27b7-4a96-b6e7-e30ae4839110.webp",
                true,
                List.of("Mozzarella", "Stracciatella", "Crema de leche", "Hojas verdes",
                        "Tomates frescos")));

        data.put(3, new Producto(3, "carpaccio di salmone", 60000,
                "Láminas finas de salmón fresco crudo dispuestas en una base circular, bañadas con una emulsión de jugo de limón y aceite de oliva virgen extra",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/0ce8ec33-3420-48cd-9b57-e5a58ddc0765.webp",
                true,
                List.of("Salmón fresco", "Jugo de limón", "Aceite de oliva virgen extra")));

        data.put(4, new Producto(4, "papas a la Francesa", 25000,
                "Bowl de papas a la francesa cubiertas de queso parmesano, champiñones y salsa de la casa",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/73a7ec70-a935-4173-96d8-33c1f7482eda.webp",
                true,
                List.of("Papas", "Queso parmesano", "Champiñones", "Salsa de la casa")));

        data.put(5, new Producto(5, "pasta gratinada del chef", 50000,
                "Pasta con queso gratinado mozzarella y acompañada de un pan en forma de estrella bañado en salsa",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/579c0e8e-d7e1-4b91-8874-c09b9966d142.webp",
                true,
                List.of("Pasta", "Queso mozzarella gratinado", "Pan artesanal", "Salsa")));

        data.put(6, new Producto(6, "polpo alla griglia", 50000,
                "Tentáculo de pulpo cocido y sellado a la parrilla, bañado en un aderezo de aceite de oliva con ajo y perejil, el cual contiene un toque de pimentón ahumado y sal marina, cortado en rodajas gruesas y servido sobre una cama de papas rústicas con una guarnición de vegetales asados.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/a3263a03-04f8-42c0-8b09-c150f1fef7dd.webp",
                true,
                List.of("Pulpo", "Aceite de oliva", "Ajo", "Perejil", "Pimentón ahumado", "Sal marina",
                        "Papas rústicas", "Vegetales asados")));

        data.put(7, new Producto(7, "Fiori di Zucca", 32000,
                "Flores de calabacín rellenas de queso ricota, fritas y servidas crujientes.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/6bb40637-870d-4e7e-923f-19e03a950a50.webp",
                true,
                List.of("Flores de calabacín", "Queso ricota")));

        data.put(8, new Producto(8, "Crema di pomodoro", 24000,
                "Sopa tersa de tomates maduros, sazonada con albahaca y crema fresca.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/927627df-b178-41f8-a7de-edeb7192d7ed.webp",
                true,
                List.of("Tomates maduros", "Albahaca", "Crema fresca")));

        data.put(9, new Producto(9, "Ensalada mediterranea", 28000,
                "Mezcla fresca de vegetales, aceitunas y queso feta con aderezo de oliva.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/a20878dc-3bd0-47a8-a694-fff42b7f8416.webp",
                true,
                List.of("Mezcla de vegetales", "Aceitunas", "Queso feta", "Aderezo de oliva")));

        data.put(10, new Producto(10, "Carbonara di mare", 48000,
                "Pasta con salsa cremosa de yemas, mariscos salteados y pimienta negra.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/d1a66561-4ffb-4c22-bc23-088e401c8fb1.webp",
                true,
                List.of("Pasta", "Yemas de huevo", "Mariscos salteados", "Pimienta negra")));

        data.put(11, new Producto(11, "Alfredo con camarones", 46000,
                "Pasta en salsa cremosa de parmesano y mantequilla, coronada con camarones salteados.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/5f6626fd-541c-4ac9-abc5-5542ec43c934.webp",
                true,
                List.of("Pasta", "Salsa cremosa de parmesano", "Mantequilla", "Camarones")));

        data.put(12, new Producto(12, "Pasta sofia loren", 38000,
                "Pasta salteada en salsa de tomate fresco, ajo, albahaca y mozzarella derretida.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/563ed4f5-2302-4c44-a1ca-e5cbe1e94be2.webp",
                true,
                List.of("Pasta", "Salsa de tomate fresco", "Ajo", "Albahaca", "Mozzarella derretida")));

        data.put(13, new Producto(13, "Arrabiata", 34000,
                "Pasta corta bañada en salsa picante de tomate, ajo y chile picante.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/b53768a0-7121-4d5b-8207-04ae94af146a.webp",
                true,
                List.of("Pasta corta", "Salsa picante de tomate", "Ajo", "Chile picante")));

        data.put(14, new Producto(14, "Pesto e pistacchio", 42000,
                "Pasta bañada en salsa cremosa de albahaca, queso y crocante de pistachos.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/177d60bb-6253-4f06-a3cc-6ec3f262cfc7.webp",
                true,
                List.of("Pasta", "Salsa cremosa de albahaca", "Queso", "Pistachos")));

        data.put(15, new Producto(15, "Salmone alla griglia", 58000,
                "Filete de salmón parrillado al término, bañado en finas hierbas y limón.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/21b7d9db-7032-40ea-9f14-8c3f8d72b33c.webp",
                true,
                List.of("Filete de salmón", "Finas hierbas", "Limón")));

        data.put(16, new Producto(16, "Atún speciale", 56000,
                "Medallón de atún sellado a la parrilla, costra de semillas y aderezo especial.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/1a785034-0061-414d-ae92-a3cfcf1e82f0.webp",
                true,
                List.of("Medallón de atún", "Costra de semillas", "Aderezo especial")));

        data.put(17, new Producto(17, "Milanesa gratinada", 44000,
                "Milanesa de carne crujiente, cubierta con salsa de tomate y queso mozzarella derretido.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/b1213c5b-3392-49f4-9bbb-92ce62f76fa9.webp",
                true,
                List.of("Milanesa de carne", "Salsa de tomate", "Queso mozzarella")));

        data.put(18, new Producto(18, "Lomo a la parrilla", 62000,
                "Corte de lomo tierno asado al fuego, sazonado con sal marina y hierbas.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/58c9310d-98ff-4fa4-82c2-f2d16105d2e0.webp",
                true,
                List.of("Lomo tierno", "Sal marina", "Hierbas")));

        data.put(19, new Producto(19, "Pepperoni", 38000,
                "Pizza crujiente cubierta de salsa de tomate, mozzarella y rodajas de pepperoni dorado.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/7332ac88-b5da-4eea-b349-91b49579d9dd.webp",
                true,
                List.of("Masa de pizza", "Salsa de tomate", "Mozzarella", "Pepperoni")));

        data.put(20, new Producto(20, "Quatro Formaggi E Mirtilli", 44000,
                "Pizza con cuatro quesos derretidos, contrastada con arándanos dulces y un toque herbal.",
                "https://dvzwo3mu4ucsq.cloudfront.net/images/restaurants/storiadamore/product/82bad0bb-fcfd-4428-a6b9-653515a0c785.webp",
                true,
                List.of("Masa de pizza", "Cuatro quesos", "Arándanos")));
    }

    // Busca un producto por su ID.
    // - Parametro: id (Integer) - El ID del producto a buscar.
    // - Retorna: Producto - El objeto Producto encontrado, o null si no existe.
    public Producto findById(Integer id) {
        return data.get(id);
    }

    // Retorna todos los productos almacenados.
    public Collection<Producto> findAll() {
        return data.values();
    }

    // METODOS PARA CRUD (CREATE, UPDATE, DELETE)

    // Guarda un producto (crea o actualiza).
    // - Si el producto no tiene ID (es null o 0), se le asigna uno nuevo.
    // - Si el producto ya tiene ID, se actualiza el existente.
    // - Parametro: producto (Producto) - El producto a guardar.
    public Producto save(Producto producto) {
        if (producto.getId() == null || producto.getId() == 0) { // Verifica si el producto es nuevo (sin ID)
            producto.setId(idGenerator.getAndIncrement());
        }
        data.put(producto.getId(), producto);
        return producto;
    }

    // Elimina un producto por su ID.
    // - Parametro: id (Integer) - El ID del producto a eliminar.
    public void deleteById(Integer id) {
        data.remove(id);
    }

    // Verifica si existe un producto con el ID dado.
    // - Parametro: id (Integer) - El ID a verificar.
    // - Retorna: boolean - true si existe, false si no.
    public boolean existsById(Integer id) {
        return data.containsKey(id);
    }
}
