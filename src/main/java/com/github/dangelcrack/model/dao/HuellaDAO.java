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
     * @return true si la huella fue creada correctamente, false si hubo algún error.
     */
    public boolean crearHuella(Huella huella) {
        try (Session session = Connection.getInstance().getSession()) {
            Transaction transaction = session.beginTransaction();
            session.save(huella);
            transaction.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualiza una entidad Huella existente en la base de datos.
     *
     * @param huellaActualizada la entidad Huella actualizada.
     * @return true si la entidad fue actualizada correctamente, false si hubo algún error.
     */
    public boolean actualizarHuella(Huella huellaActualizada) {
        try (Session session = Connection.getInstance().getSession()) {
            Transaction transaction = session.beginTransaction();
            Huella huella = session.get(Huella.class, huellaActualizada.getId());
            if (huella != null) {
                session.merge(huellaActualizada);
                transaction.commit();
                return true;
            }
            transaction.commit();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina una entidad Huella de la base de datos.
     *
     * @param huella la entidad Huella a eliminar.
     * @return true si la entidad fue eliminada correctamente, false si hubo algún error.
     */
    public boolean eliminarHuella(Huella huella) {
        try (Session session = Connection.getInstance().getSession()) {
            Transaction transaction = session.beginTransaction();
            Huella huellaExistente = session.get(Huella.class, huella.getId());
            if (huellaExistente != null) {
                session.remove(huellaExistente);
                transaction.commit();
                return true;
            }
            transaction.commit();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todas las huellas asociadas con un usuario específico.
     *
     * @param usuario la entidad Usuario que se utiliza para filtrar las huellas.
     * @return una lista de huellas asociadas con el usuario.
     */
    public List<Huella> obtenerHuellasPorUsuario(Usuario usuario) {
        try (Session session = Connection.getInstance().getSession()) {
            Query<Huella> query = session.createQuery(
                    "SELECT h FROM Huella h " +
                            "JOIN FETCH h.idActividad " +
                            "JOIN FETCH h.idUsuario " +
                            "WHERE h.idUsuario = :usuario", Huella.class
            );
            query.setParameter("usuario", usuario);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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