package com.vitorcalliari.emprestimos.repository;

import com.vitorcalliari.emprestimos.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    @Query("SELECT e FROM Emprestimo e WHERE e.cliente.id = :clienteId")
    List<Emprestimo> buscarPorCliente(@Param("clienteId") Long clienteId);
}