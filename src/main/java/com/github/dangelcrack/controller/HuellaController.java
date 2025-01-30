package com.github.dangelcrack.controller;

import com.github.dangelcrack.App;
import com.github.dangelcrack.model.dao.HuellaDAO;
import com.github.dangelcrack.model.entity.Actividad;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Units;
import com.github.dangelcrack.model.entity.Usuario;
import com.github.dangelcrack.model.entity.Scenes;
import com.github.dangelcrack.utils.Utils;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        this.usuario = usuario;
        tableView.getItems().clear();
        List<Huella> huellasList = HuellaDAO.build().obtenerHuellasPorUsuario(usuario);
        this.huellas = FXCollections.observableArrayList(huellasList);
        tableView.setItems(this.huellas);
    }

    @Override
    public void onClose(Object output) {
        // Aquí puedes añadir lógica si necesitas procesar algo antes de cerrar
    }


    public void guardarHuella(Huella nuevaHuella) {
        if (nuevaHuella != null) {
            HuellaDAO.build().creaHuella(nuevaHuella);
            this.huellas.add(nuevaHuella);
            tableView.refresh();
        }
    }
    public void actualizarHuella(Huella nuevaHuella) {
        if(nuevaHuella != null) {
            HuellaDAO.build().actualizarHuella(nuevaHuella);
            tableView.getItems().clear();
            List<Huella> huellasList = HuellaDAO.build().obtenerHuellasPorUsuario(usuario);
            this.huellas = FXCollections.observableArrayList(huellasList);
            tableView.setItems(this.huellas);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableView.refresh();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        actividadColumn.setCellValueFactory(huella -> {
            Actividad actividad = huella.getValue().getIdActividad();
            return new SimpleStringProperty(actividad != null ? actividad.getNombre() : "");
        });

        valorColumn.setCellValueFactory(new PropertyValueFactory<>("valor"));

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
            if (response == ButtonType.OK) {
                HuellaDAO.build().eliminarHuella(selectedHuella);
                huellas.remove(selectedHuella);
                tableView.refresh();
            }
        });
    }


    @FXML
    private void editHuella() throws IOException {
        App.currentController.openModal(Scenes.EDITTRACK, "Editando Huella...", this, null);
    }
}
