package com.vitorcalliari.emprestimos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmprestimoResponseDTO(
   Long id,
   Long clienteId,
   String clienteNome,
   String moedaCodigo,
   LocalDate dataEmprestimo,
   BigDecimal valorObtido,
   BigDecimal taxaConversao,
   BigDecimal valorReais,
   LocalDate dataVencimento,
   long numeroMeses,
   BigDecimal taxaJurosMensal,
   BigDecimal valorPagarVencimento,
   LocalDateTime criadoEm
) {}
