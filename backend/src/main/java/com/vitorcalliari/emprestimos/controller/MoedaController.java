package com.vitorcalliari.emprestimos.controller;

import com.vitorcalliari.emprestimos.dto.MoedaResponseDTO;
import com.vitorcalliari.emprestimos.service.MoedaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moedas")
@RequiredArgsConstructor
public class MoedaController {
    private final MoedaService moedaService;

    @GetMapping
    public ResponseEntity<List<MoedaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(moedaService.listarTodas());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<MoedaResponseDTO> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(moedaService.buscarPorCodigo(codigo));
    }

    @PostMapping("/sincronizar")
    public ResponseEntity<String> sincronizar() {
        int novas = moedaService.sincronizarComBcb();
        return ResponseEntity.ok(novas + " moeda(s) nova(s) sincronizada(s) do Banco Central");
    }
}
