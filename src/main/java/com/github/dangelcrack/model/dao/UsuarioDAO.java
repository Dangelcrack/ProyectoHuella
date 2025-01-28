package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object (DAO) for managing Usuario entities using Hibernate.
 */
public class UsuarioDAO extends Usuario{

    /**
     * Creates or inserts a new Usuario entity in the database.
     * If the Usuario already exists, it will not be duplicated.
     *
     * @param usuario the Usuario entity to create.
     * @return true if the entity was successfully created, false otherwise.
     */
    public boolean creaUsuario(Usuario usuario) {
        if (usuario == null || leeUsuario(usuario.getNombre()) != null) {
            return false; // El usuario ya existe o el usuario es nulo
        }

        boolean creado = false;
        Transaction transaction = null;
        try (Session session = Connection.getInstance().getSession()) {
            transaction = session.beginTransaction();
            session.persist(usuario);
            transaction.commit();
            creado = true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Rollback si ocurre un error
            }
            e.printStackTrace(); // Agregar log de error si se desea
        }

        return creado;
    }

    /**
     * Reads a specific Usuario entity by its name.
     *
     * @param nombre the name of the Usuario to read.
     * @return the Usuario entity if found, or null if not found.
     */
    public Usuario leeUsuario(String nombre) {
        Usuario usuario = null;
        try (Session session = Connection.getInstance().getSession()) {
            Query<Usuario> query = session.createQuery("FROM Usuario WHERE nombre = :nombre", Usuario.class);
            query.setParameter("nombre", nombre);
            usuario = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace(); // Agregar log de error si se desea
        }

        return usuario;
    }

    /**
     * Finds a Usuario entity by its username and password.
     *
     * @param nombre     the username of the Usuario to find.
     * @param contraseña the password of the Usuario to find.
     * @return the Usuario entity if found, or null if not found.
     */
    public Usuario findByUsernameAndPassword(String nombre, String contraseña) {
        Usuario usuario = null;
        try (Session session = Connection.getInstance().getSession()) {
            Query<Usuario> query = session.createQuery(
                    "FROM Usuario WHERE nombre = :nombre AND contraseña = :contraseña",
                    Usuario.class
            );
            query.setParameter("nombre", nombre);
            query.setParameter("contraseña", contraseña);
            usuario = query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace(); // Add proper logging for error
        }
        return usuario;
    }


    /**
     * Updates an existing Usuario entity in the database.
     *
     * @param usuarioActualizado the updated Usuario entity.
     * @return true if the entity was successfully updated, false otherwise.
     */
    public static boolean actualizarUsuario(Usuario usuarioActualizado) {
        if (usuarioActualizado == null || usuarioActualizado.getId() == null) {
            throw new IllegalArgumentException("El usuario o su ID no pueden ser nulos.");
        }
        boolean actualizado = false;
        Session session = Connection.getInstance().getSession();;
        Transaction tx = session.beginTransaction();;
        Usuario usuarioExistente = session.get(Usuario.class, usuarioActualizado.getId());
        if (usuarioExistente != null) {
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
            usuarioExistente.setEmail(usuarioActualizado.getEmail());
            usuarioExistente.setContraseña(usuarioActualizado.getContraseña());
            session.merge(usuarioExistente);
            actualizado = true;
        }
        tx.commit();
        session.close();


        return actualizado;
    }

    /**
     * Deletes a specific Usuario entity from the database.
     *
     * @param usuario the Usuario entity to delete.
     * @return true if the entity was successfully deleted, false otherwise.
     */
    public boolean eliminarUsuario(Usuario usuario) {
        boolean eliminado = false;
        Transaction transaction = null;
        try (Session session = Connection.getInstance().getSession()) {
            transaction = session.beginTransaction();
            Query<Usuario> query = session.createQuery("FROM Usuario WHERE nombre = :nombre", Usuario.class);
            query.setParameter("nombre", usuario.getNombre());
            Usuario usuarioExistente = query.uniqueResult();
            if (usuarioExistente != null) {
                session.remove(usuarioExistente);
                eliminado = true;
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Rollback si ocurre un error
            }
            e.printStackTrace(); // Agregar log de error si se desea
        }

        return eliminado;
    }

    public static UsuarioDAO build() {
        return new UsuarioDAO();
    }
}
