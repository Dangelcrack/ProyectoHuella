package com.github.dangelcrack.model.services;

import com.github.dangelcrack.model.dao.CategoriaDAO;
import com.github.dangelcrack.model.entity.Categoria;

import java.util.List;

public class CategoriaService {

    private final CategoriaDAO categoriaDAO;

    public CategoriaService() {
        this.categoriaDAO = CategoriaDAO.build(); // Instanciamos el DAO
    }

    // Método para obtener todas las categorías
    public List<Categoria> listar() {
        return categoriaDAO.listar();
    }
}
