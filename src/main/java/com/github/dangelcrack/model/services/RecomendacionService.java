package com.github.dangelcrack.model.services;

import com.github.dangelcrack.model.dao.RecomendacionDAO;
import com.github.dangelcrack.model.entity.Recomendacion;

import java.util.List;

/**
 * Clase de servicio que gestiona la lógica de negocio relacionada con las recomendaciones.
 */
public class RecomendacionService {

    private final RecomendacionDAO recomendacionDAO;

    public RecomendacionService() {
        this.recomendacionDAO = RecomendacionDAO.build(); // Instanciamos el DAO usando el método de fábrica
    }

    /**
     * Filtra las recomendaciones en base al ID de una unidad de la categoría.
     *
     * @param idUnidad el ID de la unidad de la categoría para filtrar las recomendaciones.
     * @return una lista de recomendaciones filtradas.
     */
    public List<Recomendacion> filtrarPorHuella(int idUnidad) {
        return recomendacionDAO.filtrarPorHuella(idUnidad);
    }

    /**
     * Obtiene las recomendaciones asociadas a una lista de unidades.
     *
     * @param unidades la lista de unidades para las cuales se desean obtener las recomendaciones.
     * @return una lista de recomendaciones asociadas a las unidades proporcionadas.
     */
    public List<Recomendacion> obtenerRecomendacionesPorUnidades(List<String> unidades) {
        return recomendacionDAO.obtenerRecomendacionesPorUnidades(unidades);
    }
}