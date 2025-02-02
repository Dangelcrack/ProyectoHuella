package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Categoria;
import com.github.dangelcrack.model.entity.Recomendacion;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class RecomendacionDAO {

    public List<Recomendacion> listar() {
        List<Recomendacion> recomendaciones = null;
        Transaction tx = null;
        try (Session session = Connection.getInstance().getSession()) {
            tx = session.beginTransaction();
            Query<Recomendacion> query = session.createQuery("FROM Recomendacion", Recomendacion.class);
            recomendaciones = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return recomendaciones;
    }

    public List<Recomendacion> filtrarPorHuella(int idUnidad) {
        List<Recomendacion> recomendacionesFiltradas = null;
        Transaction tx = null;
        try (Session session = Connection.getInstance().getSession()) {
            tx = session.beginTransaction();

            String hql = """
                    SELECT r
                    FROM Recomendacion r
                    JOIN r.idCategoria c
                    WHERE c.unidad = (
                        SELECT unidad
                        FROM Categoria
                        WHERE id = :idUnidad
                    )
                """;

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

    public static RecomendacionDAO build() {
        return new RecomendacionDAO();
    }
}
