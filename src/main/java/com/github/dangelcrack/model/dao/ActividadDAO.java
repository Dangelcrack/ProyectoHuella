package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Actividad;
import org.hibernate.Session;
import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Actividad.
 * Proporciona métodos para interactuar con la base de datos y recuperar información sobre actividades.
 */
public class ActividadDAO {

    /**
     * Método estático para crear una nueva instancia de ActividadDAO.
     *
     * @return Una nueva instancia de ActividadDAO.
     */
    public static ActividadDAO build() {
        return new ActividadDAO();
    }

    /**
     * Lista todas las actividades disponibles en la base de datos.
     *
     * @return Una lista de todas las actividades.
     */
    public List<Actividad> listar() {
        // Utiliza la sesión de Hibernate para obtener la lista de actividades
        try (Session session = Connection.getInstance().getSession()) {
            // Ejecuta una consulta HQL para obtener todas las actividades
            return session.createQuery("FROM Actividad", Actividad.class).getResultList();
        }
    }
}
