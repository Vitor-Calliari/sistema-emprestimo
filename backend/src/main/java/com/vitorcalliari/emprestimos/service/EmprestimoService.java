package com.vitorcalliari.emprestimos.service;

import com.vitorcalliari.emprestimos.client.BcbClient;
import com.vitorcalliari.emprestimos.client.dto.BcbCotacaoDTO;
import com.vitorcalliari.emprestimos.dto.EmprestimoRequestDTO;
import com.vitorcalliari.emprestimos.dto.EmprestimoResponseDTO;
import com.vitorcalliari.emprestimos.exception.RecursoNaoEncontradoException;
import com.vitorcalliari.emprestimos.exception.DadosInvalidosException;
import com.vitorcalliari.emprestimos.model.*;
import com.vitorcalliari.emprestimos.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmprestimoService {
    private final EmprestimoRepository emprestimoRepository;
    private final ClienteRepository clienteRepository;
    private final MoedaRepository moedaRepository;
    private final EmprestimoCalculoService calculoService;
    private final BcbClient bcbClient;

    @Transactional
    public EmprestimoResponseDTO cadastrar(EmprestimoRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado: id " + dto.clienteId()));

        String codigoMoeda = dto.moedaCodigo().toUpperCase();
        Moeda moeda = moedaRepository.findById(codigoMoeda)
                .orElseThrow(()-> new RecursoNaoEncontradoException(
                        "Moeda não encontrada: " + codigoMoeda));

        if (!dto.dataVencimento().isAfter(dto.dataEmprestimo())) {
            throw new DadosInvalidosException(
                    "A data de vencimento deve ser posterior a data do emprestimo");
        }

        BcbCotacaoDTO cotacao = bcbClient.buscarCotacaoComFallback(
                codigoMoeda, LocalDate.now());
        BigDecimal taxaConversao = cotacao.cotacaoVenda();

        long numeroMeses = calculoService.calcularMesesEntreDatas(
                dto.dataEmprestimo(), dto.dataVencimento());

        BigDecimal valorReais = dto.valorObtido()
                .multiply(taxaConversao)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorPagarVencimento = calculoService.calcularValorComJurosCompostos(
                valorReais, dto.taxaJurosMensal(), numeroMeses);

        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setCliente(cliente);
        emprestimo.setMoeda(moeda);
        emprestimo.setDataEmprestimo(dto.dataEmprestimo());
        emprestimo.setValorObtido(dto.valorObtido());
        emprestimo.setTaxaConversao(taxaConversao);
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

    @Transactional
    public EmprestimoResponseDTO atualizar(Long id, EmprestimoRequestDTO dto) {
        Emprestimo emprestimo = emprestimoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Emprestimo nao encontrado: id " + id));

        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente nao encontrado: id " + dto.clienteId()));

        String codigoMoeda = dto.moedaCodigo().toUpperCase();
        Moeda moeda = moedaRepository.findById(codigoMoeda)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Moeda nao encontrada: " + codigoMoeda));

        if (!dto.dataVencimento().isAfter(dto.dataEmprestimo())) {
            throw new DadosInvalidosException(
                    "A data de vencimento deve ser posterior a data do emprestimo");
        }

        BcbCotacaoDTO cotacao = bcbClient.buscarCotacaoComFallback(codigoMoeda, LocalDate.now());
        BigDecimal taxaConversao = cotacao.cotacaoVenda();

        long numeroMeses = calculoService.calcularMesesEntreDatas(
                dto.dataEmprestimo(), dto.dataVencimento());

        BigDecimal valorReais = dto.valorObtido()
                .multiply(taxaConversao)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorPagarVencimento = calculoService.calcularValorComJurosCompostos(
                valorReais, dto.taxaJurosMensal(), numeroMeses);

        emprestimo.setCliente(cliente);
        emprestimo.setMoeda(moeda);
        emprestimo.setDataEmprestimo(dto.dataEmprestimo());
        emprestimo.setValorObtido(dto.valorObtido());
        emprestimo.setTaxaConversao(taxaConversao);
        emprestimo.setValorReais(valorReais);
        emprestimo.setDataVencimento(dto.dataVencimento());
        emprestimo.setTaxaJurosMensal(dto.taxaJurosMensal());
        emprestimo.setValorPagarVencimento(valorPagarVencimento);

        Emprestimo atualizado = emprestimoRepository.save(emprestimo);

        return paraResponseDTO(atualizado, numeroMeses);
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
