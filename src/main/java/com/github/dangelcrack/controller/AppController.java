package com.github.dangelcrack.controller;

import com.github.dangelcrack.App;
import com.github.dangelcrack.model.entity.Scenes;
import com.github.dangelcrack.model.entity.Usuario;
import com.github.dangelcrack.view.View;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController extends Controller implements Initializable {

    @FXML
    private BorderPane borderPane;

    private Controller centerController;
    private Usuario usuario;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Lógica de inicialización si es necesaria
    }

    @Override
    public void onOpen(Object input) throws IOException {
        this.usuario = (Usuario) input;
        try {
            changeScene(Scenes.USERCONFIG, input); // Puedes pasar datos iniciales si es necesario
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error al cargar la configuración inicial.");
        }
    }

    @Override
    public void onClose(Object output) {
        // Lógica de cierre si es necesaria
    }

    /**
     * Cambia la escena central del BorderPane.
     *
     * @param scene La escena que se quiere cargar.
     * @param data  Los datos que se quieren pasar al controlador.
     * @throws IOException Si ocurre un error al cargar la escena.
     */
    public void changeScene(Scenes scene, Object data) throws IOException {
        try {
            View view = loadFXML(scene);
            borderPane.setCenter(view.scene); // Coloca la nueva escena en el centro del BorderPane
            this.centerController = view.controller;

            if (this.centerController != null) {
                this.centerController.onOpen(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("No se pudo cargar la escena: " + scene.name());
        }
    }


    /**
     * Carga un archivo FXML y devuelve una vista que contiene el nodo raíz y el controlador.
     *
     * @param scenes La escena que se quiere cargar.
     * @return Una instancia de {@link View} que contiene el nodo raíz y el controlador.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    public static View loadFXML(Scenes scenes) throws IOException {
        String url = scenes.getURL();
        FXMLLoader loader = new FXMLLoader(App.class.getResource(url));
        Parent root = loader.load();
        Controller controller = loader.getController();

        View view = new View();
        view.scene = root;
        view.controller = controller;
        return view;
    }

    /**
     * Muestra un mensaje de error en la consola y en una ventana emergente.
     *
     * @param message El mensaje de error que se quiere mostrar.
     */
    private void showError(String message) {
        System.err.println(message);

        // Mostrar un mensaje emergente para el usuario
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Se produjo un error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void irpreferenciasusuarios() {
        try {

            changeScene(Scenes.USERCONFIG, usuario);
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista de Ajustes de Usuario.");
        }
    }

    @FXML
    private void iractividades() {
        try {
            // Cambia a la escena de Actividades
            changeScene(Scenes.ACTIVITIES, null);
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista de Actividades.");
        }
    }

    @FXML
    private void irahuellas() {
        try {
            // Cambia a la escena de Huellas
            changeScene(Scenes.TRACKS, null);
        } catch (IOException e) {
            mostrarError("No se pudo cargar la vista de Huellas.");
        }
    }

    /**
     * Método auxiliar para mostrar una alerta de error.
     *
     * @param mensaje El mensaje a mostrar.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
