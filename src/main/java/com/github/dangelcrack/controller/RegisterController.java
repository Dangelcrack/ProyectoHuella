package com.github.dangelcrack.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import com.github.dangelcrack.model.dao.UsuarioDAO;
import com.github.dangelcrack.model.entity.Usuario;
import com.github.dangelcrack.model.entity.Scenes;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ResourceBundle;

public class RegisterController extends Controller implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button loginButton; // Botón para ir a la vista de login

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Estilo para el botón de login al pasar el ratón
        loginButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            loginButton.setStyle("""
                    -fx-font-size: 14px; -fx-background-color: #005299; -fx-text-fill: white;
                    -fx-padding: 10 20; -fx-background-radius: 20; -fx-border-radius: 20;
                    -fx-cursor: hand; -fx-pref-width: 150; -fx-pref-height: 40;""");
        });

        loginButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            loginButton.setStyle("""
                    -fx-font-size: 14px; -fx-text-fill: white;
                    -fx-padding: 10 20; -fx-background-radius: 20; -fx-border-radius: 20;
                    -fx-cursor: hand; -fx-background-color: #1e90ff; -fx-pref-width: 150; -fx-pref-height: 40;""");
        });

        // Estilo para el botón de registro al pasar el ratón
        registerButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            registerButton.setStyle("""
                    -fx-background-color: #ff3b8d; -fx-font-size: 14px; -fx-text-fill: white;
                    -fx-padding: 10 20; -fx-background-radius: 20; -fx-border-radius: 20;
                    -fx-cursor: hand; -fx-pref-width: 150; -fx-pref-height: 40;""");
        });

        registerButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            registerButton.setStyle("""
                    -fx-background-color: #ff69b4; -fx-font-size: 14px; -fx-text-fill: white;
                    -fx-padding: 10 20; -fx-background-radius: 20; -fx-border-radius: 20;
                    -fx-cursor: hand; -fx-pref-width: 150; -fx-pref-height: 40;""");
        });
    }


    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        // Verificar que los campos no estén vacíos
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        // Verificar si las contraseñas coinciden
        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Las contraseñas no coinciden.", Alert.AlertType.ERROR);
            return;
        }

        // Verificar si el nombre de usuario ya está en uso
        if (usuarioDAO.leeUsuario(username) != null) {
            showAlert("Error", "El nombre de usuario ya está en uso.", Alert.AlertType.ERROR);
            return;
        }

        // Crear un nuevo usuario y guardarlo en la base de datos
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(username);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContraseña(password);
        nuevoUsuario.setFechaRegistro(Instant.now());

        if (usuarioDAO.creaUsuario(nuevoUsuario)) {
            showAlert("Éxito", "Registro exitoso. Ahora puede iniciar sesión.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Error", "No se pudo crear el usuario. Intente nuevamente.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogin() throws IOException {
        loadScene(Scenes.LOGIN);
    }

    private void loadScene(Scenes scene) throws IOException {
        // Usar el enum para obtener la URL de la escena
        FXMLLoader loader = new FXMLLoader(getClass().getResource(scene.getURL()));
        Parent root = loader.load();
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));

        // Mostrar la nueva escena
        stage.show();
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void onOpen(Object input) throws IOException {

    }

    @Override
    public void onClose(Object output) {

    }
}
