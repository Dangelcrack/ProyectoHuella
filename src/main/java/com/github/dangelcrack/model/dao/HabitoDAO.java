package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Habito;
import com.github.dangelcrack.model.entity.HabitoId;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object (DAO) for managing Habito entities using Hibernate.
 */
public class HabitoDAO extends Habito{

    /**
     * Creates or inserts a new Habito entity in the database.
     *
     * @param habito the Habito entity to create.
     * @return true if the entity was successfully created, false otherwise.
     */
    public boolean crearHabito(Habito habito) {
        if (habito == null || leerHabito(habito.getId()) != null) {
            return false;
        }

        boolean creado = false;
        Transaction transaction = null;
        try (Session session = Connection.getInstance().getSession()) {
            transaction = session.beginTransaction();
            session.persist(habito);
            transaction.commit();
            creado = true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return creado;
    }

    /**
     * Reads a specific Habito entity by its ID.
     *
     * @param id the composite ID of the Habito to read.
     * @return the Habito entity if found, or null if not found.
     */
    public Habito leerHabito(HabitoId id) {
        Habito habito = null;
        try (Session session = Connection.getInstance().getSession()) {
            habito = session.get(Habito.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return habito;
    }

    /**
     * Reads all Habito entities from the database.
     *
     * @return a list of all Habito entities.
     */
    public List<Habito> leerTodosHabitos() {
        List<Habito> habitos = null;
        try (Session session = Connection.getInstance().getSession()) {
            Query<Habito> query = session.createQuery("FROM Habito", Habito.class);
            habitos = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return habitos;
    }

    /**
     * Updates an existing Habito entity in the database.
     *
     * @param habitoActualizado the updated Habito entity.
     * @return true if the entity was successfully updated, false otherwise.
     */
    public boolean actualizarHabito(Habito habitoActualizado) {
        boolean actualizado = false;
        Transaction transaction = null;
        try (Session session = Connection.getInstance().getSession()) {
            transaction = session.beginTransaction();
            Habito habito = session.get(Habito.class, habitoActualizado.getId());
            if (habito != null) {
                session.merge(habitoActualizado);
                actualizado = true;
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return actualizado;
    }

    /**
     * Deletes a specific Habito entity from the database.
     *
     * @param id the composite ID of the Habito entity to delete.
     * @return true if the entity was successfully deleted, false otherwise.
     */
    public boolean eliminarHabito(HabitoId id) {
        boolean eliminado = false;
        Transaction transaction = null;
        try (Session session = Connection.getInstance().getSession()) {
            transaction = session.beginTransaction();
            Habito habito = session.get(Habito.class, id);
            if (habito != null) {
                session.remove(habito);
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
     * @param idUsuario the ID of the Usuario entity.
     * @return a list of Habito entities linked to the Usuario.
     */
    public List<Habito> obtenerHabitosPorUsuario(Integer idUsuario) {
        List<Habito> habitos = null;
        try (Session session = Connection.getInstance().getSession()) {
            Query<Habito> query = session.createQuery("FROM Habito WHERE id.idUsuario = :idUsuario", Habito.class);
            query.setParameter("idUsuario", idUsuario);
            habitos = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return habitos;
    }
}
