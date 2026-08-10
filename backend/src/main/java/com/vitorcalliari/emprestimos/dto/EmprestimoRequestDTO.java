package com.vitorcalliari.emprestimos.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmprestimoRequestDTO(
   @NotNull Long clienteId,
   @NotBlank String moedaCodigo,
   @NotNull @PastOrPresent LocalDate dataEmprestimo,
   @NotNull @Positive BigDecimal valorObtido,
   @NotNull LocalDate dataVencimento,
   @NotNull @PositiveOrZero BigDecimal taxaJurosMensal
) {}
