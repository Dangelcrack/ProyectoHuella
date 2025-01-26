package com.github.dangelcrack.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import com.github.dangelcrack.model.entity.Usuario;
public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Usuario user = new Usuario();
        user.setNombre(username);
        user.setContraseña(password);
        System.out.println("Usuario guardado: " + username);
    }
}
