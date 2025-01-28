package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Actividad;
import com.github.dangelcrack.model.entity.Categoria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class CategoriaDAO {
    public List<Categoria> listar() {
        List<Categoria> categorias = null;
        Transaction tx = null;
        try(Session session = Connection.getInstance().getSession()) {
            tx = session.beginTransaction();
            Query query = session.createQuery("from Categoria");
            categorias= query.list();
            tx.commit();
            return categorias;
        }catch(Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return categorias;
    }
    public static CategoriaDAO build() {
        return new CategoriaDAO();
    }
}
