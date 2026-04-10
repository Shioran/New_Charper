package com.example.NewCharper.service;

import com.example.NewCharper.model.Adiccion;
import com.example.NewCharper.model.Recaida;
import com.example.NewCharper.repository.AdiccionRepository;
import com.example.NewCharper.repository.RecaidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecaidaService {

    private final RecaidaRepository recaidaRepo;
    private final AdiccionRepository adiccionRepo;

    // Listar todas las recaídas de una adicción, más recientes primero
    public List<Recaida> listarPorAdiccion(Long adiccionId) {
        return recaidaRepo.findByAdiccionIdOrderByFechaDesc(adiccionId);
    }

    // Buscar recaída por ID
    public Optional<Recaida> buscarPorId(Long id) {
        return recaidaRepo.findById(id);
    }

    // Registrar una nueva recaída
    // Esto reinicia el contador de días de la adicción correspondiente
    public Recaida registrar(Recaida recaida) {
        // Validamos que la adicción exista
        Long adiccionId = recaida.getAdiccion().getId();
        adiccionRepo.findById(adiccionId)
                .orElseThrow(() -> new RuntimeException("Adicción no encontrada: " + adiccionId));

        // Si no viene fecha, usamos la actual
        if (recaida.getFecha() == null) {
            recaida.setFecha(LocalDateTime.now());
        }

        return recaidaRepo.save(recaida);
    }

    // Eliminar una recaída por ID
    public void eliminar(Long id) {
        if (!recaidaRepo.existsById(id)) {
            throw new RuntimeException("Recaída no encontrada: " + id);
        }
        recaidaRepo.deleteById(id);
    }

    // Calcular días sin recaída de una adicción (lógica RF-03)
    public long calcularDiasSinRecaida(Long adiccionId) {
        Adiccion adiccion = adiccionRepo.findById(adiccionId)
                .orElseThrow(() -> new RuntimeException("Adicción no encontrada: " + adiccionId));

        Optional<Recaida> ultimaRecaida = recaidaRepo
                .findTopByAdiccionIdOrderByFechaDesc(adiccionId);

        // Si hay recaídas → desde la última; si no → desde fecha_inicio
        LocalDate desde = ultimaRecaida
                .map(r -> r.getFecha().toLocalDate())
                .orElse(adiccion.getFechaInicio());

        return ChronoUnit.DAYS.between(desde, LocalDate.now());
    }

    // Récord histórico: mayor racha alcanzada entre recaídas
    public long calcularRecordHistorico(Long adiccionId) {
        Adiccion adiccion = adiccionRepo.findById(adiccionId)
                .orElseThrow(() -> new RuntimeException("Adicción no encontrada: " + adiccionId));

        List<Recaida> recaidas = recaidaRepo
                .findByAdiccionIdOrderByFechaDesc(adiccionId);

        if (recaidas.isEmpty()) {
            // Sin recaídas: el récord es el tiempo total desde inicio
            return ChronoUnit.DAYS.between(adiccion.getFechaInicio(), LocalDate.now());
        }

        long record = 0;

        // Comparamos cada intervalo entre recaídas
        // La lista viene ordenada DESC, así que la invertimos mentalmente
        for (int i = 0; i < recaidas.size(); i++) {
            LocalDate fechaRecaida = recaidas.get(i).getFecha().toLocalDate();
            LocalDate fechaAnterior;

            if (i == recaidas.size() - 1) {
                // El intervalo más antiguo: desde fecha_inicio hasta la primera recaída
                fechaAnterior = adiccion.getFechaInicio();
            } else {
                // Intervalo entre dos recaídas consecutivas
                fechaAnterior = recaidas.get(i + 1).getFecha().toLocalDate();
            }

            long dias = ChronoUnit.DAYS.between(fechaAnterior, fechaRecaida);
            if (dias > record) record = dias;
        }

        // También consideramos el intervalo actual (desde la última recaída hasta hoy)
        LocalDate desdeUltima = recaidas.get(0).getFecha().toLocalDate();
        long diasActuales = ChronoUnit.DAYS.between(desdeUltima, LocalDate.now());
        if (diasActuales > record) record = diasActuales;

        return record;
    }
}
