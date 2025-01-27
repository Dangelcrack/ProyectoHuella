package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Usuario;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object (DAO) for managing Huella entities using Hibernate.
 */
public class HuellaDAO extends Huella{

    /**
     * Creates or inserts a new Huella entity in the database.
     * If the Huella already exists, it will not be duplicated.
     *
     * @param huella the Huella entity to create.
     * @return true if the entity was successfully created, false otherwise.
     */
    public boolean creaHuella(Huella huella) {
        if (huella == null || leeHuella(huella.getId()) != null) {
            return false;
        }

        boolean creado = false;
        Transaction transaction = null;
        try (Session session = Connection.getInstance().getSession()) {
            transaction = session.beginTransaction();
            session.persist(huella);
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
     * Reads a specific Huella entity by its ID.
     *
     * @param id the ID of the Huella to read.
     * @return the Huella entity if found, or null if not found.
     */
    public Huella leeHuella(Integer id) {
        Huella huella = null;
        try (Session session = Connection.getInstance().getSession()) {
            huella = session.get(Huella.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return huella;
    }

    /**
     * Reads all Huella entities from the database.
     *
     * @return a list of all Huella entities.
     */
    public List<Huella> leerTodasHuellas() {
        List<Huella> huellas = null;
        try (Session session = Connection.getInstance().getSession()) {
            Query<Huella> query = session.createQuery("FROM Huella", Huella.class);
            huellas = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return huellas;
    }

    /**
     * Updates an existing Huella entity in the database.
     *
     * @param huellaActualizada the updated Huella entity.
     * @return true if the entity was successfully updated, false otherwise.
     */
    public boolean actualizarHuella(Huella huellaActualizada) {
        boolean actualizado = false;
        Transaction transaction = null;
        try (Session session = Connection.getInstance().getSession()) {
            transaction = session.beginTransaction();
            Huella huella = session.get(Huella.class, huellaActualizada.getId());
            if (huella != null) {
                session.merge(huellaActualizada);
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
     * Deletes a specific Huella entity from the database.
     *
     * @param huella the Huella entity to delete.
     * @return true if the entity was successfully deleted, false otherwise.
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
     * Finds all Huella entities associated with a specific Usuario.
     *
     * @param usuario the Usuario entity.
     * @return a list of Huella entities linked to the Usuario.
     */
    public List<Huella> obtenerHuellasPorUsuario(Usuario usuario) {
        List<Huella> huellas = null;
        try (Session session = Connection.getInstance().getSession()) {
            Query<Huella> query = session.createQuery("FROM Huella WHERE Usuario = :usuario", Huella.class);
            query.setParameter("usuario", usuario);
            huellas = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return huellas;
    }
}
