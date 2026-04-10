package com.example.NewCharper.controller;

import com.example.NewCharper.model.Recaida;
import com.example.NewCharper.service.RecaidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recaidas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecaidaController {

    private final RecaidaService service;

    // GET /api/recaidas/adiccion/{adiccionId}
    // Trae el historial completo de recaídas de una adicción
    @GetMapping("/adiccion/{adiccionId}")
    public ResponseEntity<List<Recaida>> listarPorAdiccion(@PathVariable Long adiccionId) {
        return ResponseEntity.ok(service.listarPorAdiccion(adiccionId));
    }

    // GET /api/recaidas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Recaida> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/recaidas
    // Body: { "fecha": "2026-04-01T18:30:00", "motivo": "...", "adiccion": { "id": 1 } }
    // Si omitís "fecha", se usa la fecha/hora actual automáticamente
    @PostMapping
    public ResponseEntity<?> registrar(@RequestBody Recaida recaida) {
        try {
            Recaida nueva = service.registrar(recaida);
            return ResponseEntity.status(201).body(nueva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/recaidas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/recaidas/adiccion/{adiccionId}/dias
    // Devuelve los días actuales sin recaída (RF-03)
    @GetMapping("/adiccion/{adiccionId}/dias")
    public ResponseEntity<?> diasSinRecaida(@PathVariable Long adiccionId) {
        try {
            long dias = service.calcularDiasSinRecaida(adiccionId);
            return ResponseEntity.ok(Map.of("diasSinRecaida", dias));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/recaidas/adiccion/{adiccionId}/record
    // Devuelve el récord histórico de días sin recaída
    @GetMapping("/adiccion/{adiccionId}/record")
    public ResponseEntity<?> recordHistorico(@PathVariable Long adiccionId) {
        try {
            long record = service.calcularRecordHistorico(adiccionId);
            return ResponseEntity.ok(Map.of("recordHistoricoDias", record));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}