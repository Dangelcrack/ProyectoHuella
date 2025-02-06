package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Habito;
import com.github.dangelcrack.model.entity.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object (DAO) para gestionar las entidades Habito utilizando Hibernate.
 * Proporciona métodos para realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los hábitos en la base de datos.
 */
public class HabitoDAO {

    /**
     * Crea o inserta una nueva entidad Habito en la base de datos.
     * Si el Habito ya existe, no se duplicará.
     *
     * @param habito la entidad Habito a crear.
     * @return
     */
    public boolean crearHabito(Habito habito) {
        boolean resultado = false;
        Session session = Connection.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(habito);
            transaction.commit();
            resultado = true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            // Cierra la sesión
            session.close();
        }
        return resultado;
    }

    /**
     * Actualiza una entidad Habito existente en la base de datos.
     *
     * @param habitoActualizado la entidad Habito actualizada.
     * @return true si la entidad fue actualizada correctamente, false si hubo algún error.
     */
    public boolean actualizarHabito(Habito habitoActualizado) {
        boolean actualizado = false;
        // Inicia la sesión y la transacción
        Session session = Connection.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        // Obtiene el Habito existente a partir de su ID
        Habito habito = session.get(Habito.class, habitoActualizado.getId());
        if (habito != null) {
            // Actualiza el Habito con los nuevos datos
            habito = habitoActualizado;
            session.merge(habito);
            actualizado = true;
        }
        // Finaliza la transacción
        transaction.commit();
        session.close();
        return actualizado;
    }

    /**
     * Elimina una entidad Habito de la base de datos.
     *
     * @param habito la entidad Habito a eliminar.
     * @return true si la entidad fue eliminada correctamente, false si hubo algún error.
     */
    public boolean eliminarHabito(Habito habito) {
        boolean eliminado = false;
        Transaction transaction = null;
        try (Session session = Connection.getInstance().getSession()) {
            // Inicia la transacción
            transaction = session.beginTransaction();
            // Obtiene el Habito a partir de su ID
            Habito habitoExistente = session.get(Habito.class, habito.getId());
            if (habitoExistente != null) {
                // Elimina el Habito de la base de datos
                session.remove(habitoExistente);
                eliminado = true;
            }
            // Finaliza la transacción
            transaction.commit();
        } catch (Exception e) {
            // En caso de error, realiza un rollback
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return eliminado;
    }

    /**
     * Obtiene todos los hábitos asociados con un usuario específico.
     *
     * @param usuario la entidad Usuario que se utiliza para filtrar los hábitos.
     * @return una lista de hábitos asociados con el usuario.
     */
    public List<Habito> obtenerHabitosPorUsuario(Usuario usuario) {
        List<Habito> habitos = null;
        try (Session session = Connection.getInstance().getSession()) {
            // Crea una consulta para obtener los hábitos del usuario
            Query<Habito> query = session.createQuery(
                    "SELECT h FROM Habito h " +
                            "JOIN FETCH h.idActividad " +
                            "JOIN FETCH h.idUsuario " +  // Carga usuario si es necesario
                            "WHERE h.idUsuario = :usuario",
                    Habito.class
            );
            // Establece el parámetro de la consulta
            query.setParameter("usuario", usuario);
            // Ejecuta la consulta y obtiene los resultados
            habitos = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return habitos;
    }

    /**
     * Método de fábrica para crear una instancia de HabitoDAO.
     *
     * @return una nueva instancia de HabitoDAO.
     */
    public static HabitoDAO build() {
        return new HabitoDAO();
    }
}
