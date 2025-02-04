package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Categoria;
import org.hibernate.Session;

import java.util.List;

/**
 * Data Access Object (DAO) para la entidad Categoria.
 * Proporciona métodos para interactuar con la base de datos y recuperar información sobre categorías.
 */
public class CategoriaDAO {

    /**
     * Método estático para crear una nueva instancia de CategoriaDAO.
     *
     * @return Una nueva instancia de CategoriaDAO.
     */
    public static CategoriaDAO build() {
        return new CategoriaDAO();
    }

    /**
     * Lista todas las categorías disponibles en la base de datos.
     *
     * @return Una lista de todas las categorías.
     */
    public List<Categoria> listar() {
        // Utiliza la sesión de Hibernate para obtener la lista de categorías
        try (Session session = Connection.getInstance().getSession()) {
            // Ejecuta una consulta HQL para obtener todas las categorías
            return session.createQuery("FROM Categoria", Categoria.class).getResultList();
        }
    }
}
