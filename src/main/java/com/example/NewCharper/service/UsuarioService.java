

package com.example.NewCharper.service;

import com.example.NewCharper.model.Usuario;
import com.example.NewCharper.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;

    // Listar todos los usuarios
    public List<Usuario> listarTodos() {
        return usuarioRepo.findAll();
    }

    // Buscar por ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepo.findById(id);
    }

    // Registrar nuevo usuario
    public Usuario registrar(Usuario usuario) {
        // Verificamos que el email no esté ya registrado
        if (usuarioRepo.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }
        return usuarioRepo.save(usuario);
    }

    // Actualizar nombre o email
    public Usuario actualizar(Long id, Usuario datos) {
        Usuario existente = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        existente.setNombre(datos.getNombre());
        existente.setEmail(datos.getEmail());
        // No actualizamos password aquí para tener control separado
        return usuarioRepo.save(existente);
    }

    // Cambiar contraseña
    public Usuario cambiarPassword(Long id, String nuevaPassword) {
        Usuario existente = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        existente.setPassword(nuevaPassword);
        return usuarioRepo.save(existente);
    }

    // Eliminar usuario
    public void eliminar(Long id) {
        if (!usuarioRepo.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado: " + id);
        }
        usuarioRepo.deleteById(id);
    }

    // Login simple (sin JWT por ahora — solo validación de credenciales)
    public Usuario login(String email, String password) {
        Usuario usuario = usuarioRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email no registrado"));

        if (!usuario.getPassword().equals(password)) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return usuario;
    }
}
