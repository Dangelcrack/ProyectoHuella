package com.github.dangelcrack.model.services;

import com.github.dangelcrack.model.dao.HabitoDAO;
import com.github.dangelcrack.model.entity.Habito;
import com.github.dangelcrack.model.entity.Usuario;

import java.util.List;

/**
 * Clase de servicio que gestiona la lógica de negocio de los hábitos.
 */
public class HabitoService {

    private final HabitoDAO habitoDAO;

    public HabitoService() {
        this.habitoDAO = HabitoDAO.build(); // Instanciamos el DAO
    }

    /**
     * Guarda un hábito en la base de datos.
     *
     * @param habito el hábito a insertar.
     * @return
     */
    public boolean guardarHabito(Habito habito) {
        boolean result = false;
        if(habitoDAO.crearHabito(habito)){
            result = true;
        }
        return result;
    }

    /**
     * Actualiza un hábito existente en la base de datos.
     *
     * @param habito el hábito con los datos actualizados.
     * @return true si el hábito fue actualizado exitosamente, false si no fue encontrado.
     */
    public boolean actualizarHabito(Habito habito) {
        return habitoDAO.actualizarHabito(habito);
    }

    /**
     * Elimina un hábito de la base de datos.
     *
     * @param habito el hábito a eliminar.
     * @return true si el hábito fue eliminado exitosamente, false si no fue encontrado.
     */
    public boolean eliminarHabito(Habito habito) {
        return habitoDAO.eliminarHabito(habito);
    }

    /**
     * Recupera todos los hábitos de un usuario específico.
     *
     * @param usuario el usuario cuyo listado de hábitos se desea recuperar.
     * @return la lista de hábitos asociados a ese usuario.
     */
    public List<Habito> obtenerHabitosPorUsuario(Usuario usuario) {
        return habitoDAO.obtenerHabitosPorUsuario(usuario);
    }
}
