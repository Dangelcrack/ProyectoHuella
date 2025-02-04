package com.github.dangelcrack.model.services;

import com.github.dangelcrack.model.dao.CategoriaDAO;
import com.github.dangelcrack.model.entity.Categoria;

import java.util.List;

/**
 * Servicio para manejar la lógica de negocio relacionada con Categorías.
 */
public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    /**
     * Constructor que inicializa el DAO de Categoría.
     */
    public CategoriaService() {
        this.categoriaDAO = CategoriaDAO.build(); // Instanciamos el DAO
    }

    /**
     * Lista todas las categorías disponibles en la base de datos.
     *
     * @return una lista de categorías.
     */
    public List<Categoria> listar() {
        return categoriaDAO.listar();
    }
}
