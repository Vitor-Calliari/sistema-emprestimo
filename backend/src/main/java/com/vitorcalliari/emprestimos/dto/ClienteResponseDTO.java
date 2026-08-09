package com.vitorcalliari.emprestimos.dto;

import java.time.LocalDateTime;

public record ClienteResponseDTO (
        Long id,
        String nome,
        String documento,
        String email,
        String telefone,
        LocalDateTime criadoEm
) {}
