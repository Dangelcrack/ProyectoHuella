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

/**
 * Controlador para la vista de agregar una nueva huella de carbono.
 */
public class AddTrackController extends Controller implements Initializable {

    // Elementos de la interfaz gráfica
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

    // Servicios y controladores
    private HuellaController huellaController;
    private Usuario usuario;
    private HuellaService huellaService;
    private ActividadService actividadService;
    private CategoriaService categoriaService;

    /**
     * Método que se ejecuta al abrir la vista.
     * @param usuario Usuario autenticado
     * @param input Objeto de entrada (debe ser una instancia de HuellaController)
     */
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
        this.actividadService = new ActividadService();
        this.categoriaService = new CategoriaService();
        this.huellaController = (HuellaController) input;

        // Obtener listas de actividades y categorías
        List<Actividad> actividades = actividadService.listar();
        List<Categoria> categorias = categoriaService.listar();

        // Convertir listas en observables para los ComboBox
        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        ObservableList<Categoria> categoriasObservable = FXCollections.observableArrayList(categorias);

        actividad.setItems(actividadesObservable);
        categoria.setItems(categoriasObservable);

        // Configurar celdas de los ComboBox
        configurarCeldaComboBoxCategoria(categoria, categoriasObservable);
        configurarCeldaComboBoxActividad(actividad, actividadesObservable);
    }

    @Override
    public void onClose(Object output) {
        // Método vacío intencionalmente
    }

    /**
     * Método que se ejecuta al inicializar la vista.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actividadService = new ActividadService();
        categoriaService = new CategoriaService();

        List<Actividad> actividades = actividadService.listar();
        List<Categoria> categorias = categoriaService.listar();

        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        ObservableList<Categoria> categoriasObservable = FXCollections.observableArrayList(categorias);

        actividad.setItems(actividadesObservable);
        categoria.setItems(categoriasObservable);

        configurarCeldaComboBoxCategoria(categoria, categoriasObservable);
        configurarCeldaComboBoxActividad(actividad, actividadesObservable);
        categoria.setOnAction(event -> {
            Categoria selectedCategoria = categoria.getValue();
            if (selectedCategoria != null) {
                unidad.setText("Unidad: " + selectedCategoria.getUnidad());
            }
        });

        // Listener para actualizar la unidad al seleccionar una actividad
        actividad.setOnAction(event -> {
            Actividad selectedActividad = actividad.getValue();
            if (selectedActividad != null) {
                Categoria categoriaActividad = selectedActividad.getIdCategoria();
                if (categoriaActividad != null) {
                    unidad.setText("Unidad: " + categoriaActividad.getUnidad());
                }
            }
        });
    }

    /**
     * Configura las celdas del ComboBox de categorías.
     */
    private void configurarCeldaComboBoxCategoria(ComboBox<Categoria> comboBox, ObservableList<Categoria> observableList) {
        comboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                setText((item != null && !empty) ? item.getNombre() : null);
            }
        });
        comboBox.setButtonCell(comboBox.getCellFactory().call(null));
    }

    /**
     * Configura las celdas del ComboBox de actividades.
     */
    private void configurarCeldaComboBoxActividad(ComboBox<Actividad> comboBox, ObservableList<Actividad> observableList) {
        comboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Actividad item, boolean empty) {
                super.updateItem(item, empty);
                setText((item != null && !empty) ? item.getNombre() : null);
            }
        });
        comboBox.setButtonCell(comboBox.getCellFactory().call(null));
    }

    /**
     * Cierra la ventana y guarda la huella de carbono.
     */
    @FXML
    private void closeWindow(Event event) {
        try {
            Categoria selectedCategoria = categoria.getValue();
            Actividad selectedActividad = actividad.getValue();
            LocalDate selectedDate = fecha.getValue();
            String valorInput = valor.getText();

            // Validaciones de datos ingresados
            if (selectedDate != null && selectedDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha no puede ser posterior a la fecha de hoy.");
            }
            if (selectedCategoria == null || selectedActividad == null || selectedDate == null) {
                throw new IllegalArgumentException("Debe completar todos los campos.");
            }
            if (valorInput == null || valorInput.isEmpty() || !valorInput.matches("\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException("El valor debe ser un número válido.");
            }
            if (!selectedCategoria.getId().equals(selectedActividad.getIdCategoria().getId())) {
                throw new IllegalArgumentException("La categoría y la actividad deben tener la misma unidad.");
            }

            // Creación de la huella de carbono
            double valorNumerico = Double.parseDouble(valorInput);
            Huella huella = new Huella();
            huella.setValor(BigDecimal.valueOf(valorNumerico));
            huella.setUnidad(selectedCategoria.getUnidad());
            huella.setIdActividad(selectedActividad);
            huella.setFecha(LocalDateTime.of(selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDayOfMonth(), 0, 0));
            huella.setIdUsuario(usuario);

            // Guardar la huella y cerrar la ventana
            huellaController.guardarHuella(huella);
            huellaService.guardarHuella(huella);
            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Utils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
