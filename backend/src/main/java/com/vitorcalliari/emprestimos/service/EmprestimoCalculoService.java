package com.vitorcalliari.emprestimos.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Component
public class EmprestimoCalculoService {

    private static final MathContext MC = new MathContext(10);

    public long calcularMesesEntreDatas(LocalDate dataInicio, LocalDate dataFim) {
        return Period.between(dataInicio, dataFim).toTotalMonths();
    }

    public BigDecimal calcularValorComJurosCompostos(
            BigDecimal valorPrincipal,
            BigDecimal taxaJurosMensalPercentual,
            long numeroMeses) {

        BigDecimal taxaDecimal = taxaJurosMensalPercentual
                .divide(BigDecimal.valueOf(100), MC);

        BigDecimal umMaisTaxa = BigDecimal.ONE.add(taxaDecimal);

        BigDecimal fatorComposto = umMaisTaxa.pow((int) numeroMeses, MC);

        return valorPrincipal
                .multiply(fatorComposto, MC)
                .setScale(2, RoundingMode.HALF_UP);
    }
}