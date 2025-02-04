package com.github.dangelcrack.model.services;

import com.github.dangelcrack.model.dao.UsuarioDAO;
import com.github.dangelcrack.model.entity.Usuario;
import java.util.List;

/**
 * Clase de servicio que gestiona la lógica de negocio de los usuarios.
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO(); // Instanciamos el DAO
    }

    /**
     * Guarda un nuevo usuario en la base de datos si no existe.
     *
     * @param usuario el usuario a insertar.
     * @return true si el usuario fue creado exitosamente, false si ya existe.
     */
    public boolean guardarUsuario(Usuario usuario) {
        if (usuario.getId() != null && obtenerUsuarioPorId(usuario.getId()) != null) {
            return false;
        }
        return usuarioDAO.creaUsuario(usuario);
    }

    /**
     * Actualiza un usuario existente en la base de datos.
     *
     * @param usuario el usuario con los datos actualizados.
     * @return true si el usuario fue actualizado exitosamente, false si no fue encontrado.
     */
    public boolean actualizarUsuario(Usuario usuario) {
        if (usuario.getId() == null || obtenerUsuarioPorId(usuario.getId()) == null) {
            return false;
        }
        return UsuarioDAO.actualizarUsuario(usuario);
    }
    /**
     * Obtiene un usuario por su ID.
     *
     * @param id el ID del usuario a recuperar.
     * @return el usuario correspondiente al ID o null si no se encuentra.
     */
    public Usuario obtenerUsuarioPorId(Integer id) {
        List<Usuario> usuarios = usuarioDAO.leeUsuarios();
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }
        return null;
    }

    /**
     * Obtiene un usuario por su nombre y contraseña.
     *
     * @param nombre     el nombre del usuario.
     * @param contrasena la contraseña del usuario.
     * @return el usuario si se encuentra, de lo contrario, null.
     */
    public Usuario obtenerUsuarioPorCredenciales(String nombre, String contrasena) {
        return usuarioDAO.findByUsernameAndPassword(nombre, contrasena);
    }
    public Usuario obtenerUsuario(String nombre) {
        return usuarioDAO.leeUsuario(nombre);
    }
}
