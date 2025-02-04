package com.github.dangelcrack.controller;

import com.github.dangelcrack.model.dao.ActividadDAO;
import com.github.dangelcrack.model.dao.HabitoDAO;
import com.github.dangelcrack.model.entity.*;
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
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

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
    private HabitoController habitoController;
    private ObservableList<Habito> habitosObservable;

    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (input == null) {
            throw new IllegalArgumentException("HabitoController no debe ser nulo.");
        }
        this.usuario = usuario;
        this.habitoController = (HabitoController) input;
        cargarHabitosUsuario();
    }

    @Override
    public void onClose(Object output) {
        // No se necesita implementación para este caso
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarDatos();
        habitoComboBox.setOnAction(event -> rellenarCamposConHabitoSeleccionado());

        // Modificar el TextFormatter para aceptar solo números enteros
        frecuencia.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) { // Permitir solo números
                return change;
            }
            return null;
        }));
    }

    private void cargarHabitosUsuario() {
        List<Habito> habitos = HabitoDAO.build().obtenerHabitosPorUsuario(usuario);
        habitosObservable = FXCollections.observableArrayList(habitos);
        habitoComboBox.setItems(habitosObservable);
        habitoComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Habito item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText("Actividad: " + item.getIdActividad().getNombre() + ", Frecuencia: " + item.getFrecuencia() + " " + item.getTipo());
                } else {
                    setText(null);
                }
            }
        });
        habitoComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Habito item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText("Actividad: " + item.getIdActividad().getNombre() + ", Frecuencia: " + item.getFrecuencia() + " " + item.getTipo());
                } else {
                    setText(null);
                }
            }
        });
    }

    private void cargarDatos() {
        // Cargar datos para el ComboBox de Actividades
        List<Actividad> actividades = ActividadDAO.build().listar();
        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        actividad.setItems(actividadesObservable);

        // Configurar CellFactory para mostrar los nombres y detalles en el ComboBox de Actividades
        actividad.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Actividad item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText("Actividad: " + item.getNombre());
                } else {
                    setText(null);
                }
            }
        });

        actividad.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Actividad item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    setText("Actividad: " + item.getNombre());
                } else {
                    setText(null);
                }
            }
        });

        // Cargar tipos de frecuencia
        tipoFrecuencia.setItems(FXCollections.observableArrayList(Frecuencies.values()));
    }

    private void rellenarCamposConHabitoSeleccionado() {
        Habito habitoSeleccionado = habitoComboBox.getValue();
        if (habitoSeleccionado != null) {
            frecuencia.setText(String.valueOf(habitoSeleccionado.getFrecuencia()));
            tipoFrecuencia.setValue(Frecuencies.valueOf(habitoSeleccionado.getTipo()));
            actividad.setValue(habitoSeleccionado.getIdActividad());
            fecha.setValue(habitoSeleccionado.getUltimaFecha().toLocalDate());

            // Asegurar que se selecciona correctamente la actividad en el ComboBox
            ObservableList<Actividad> actividades = actividad.getItems();
            for (Actividad act : actividades) {
                if (act.getId().equals(habitoSeleccionado.getIdActividad().getId())) {
                    actividad.setValue(act);
                    break;
                }
            }
        }
    }


    @FXML
    private void closeWindow(Event event) {
        Actividad selectedActividad = actividad.getValue();
        Frecuencies selectedFrecuencia = tipoFrecuencia.getValue();
        LocalDate selectedDate = fecha.getValue();
        String valorInput = frecuencia.getText();

        if (selectedActividad == null || selectedFrecuencia == null || selectedDate == null || valorInput.isEmpty()) {
            throw new IllegalArgumentException("Todos los campos deben estar completos.");
        }

        int valorNumerico;
        try {
            valorNumerico = Integer.parseInt(valorInput);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La frecuencia debe ser un número válido.");
        }

        LocalDateTime fechaCompleta = selectedDate.atStartOfDay();

        Habito nuevoHabito = new Habito();
        nuevoHabito.setId(habitoComboBox.getValue().getId());
        nuevoHabito.setFrecuencia(valorNumerico);
        nuevoHabito.setTipo(selectedFrecuencia.toString());
        nuevoHabito.setIdActividad(selectedActividad);  // Updating idActividad
        nuevoHabito.setUltimaFecha(fechaCompleta);
        nuevoHabito.setIdUsuario(usuario);
        habitoController.actualizarHabito(nuevoHabito);
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }


}
