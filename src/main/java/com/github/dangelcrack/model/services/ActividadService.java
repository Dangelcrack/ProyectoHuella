package com.github.dangelcrack.model.services;

import com.github.dangelcrack.model.dao.ActividadDAO;
import com.github.dangelcrack.model.entity.Actividad;

import java.util.List;

/**
 * Servicio para manejar la lógica de negocio relacionada con Actividades.
 */
public class ActividadService {

    private final ActividadDAO actividadDAO;

    /**
     * Constructor que inicializa el DAO de Actividad.
     */
    public ActividadService() {
        this.actividadDAO = ActividadDAO.build(); // Instanciamos el DAO
    }

    /**
     * Lista todas las actividades disponibles en la base de datos.
     *
     * @return una lista de actividades.
     */
    public List<Actividad> listar() {
        return actividadDAO.listar();
    }
}
