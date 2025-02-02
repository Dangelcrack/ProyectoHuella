package com.github.dangelcrack.controller;

import com.github.dangelcrack.App;
import com.github.dangelcrack.model.dao.HabitoDAO;
import com.github.dangelcrack.model.dao.HuellaDAO;
import com.github.dangelcrack.model.entity.*;
import com.github.dangelcrack.utils.Utils;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class HabitoController extends Controller implements Initializable {
    @FXML
    private VBox vBox;
    @FXML
    private TableView<Habito> tableView;
    @FXML
    private TableColumn<Habito, Integer> frecuenciaColumn;
    @FXML
    private TableColumn<Habito, String> tipoColumn;
    @FXML
    private TableColumn<Habito, String> fechaColumn;
    @FXML
    private TableColumn<Habito, String> actividadColumn;

    private ObservableList<Habito> habitos;
    private Usuario usuario;

    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        this.usuario = usuario;
        tableView.getItems().clear();
        List<Habito> habitosList = HabitoDAO.build().obtenerHabitosPorUsuario(usuario);
        this.habitos = FXCollections.observableArrayList(habitosList);
        tableView.setItems(this.habitos);
    }

    @Override
    public void onClose(Object output) {
        // Aquí puedes añadir lógica si necesitas procesar algo antes de cerrar
    }


    public void guardarHabito(Habito nuevoHabito) {
        HabitoDAO.build().crearHabito(nuevoHabito);
        this.habitos.add(nuevoHabito);
        tableView.refresh();
    }

    public void actualizarHabito(Habito nuevoHabito) {
        if (nuevoHabito != null) {
            HabitoDAO.build().actualizarHabito(nuevoHabito);
            tableView.getItems().clear();
            List<Habito> habitoList = HabitoDAO.build().obtenerHabitosPorUsuario(usuario);
            this.habitos = FXCollections.observableArrayList(habitoList);
            tableView.setItems(this.habitos);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableView.refresh();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        actividadColumn.setCellValueFactory(habito -> {
            Actividad actividad = habito.getValue().getIdActividad();
            return new SimpleStringProperty(actividad != null ? actividad.getNombre() : "");
        });
        frecuenciaColumn.setCellValueFactory(habito ->
                new ReadOnlyIntegerWrapper(habito.getValue().getFrecuencia()).asObject());

        tipoColumn.setCellValueFactory(habito -> {
            Frecuencies frecuencies = Frecuencies.valueOf(habito.getValue().getTipo());
            return new SimpleStringProperty(frecuencies != null ? frecuencies.toString() : "");
        });
        fechaColumn.setCellValueFactory(habito -> new SimpleStringProperty(
                habito.getValue().getUltimaFecha() != null ? habito.getValue().getUltimaFecha().toLocalDate().toString() : ""
        ));
        tableView.getStylesheets().add(getClass().getResource("/com/github/dangelcrack/css/styles.css").toExternalForm());
    }

    @FXML
    private void addHabito() throws IOException {
        if (App.currentController == null) {
            throw new IllegalStateException("El controlador actual no está inicializado.");
        }

        App.currentController.openModal(Scenes.ADDHABITO, "Añadiendo Habito...", this, null);
    }

    @FXML
    private void deleteHabito() {
        Habito selectedHabito = tableView.getSelectionModel().getSelectedItem();

        if (selectedHabito == null) {
            Utils.showAlert("Eliminar habito", "No hay ninguna habito seleccionada", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar eliminación");
        confirmAlert.setContentText("¿Eliminar habito seleccionada?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                HabitoDAO.build().eliminarHabito(selectedHabito);
                habitos.remove(selectedHabito);
                tableView.refresh();
            }
        });
    }


    @FXML
    private void editHabito() throws IOException {
        App.currentController.openModal(Scenes.EDITHABITO, "Editando Habito...", this, null);
    }
}
