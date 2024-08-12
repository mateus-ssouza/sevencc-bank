package br.acc.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.acc.bank.exception.ConflictException;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.exception.RepositoryException;
import br.acc.bank.model.Agencia;
import br.acc.bank.repository.AgenciaRepository;
import br.acc.bank.repository.ContaRepository;
import br.acc.bank.util.Strings;

@Service
public class AgenciaService {

    @Autowired
    private AgenciaRepository agenciaRepository;

    @Autowired
    private ContaRepository contaRepository;

    // Listar todas as agencias
    public List<Agencia> getAll() {
        try {
            return agenciaRepository.findAll();
        } catch (Exception e) {
            throw new RepositoryException(Strings.AGENCIA.ERROR_FIND_ALL_LIST, e);
        }
    }

    // Listar agencia pelo seu id 
    public Optional<Agencia> getById(Long id) {
        try {
            Optional<Agencia> agencia = agenciaRepository.findById(id);
            // Verificar se não existe agencia com o id passado
            if (!agencia.isPresent())
                throw new NotFoundException(Strings.AGENCIA.NOT_FOUND);

            return agencia;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.AGENCIA.ERROR_FIND_BY_ID, e);
        }
    }

    // Criar uma agencia
    @Transactional
    public Agencia create(Agencia agencia) {
        try {
            Optional<Agencia> vericiarNumeroAgencia = agenciaRepository.findByNumero(agencia.getNumero());
            // Verificar se já existe uma agencia com o mesmo número
            if (vericiarNumeroAgencia.isPresent())
                throw new ConflictException(Strings.AGENCIA.CONFLICT);

            return agenciaRepository.save(agencia);
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.AGENCIA.ERROR_CREATE, e);
        }
    }

    // Atualizar dados de uma agencia
    @Transactional
    public Agencia update(Long id, Agencia agencia) {
        try {
            Optional<Agencia> agenciaModel = agenciaRepository.findById(id);

            // Verificar se existe uma agencia pelo id
            if (!agenciaModel.isPresent())
                throw new NotFoundException(Strings.AGENCIA.NOT_FOUND);
            
            Optional<Agencia> vericiarNumeroAgencia = agenciaRepository.findByNumero(agencia.getNumero());

            // Verificar se já existe uma agencia com o mesmo número
            // E se é a mesma agencia que deseja mudar o número
            if (vericiarNumeroAgencia.isPresent() &&
                (vericiarNumeroAgencia.get().getId() != agenciaModel.get().getId()))
                throw new ConflictException(Strings.AGENCIA.CONFLICT);

            // Atualizar agencia com novos dados
            Optional<Agencia> agenciaUpdate = agenciaModel.map(agenciaMap -> {
                agenciaMap.setNome(agencia.getNome());
                agenciaMap.setNumero(agencia.getNumero());
                agenciaMap.setTelefone(agencia.getTelefone());
                agenciaMap.setEndereco(agencia.getEndereco());
                return agenciaMap;
            });

            return agenciaRepository.save(agenciaUpdate.get());
        } catch (NotFoundException e) {
            throw e;
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.AGENCIA.ERROR_UPDATE, e);
        }
    }

    // Remover uma agencia
    public void delete(Long id) {
        try {
            // Verificar se já existe uma agencia pelo id e remover a mesma
            if (agenciaRepository.existsById(id)) {
                // Verificar se existem contas cadastradas vinculadas a agencia
                if (contaRepository.existsByAgenciaId(id)) {
                    throw new ConflictException(Strings.AGENCIA.DELETE_CONFLICT);
                }

                agenciaRepository.deleteById(id);
            } else {
                throw new NotFoundException(Strings.AGENCIA.NOT_FOUND);
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.AGENCIA.ERROR_DELETE, e);
        }
    }
}
