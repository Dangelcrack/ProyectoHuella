package com.github.dangelcrack.controller;

import com.github.dangelcrack.model.entity.*;
import com.github.dangelcrack.model.services.ActividadService;
import com.github.dangelcrack.model.services.HabitoService;
import com.github.dangelcrack.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador para agregar hábitos.
 * Gestiona la interfaz y la lógica para agregar un nuevo hábito asociado a un usuario.
 */
public class AddHabitoController extends Controller implements Initializable {

    // Elementos de la interfaz de usuario
    @FXML
    private VBox vbox;
    @FXML
    private TextField frecuencia;
    @FXML
    private ComboBox<Frecuencies> tipoFrecuencia;
    @FXML
    private ComboBox<Actividad> actividad;
    @FXML
    private DatePicker fecha;

    // Servicios y controladores asociados
    private Usuario usuario;
    private HabitoService habitoService;
    private ActividadService actividadService;
    private HabitoController habitoController;

    /**
     * Método que se ejecuta al abrir la ventana de agregar hábito.
     * @param usuario Usuario actual
     * @param input Instancia del controlador de hábitos
     * @throws IOException Si ocurre un error de carga
     */
    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (input == null) {
            throw new IllegalArgumentException("HabitoService no debe ser nulo.");
        }

        this.usuario = usuario;
        this.habitoService = new HabitoService();
        this.actividadService = new ActividadService();
        this.habitoController = (HabitoController) input;

        // Cargar lista de actividades y frecuencias
        List<Actividad> actividades = actividadService.listar();
        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        actividad.setItems(actividadesObservable);
        tipoFrecuencia.setItems(FXCollections.observableArrayList(Frecuencies.values()));

        // Configurar la visualización de actividades en el ComboBox
        configurarCeldaComboBoxActividad(actividad, actividadesObservable);
    }

    /**
     * Método que se ejecuta al cerrar la ventana (no implementado).
     */
    @Override
    public void onClose(Object output) {
        // No se necesita implementación
    }

    /**
     * Método de inicialización de la ventana.
     * @param url URL del recurso
     * @param resourceBundle Recursos internacionales
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actividadService = new ActividadService();
        List<Actividad> actividades = actividadService.listar();
        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        actividad.setItems(actividadesObservable);
        tipoFrecuencia.setItems(FXCollections.observableArrayList(Frecuencies.values()));
        configurarCeldaComboBoxActividad(actividad, actividadesObservable);
    }

    /**
     * Configura la celda del ComboBox para mostrar correctamente los nombres de las actividades.
     * @param comboBox ComboBox de actividades
     * @param observableList Lista observable de actividades
     */
    private void configurarCeldaComboBoxActividad(ComboBox<Actividad> comboBox, ObservableList<Actividad> observableList) {
        Callback<ListView<Actividad>, ListCell<Actividad>> actividadCellFactory = param -> new ListCell<>() {
            @Override
            protected void updateItem(Actividad item, boolean empty) {
                super.updateItem(item, empty);
                setText((item != null && !empty) ? item.getNombre() : null);
            }
        };
        comboBox.setCellFactory(actividadCellFactory);
        comboBox.setButtonCell(actividadCellFactory.call(null));
    }

    /**
     * Cierra la ventana después de validar y guardar el nuevo hábito.
     * @param event Evento de cierre
     */
    @FXML
    private void closeWindow(Event event) {
        try {
            // Validar los campos del formulario
            Actividad selectedActividad = actividad.getValue();
            LocalDate selectedDate = fecha.getValue();
            String valorInput = frecuencia.getText();
            Frecuencies selectedFrecuencia = tipoFrecuencia.getValue();

            if (selectedActividad == null) {
                throw new IllegalArgumentException("Debe seleccionar una actividad.");
            }
            if (selectedFrecuencia == null) {
                throw new IllegalArgumentException("Debe seleccionar una frecuencia.");
            }
            if (selectedDate == null) {
                throw new IllegalArgumentException("Debe seleccionar una fecha.");
            }
            if (valorInput == null || valorInput.isEmpty() || !valorInput.matches("\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException("El valor debe ser un número válido.");
            }

            // Crear el nuevo hábito
            double valorNumerico = Double.parseDouble(valorInput);
            Habito habito = new Habito();
            HabitoId habitoId = new HabitoId();
            habitoId.setIdActividad(selectedActividad.getId());
            habitoId.setIdUsuario(usuario.getId());
            habito.setId(habitoId);
            habito.setIdUsuario(usuario);
            habito.setIdActividad(selectedActividad);
            habito.setFrecuencia((int) valorNumerico);
            habito.setTipo(selectedFrecuencia.toString());
            habito.setUltimaFecha(LocalDateTime.of(selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDayOfMonth(), 0, 0));

            if (!habitoService.guardarHabito(habito)) {
                throw new IllegalArgumentException("No se pudo añadir el hábito.(Ya existe)");
            }
            habitoController.guardarHabito(habito);
            // Cerrar la ventana
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Utils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
