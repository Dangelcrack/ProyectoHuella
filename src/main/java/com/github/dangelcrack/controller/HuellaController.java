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

    /** FXML fields to interact with the UI elements */
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

    /** Observable list to hold huellas */
    private ObservableList<Huella> huellas;
    private Usuario usuario;
    private HuellaService huellaService;

    /**
     * This method is called when the view is opened with the specific user.
     * @param usuario the user whose huellas are loaded
     * @param input any additional input, currently unused
     */
    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        this.usuario = usuario;
        this.huellaService = new HuellaService(); // Initializing the service
        cargarHuellas(); // Load huellas for the user
    }

    /**
     * This method is called when the view is closed, no implementation for now.
     * @param output any output from the view
     */
    @Override
    public void onClose(Object output) {
        // Additional logic if needed when closing the window
    }

    /**
     * Method to load huellas into the table.
     * Clears existing items and fetches the latest huellas for the user.
     */
    private void cargarHuellas() {
        tableView.getItems().clear();  // Clear existing items
        List<Huella> huellasList = huellaService.obtenerHuellasPorUsuario(usuario);  // Fetch huellas from the service
        this.huellas = FXCollections.observableArrayList(huellasList); // Convert list to observable
        tableView.setItems(this.huellas);  // Set the table's items
    }

    /**
     * Method to save a new huella.
     * Adds the huella to the list and refreshes the table.
     * @param nuevaHuella the huella to be saved
     */
    public void guardarHuella(Huella nuevaHuella) {
        if (nuevaHuella != null) {
            this.huellas.add(nuevaHuella);  // Add the new huella to the list
            tableView.refresh();  // Refresh the table view
        } else {
            Utils.showAlert("Error", "No se pudo guardar la huella", Alert.AlertType.ERROR);  // Show error alert
        }
    }

    /**
     * Method to update an existing huella.
     * If the update is successful, reloads the huellas from the service.
     * @param huellaActualizada the huella to be updated
     */
    public void actualizarHuella(Huella huellaActualizada) {
        if (huellaActualizada != null && huellaService.actualizarHuella(huellaActualizada)) {
            this.huellas.clear();  // Clear the list
            tableView.refresh();  // Refresh the table view
            cargarHuellas();  // Reload the huellas
        } else {
            Utils.showAlert("Error", "No se pudo actualizar la huella", Alert.AlertType.ERROR);  // Show error alert
        }
    }

    /**
     * Initialize the table and columns.
     * Called when the controller is initialized.
     * @param url the location used to resolve relative paths for the root object, or null
     * @param resourceBundle the resources used to localize the root object, or null
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();  // Set up the columns
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);  // Enable column resizing
        tableView.getStylesheets().add(getClass().getResource("/com/github/dangelcrack/css/styles.css").toExternalForm());  // Add styles
    }

    /**
     * Method to configure the columns in the table.
     * Sets up the cell value factories for each column.
     */
    private void configurarColumnas() {
        // Actividad column configuration
        actividadColumn.setCellValueFactory(huella -> {
            Actividad actividad = huella.getValue().getIdActividad();
            return new SimpleStringProperty(actividad != null ? actividad.getNombre() : "");
        });

        // Valor column configuration (integer value)
        valorColumn.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getValor().intValue())
        );

        // Unidad column configuration (unit value)
        unidadColumn.setCellValueFactory(huella -> {
            Units unidad = Units.valueOf(huella.getValue().getUnidad());
            return new SimpleStringProperty(unidad != null ? unidad.toString() : "");
        });

        // Fecha column configuration (date value)
        fechaColumn.setCellValueFactory(huella -> new SimpleStringProperty(
                huella.getValue().getFecha() != null ? huella.getValue().getFecha().toLocalDate().toString() : ""
        ));
    }

    /**
     * Method to open the modal for adding a new huella.
     * @throws IOException if there is an error while opening the modal
     */
    @FXML
    private void addHuella() throws IOException {
        if (App.currentController == null) {
            throw new IllegalStateException("El controlador actual no está inicializado.");  // Throw error if no controller
        }
        App.currentController.openModal(Scenes.ADDTRACK, "Añadiendo Huella...", this, null);  // Open modal
    }

    /**
     * Method to delete a selected huella from the table.
     * If a huella is selected, shows a confirmation alert.
     * If confirmed, the huella is removed from the table.
     */
    @FXML
    private void deleteHuella() {
        Huella selectedHuella = tableView.getSelectionModel().getSelectedItem();  // Get selected huella

        if (selectedHuella == null) {
            Utils.showAlert("Eliminar Huella", "No hay ninguna huella seleccionada", Alert.AlertType.WARNING);  // Show warning if no huella selected
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);  // Create a confirmation alert
        confirmAlert.setTitle("Confirmar eliminación");
        confirmAlert.setContentText("¿Eliminar huella seleccionada?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK && huellaService.eliminarHuella(selectedHuella)) {
                huellas.remove(selectedHuella);  // Remove the selected huella
                tableView.refresh();  // Refresh the table view
            } else {
                Utils.showAlert("Error", "No se pudo eliminar la huella", Alert.AlertType.ERROR);  // Show error alert
            }
        });
    }

    /**
     * Method to edit a selected huella.
     * Opens a modal for editing the huella.
     * @throws IOException if there is an error while opening the modal
     */
    @FXML
    private void editHuella() throws IOException {
        App.currentController.openModal(Scenes.EDITTRACK, "Editando Huella...", this, null);  // Open edit modal
    }
}
