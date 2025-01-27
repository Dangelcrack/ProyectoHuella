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

import com.github.dangelcrack.model.dao.UsuarioDAO;
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

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Estilizar botones al pasar el ratón por encima
        loginButton.setOnMouseEntered(e -> loginButton.setStyle("""
                -fx-font-size: 14px; -fx-background-color: #005299; -fx-text-fill: white;
                -fx-padding: 10 20; -fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;"""));
        loginButton.setOnMouseExited(e -> loginButton.setStyle("""
                -fx-font-size: 14px; -fx-background-color: #1e90ff; -fx-text-fill: white;
                -fx-padding: 10 20; -fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;"""));

        registerButton.setOnMouseEntered(e -> registerButton.setStyle("""
                -fx-font-size: 14px; -fx-background-color: #ff3b8d; -fx-text-fill: white;
                -fx-padding: 10 20; -fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;"""));
        registerButton.setOnMouseExited(e -> registerButton.setStyle("""
                -fx-font-size: 14px; -fx-background-color: #ff69b4; -fx-text-fill: white;
                -fx-padding: 10 20; -fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;"""));
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            Utils.showAlert("Error", "Por favor, ingrese su nombre de usuario y contraseña.", Alert.AlertType.ERROR);
            return;
        }

        Usuario usuario = usuarioDAO.findByUsernameAndPassword(username, password);
        if (usuario != null) {
            try {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                navigateToMainLayout(stage, usuario);
            } catch (IOException e) {
                Utils.showAlert("Error", "No se pudo cargar la siguiente pantalla.", Alert.AlertType.ERROR);
            }
        } else {
            Utils.showAlert("Error", "Credenciales inválidas. Por favor, intente de nuevo.", Alert.AlertType.ERROR);
        }
    }


    private void navigateToMainLayout(Stage stage, Usuario usuario) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Scenes.ROOT.getURL()));
            Parent root = loader.load();
            AppController appController = loader.getController();
            appController.onOpen(usuario);
            Scene scene = new Scene(root, 1105, 654);
            stage.setScene(scene);
            stage.setTitle("Bienvenido");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Para obtener más detalles sobre el error
            Utils.showAlert("Error", "No se pudo cargar la siguiente pantalla: " + e.getMessage(), Alert.AlertType.ERROR);
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
    public void onOpen(Object input) throws IOException {
    }

    @Override
    public void onClose(Object output) {
    }
}
