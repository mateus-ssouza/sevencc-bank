package br.acc.bank.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.acc.bank.exception.ConflictException;
import br.acc.bank.exception.NotFoundException;
import br.acc.bank.exception.RepositoryException;
import br.acc.bank.model.Admin;
import br.acc.bank.model.Usuario;
import br.acc.bank.model.enums.UsuarioRole;
import br.acc.bank.repository.AdminRepository;
import br.acc.bank.repository.UsuarioRepository;
import br.acc.bank.util.Strings;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdminRepository adminRepository;

    public List<Admin> getAll() {
        try {
            return adminRepository.findAll();
        } catch (Exception e) {
            throw new RepositoryException(Strings.ADMIN.ERROR_FIND_ALL_LIST, e);
        }
    }

    public Optional<Admin> getById(Long id) {
        try {
            Optional<Admin> admin = adminRepository.findById(id);
            // Verificar se não existe admin com o id passado
            if (!admin.isPresent())
                throw new NotFoundException(Strings.ADMIN.NOT_FOUND);

            return admin;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.ADMIN.ERROR_FIND_BY_ID, e);
        }
    }

    @Transactional
    public Admin create(Admin admin) {
        try {
            Optional<Usuario> verificarEmailUsuario = usuarioRepository.findByEmail(admin.getEmail());
            Optional<Usuario> verificarCpfUsuario = usuarioRepository.findByCpf(admin.getCpf());
            UserDetails verificarLoginUsuario = usuarioRepository.findByLogin(admin.getLogin());

            // Verificar se já existe um usuário com o mesmo email, CPF ou login
            if (verificarEmailUsuario.isPresent() || verificarCpfUsuario.isPresent()
                    || verificarLoginUsuario != null)
                throw new ConflictException(Strings.USER.CONFLICT);

            String encryptedPassword = new BCryptPasswordEncoder().encode(admin.getPassword());
            admin.setPassword(encryptedPassword);
            admin.setRole(UsuarioRole.ADMIN);

            return adminRepository.save(admin);
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.ADMIN.ERROR_CREATE, e);
        }
    }

    @Transactional
    public Admin update(Long id, Admin admin) {
        try {
            Optional<Admin> adminModel = adminRepository.findById(id);

            if (!adminModel.isPresent())
                throw new NotFoundException(Strings.ADMIN.NOT_FOUND);

            Optional<Usuario> verificarEmailUsuario = usuarioRepository.findByEmail(admin.getEmail());
            Optional<Usuario> verificarCpfUsuario = usuarioRepository.findByCpf(admin.getCpf());
            UserDetails verificarLoginUsuario = usuarioRepository.findByLogin(admin.getLogin());
            Optional<Long> idUsuario = usuarioRepository.findIdByLogin(admin.getLogin());

            // Verificar se já existe um admin com o mesmo email, CPF ou login
            // E se é o mesmo admin que deseja mudar esses campos
            if ((verificarEmailUsuario.isPresent()
                    && !verificarEmailUsuario.get().getId().equals(adminModel.get().getId())) ||
                    (verificarCpfUsuario.isPresent()
                            && !verificarCpfUsuario.get().getId().equals(adminModel.get().getId()))
                    ||
                    (verificarLoginUsuario != null && !idUsuario.get().equals(adminModel.get().getId())))
                throw new ConflictException(Strings.USER.CONFLICT);

            Admin adminAtualizado = adminModel.get();
            adminAtualizado.setNome(admin.getNome());
            adminAtualizado.setCpf(admin.getCpf());
            adminAtualizado.setTelefone(admin.getTelefone());
            adminAtualizado.setDataNascimento(admin.getDataNascimento());
            adminAtualizado.setEmail(admin.getEmail());
            adminAtualizado.setLogin(admin.getLogin());
            String encryptedPassword = new BCryptPasswordEncoder().encode(admin.getPassword());
            adminAtualizado.setPassword(encryptedPassword);

            return adminRepository.save(adminAtualizado);
        } catch (NotFoundException e) {
            throw e;
        } catch (ConflictException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.ADMIN.ERROR_UPDATE, e);
        }
    }

    public void delete(Long id) {
        try {
            // Verificar se já existe um admin pelo id e remover o mesmo
            if (adminRepository.existsById(id)) {
                adminRepository.deleteById(id);
            } else {
                throw new NotFoundException(Strings.ADMIN.NOT_FOUND);
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RepositoryException(Strings.ADMIN.ERROR_DELETE, e);
        }
    }
}
