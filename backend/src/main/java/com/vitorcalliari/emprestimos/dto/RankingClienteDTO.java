package com.vitorcalliari.emprestimos.dto;

import java.math.BigDecimal;

public record RankingClienteDTO(
        Long clienteId,
        String clienteNome,
        long quantidadeEmprestimos,
        BigDecimal totalEmprestado
) {}
