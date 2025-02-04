package com.github.dangelcrack.controller;

import com.github.dangelcrack.model.entity.*;
import com.github.dangelcrack.model.services.ActividadService;
import com.github.dangelcrack.model.services.CategoriaService;
import com.github.dangelcrack.model.services.HuellaService;
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

public class AddTrackController extends Controller implements Initializable {

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
    private HuellaController huellaController;
    private Usuario usuario;
    private HuellaService huellaService;
    private ActividadService actividadService;
    private CategoriaService categoriaService;

    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }

        if (input == null) {
            throw new IllegalArgumentException("HuellaService no debe ser nulo.");
        }

        this.usuario = usuario;
        this.huellaService = new HuellaService();
        this.actividadService = new ActividadService(); // Asegúrate de inicializar ActividadService
        this.categoriaService = new CategoriaService(); // Asegúrate de inicializar CategoriaService
        this.huellaController = (HuellaController) input;
        // Cargar Actividades y Categorías de manera explícita
        List<Actividad> actividades = actividadService.listar(); // Usamos el servicio ActividadService
        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        actividad.setItems(actividadesObservable);

        List<Categoria> categorias = categoriaService.listar(); // Usamos el servicio CategoriaService
        ObservableList<Categoria> categoriasObservable = FXCollections.observableArrayList(categorias);
        categoria.setItems(categoriasObservable);

        // Inicializamos las celdas personalizadas para las ComboBox
        configurarCeldaComboBoxCategoria(categoria, categoriasObservable);
        configurarCeldaComboBoxActividad(actividad, actividadesObservable);
    }

    @Override
    public void onClose(Object output) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actividadService = new ActividadService();
        categoriaService = new CategoriaService();
        List<Actividad> actividades = actividadService.listar();
        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        actividad.setItems(actividadesObservable);
        List<Categoria> categorias = categoriaService.listar();
        ObservableList<Categoria> categoriasObservable = FXCollections.observableArrayList(categorias);
        categoria.setItems(categoriasObservable);
        configurarCeldaComboBoxCategoria(categoria, categoriasObservable);
        configurarCeldaComboBoxActividad(actividad, actividadesObservable);
    }


    private void configurarCeldaComboBoxCategoria(ComboBox<Categoria> comboBox, ObservableList<Categoria> observableList) {
        Callback<ListView<Categoria>, ListCell<Categoria>> categoriaCellFactory = new Callback<ListView<Categoria>, ListCell<Categoria>>() {
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

        comboBox.setCellFactory(categoriaCellFactory);
        comboBox.setButtonCell(categoriaCellFactory.call(null));
    }

    private void configurarCeldaComboBoxActividad(ComboBox<Actividad> comboBox, ObservableList<Actividad> observableList) {
        Callback<ListView<Actividad>, ListCell<Actividad>> actividadCellFactory = new Callback<ListView<Actividad>, ListCell<Actividad>>() {
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

        comboBox.setCellFactory(actividadCellFactory);
        comboBox.setButtonCell(actividadCellFactory.call(null));
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

            // Validaciones adicionales
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
            huellaService.guardarHuella(huella);
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Utils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
