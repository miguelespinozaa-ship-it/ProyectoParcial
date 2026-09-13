package com.cloudeats.catalogo.service;

import com.cloudeats.catalogo.exception.ResourceNotFoundException;
import com.cloudeats.catalogo.model.Plato;
import com.cloudeats.catalogo.model.Resena;
import com.cloudeats.catalogo.model.Restaurante;
import com.cloudeats.catalogo.repository.RestauranteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;

    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    public Restaurante crear(Restaurante restaurante) {
        asignarIds(restaurante);
        return restauranteRepository.save(restaurante);
    }

    public Restaurante obtenerPorId(String id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurante no encontrado: " + id));
    }

    public List<Restaurante> buscar(String nombre, String categoria) {
        boolean tieneNombre = nombre != null && !nombre.isBlank();
        boolean tieneCategoria = categoria != null && !categoria.isBlank();

        if (tieneNombre && tieneCategoria) {
            return restauranteRepository
                    .findByNombreContainingIgnoreCaseAndCategoriaIgnoreCaseAndActivoTrue(nombre, categoria);
        }

        if (tieneNombre) {
            return restauranteRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
        }

        if (tieneCategoria) {
            return restauranteRepository.findByCategoriaIgnoreCaseAndActivoTrue(categoria);
        }

        return restauranteRepository.findByActivoTrue();
    }

    public List<Plato> obtenerMenu(String restauranteId) {
        return obtenerPorId(restauranteId).getPlatos();
    }

    public Restaurante agregarPlato(String restauranteId, Plato plato) {
        Restaurante restaurante = obtenerPorId(restauranteId);
        plato.setId(UUID.randomUUID().toString());
        restaurante.getPlatos().add(plato);
        return restauranteRepository.save(restaurante);
    }

    public Restaurante eliminarPlato(String restauranteId, String platoId) {
        Restaurante restaurante = obtenerPorId(restauranteId);

        boolean eliminado = restaurante.getPlatos().removeIf(plato -> platoId.equals(plato.getId()));

        if (!eliminado) {
            throw new ResourceNotFoundException("Plato no encontrado: " + platoId);
        }

        return restauranteRepository.save(restaurante);
    }

    public Restaurante agregarResena(String restauranteId, Resena resena) {
        Restaurante restaurante = obtenerPorId(restauranteId);
        resena.setId(UUID.randomUUID().toString());
        restaurante.getResenas().add(resena);
        return restauranteRepository.save(restaurante);
    }

    public void eliminar(String restauranteId) {
        Restaurante restaurante = obtenerPorId(restauranteId);
        restaurante.setActivo(false);
        restauranteRepository.save(restaurante);
    }

    private void asignarIds(Restaurante restaurante) {
        restaurante.getPlatos().forEach(plato -> {
            if (plato.getId() == null || plato.getId().isBlank()) {
                plato.setId(UUID.randomUUID().toString());
            }
        });

        restaurante.getResenas().forEach(resena -> {
            if (resena.getId() == null || resena.getId().isBlank()) {
                resena.setId(UUID.randomUUID().toString());
            }
        });
    }
}
