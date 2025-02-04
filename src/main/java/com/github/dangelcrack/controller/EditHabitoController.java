package com.github.dangelcrack.controller;

import com.github.dangelcrack.model.entity.*;
import com.github.dangelcrack.model.services.HabitoService;
import com.github.dangelcrack.model.services.ActividadService;
import com.github.dangelcrack.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador para la edición de hábitos.
 */
public class EditHabitoController extends Controller implements Initializable {

    @FXML
    private VBox vbox;
    @FXML
    private ComboBox<Habito> habitoComboBox;
    @FXML
    private TextField frecuencia;
    @FXML
    private ComboBox<Frecuencies> tipoFrecuencia;
    @FXML
    private ComboBox<Actividad> actividad;
    @FXML
    private DatePicker fecha;

    private Usuario usuario;
    private HabitoService habitoService;
    private ActividadService actividadService;
    private HabitoController habitoController;

    /**
     * Método llamado al abrir la ventana de edición de hábitos.
     *
     * @param usuario El usuario que está editando el hábito.
     * @param input   El controlador de hábitos.
     * @throws IOException Si ocurre un error al abrir la ventana.
     */
    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (input == null) {
            throw new IllegalArgumentException("HabitoController no debe ser nulo.");
        }
        this.usuario = usuario;
        this.habitoService = new HabitoService();
        this.actividadService = new ActividadService();
        this.habitoController = (HabitoController) input;
        cargarHabitosUsuario();
    }

    /**
     * Método llamado al cerrar la ventana de edición de hábitos.
     *
     * @param output El objeto de salida (no utilizado en este caso).
     */
    @Override
    public void onClose(Object output) {
    }

    /**
     * Método de inicialización del controlador.
     *
     * @param url            La ubicación relativa del archivo FXML.
     * @param resourceBundle El recurso de paquete para localizar el archivo FXML.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.habitoService = new HabitoService();
        this.actividadService = new ActividadService();
        cargarDatos();
        habitoComboBox.setOnAction(event -> rellenarCamposConHabitoSeleccionado());
        frecuencia.setTextFormatter(new TextFormatter<>(change -> {
            return change.getControlNewText().matches("\\d*") ? change : null;
        }));
    }

    /**
     * Carga los hábitos del usuario en el ComboBox.
     */
    private void cargarHabitosUsuario() {
        List<Habito> habitos = habitoService.obtenerHabitosPorUsuario(usuario);
        ObservableList<Habito> habitosObservable = FXCollections.observableArrayList(habitos);
        habitoComboBox.setItems(habitosObservable);

        // Mostrar el nombre de la actividad asociada al hábito
        habitoComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Habito item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getIdActividad().getNombre());
            }
        });
        habitoComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Habito item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "Seleccione un hábito" : item.getIdActividad().getNombre());
            }
        });
    }

    /**
     * Carga los datos iniciales en los ComboBox y otros controles.
     */
    private void cargarDatos() {
        List<Actividad> actividades = actividadService.listar();
        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        actividad.setItems(actividadesObservable);

        // Configurar cómo se muestra la actividad en el ComboBox
        actividad.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Actividad item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.getNombre());
            }
        });
        actividad.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Actividad item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "Seleccione una actividad" : item.getNombre());
            }
        });

        tipoFrecuencia.setItems(FXCollections.observableArrayList(Frecuencies.values()));

        // Configurar cómo se muestra la frecuencia en el ComboBox
        tipoFrecuencia.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Frecuencies item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : item.toString());
            }
        });
        tipoFrecuencia.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Frecuencies item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "Seleccione una frecuencia" : item.toString());
            }
        });

        cargarHabitosUsuario();
    }

    /**
     * Rellena los campos del formulario con los datos del hábito seleccionado.
     */
    private void rellenarCamposConHabitoSeleccionado() {
        Habito habitoSeleccionado = habitoComboBox.getValue();
        if (habitoSeleccionado != null) {
            frecuencia.setText(String.valueOf(habitoSeleccionado.getFrecuencia()));
            tipoFrecuencia.setValue(Frecuencies.valueOf(habitoSeleccionado.getTipo()));
            for (Actividad act : actividad.getItems()) {
                if (act.getId().equals(habitoSeleccionado.getIdActividad().getId())) {
                    actividad.setValue(act);
                    break;
                }
            }
            fecha.setValue(habitoSeleccionado.getUltimaFecha().toLocalDate());
        }
    }

    /**
     * Método para cerrar la ventana y guardar los cambios.
     *
     * @param event El evento que desencadena el cierre de la ventana.
     */
    @FXML
    private void closeWindow(Event event) {
        try {
            Actividad selectedActividad = actividad.getValue();
            Frecuencies selectedFrecuencia = tipoFrecuencia.getValue();
            LocalDate selectedDate = fecha.getValue();
            String valorInput = frecuencia.getText();

            if (selectedActividad == null || selectedFrecuencia == null || selectedDate == null || valorInput.isEmpty()) {
                throw new IllegalArgumentException("Todos los campos deben estar completos.");
            }

            int valorNumerico = Integer.parseInt(valorInput);
            LocalDateTime fechaCompleta = selectedDate.atStartOfDay();

            Habito nuevoHabito = new Habito();
            nuevoHabito.setId(habitoComboBox.getValue().getId());
            nuevoHabito.setFrecuencia(valorNumerico);
            nuevoHabito.setTipo(selectedFrecuencia.toString());
            nuevoHabito.setIdActividad(selectedActividad);
            nuevoHabito.setUltimaFecha(fechaCompleta);
            nuevoHabito.setIdUsuario(usuario);

            if (!habitoService.actualizarHabito(nuevoHabito)) {
                throw new IllegalArgumentException("No se pudo actualizar el hábito.");
            }

            habitoController.actualizarHabito(nuevoHabito);
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (Exception e) {
            Utils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}