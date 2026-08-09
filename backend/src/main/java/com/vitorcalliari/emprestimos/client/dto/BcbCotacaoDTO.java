package com.vitorcalliari.emprestimos.client.dto;

import java.math.BigDecimal;

public record BcbCotacaoDTO(
        BigDecimal cotacaoCompra,
        BigDecimal cotacaoVenda,
        String dataHoraCotacao,
        String tipoBoletim
) {}
