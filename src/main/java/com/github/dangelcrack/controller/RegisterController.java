package com.github.dangelcrack.controller;

import com.github.dangelcrack.utils.Utils;
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
    private Button loginButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Utils.showAlert("Error", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }
        if (!password.equals(confirmPassword)) {
            Utils.showAlert("Error", "Las contraseñas no coinciden.", Alert.AlertType.ERROR);
            return;
        }

        if (UsuarioDAO.build().leeUsuario(username) != null) {
            Utils.showAlert("Error", "El nombre de usuario ya está en uso.", Alert.AlertType.ERROR);
            return;
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(username);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setContraseña(password);
        nuevoUsuario.setFechaRegistro(Instant.now());

        if (UsuarioDAO.build().creaUsuario(nuevoUsuario)) {
            Utils.showAlert("Éxito", "Registro exitoso. Ahora puede iniciar sesión.", Alert.AlertType.INFORMATION);
        } else {
            Utils.showAlert("Error", "No se pudo crear el usuario. Intente nuevamente.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleLogin() throws IOException {
        loadScene(Scenes.LOGIN);
    }

    private void loadScene(Scenes scene) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(scene.getURL()));
        Parent root = loader.load();
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void onOpen(Usuario usuario,Object input) throws IOException {

    }

    @Override
    public void onClose(Object output) {

    }
}
