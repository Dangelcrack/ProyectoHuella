package com.github.dangelcrack.model.services;

import com.github.dangelcrack.model.dao.ActividadDAO;
import com.github.dangelcrack.model.entity.Actividad;

import java.util.List;

public class ActividadService {

    private final ActividadDAO actividadDAO;

    public ActividadService() {
        this.actividadDAO = ActividadDAO.build(); // Instanciamos el DAO
    }
    public List<Actividad> listar() {
        return actividadDAO.listar();
    }
}
