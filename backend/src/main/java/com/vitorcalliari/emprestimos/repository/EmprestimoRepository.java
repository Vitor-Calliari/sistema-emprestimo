package com.vitorcalliari.emprestimos.repository;

import com.vitorcalliari.emprestimos.model.Emprestimo;
import org.apache.tomcat.util.http.fileupload.util.LimitedInputStream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    @Query("SELECT e FROM Emprestimo e WHERE e.cliente.id = :clienteId")
    List<Emprestimo> buscarPorCliente(@Param("clienteId") Long clienteId);

    @Query(value = """
        SELECT m.codigo, m.nome, 
                COALESCE(SUM(e.valor_reais), 0) AS total_emprestado,
                COUNT (e.id) AS quantidade
            FROM emprestimo e
            JOIN moeda m ON m.codigo = e.moeda_codigo
            GROUP BY m.codigo, m.nome
            ORDER BY total_emprestado DESC 
        """, nativeQuery = true)
    List<Object[]> totalEmprestadoPorMoedaRaw();

    @Query(value = """
        SELECT c.id, c.nome,
                COUNT (e.id) AS quantidade_emprestimos,
                COALESCE(SUM(e.valor_reais), 0) AS total_emprestado
        FROM cliente c
        JOIN emprestimo e ON e.cliente_id = c.id
        GROUP BY c.id, c.nome
        ORDER BY quantidade_emprestimos DESC
        LIMIT 10      
        """, nativeQuery = true)
    List<Object[]> rankingClientesRaw();
}