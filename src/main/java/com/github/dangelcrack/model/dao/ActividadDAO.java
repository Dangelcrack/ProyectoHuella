package com.github.dangelcrack.model.dao;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.entity.Actividad;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ActividadDAO {
    public List<Actividad> listar() {
        List<Actividad> actividades = null;
        Transaction tx = null;
        try(Session session = Connection.getInstance().getSession()) {
            tx = session.beginTransaction();
            Query query = session.createQuery("from Actividad");
             actividades= query.list();
            tx.commit();
            return actividades;
        }catch(Exception e) {
            if(tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return actividades;
    }
    public static ActividadDAO build() {
        return new ActividadDAO();
    }
}
