package com.github.dangelcrack.controller;

import com.github.dangelcrack.model.dao.ActividadDAO;
import com.github.dangelcrack.model.dao.CategoriaDAO;
import com.github.dangelcrack.model.dao.HuellaDAO;
import com.github.dangelcrack.model.entity.Actividad;
import com.github.dangelcrack.model.entity.Categoria;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Usuario;
import com.github.dangelcrack.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class EditTrackController extends Controller implements Initializable {

    @FXML
    private VBox vbox;
    @FXML
    private TextField valor;
    @FXML
    private Text unidad;
    @FXML
    private ComboBox<Categoria> categoria;
    @FXML
    private ComboBox<Actividad> actividad;
    @FXML
    private DatePicker fecha;

    private Usuario usuario;
    private HuellaController huellaController;

    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        if (input == null) {
            throw new IllegalArgumentException("HuellaController no debe ser nulo.");
        }
        this.usuario = usuario;
        this.huellaController = (HuellaController) input;
    }

    @Override
    public void onClose(Object output) {
        // No se necesita implementación para este caso
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarCategoriasYActividades();

        // Modificar el TextFormatter para aceptar solo números decimales
        valor.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*\\.?\\d*")) {  // Permitir números con punto decimal
                return change;
            }
            return null;
        }));
    }

    private void cargarCategoriasYActividades() {
        List<Actividad> actividades = ActividadDAO.build().listar();
        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        actividad.setItems(actividadesObservable);
        List<Categoria> categorias = CategoriaDAO.build().listar();
        ObservableList<Categoria> categoriasObservable = FXCollections.observableArrayList(categorias);
        categoria.setItems(categoriasObservable);
        Callback<ListView<Categoria>, ListCell<Categoria>> categoriaCellFactory = new Callback<>() {
            @Override
            public ListCell<Categoria> call(ListView<Categoria> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Categoria item, boolean empty) {
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
        categoria.setCellFactory(categoriaCellFactory);
        categoria.setButtonCell(categoriaCellFactory.call(null));
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
        categoria.setOnAction(event -> {
            Categoria selectedCategoria = categoria.getValue();
            if (selectedCategoria != null && unidad != null) {
                unidad.setText("Unidad: " + selectedCategoria.getUnidad());
            }
        });
    }

    @FXML
    private void closeWindow(Event event) {
        try {
            Categoria selectedCategoria = categoria.getValue();
            Actividad selectedActividad = actividad.getValue();
            LocalDate selectedDate = fecha.getValue();
            String valorInput = valor.getText();
            if (selectedDate != null && selectedDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha no puede ser posterior a la fecha de hoy.");
            }

            if (selectedCategoria == null) {
                throw new IllegalArgumentException("Debe seleccionar una categoría.");
            }

            if (selectedActividad == null) {
                throw new IllegalArgumentException("Debe seleccionar una actividad.");
            }

            if (selectedDate == null) {
                throw new IllegalArgumentException("Debe seleccionar una fecha.");
            }

            if (valorInput == null || valorInput.isEmpty() || !valorInput.matches("\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException("El valor debe ser un número válido.");
            }
            if (!selectedCategoria.getId().equals(selectedActividad.getIdCategoria().getId())) {
                throw new IllegalArgumentException("La categoría y la actividad deben tener la misma unidad.");
            }

            double valorNumerico = Double.parseDouble(valorInput);
            Huella huella = new Huella();
            huella.setValor(BigDecimal.valueOf(valorNumerico));
            huella.setUnidad(selectedCategoria.getUnidad());
            huella.setIdActividad(selectedActividad);
            huella.setFecha(LocalDateTime.of(selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDayOfMonth(), 0, 0));
            huella.setIdUsuario(usuario);
            huellaController.guardarHuella(huella);
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Utils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
