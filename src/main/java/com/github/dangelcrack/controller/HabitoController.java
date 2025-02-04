package com.github.dangelcrack.controller;

import com.github.dangelcrack.App;
import com.github.dangelcrack.model.entity.Actividad;
import com.github.dangelcrack.model.entity.Habito;
import com.github.dangelcrack.model.entity.Usuario;
import com.github.dangelcrack.model.entity.Frecuencies;
import com.github.dangelcrack.model.entity.Scenes;
import com.github.dangelcrack.model.services.HabitoService;
import com.github.dangelcrack.utils.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
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
    private HabitoService habitoService;

    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        this.usuario = usuario;
        this.habitoService = new HabitoService();
        cargarHabitos();
    }

    @Override
    public void onClose(Object output) {
        // Lógica adicional si es necesaria al cerrar la ventana
    }

    private void cargarHabitos() {
        tableView.getItems().clear();
        List<Habito> habitosList = habitoService.obtenerHabitosPorUsuario(usuario);
        this.habitos = FXCollections.observableArrayList(habitosList);
        tableView.setItems(this.habitos);
    }

    public void guardarHabito(Habito nuevoHabito) {
        if (nuevoHabito != null) {
            this.habitos.add(nuevoHabito);
            tableView.refresh();
        } else {
            Utils.showAlert("Error", "No se pudo guardar el hábito", Alert.AlertType.ERROR);
        }
    }

    public void actualizarHabito(Habito nuevoHabito) {
        if (nuevoHabito != null && habitoService.actualizarHabito(nuevoHabito)) {
            cargarHabitos();
        } else {
            Utils.showAlert("Error", "No se pudo actualizar el hábito", Alert.AlertType.ERROR);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStylesheets().add(getClass().getResource("/com/github/dangelcrack/css/styles.css").toExternalForm());
    }

    private void configurarColumnas() {
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
    }

    @FXML
    private void addHabito() throws IOException {
        if (App.currentController == null) {
            throw new IllegalStateException("El controlador actual no está inicializado.");
        }
        App.currentController.openModal(Scenes.ADDHABITO, "Añadiendo Hábito...", this, null);
    }

    @FXML
    private void deleteHabito() {
        Habito selectedHabito = tableView.getSelectionModel().getSelectedItem();

        if (selectedHabito == null) {
            Utils.showAlert("Eliminar Hábito", "No hay ningún hábito seleccionado", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmar eliminación");
        confirmAlert.setContentText("¿Eliminar hábito seleccionado?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && habitoService.eliminarHabito(selectedHabito)) {
                habitos.remove(selectedHabito);
                tableView.refresh();
            } else {
                Utils.showAlert("Error", "No se pudo eliminar el hábito", Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void editHabito() throws IOException {
        App.currentController.openModal(Scenes.EDITHABITO, "Editando Hábito...", this, null);
    }
}
