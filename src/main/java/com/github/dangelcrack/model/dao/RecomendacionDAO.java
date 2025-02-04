package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Categoria;
import com.github.dangelcrack.model.entity.Recomendacion;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Recomendacion.
 * Proporciona métodos para interactuar con la base de datos y realizar operaciones sobre las recomendaciones.
 */
public class RecomendacionDAO {

    /**
     * Filtra las recomendaciones en base al ID de una unidad de la categoría.
     * Utiliza una consulta HQL (Hibernate Query Language) para obtener las recomendaciones asociadas a la unidad de la categoría.
     *
     * @param idUnidad el ID de la unidad de la categoría para filtrar las recomendaciones.
     * @return una lista de recomendaciones filtradas.
     */
    public List<Recomendacion> filtrarPorHuella(int idUnidad) {
        List<Recomendacion> recomendacionesFiltradas = null;
        Transaction tx = null;
        try (Session session = Connection.getInstance().getSession()) {
            tx = session.beginTransaction();
            String hql = "SELECT r FROM Recomendacion r JOIN r.idCategoria c WHERE c.unidad = (SELECT unidad FROM Categoria WHERE id = :idUnidad)";
            Query<Recomendacion> query = session.createQuery(hql, Recomendacion.class);
            query.setParameter("idUnidad", idUnidad);
            recomendacionesFiltradas = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return recomendacionesFiltradas;
    }

    /**
     * Método de fábrica para crear una instancia de RecomendacionDAO.
     *
     * @return una nueva instancia de RecomendacionDAO.
     */
    public static RecomendacionDAO build() {
        return new RecomendacionDAO();
    }
}
