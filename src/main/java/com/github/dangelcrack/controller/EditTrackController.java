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

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador para la edición de huellas de carbono.
 */
public class EditTrackController extends Controller implements Initializable {

    @FXML
    private VBox vbox;
    @FXML
    private TextField valor;
    @FXML
    private ComboBox<Huella> huellaComboBox;
    @FXML
    private Text unidad;
    @FXML
    private ComboBox<Categoria> categoria;
    @FXML
    private ComboBox<Actividad> actividad;
    @FXML
    private DatePicker fecha;

    private Usuario usuario;
    private HuellaService huellaService;
    private ActividadService actividadService;
    private CategoriaService categoriaService;
    private HuellaController huellaController;

    /**
     * Método llamado al abrir la ventana de edición de huellas.
     *
     * @param usuario El usuario que está editando la huella.
     * @param input   El controlador de huellas.
     * @throws IOException Si ocurre un error al abrir la ventana.
     */
    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        this.usuario = usuario;
        this.huellaService = new HuellaService();
        this.actividadService = new ActividadService();
        this.categoriaService = new CategoriaService();
        this.huellaController = (HuellaController) input;
        cargarHuellasUsuario();
    }

    /**
     * Método llamado al cerrar la ventana de edición de huellas.
     *
     * @param output El objeto de salida (no utilizado en este caso).
     */
    @Override
    public void onClose(Object output) {}

    /**
     * Método de inicialización del controlador.
     *
     * @param url            La ubicación relativa del archivo FXML.
     * @param resourceBundle El recurso de paquete para localizar el archivo FXML.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.actividadService = new ActividadService();
        this.categoriaService = new CategoriaService();
        cargarCategoriasYActividades();
        valor.setTextFormatter(new TextFormatter<>(change ->
                change.getControlNewText().matches("\\d*\\.?\\d*") ? change : null
        ));

        huellaComboBox.setOnAction(event -> rellenarCampos(huellaComboBox.getValue()));
    }

    /**
     * Carga las huellas del usuario en el ComboBox.
     */
    private void cargarHuellasUsuario() {
        List<Huella> huellas = huellaService.obtenerHuellasPorUsuario(usuario);
        ObservableList<Huella> huellasObservable = FXCollections.observableArrayList(huellas);
        huellaComboBox.setItems(huellasObservable);

        huellaComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Huella item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null :
                        item.getIdActividad().getNombre() + " - " + item.getValor() + " " + item.getUnidad());
            }
        });

        huellaComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Huella item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "Seleccione una huella" :
                        item.getIdActividad().getNombre() + " - " + item.getValor() + " " + item.getUnidad());
            }
        });
    }

    /**
     * Carga las categorías y actividades en los ComboBox correspondientes.
     */
    private void cargarCategoriasYActividades() {
        // Obtener datos desde servicios
        List<Actividad> actividades = actividadService.listar();
        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        actividad.setItems(actividadesObservable);

        actividad.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Actividad item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : "Actividad: " + item.getNombre());
            }
        });

        actividad.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Actividad item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "Seleccione una actividad" : item.getNombre());
            }
        });

        List<Categoria> categorias = categoriaService.listar();
        ObservableList<Categoria> categoriasObservable = FXCollections.observableArrayList(categorias);
        categoria.setItems(categoriasObservable);

        categoria.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? null : "Categoría: " + item.getNombre());
            }
        });

        categoria.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Categoria item, boolean empty) {
                super.updateItem(item, empty);
                setText((item == null || empty) ? "Seleccione una categoría" : item.getNombre());
            }
        });

        categoria.setOnAction(event -> {
            Categoria selectedCategoria = categoria.getValue();
            if (selectedCategoria != null) {
                unidad.setText("Unidad: " + selectedCategoria.getUnidad());
            }
        });
    }

    /**
     * Rellena los campos del formulario con los datos de la huella seleccionada.
     *
     * @param huella La huella seleccionada.
     */
    private void rellenarCampos(Huella huella) {
        if (huella != null) {
            for (Actividad act : actividad.getItems()) {
                if (act.getId().equals(huella.getIdActividad().getId())) {
                    actividad.setValue(act);
                    break;
                }
            }
            for (Categoria cat : categoria.getItems()) {
                if (cat.getId().equals(huella.getIdActividad().getIdCategoria().getId())) {
                    categoria.setValue(cat);
                    break;
                }
            }
            valor.setText(huella.getValor().toString());
            unidad.setText("Unidad: " + huella.getUnidad());
            fecha.setValue(huella.getFecha().toLocalDate());
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
            Categoria selectedCategoria = categoria.getValue();
            Actividad selectedActividad = actividad.getValue();
            LocalDate selectedDate = fecha.getValue();
            String valorInput = valor.getText();

            if (selectedDate == null || selectedDate.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha no puede ser posterior a la fecha de hoy.");
            }
            if (selectedCategoria == null) {
                throw new IllegalArgumentException("Debe seleccionar una categoría.");
            }
            if (selectedActividad == null) {
                throw new IllegalArgumentException("Debe seleccionar una actividad.");
            }
            if (valorInput == null || valorInput.isEmpty() || !valorInput.matches("\\d+(\\.\\d+)?")) {
                throw new IllegalArgumentException("El valor debe ser un número válido.");
            }
            if (!selectedCategoria.getId().equals(selectedActividad.getIdCategoria().getId())) {
                throw new IllegalArgumentException("La categoría y la actividad deben coincidir.");
            }

            double valorNumerico = Double.parseDouble(valorInput);
            Huella huella = new Huella();
            huella.setValor(BigDecimal.valueOf(valorNumerico));
            huella.setUnidad(selectedCategoria.getUnidad());
            huella.setIdActividad(selectedActividad);
            huella.setFecha(LocalDateTime.of(selectedDate.getYear(), selectedDate.getMonth(), selectedDate.getDayOfMonth(), 0, 0));
            huella.setIdUsuario(usuario);
            huella.setId(huellaComboBox.getValue().getId());
            if (!huellaService.actualizarHuella(huella)) {
                throw new IllegalArgumentException("No se pudo guardar la huella.");
            }
            huellaController.actualizarHuella(huella);

            ((Node) (event.getSource())).getScene().getWindow().hide();
        } catch (IllegalArgumentException e) {
            Utils.showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}