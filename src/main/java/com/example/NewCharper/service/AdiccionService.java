package com.example.NewCharper.service;

import com.example.NewCharper.model.Adiccion;
import com.example.NewCharper.model.Recaida;
import com.example.NewCharper.repository.AdiccionRepository;
import com.example.NewCharper.repository.RecaidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdiccionService {

    private final AdiccionRepository adiccionRepo;
    private final RecaidaRepository recaidaRepo;

    public List<Adiccion> listarPorUsuario(Long usuarioId) {
        return adiccionRepo.findByUsuarioId(usuarioId);
    }

    public Adiccion crear(Adiccion adiccion) {
        return adiccionRepo.save(adiccion);
    }

    public Adiccion actualizar(Long id, Adiccion datos) {
        Adiccion existente = adiccionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Adicción no encontrada"));
        existente.setNombre(datos.getNombre());
        existente.setFechaInicio(datos.getFechaInicio());
        return adiccionRepo.save(existente);
    }

    public void eliminar(Long id) {
        adiccionRepo.deleteById(id);
    }

    // RF-03: cálculo automático de días
    public long calcularDiasSinRecaida(Long adiccionId) {
        Adiccion a = adiccionRepo.findById(adiccionId)
                .orElseThrow(() -> new RuntimeException("Adicción no encontrada"));

        Optional<Recaida> ultima = recaidaRepo
                .findTopByAdiccionIdOrderByFechaDesc(adiccionId);

        LocalDate desde = ultima
                .map(r -> r.getFecha().toLocalDate())
                .orElse(a.getFechaInicio());

        return ChronoUnit.DAYS.between(desde, LocalDate.now());
    }
}
