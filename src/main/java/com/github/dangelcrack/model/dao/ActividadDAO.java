package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Actividad;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ActividadDAO {
    public static ActividadDAO build() {
        return new ActividadDAO();
    }

    public List<Actividad> listar() {
        try (Session session = Connection.getInstance().getSession()) {
            return session.createQuery("FROM Actividad", Actividad.class).getResultList();
        }
    }

}
