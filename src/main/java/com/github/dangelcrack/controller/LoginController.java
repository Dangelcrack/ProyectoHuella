package com.github.dangelcrack.controller;

import com.github.dangelcrack.App;
import com.github.dangelcrack.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.stage.Stage;

import com.github.dangelcrack.model.services.UsuarioService;
import com.github.dangelcrack.model.entity.Usuario;
import com.github.dangelcrack.model.entity.Scenes;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la pantalla de inicio de sesión.
 */
public class LoginController extends Controller implements Initializable {

    /** Campo de texto para ingresar el nombre de usuario */
    @FXML
    private TextField usernameField;

    /** Campo de texto para ingresar la contraseña */
    @FXML
    private PasswordField passwordField;

    /** Botón para iniciar sesión */
    @FXML
    private Button loginButton;

    /** Botón para registrarse */
    @FXML
    private Button registerButton;

    /** Servicio de usuario para gestionar las credenciales */
    private final UsuarioService usuarioService;

    /**
     * Constructor de LoginController.
     * Inicializa el servicio de usuario.
     */
    public LoginController() {
        this.usuarioService = new UsuarioService();
    }

    /**
     * Método inicializado cuando se carga el controlador.
     * En este caso no se realiza ninguna acción al inicio.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * Maneja el evento de inicio de sesión.
     * Verifica las credenciales y cambia a la siguiente pantalla si son válidas.
     */
    @FXML
    private void handleLogin() {
        // Obtiene los datos de los campos de usuario y contraseña
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Verifica que los campos no estén vacíos
        if (username.isEmpty() || password.isEmpty()) {
            Utils.showAlert("Error", "Por favor, ingrese su nombre de usuario y contraseña.", Alert.AlertType.ERROR);
            return;
        }

        // Verifica las credenciales del usuario
        Usuario usuario = usuarioService.obtenerUsuarioPorCredenciales(username, password);
        if (usuario != null) {
            try {
                // Si las credenciales son válidas, cambia a la pantalla principal
                App.changeToMainScene(usuario);
            } catch (IOException e) {
                // Si ocurre un error al cambiar de pantalla, muestra una alerta
                Utils.showAlert("Error", "No se pudo cargar la siguiente pantalla.", Alert.AlertType.ERROR);
            }
        } else {
            // Si las credenciales son incorrectas, muestra una alerta
            Utils.showAlert("Error", "Credenciales inválidas. Por favor, intente de nuevo.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Maneja el evento de registro de un nuevo usuario.
     * Cambia a la pantalla de registro.
     */
    @FXML
    private void handleRegister() throws IOException {
        // Navega a la pantalla de registro
        navigateToScene(Scenes.REGISTER);
    }

    /**
     * Navega a una escena específica.
     *
     * @param scene La escena a la que se desea navegar.
     * @throws IOException Si ocurre un error al cargar la escena.
     */
    private void navigateToScene(Scenes scene) throws IOException {
        // Carga el archivo FXML de la escena
        FXMLLoader loader = new FXMLLoader(getClass().getResource(scene.getURL()));
        Parent root = loader.load();
        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Método que se ejecuta al abrir la ventana de este controlador.
     *
     * @param usuario El usuario que se pasa al abrir la ventana.
     * @param input Entrada adicional (no utilizada en este caso).
     * @throws IOException Si ocurre un error al abrir la ventana.
     */
    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
    }

    /**
     * Método que se ejecuta al cerrar la ventana de este controlador.
     *
     * @param output Salida adicional (no utilizada en este caso).
     */
    @Override
    public void onClose(Object output) {
    }
}
