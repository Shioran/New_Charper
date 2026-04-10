package com.example.NewCharper.controller;

import com.example.NewCharper.model.Adiccion;
import com.example.NewCharper.service.AdiccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adicciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdiccionController {

    private final AdiccionService service;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Adiccion>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId));
    }

    @PostMapping
    public ResponseEntity<Adiccion> crear(@RequestBody Adiccion adiccion) {
        return ResponseEntity.status(201).body(service.crear(adiccion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Adiccion> actualizar(
            @PathVariable Long id, @RequestBody Adiccion datos) {
        return ResponseEntity.ok(service.actualizar(id, datos));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/dias")
    public ResponseEntity<Long> diasSinRecaida(@PathVariable Long id) {
        return ResponseEntity.ok(service.calcularDiasSinRecaida(id));
    }
}
