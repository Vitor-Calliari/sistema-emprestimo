package com.vitorcalliari.emprestimos.controller;

import com.vitorcalliari.emprestimos.dto.RankingClienteDTO;
import com.vitorcalliari.emprestimos.dto.TotalPorMoedaDTO;
import com.vitorcalliari.emprestimos.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/relatorios")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/total-por-moeda")
    public ResponseEntity<List<TotalPorMoedaDTO>> totalPorMoeda() {
        return ResponseEntity.ok(relatorioService.totalPorMoeda());
    }

    @GetMapping("/ranking-clientes")
    public ResponseEntity<List<RankingClienteDTO>> rankingClientes() {
        return ResponseEntity.ok(relatorioService.rankingClientes());
    }
}
