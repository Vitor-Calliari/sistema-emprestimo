package com.vitorcalliari.emprestimos.dto;

import java.math.BigDecimal;

public record TotalPorMoedaDTO(
        String moedaCodigo,
        String moedaNome,
        BigDecimal totalEmprestado,
        long quantidadeEmprestimos
) {}
