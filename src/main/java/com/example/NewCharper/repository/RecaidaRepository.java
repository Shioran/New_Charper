package com.example.NewCharper.repository;

import com.example.NewCharper.model.Recaida;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecaidaRepository extends JpaRepository<Recaida, Long> {
    List<Recaida> findByAdiccionIdOrderByFechaDesc(Long adiccionId);
    Optional<Recaida> findTopByAdiccionIdOrderByFechaDesc(Long adiccionId);
}