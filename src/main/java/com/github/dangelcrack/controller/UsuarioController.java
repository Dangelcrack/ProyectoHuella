package com.github.dangelcrack.controller;

import com.github.dangelcrack.model.dao.UsuarioDAO;
import com.github.dangelcrack.model.entity.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import com.github.dangelcrack.utils.Utils;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UsuarioController extends Controller implements Initializable {
    private Usuario usuario;
    @FXML
    private TextField nombreField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField contraseñaField;

    @FXML
    private Button guardarButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        guardarButton.setOnMouseEntered(e -> guardarButton.setStyle("""
                -fx-font-size: 14px; -fx-background-color: #005299; -fx-text-fill: white;
                -fx-padding: 10 20; -fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand;"""));
        guardarButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            guardarButton.setStyle("""
                    -fx-font-size: 14px; -fx-text-fill: white;
                    -fx-padding: 10 20; -fx-background-radius: 20; -fx-border-radius: 20;
                    -fx-cursor: hand; -fx-background-color: #1e90ff; -fx-pref-width: 150; -fx-pref-height: 40;""");
        });
    }

    @Override
    public void onOpen(Object input) throws IOException {
        // Populate fields if input is provided
        if (input instanceof Usuario usuario) {
            this.usuario = usuario;
            nombreField.setText(usuario.getNombre());
            emailField.setText(usuario.getEmail());
        }
    }

    @Override
    public void onClose(Object output) {
        // Handle output logic if needed
        if (output instanceof Usuario usuario) {
            usuario.setNombre(nombreField.getText());
            usuario.setEmail(emailField.getText());
            usuario.setContraseña(contraseñaField.getText());
        }
    }

    @FXML
    private void handleGuardar() {
        Usuario usuario = new Usuario();
        usuario.setId(this.usuario.getId());
        usuario.setNombre(nombreField.getText());
        usuario.setEmail(emailField.getText());
        usuario.setContraseña(contraseñaField.getText());
        if(UsuarioDAO.actualizarUsuario(usuario)){
            Utils.showAlert("Éxito", "Usuario actualizado correctamente.", Alert.AlertType.INFORMATION);
        }else {
            Utils.showAlert("Error", "Algo ha fallado", Alert.AlertType.ERROR);
        }
    }
}