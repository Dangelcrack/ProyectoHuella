/**
 * Controlador para la gestión de hábitos en la aplicación.
 */
package com.github.dangelcrack.controller;

import com.github.dangelcrack.App;
import com.github.dangelcrack.model.entity.*;
import com.github.dangelcrack.model.services.HabitoService;
import com.github.dangelcrack.utils.Utils;
import javafx.beans.property.ReadOnlyIntegerWrapper;
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

public class HabitoController extends Controller implements Initializable {

    /** Contenedor principal en la interfaz gráfica */
    @FXML
    private VBox vBox;

    /** Tabla para mostrar los hábitos */
    @FXML
    private TableView<Habito> tableView;

    /** Columnas de la tabla */
    @FXML
    private TableColumn<Habito, Integer> frecuenciaColumn;
    @FXML
    private TableColumn<Habito, String> tipoColumn;
    @FXML
    private TableColumn<Habito, String> fechaColumn;
    @FXML
    private TableColumn<Habito, String> actividadColumn;

    /** Lista observable de hábitos */
    private ObservableList<Habito> habitos;

    /** Usuario actual */
    private Usuario usuario;

    /** Servicio para la gestión de hábitos */
    private HabitoService habitoService;

    /**
     * Método llamado al abrir la ventana.
     * @param usuario Usuario actual.
     * @param input Datos de entrada.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        this.usuario = usuario;
        this.habitoService = new HabitoService();
        cargarHabitos();
    }

    /**
     * Método llamado al cerrar la ventana.
     * @param output Datos de salida.
     */
    @Override
    public void onClose(Object output) {
        // Lógica adicional si es necesaria al cerrar la ventana
    }

    /**
     * Carga los hábitos del usuario en la tabla.
     */
    private void cargarHabitos() {
        tableView.getItems().clear();
        List<Habito> habitosList = habitoService.obtenerHabitosPorUsuario(usuario);
        this.habitos = FXCollections.observableArrayList(habitosList);
        tableView.setItems(this.habitos);
    }

    /**
     * Guarda un nuevo hábito en la lista.
     * @param nuevoHabito Hábito a guardar.
     */
    public void guardarHabito(Habito nuevoHabito) {
        if (nuevoHabito != null) {
            this.habitos.add(nuevoHabito);
            tableView.refresh();
        } else {
            Utils.showAlert("Error", "No se pudo guardar el hábito", Alert.AlertType.ERROR);
        }
    }

    /**
     * Actualiza un hábito existente.
     * @param nuevoHabito Hábito actualizado.
     */
    public void actualizarHabito(Habito nuevoHabito) {
        if (nuevoHabito != null && habitoService.actualizarHabito(nuevoHabito)) {
            cargarHabitos();
        } else {
            Utils.showAlert("Error", "No se pudo actualizar el hábito", Alert.AlertType.ERROR);
        }
    }

    /**
     * Inicializa la configuración del controlador.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getStylesheets().add(getClass().getResource("/com/github/dangelcrack/css/styles.css").toExternalForm());
    }

    /**
     * Configura las columnas de la tabla de hábitos.
     */
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

    /**
     * Abre la ventana para añadir un nuevo hábito.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @FXML
    private void addHabito() throws IOException {
        if (App.currentController == null) {
            throw new IllegalStateException("El controlador actual no está inicializado.");
        }
        App.currentController.openModal(Scenes.ADDHABITO, "Añadiendo Hábito...", this, null);
    }

    /**
     * Elimina el hábito seleccionado.
     */
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

    /**
     * Abre la ventana para editar un hábito.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    @FXML
    private void editHabito() throws IOException {
        App.currentController.openModal(Scenes.EDITHABITO, "Editando Hábito...", this, null);
    }
}
