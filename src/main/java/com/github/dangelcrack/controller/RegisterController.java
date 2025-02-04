package com.github.dangelcrack.controller;

import com.github.dangelcrack.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import com.github.dangelcrack.model.services.UsuarioService;
import com.github.dangelcrack.model.entity.Usuario;
import com.github.dangelcrack.model.entity.Scenes;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ResourceBundle;

/**
 * Controlador para la pantalla de registro de usuarios.
 * Permite a los usuarios registrarse proporcionando un nombre de usuario, correo electrónico y contraseña.
 */
public class RegisterController extends Controller implements Initializable {

    /** Campo de texto para el nombre de usuario */
    @FXML
    private TextField usernameField;

    /** Campo de texto para el correo electrónico */
    @FXML
    private TextField emailField;

    /** Campo de texto para la contraseña */
    @FXML
    private PasswordField passwordField;

    /** Campo de texto para la confirmación de la contraseña */
    @FXML
    private PasswordField confirmPasswordField;

    /** Botón para realizar el registro */
    @FXML
    private Button registerButton;

    /** Botón para ir a la pantalla de inicio de sesión */
    @FXML
    private Button loginButton;

    /** Servicio que maneja las operaciones de usuario */
    private final UsuarioService usuarioService;

    /**
     * Constructor de RegisterController.
     * Inicializa el servicio de usuario.
     */
    public RegisterController() {
        this.usuarioService = new UsuarioService();
    }

    /**
     * Método que se ejecuta al cargar la ventana de registro.
     * Este método no realiza ninguna acción al inicio.
     *
     * @param url La URL del archivo FXML (no utilizada aquí).
     * @param resourceBundle El recurso de idioma (no utilizado aquí).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    /**
     * Maneja el evento de registro de un nuevo usuario.
     * Valida los campos de entrada, y si son correctos, guarda al nuevo usuario.
     */
    @FXML
    private void handleRegister() {
        // Obtiene los valores de los campos de texto
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Verifica que todos los campos estén completos
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Utils.showAlert("Error", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        // Verifica que las contraseñas coincidan
        if (!password.equals(confirmPassword)) {
            Utils.showAlert("Error", "Las contraseñas no coinciden.", Alert.AlertType.ERROR);
            return;
        }

        // Verifica si el nombre de usuario ya está en uso
        if (usuarioService.obtenerUsuario(username) != null) {
            Utils.showAlert("Error", "El nombre de usuario ya está en uso.", Alert.AlertType.ERROR);
            return;
        }

        // Crea un nuevo objeto Usuario con los datos ingresados
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(username);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContraseña(password);
        nuevoUsuario.setFechaRegistro(Instant.now());

        // Intenta guardar el nuevo usuario
        if (usuarioService.guardarUsuario(nuevoUsuario)) {
            // Si el registro es exitoso, muestra una alerta de éxito
            Utils.showAlert("Éxito", "Registro exitoso. Ahora puede iniciar sesión.", Alert.AlertType.INFORMATION);
        } else {
            // Si ocurre un error, muestra una alerta de error
            Utils.showAlert("Error", "No se pudo crear el usuario. Intente nuevamente.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Maneja el evento para redirigir al usuario a la pantalla de inicio de sesión.
     *
     * @throws IOException Si ocurre un error al cargar la escena.
     */
    @FXML
    private void handleLogin() throws IOException {
        // Carga la escena de inicio de sesión
        loadScene(Scenes.LOGIN);
    }

    /**
     * Carga la escena indicada por el parámetro.
     *
     * @param scene La escena que se desea cargar.
     * @throws IOException Si ocurre un error al cargar la escena.
     */
    private void loadScene(Scenes scene) throws IOException {
        // Carga el archivo FXML correspondiente a la escena
        FXMLLoader loader = new FXMLLoader(getClass().getResource(scene.getURL()));
        Parent root = loader.load();

        // Obtiene el escenario actual y cambia la escena
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Método que se ejecuta al abrir la ventana de este controlador.
     *
     * @param usuario El usuario que abre la ventana (no utilizado en este caso).
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
