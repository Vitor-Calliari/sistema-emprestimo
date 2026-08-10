package com.vitorcalliari.emprestimos.service;

import com.vitorcalliari.emprestimos.dto.ClienteRequestDTO;
import com.vitorcalliari.emprestimos.dto.ClienteResponseDTO;
import com.vitorcalliari.emprestimos.exception.RecursoNaoEncontradoException;
import com.vitorcalliari.emprestimos.model.Cliente;
import com.vitorcalliari.emprestimos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository clienteRepository;

    @Transactional
    public ClienteResponseDTO cadastrar(ClienteRequestDTO dto) {
        clienteRepository.findByDocumento(dto.documento())
                .ifPresent(c -> {
                    throw new IllegalArgumentException(
                            "Ja existe um cliente cadastrado com esse documento");
                });
        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setDocumento(dto.documento());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());

        Cliente salvo = clienteRepository.save(cliente);
        return paraResponseDTO(salvo);
    }

    @Transactional (readOnly = true)
    public List<ClienteResponseDTO> listarTodos() {
        return clienteRepository.findAll().stream()
                .map(this::paraResponseDTO)
                .toList();
    }

    @Transactional (readOnly = true)
    public ClienteResponseDTO buscarPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado: id " + id));
        return paraResponseDTO(cliente);
    }

    @Transactional
    public ClienteResponseDTO atualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Cliente não encontrado: id " + id));

        clienteRepository.findByDocumento(dto.documento())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new IllegalArgumentException(
                            "Já existe um cliente cadastrado com esse documento");
                });
        cliente.setNome(dto.nome());
        cliente.setDocumento(dto.documento());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());

        Cliente atualizado = clienteRepository.save(cliente);
        return paraResponseDTO(atualizado);
    }

    @Transactional
    public void deletar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado: id " + id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteResponseDTO paraResponseDTO(Cliente c) {
        return new ClienteResponseDTO(
                c.getId(), c.getNome(), c.getDocumento(),
                c.getEmail(), c.getTelefone(), c.getCriadoEm());
    }
}
