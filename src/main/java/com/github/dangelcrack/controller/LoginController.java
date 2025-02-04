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

public class LoginController extends Controller implements Initializable {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    private final UsuarioService usuarioService;

    public LoginController() {
        this.usuarioService = new UsuarioService();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Utils.showAlert("Error", "Por favor, ingrese su nombre de usuario y contraseña.", Alert.AlertType.ERROR);
            return;
        }

        Usuario usuario = usuarioService.obtenerUsuarioPorCredenciales(username, password);
        if (usuario != null) {
            try {
                App.changeToMainScene(usuario);
            } catch (IOException e) {
                Utils.showAlert("Error", "No se pudo cargar la siguiente pantalla.", Alert.AlertType.ERROR);
            }
        } else {
            Utils.showAlert("Error", "Credenciales inválidas. Por favor, intente de nuevo.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRegister() throws IOException {
        navigateToScene(Scenes.REGISTER);
    }

    private void navigateToScene(Scenes scene) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(scene.getURL()));
        Parent root = loader.load();
        Stage stage = (Stage) registerButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
    }

    @Override
    public void onClose(Object output) {
    }
}