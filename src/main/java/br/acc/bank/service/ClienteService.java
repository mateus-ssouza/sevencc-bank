package br.acc.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.acc.bank.exception.ConflictException;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.exception.RepositoryException;
import br.acc.bank.model.Cliente;
import br.acc.bank.repository.ClienteRepository;
import br.acc.bank.repository.EnderecoRepository;
import br.acc.bank.util.Strings;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    public List<Cliente> getAll() {
        try {
            return clienteRepository.findAll();
        } catch (Exception e) {
            throw new RepositoryException(Strings.CLIENTE.ERROR_FIND_ALL_LIST, e);
        }
    }

    public Optional<Cliente> getById(Long id) {
        try {
            Optional<Cliente> cliente = clienteRepository.findById(id);
            // Verificar se não existe cliente com o id passado
            if (!cliente.isPresent())
                throw new NotFoundException(Strings.CLIENTE.NOT_FOUND);

            return cliente;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.CLIENTE.ERROR_FIND_BY_ID, e);
        }
    }

    @Transactional
    public Cliente create(Cliente cliente) {
        try {
            Optional<Cliente> verificarEmailCliente = clienteRepository.findByEmail(cliente.getEmail());
            Optional<Cliente> verificarCpfCliente = clienteRepository.findByCpf(cliente.getCpf());

            // Verificar se já existe um cliente com o mesmo email ou CPF
            if (verificarEmailCliente.isPresent() || verificarCpfCliente.isPresent())
                throw new ConflictException(Strings.CLIENTE.CONFLICT);

            enderecoRepository.save(cliente.getEndereco());

            return clienteRepository.save(cliente);
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.CLIENTE.ERROR_CREATE, e);
        }
    }

    @Transactional
    public Cliente update(Long id, Cliente cliente) {
        try {
            Optional<Cliente> clienteModel = clienteRepository.findById(id);

            if (!clienteModel.isPresent())
                throw new NotFoundException(Strings.CLIENTE.NOT_FOUND);

            Optional<Cliente> verificarEmailCliente = clienteRepository.findByEmail(cliente.getEmail());
            Optional<Cliente> verificarCpfCliente = clienteRepository.findByCpf(cliente.getCpf());

            // Verificar se já existe um cliente com o mesmo email ou CPF
            // E se é o mesmo cliente que deseja mudar esses campos
            if ((verificarEmailCliente.isPresent()
                    && !verificarEmailCliente.get().getId().equals(clienteModel.get().getId())) ||
                    (verificarCpfCliente.isPresent()
                            && !verificarCpfCliente.get().getId().equals(clienteModel.get().getId())))
                throw new ConflictException(Strings.CLIENTE.CONFLICT);

            // Atualize apenas os campos necessários e mantenha o valor de dataCadastro
            Cliente clienteAtualizado = clienteModel.get();
            clienteAtualizado.setNome(cliente.getNome());
            clienteAtualizado.setCpf(cliente.getCpf());
            clienteAtualizado.setTelefone(cliente.getTelefone());
            clienteAtualizado.setEmail(cliente.getEmail());
            clienteAtualizado.setEndereco(cliente.getEndereco());
            // Não atualize a dataCadastro
            // clienteAtualizado.setDataCadastro(clienteModel.get().getDataCadastro());

            enderecoRepository.save(clienteAtualizado.getEndereco());

            return clienteRepository.save(clienteAtualizado);
        } catch (NotFoundException e) {
            throw e;
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.CLIENTE.ERROR_UPDATE, e);
        }
    }

    public void delete(Long id) {
        try {
            // Verificar se já existe um cliente pelo id e remover o mesmo
            if (clienteRepository.existsById(id)) {
                clienteRepository.deleteById(id);
            } else {
                throw new NotFoundException(Strings.CLIENTE.NOT_FOUND);
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.CLIENTE.ERROR_DELETE, e);
        }
    }
}
