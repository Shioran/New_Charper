package com.example.NewCharper.repository;
import com.example.NewCharper.model.Adiccion;
import com.example.NewCharper.model.Adiccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdiccionRepository extends JpaRepository<Adiccion, Long> {
    List<Adiccion> findByUsuarioId(Long usuarioId);
}
