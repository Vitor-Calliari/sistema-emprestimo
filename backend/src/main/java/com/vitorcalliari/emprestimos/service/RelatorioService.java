package com.vitorcalliari.emprestimos.service;

import com.vitorcalliari.emprestimos.dto.RankingClienteDTO;
import com.vitorcalliari.emprestimos.dto.TotalPorMoedaDTO;
import com.vitorcalliari.emprestimos.repository.EmprestimoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {
    private final EmprestimoRepository emprestimoRepository;

    @Transactional(readOnly = true)
    public List<TotalPorMoedaDTO> totalPorMoeda() {
        return emprestimoRepository.totalEmprestadoPorMoedaRaw().stream()
                .map(row -> new TotalPorMoedaDTO(
                        (String) row[0],
                        (String) row[1],
                        (BigDecimal) row[2],
                        ((Number) row[3]).longValue()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RankingClienteDTO> rankingClientes() {
        return emprestimoRepository.rankingClientesRaw().stream()
                .map(row -> new RankingClienteDTO(
                        ((Number) row[0]).longValue(),
                        (String) row [1],
                        ((Number) row[2]).longValue(),
                        (BigDecimal) row[3]
                ))
                .toList();
    }
}
