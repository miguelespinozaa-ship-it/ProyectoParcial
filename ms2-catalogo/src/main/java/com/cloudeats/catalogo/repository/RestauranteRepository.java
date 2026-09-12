package com.cloudeats.catalogo.repository;

import com.cloudeats.catalogo.model.Restaurante;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RestauranteRepository extends MongoRepository<Restaurante, String> {

    List<Restaurante> findByActivoTrue();

    List<Restaurante> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

    List<Restaurante> findByCategoriaIgnoreCaseAndActivoTrue(String categoria);

    List<Restaurante> findByNombreContainingIgnoreCaseAndCategoriaIgnoreCaseAndActivoTrue(
            String nombre,
            String categoria
    );
}
