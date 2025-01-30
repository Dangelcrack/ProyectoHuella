package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Habito;
import com.github.dangelcrack.model.entity.HabitoId;
import com.github.dangelcrack.model.entity.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object (DAO) for managing Habito entities using Hibernate.
 */
public class HabitoDAO {

    /**
     * Creates or inserts a new Habito entity in the database.
     * If the Habito already exists, it will not be duplicated.
     *
     * @param habito the Habito entity to create.
     * @return true if the entity was successfully created, false otherwise.
     */
    public void crearHabito(Habito habito) {
        Session session = Connection.getInstance().getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.save(habito);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }


    /**
     * Updates an existing Habito entity in the database.
     *
     * @param habitoActualizado the updated Habito entity.
     * @return true if the entity was successfully updated, false otherwise.
     */
    public boolean actualizarHabito(Habito habitoActualizado) {
        boolean actualizado = false;
        Session session = Connection.getInstance().getSession();
        Transaction transaction =session.beginTransaction();
        Habito habito = session.get(Habito.class, habitoActualizado.getId());
        if (habito != null) {
            habito = habitoActualizado;
            session.merge(habito);
            actualizado = true;
        }
        transaction.commit();
        session.close();
        return actualizado;
    }

    /**
     * Deletes a specific Habito entity from the database.
     *
     * @param habito the Habito entity to delete.
     * @return true if the entity was successfully deleted, false otherwise.
     */
    public boolean eliminarHabito(Habito habito) {
        boolean eliminado = false;
        Transaction transaction = null;
        try (Session session = Connection.getInstance().getSession()) {
            transaction = session.beginTransaction();
            Habito habitoExistente = session.get(Habito.class, habito.getId());
            if (habitoExistente != null) {
                session.remove(habitoExistente);
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
     * Finds all Habito entities associated with a specific Usuario.
     *
     * @param usuario the Usuario entity.
     * @return a list of Habito entities linked to the Usuario.
     */
    public List<Habito> obtenerHabitosPorUsuario(Usuario usuario) {
        List<Habito> habitos = null;
        try (Session session = Connection.getInstance().getSession()) {
            Query<Habito> query = session.createQuery("FROM Habito h JOIN FETCH h.idActividad WHERE h.idUsuario = :usuario", Habito.class);
            query.setParameter("usuario", usuario);
            habitos = query.getResultList();
            for (Habito habito : habitos) {
                if (habito.getIdActividad() != null) {
                    habito.getIdActividad().getNombre(); // Initialize lazy-loaded data if necessary
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return habitos;
    }

    /**
     * Factory method to create an instance of HabitoDAO.
     *
     * @return a new instance of HabitoDAO.
     */
    public static HabitoDAO build() {
        return new HabitoDAO();
    }
}
