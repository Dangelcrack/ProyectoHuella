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
import javafx.scene.layout.BorderPane;

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

    }

    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
            nombreField.setText(usuario.getNombre());
            emailField.setText(usuario.getEmail());
    }

    @Override
    public void onClose(Object output) {

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