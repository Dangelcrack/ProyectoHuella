package com.github.dangelcrack.controller;

import com.github.dangelcrack.App;
import com.github.dangelcrack.model.entity.Scenes;
import com.github.dangelcrack.model.entity.Usuario;
import com.github.dangelcrack.utils.Utils;
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

    /**
     * Método de inicialización de la clase.
     * @param url URL de referencia.
     * @param resourceBundle Recursos de internacionalización.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Lógica de inicialización si es necesaria
    }

    /**
     * Método que se ejecuta al abrir el controlador.
     * @param usuario Usuario que está usando la aplicación.
     * @param input Datos adicionales.
     * @throws IOException Si ocurre un error al cambiar de escena.
     */
    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        this.usuario = usuario;
        try {
            changeScene(Scenes.USERCONFIG, input);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.showAlert("Error", "Error al cargar la configuración inicial.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Abre una ventana modal con la escena especificada.
     * @param scene Escena a cargar.
     * @param title Título de la ventana.
     * @param parent Controlador padre.
     * @param data Datos a enviar al controlador.
     * @throws IOException Si ocurre un error al cargar la escena.
     */
    public void openModal(Scenes scene, String title, Controller parent, Object data) throws IOException {
        View view = loadFXML(scene);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(App.stage);
        Scene _scene = new Scene(view.scene);
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/github/dangelcrack/img/huella.png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        stage.setScene(_scene);
        view.controller.onOpen(usuario, parent);
        stage.showAndWait();
    }

    /**
     * Método que se ejecuta al cerrar el controlador.
     * @param output Datos de salida.
     */
    @Override
    public void onClose(Object output) {
        // Lógica de cierre si es necesaria
    }

    /**
     * Cambia la escena central del BorderPane.
     * @param scene La escena que se quiere cargar.
     * @param data Datos a pasar al controlador.
     * @throws IOException Si ocurre un error al cargar la escena.
     */
    public void changeScene(Scenes scene, Object data) throws IOException {
        View view = loadFXML(scene);
        borderPane.setCenter(view.scene); // Coloca la nueva escena en el centro del BorderPane
        this.centerController = view.controller;
        this.centerController.onOpen(usuario, data);
    }

    /**
     * Carga un archivo FXML y devuelve una vista con el nodo raíz y el controlador.
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
     * Cambia la escena a las preferencias de usuario.
     * @throws IOException Si ocurre un error al cargar la escena.
     */
    @FXML
    private void irpreferenciasusuarios() throws IOException {
        changeScene(Scenes.USERCONFIG, usuario);
    }
    /**
     * Cambia la escena a la sección de hábitos.
     * @throws IOException Si ocurre un error al cargar la escena.
     */
    @FXML
    private void irahabitos() throws IOException {
        changeScene(Scenes.HABITOS, usuario);
    }
    /**
     * Cambia la escena a la sección de huellas.
     * @throws IOException Si ocurre un error al cargar la escena.
     */
    @FXML
    private void irahuellas() throws IOException {
        changeScene(Scenes.TRACKS, usuario);
    }
    /**
     * Cambia la escena a la sección de cálculo de impacto.
     * @throws IOException Si ocurre un error al cargar la escena.
     */
    @FXML
    private void irCalcularImpacto () throws IOException {
        changeScene(Scenes.IMPACTS,usuario);
    }
    @FXML
    private void irMostrarGraficos () throws IOException {
        changeScene(Scenes.GRAFICOS, usuario);
    }

    /**
     * Cambia la escena a la sección de ranking.
     * @throws IOException Si ocurre un error al cargar la escena.
     */
    @FXML
    private void irRanking () throws IOException {
        changeScene(Scenes.RANKING,usuario);
    }
}
