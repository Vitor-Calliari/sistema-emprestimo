package com.vitorcalliari.emprestimos.service;

import com.vitorcalliari.emprestimos.dto.*;
import com.vitorcalliari.emprestimos.exception.RecursoNaoEncontradoException;
import com.vitorcalliari.emprestimos.model.*;
import com.vitorcalliari.emprestimos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmprestimoService {
    private final EmprestimoRepository emprestimoRepository;
    private final ClienteRepository clienteRepository;
    private final MoedaRepository moedaRepository;
    private final EmprestimoCalculoService calculoService;

    @Transactional
    public EmprestimoResponseDTO cadastrar(EmprestimoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado: id " + dto.clienteId()));

        Moeda moeda = moedaRepository.findById(dto.moedaCodigo())
                .orElseThrow(()-> new RecursoNaoEncontradoException(
                        "Moeda não encontrada: " + dto.moedaCodigo()));

        long numeroMeses = calculoService.calcularMesesEntreDatas(
                dto.dataEmprestimo(), dto.dataVencimento());

        BigDecimal valorReais = dto.valorObtido().multiply(dto.taxaConversao());

        BigDecimal valorPagarVencimento = calculoService.calcularValorComJurosCompostos(
                valorReais, dto.taxaJurosMensal(), numeroMeses);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setCliente(cliente);
        emprestimo.setMoeda(moeda);
        emprestimo.setDataEmprestimo(dto.dataEmprestimo());
        emprestimo.setValorObtido(dto.valorObtido());
        emprestimo.setTaxaConversao(dto.taxaConversao());
        emprestimo.setValorReais(valorReais);
        emprestimo.setDataVencimento(dto.dataVencimento());
        emprestimo.setTaxaJurosMensal(dto.taxaJurosMensal());
        emprestimo.setValorPagarVencimento(valorPagarVencimento);

        Emprestimo salvo = emprestimoRepository.save(emprestimo);

        return paraResponseDTO(salvo, numeroMeses);
    }

    @Transactional(readOnly = true)
    public List<EmprestimoResponseDTO> listarTodos(){
        return emprestimoRepository.findAll().stream()
                .map(e -> paraResponseDTO(e, calculoService.calcularMesesEntreDatas(
                        e.getDataEmprestimo(), e.getDataVencimento())))
                .toList();
    }

    @Transactional(readOnly = true)
    public EmprestimoResponseDTO buscarPorId(Long id) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Emprestimo não encontrado: id " + id));

        long numeroMeses = calculoService.calcularMesesEntreDatas(
                emprestimo.getDataEmprestimo(), emprestimo.getDataVencimento());
        return paraResponseDTO(emprestimo, numeroMeses);
    }

    @Transactional
    public void deletar(Long id) {
        if (!emprestimoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Emprestimo não encontrado: id " + id);
        }
        emprestimoRepository.deleteById(id);
    }

    private EmprestimoResponseDTO paraResponseDTO (Emprestimo e, long numeroMeses) {
        return new EmprestimoResponseDTO(
                e.getId(),
                e.getCliente().getId(),
                e.getCliente().getNome(),
                e.getMoeda().getCodigo(),
                e.getDataEmprestimo(),
                e.getValorObtido(),
                e.getTaxaConversao(),
                e.getValorReais(),
                e.getDataVencimento(),
                numeroMeses,
                e.getTaxaJurosMensal(),
                e.getValorPagarVencimento(),
                e.getCriadoEm()
        );
    }
}
