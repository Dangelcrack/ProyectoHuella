package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object (DAO) para gestionar las entidades Huella utilizando Hibernate.
 * Proporciona métodos para realizar operaciones CRUD sobre las huellas en la base de datos.
 */
public class HuellaDAO {

    /**
     * Crea o inserta una nueva entidad Huella en la base de datos.
     *
     * @param huella la entidad Huella a crear.
     * @return
     */
    public boolean crearHuella(Huella huella) {
        boolean resultado = false;
        Session session = Connection.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(huella);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
            resultado = true;
        }
        return resultado;
    }

    /**
     * Actualiza una entidad Huella existente en la base de datos.
     *
     * @param huellaActualizada la entidad Huella actualizada.
     * @return true si la entidad fue actualizada correctamente, false si hubo algún error.
     */
    public boolean actualizarHuella(Huella huellaActualizada) {
        boolean actualizado = false;
        Session session = Connection.getInstance().getSession();
        Transaction transaction = session.beginTransaction();
        Huella huella = session.get(Huella.class, huellaActualizada.getId());
        if (huella != null) {
            huella = huellaActualizada;
            session.merge(huella);
            actualizado = true;
        }
        transaction.commit();
        session.close();
        return actualizado;
    }

    /**
     * Elimina una entidad Huella de la base de datos.
     *
     * @param huella la entidad Huella a eliminar.
     * @return true si la entidad fue eliminada correctamente, false si hubo algún error.
     */
    public boolean eliminarHuella(Huella huella) {
        boolean eliminado = false;
        Transaction transaction = null;
        try (Session session = Connection.getInstance().getSession()) {
            transaction = session.beginTransaction();
            Huella huellaExistente = session.get(Huella.class, huella.getId());
            if (huellaExistente != null) {
                session.remove(huellaExistente);
                eliminado = true;
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return eliminado;
    }

    /**
     * Obtiene todas las huellas asociadas con un usuario específico.
     *
     * @param usuario la entidad Usuario que se utiliza para filtrar las huellas.
     * @return una lista de huellas asociadas con el usuario.
     */
    public List<Huella> obtenerHuellasPorUsuario(Usuario usuario) {
        List<Huella> huellas = null;
        try (Session session = Connection.getInstance().getSession()) {
            Query<Huella> query = session.createQuery(
                    "SELECT h FROM Huella h " +
                            "JOIN FETCH h.idActividad " +
                            "JOIN FETCH h.idUsuario " +
                            "WHERE h.idUsuario = :usuario",
                    Huella.class
            );
            query.setParameter("usuario", usuario);
            huellas = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return huellas;
    }

    /**
     * Método de fábrica para crear una instancia de HuellaDAO.
     *
     * @return una nueva instancia de HuellaDAO.
     */
    public static HuellaDAO build() {
        return new HuellaDAO();
    }
}
