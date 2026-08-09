package com.vitorcalliari.emprestimos.dto;

import jakarta.validation.constraints.NotBlank;

public record MoedaRequestDTO(
    @NotBlank String codigo,
    @NotBlank String nome
){}
