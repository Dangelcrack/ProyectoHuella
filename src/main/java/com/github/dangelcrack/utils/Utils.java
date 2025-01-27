package com.github.dangelcrack.utils;

import javafx.scene.control.Alert;

public class Utils {
    public static void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
