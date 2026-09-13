package com.cloudeats.catalogo.controller;

import com.cloudeats.catalogo.model.Plato;
import com.cloudeats.catalogo.model.Resena;
import com.cloudeats.catalogo.model.Restaurante;
import com.cloudeats.catalogo.service.RestauranteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurantes")
public class RestauranteController {

    private final RestauranteService restauranteService;

    public RestauranteController(RestauranteService restauranteService) {
        this.restauranteService = restauranteService;
    }

    @PostMapping
    public ResponseEntity<Restaurante> crear(@Valid @RequestBody Restaurante restaurante) {
        return ResponseEntity.ok(restauranteService.crear(restaurante));
    }

    @GetMapping
    public ResponseEntity<List<Restaurante>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria) {
        return ResponseEntity.ok(restauranteService.buscar(nombre, categoria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurante> obtenerPorId(@PathVariable String id) {
        return ResponseEntity.ok(restauranteService.obtenerPorId(id));
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<List<Plato>> obtenerMenu(@PathVariable String id) {
        return ResponseEntity.ok(restauranteService.obtenerMenu(id));
    }

    @PostMapping("/{id}/platos")
    public ResponseEntity<Restaurante> agregarPlato(
            @PathVariable String id,
            @Valid @RequestBody Plato plato) {
        return ResponseEntity.ok(restauranteService.agregarPlato(id, plato));
    }

    @DeleteMapping("/{restauranteId}/platos/{platoId}")
    public ResponseEntity<Restaurante> eliminarPlato(
            @PathVariable String restauranteId,
            @PathVariable String platoId) {
        return ResponseEntity.ok(restauranteService.eliminarPlato(restauranteId, platoId));
    }

    @PostMapping("/{id}/resenas")
    public ResponseEntity<Restaurante> agregarResena(
            @PathVariable String id,
            @Valid @RequestBody Resena resena) {
        return ResponseEntity.ok(restauranteService.agregarResena(id, resena));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        restauranteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
