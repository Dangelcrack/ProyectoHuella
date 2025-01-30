package com.github.dangelcrack.controller;

import com.github.dangelcrack.model.dao.ActividadDAO;
import com.github.dangelcrack.model.entity.*;
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

public class AddHabitoController extends Controller implements Initializable {

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

    private Usuario usuario;
    private HabitoController habitoController;

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
    }

    @Override
    public void onClose(Object output) {
        // No se necesita implementación para este caso
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<Actividad> actividades = ActividadDAO.build().listar();
        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        actividad.setItems(actividadesObservable);
        tipoFrecuencia.setItems(FXCollections.observableArrayList(Frecuencies.values()));

        Callback<ListView<Actividad>, ListCell<Actividad>> actividadCellFactory = new Callback<>() {
            @Override
            public ListCell<Actividad> call(ListView<Actividad> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Actividad item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            setText(item.getNombre());
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        };
        actividad.setCellFactory(actividadCellFactory);
        actividad.setButtonCell(actividadCellFactory.call(null));

        frecuencia.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*\\.?\\d*")) {
                return change;
            }
            return null;
        }));
    }

    @FXML
    private void closeWindow(Event event) {
        try {
            Actividad selectedActividad = actividad.getValue();
            LocalDate selectedDate = fecha.getValue();
            String valorInput = frecuencia.getText();
            Frecuencies frecuencia = tipoFrecuencia.getValue();

            if (selectedActividad == null) {
                throw new IllegalArgumentException("Debe seleccionar una actividad.");
            }
            if (frecuencia == null) {
                throw new IllegalArgumentException("Debe seleccionar una frecuencia.");
            }
            if (selectedDate == null) {
                throw new IllegalArgumentException("Debe seleccionar una fecha.");
            }
            if (valorInput == null || valorInput.isEmpty() || !valorInput.matches("\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException("El valor debe ser un número válido.");
            }

            double valorNumerico = Double.parseDouble(valorInput);

            Habito habito = new Habito();
            HabitoId habitoId = new HabitoId();
            habitoId.setIdActividad(selectedActividad.getId());
            habitoId.setIdUsuario(usuario.getId());
            habito.setId(habitoId);
            habito.setIdUsuario(usuario);
            habito.setIdActividad(selectedActividad);
            habito.setFrecuencia((int) valorNumerico);
            habito.setTipo(frecuencia.toString());
            System.out.println(habito);
            habito.setUltimaFecha(LocalDateTime.of(selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDayOfMonth(), 0, 0));
            habitoController.guardarHabito(habito);
            ((Node) (event.getSource())).getScene().getWindow().hide();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Utils.showAlert("Error", "Error al guardar el hábito", Alert.AlertType.ERROR);

        } catch (Exception e) {
            e.printStackTrace();
            Utils.showAlert("Error", "Error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

}

