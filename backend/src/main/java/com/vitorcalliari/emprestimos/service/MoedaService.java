package com.vitorcalliari.emprestimos.service;

import com.vitorcalliari.emprestimos.client.BcbClient;
import com.vitorcalliari.emprestimos.client.dto.BcbMoedaDTO;
import com.vitorcalliari.emprestimos.dto.MoedaRequestDTO;
import com.vitorcalliari.emprestimos.dto.MoedaResponseDTO;
import com.vitorcalliari.emprestimos.exception.RecursoNaoEncontradoException;
import com.vitorcalliari.emprestimos.model.Moeda;
import com.vitorcalliari.emprestimos.repository.MoedaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MoedaService {

    private final MoedaRepository moedaRepository;
    private final BcbClient bcbClient;

    @Transactional(readOnly = true)
    public List<MoedaResponseDTO> listarTodas() {
        return moedaRepository.findAll().stream()
                .map(this::paraResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MoedaResponseDTO buscarPorCodigo(String codigo) {
        Moeda moeda = moedaRepository.findById(codigo)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Moeda não encontrada: " + codigo));
        return paraResponseDTO(moeda);
    }

    @Transactional
    public int sincronizarComBcb(){
        List<BcbMoedaDTO> moedasBcb = bcbClient.buscarMoedas();
        int novasMoedas = 0;

        for(BcbMoedaDTO m : moedasBcb) {
            String codigo = m.simbolo().toUpperCase();

            if (!moedaRepository.existsById(codigo)) {
                Moeda moeda = new Moeda();
                moeda.setCodigo(codigo);
                moeda.setNome(m.nomeFormatado());
                moedaRepository.save(moeda);
                novasMoedas++;
            }
        }
        return novasMoedas;
    }

    private MoedaResponseDTO paraResponseDTO (Moeda m) {
        return new MoedaResponseDTO(
                m.getCodigo(), m.getNome());
    }
}
