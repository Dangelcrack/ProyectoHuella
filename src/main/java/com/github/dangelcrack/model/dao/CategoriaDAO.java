package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Actividad;
import com.github.dangelcrack.model.entity.Categoria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class CategoriaDAO {
    public static CategoriaDAO build() {
        return new CategoriaDAO();
    }

    public List<Categoria> listar() {
        try (Session session = Connection.getInstance().getSession()) {
            return session.createQuery("FROM Categoria", Categoria.class).getResultList();
        }
    }
}
