package com.github.dangelcrack.controller;

import com.github.dangelcrack.App;
import com.github.dangelcrack.model.services.UsuarioService;
import com.github.dangelcrack.model.entity.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import com.github.dangelcrack.utils.Utils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la pantalla de gestión de usuario.
 * Permite a los usuarios actualizar su nombre, correo electrónico y contraseña.
 */
public class UsuarioController extends Controller implements Initializable {

    /** El usuario que está gestionando sus datos */
    private Usuario usuario;

    /** Servicio que maneja las operaciones de usuario */
    private final UsuarioService usuarioService;

    /** Campo de texto para el nombre del usuario */
    @FXML
    private TextField nombreField;

    /** Campo de texto para el correo electrónico del usuario */
    @FXML
    private TextField emailField;

    /** Campo de texto para la contraseña del usuario */
    @FXML
    private PasswordField contraseñaField;

    /** Botón para guardar los cambios del usuario */
    @FXML
    private Button guardarButton;

    /** Botón para volver al login */
    @FXML
    private Button backtologin;

    /**
     * Constructor de UsuarioController.
     * Inicializa el servicio de usuario.
     */
    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }

    /**
     * Método que se ejecuta al cargar la ventana de usuario.
     * Rellena los campos de texto con los datos del usuario.
     *
     * @param url La URL del archivo FXML (no utilizada aquí).
     * @param resourceBundle El recurso de idioma (no utilizado aquí).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * Método que se ejecuta cuando se abre la ventana del controlador.
     * Rellena los campos con la información del usuario pasado como parámetro.
     *
     * @param usuario El usuario que será gestionado.
     * @param input Entrada adicional (no utilizada en este caso).
     * @throws IOException Si ocurre un error al abrir la ventana.
     */
    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        this.usuario = usuario;
        // Rellena los campos de texto con los datos del usuario
        nombreField.setText(usuario.getNombre());
        emailField.setText(usuario.getEmail());
    }

    /**
     * Método que se ejecuta cuando se cierra la ventana del controlador.
     *
     * @param output Salida adicional (no utilizada en este caso).
     */
    @Override
    public void onClose(Object output) {
    }

    /**
     * Maneja el evento de guardar los cambios del usuario.
     * Actualiza el nombre, correo electrónico y contraseña del usuario.
     * Muestra una alerta si la operación es exitosa o si ocurre un error.
     */
    @FXML
    private void handleGuardar() {
        // Actualiza los datos del usuario con los valores de los campos de texto
        usuario.setNombre(nombreField.getText());
        usuario.setEmail(emailField.getText());
        usuario.setContraseña(contraseñaField.getText());

        // Intenta actualizar el usuario en la base de datos
        if (usuarioService.actualizarUsuario(usuario)) {
            // Si la actualización es exitosa, muestra una alerta de éxito
            Utils.showAlert("Éxito", "Usuario actualizado correctamente.", Alert.AlertType.INFORMATION);
        } else {
            // Si ocurre un error, muestra una alerta de error
            Utils.showAlert("Error", "Algo ha fallado", Alert.AlertType.ERROR);
        }
    }

    /**
     * Maneja el evento de volver a la pantalla de inicio de sesión.
     *
     * @throws IOException Si ocurre un error al cambiar de escena.
     */
    @FXML
    private void backtologin() throws IOException {
        App.changeToLoginScene();
    }
}
