package com.github.dangelcrack.controller;

import com.github.dangelcrack.App;
import com.github.dangelcrack.model.entity.Actividad;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Units;
import com.github.dangelcrack.model.entity.Usuario;
import com.github.dangelcrack.model.entity.Scenes;
import com.github.dangelcrack.model.services.HuellaService;
import com.github.dangelcrack.utils.Utils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HuellaController extends Controller implements Initializable {
    @FXML
    private VBox vBox;
    @FXML
    private TableView<Huella> tableView;
    @FXML
    private TableColumn<Huella, Integer> valorColumn;
    @FXML
    private TableColumn<Huella, String> unidadColumn;
    @FXML
    private TableColumn<Huella, String> fechaColumn;
    @FXML
    private TableColumn<Huella, String> actividadColumn;

    private ObservableList<Huella> huellas;
    private Usuario usuario;
    private HuellaService huellaService;

    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        this.usuario = usuario;
        this.huellaService = new HuellaService(); // Inicializamos el servicio
        cargarHuellas();
    }

    @Override
    public void onClose(Object output) {
        // Lógica adicional si es necesaria al cerrar la ventana
    }

    private void cargarHuellas() {
        tableView.getItems().clear();
        List<Huella> huellasList = huellaService.obtenerHuellasPorUsuario(usuario);
        this.huellas = FXCollections.observableArrayList(huellasList);
        tableView.setItems(this.huellas);
    }

    public void guardarHuella(Huella nuevaHuella) {
        if (nuevaHuella != null) {
            this.huellas.add(nuevaHuella);
            tableView.refresh();
        } else {
            Utils.showAlert("Error", "No se pudo guardar la huella", Alert.AlertType.ERROR);
        }
    }

    public void actualizarHuella(Huella huellaActualizada) {
        if (huellaActualizada != null && huellaService.actualizarHuella(huellaActualizada)) {
            this.huellas.clear();
            tableView.refresh();
            cargarHuellas(); // Recargamos las huellas después de la actualización
        } else {
            Utils.showAlert("Error", "No se pudo actualizar la huella", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStylesheets().add(getClass().getResource("/com/github/dangelcrack/css/styles.css").toExternalForm());
    }

    private void configurarColumnas() {
        actividadColumn.setCellValueFactory(huella -> {
            Actividad actividad = huella.getValue().getIdActividad();
            return new SimpleStringProperty(actividad != null ? actividad.getNombre() : "");
        });

        valorColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getValor().intValue())
        );

        unidadColumn.setCellValueFactory(huella -> {
            Units unidad = Units.valueOf(huella.getValue().getUnidad());
            return new SimpleStringProperty(unidad != null ? unidad.toString() : "");
        });

        fechaColumn.setCellValueFactory(huella -> new SimpleStringProperty(
                huella.getValue().getFecha() != null ? huella.getValue().getFecha().toLocalDate().toString() : ""
        ));
    }

    @FXML
    private void addHuella() throws IOException {
        if (App.currentController == null) {
            throw new IllegalStateException("El controlador actual no está inicializado.");
        }
        App.currentController.openModal(Scenes.ADDTRACK, "Añadiendo Huella...", this, null);
    }

    @FXML
    private void deleteHuella() {
        Huella selectedHuella = tableView.getSelectionModel().getSelectedItem();

        if (selectedHuella == null) {
            Utils.showAlert("Eliminar Huella", "No hay ninguna huella seleccionada", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar eliminación");
        confirmAlert.setContentText("¿Eliminar huella seleccionada?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && huellaService.eliminarHuella(selectedHuella)) {
                huellas.remove(selectedHuella);
                tableView.refresh();
            } else {
                Utils.showAlert("Error", "No se pudo eliminar la huella", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void editHuella() throws IOException {
        App.currentController.openModal(Scenes.EDITTRACK, "Editando Huella...", this, null);
    }
}