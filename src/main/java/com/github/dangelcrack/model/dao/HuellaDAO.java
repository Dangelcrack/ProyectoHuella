package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Huella.
 */
public class HuellaDAO {

    // Método privado para manejar la transacción
    private <T> T ejecutarTransaccion(TransaccionOperacion<T> operacion) {
        Transaction transaction = null;
        T resultado = null;
        try (Session session = Connection.getInstance().getSession()) {
            transaction = session.beginTransaction();
            resultado = operacion.ejecutar(session);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return resultado;
    }

    // Método para crear una huella
    public boolean creaHuella(Huella huella) {
        return ejecutarTransaccion(session -> {
            session.persist(huella);
            return true;
        });
    }

    // Método para actualizar una huella
    public boolean actualizarHuella(Huella huellaActualizada) {
        return ejecutarTransaccion(session -> {
            Huella huella = session.get(Huella.class, huellaActualizada.getId());
            if (huella != null) {
                session.merge(huellaActualizada);
                return true;
            }
            return false;
        });
    }

    // Método para eliminar una huella
    public boolean eliminarHuella(Huella huella) {
        return ejecutarTransaccion(session -> {
            Huella huellaExistente = session.get(Huella.class, huella.getId());
            if (huellaExistente != null) {
                session.remove(huellaExistente);
                return true;
            }
            return false;
        });
    }

    // Método para obtener todas las huellas
    public List<Huella> leeHuellas() {
        List<Huella> huellas = null;
        try (Session session = Connection.getInstance().getSession()) {
            Query<Huella> query = session.createQuery("FROM Huella", Huella.class);
            huellas = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return huellas;
    }

    // Método para obtener las huellas por usuario
    public List<Huella> obtenerHuellasPorUsuario(Usuario usuario) {
        List<Huella> huellas = null;
        try (Session session = Connection.getInstance().getSession()) {
            Query<Huella> query = session.createQuery("SELECT h FROM Huella h LEFT JOIN FETCH h.idActividad WHERE h.idUsuario = :usuario", Huella.class);
            query.setParameter("usuario", usuario);
            huellas = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return huellas;
    }
    public static HuellaDAO build(){
        return new HuellaDAO();
    }
}

