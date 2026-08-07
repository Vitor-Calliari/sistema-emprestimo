package com.vitorcalliari.emprestimos.repository;

import com.vitorcalliari.emprestimos.model.Moeda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoedaRepository extends JpaRepository<Moeda, String> {
}
