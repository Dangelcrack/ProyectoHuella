package com.github.dangelcrack.model.services;

import com.github.dangelcrack.model.dao.HuellaDAO;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Usuario;

import java.util.List;

/**
 * Clase de servicio que gestiona la lógica de negocio de las huellas.
 */
public class HuellaService {

    private final HuellaDAO huellaDAO;

    public HuellaService() {
        this.huellaDAO = new HuellaDAO(); // Instanciamos el DAO
    }

    /**
     * Verifica si una huella ya existe en la base de datos por su ID y, en caso negativo, la crea.
     *
     * @param huella la huella a insertar.
     * @return true si la huella fue creada exitosamente, false si ya existe.
     */
    public boolean guardarHuella(Huella huella) {
        if (huella.getId() != null && obtenerHuellaPorId(huella.getId()) != null) {
            return false;
        }

        // Si no existe, la creamos
        return huellaDAO.creaHuella(huella);
    }

    /**
     * Actualiza una huella existente en la base de datos.
     *
     * @param huella la huella con los datos actualizados.
     * @return true si la huella fue actualizada exitosamente, false si no fue encontrada.
     */
    public boolean actualizarHuella(Huella huella) {
        // Validamos si la huella existe antes de actualizar
        if (huella.getId() == null || obtenerHuellaPorId(huella.getId()) == null) {
            return false;
        }

        return huellaDAO.actualizarHuella(huella);
    }

    /**
     * Elimina una huella de la base de datos.
     *
     * @param huella la huella a eliminar.
     * @return true si la huella fue eliminada exitosamente, false si no fue encontrada.
     */
    public boolean eliminarHuella(Huella huella) {
        return huellaDAO.eliminarHuella(huella);
    }

    /**
     * Recupera todas las huellas de un usuario específico.
     *
     * @param usuario el usuario cuyo listado de huellas se desea recuperar.
     * @return la lista de huellas asociadas a ese usuario.
     */
    public List<Huella> obtenerHuellasPorUsuario(Usuario usuario) {
        return huellaDAO.obtenerHuellasPorUsuario(usuario);
    }

    /**
     * Obtiene una huella por su ID.
     *
     * @param id el ID de la huella a recuperar.
     * @return la huella correspondiente al ID o null si no se encuentra.
     */
    public Huella obtenerHuellaPorId(Integer id) {
        List<Huella> huellas = huellaDAO.leeHuellas();
        for (Huella huella : huellas) {
            if (huella.getId().equals(id)) {
                return huella;
            }
        }
        return null;
    }
}
