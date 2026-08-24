package com.example.demo.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.example.demo.entities.Producto;

@Repository
public class ProductoRepository {

    private Map<Integer, Producto> data = new HashMap<>();

    public ProductoRepository() {
        data.put(1, new Producto(1, "berenjenas a la parmesana", 40000,
                "capas de rodajas de berenjena fritas o asadas, alternadas con salsa de tomate, hojas de albahaca fresca, queso mozzarella y queso parmesano",
                "/Images/comida-berenjenas_a_la_parmesana.png", true,
                List.of("Berenjena", "Salsa de tomate", "Albahaca fresca", "Queso mozzarella", "Queso parmesano")));

        data.put(2, new Producto(2, "Burrata", 45000,
                "Saco exterior de queso mozzarella bañado en su propio suero para conservar la frescura, el cual contiene un relleno de hilos de queso stracciatella mezclados con crema de leche fresca, cortado al momento de servir y presentado sobre una base de hojas verdes con tomates frescos.",
                "/Images/Comida-burrata.png", true,
                List.of("Mozzarella", "Stracciatella", "Crema de leche", "Hojas verdes", "Tomates frescos")));

        data.put(3, new Producto(3, "carpaccio di salmone", 60000,
                "Láminas finas de salmón fresco crudo dispuestas en una base circular, bañadas con una emulsión de jugo de limón y aceite de oliva virgen extra",
                "/Images/comida-carpaccio_di_salmone.png", true,
                List.of("Salmón fresco", "Jugo de limón", "Aceite de oliva virgen extra")));

        data.put(4, new Producto(4, "papas a la Francesa", 25000,
                "Bowl de papas a la francesa cubiertas de queso parmesano, champiñones y salsa de la casa",
                "/Images/comida-papas_a_la_francesa.png", true,
                List.of("Papas", "Queso parmesano", "Champiñones", "Salsa de la casa")));

        data.put(5, new Producto(5, "pasta gratinada del chef", 50000,
                "Pasta con queso gratinado mozzarella y acompañada de un pan en forma de estrella bañado en salsa",
                "/Images/comida-pasta_gratinada_del_chef.png", true,
                List.of("Pasta", "Queso mozzarella gratinado", "Pan artesanal", "Salsa")));

        data.put(6, new Producto(6, "polpo alla griglia", 50000,
                "Tentáculo de pulpo cocido y sellado a la parrilla, bañado en un aderezo de aceite de oliva con ajo y perejil, el cual contiene un toque de pimentón ahumado y sal marina, cortado en rodajas gruesas y servido sobre una cama de papas rústicas con una guarnición de vegetales asados.",
                "/Images/comida-polpo_alla_griglia.png", true,
                List.of("Pulpo", "Aceite de oliva", "Ajo", "Perejil", "Pimentón ahumado", "Sal marina", "Papas rústicas", "Vegetales asados")));
    }

    public Producto findById(Integer id) {
        return data.get(id);
    }

    public Collection<Producto> findAll() {
        return data.values();
    }
}

