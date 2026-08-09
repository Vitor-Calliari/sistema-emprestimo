package com.vitorcalliari.emprestimos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequestDTO (
        @NotBlank String nome,
        @NotBlank String documento,
        @Email String email,
        String telefone
) {}
