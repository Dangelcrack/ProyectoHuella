package com.github.dangelcrack.controller;

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

public class UsuarioController extends Controller implements Initializable {
    private Usuario usuario;
    private final UsuarioService usuarioService;

    @FXML
    private TextField nombreField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField contraseñaField;

    @FXML
    private Button guardarButton;

    public UsuarioController() {
        this.usuarioService = new UsuarioService();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        this.usuario = usuario;
        nombreField.setText(usuario.getNombre());
        emailField.setText(usuario.getEmail());
    }

    @Override
    public void onClose(Object output) {
    }

    @FXML
    private void handleGuardar() {
        usuario.setNombre(nombreField.getText());
        usuario.setEmail(emailField.getText());
        usuario.setContraseña(contraseñaField.getText());

        if (usuarioService.actualizarUsuario(usuario)) {
            Utils.showAlert("Éxito", "Usuario actualizado correctamente.", Alert.AlertType.INFORMATION);
        } else {
            Utils.showAlert("Error", "Algo ha fallado", Alert.AlertType.ERROR);
        }
    }
}