package com.github.dangelcrack.controller;

import com.github.dangelcrack.model.dao.ActividadDAO;
import com.github.dangelcrack.model.dao.CategoriaDAO;
import com.github.dangelcrack.model.dao.HuellaDAO;
import com.github.dangelcrack.model.entity.Actividad;
import com.github.dangelcrack.model.entity.Categoria;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Usuario;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;

public class EditTrackController extends Controller implements Initializable {

    @FXML
    private VBox vbox;
    @FXML
    private ComboBox<Huella> huellaComboBox;
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
    private ObservableList<Huella> huellasObservable;

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
        cargarHuellasUsuario();
    }

    @Override
    public void onClose(Object output) {
        // No se necesita implementación para este caso
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarCategoriasYActividades();
        huellaComboBox.setOnAction(event -> rellenarCamposConHuellaSeleccionada());

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
        // Cargar datos para el ComboBox de Actividad
        List<Actividad> actividades = ActividadDAO.build().listar();
        ObservableList<Actividad> actividadesObservable = FXCollections.observableArrayList(actividades);
        actividad.setItems(actividadesObservable);

        // Cargar datos para el ComboBox de Categoría
        List<Categoria> categorias = CategoriaDAO.build().listar();
        ObservableList<Categoria> categoriasObservable = FXCollections.observableArrayList(categorias);
        categoria.setItems(categoriasObservable);

        // Configurar CellFactory para mostrar los nombres en los ComboBox
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

        // Actualizar la unidad cuando se selecciona una categoría
        categoria.setOnAction(event -> {
            Categoria selectedCategoria = categoria.getValue();
            if (selectedCategoria != null && unidad != null) {
                unidad.setText("Unidad: " + selectedCategoria.getUnidad());
            }
        });
    }

    private void cargarHuellasUsuario() {
        List<Huella> huellas = HuellaDAO.build().obtenerHuellasPorUsuario(usuario);
        huellasObservable = FXCollections.observableArrayList(huellas);
        huellaComboBox.setItems(huellasObservable);
        huellaComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Huella item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    String displayText = "Actividad: " + item.getIdActividad().getNombre();
                    BigDecimal valor = item.getValor();
                    LocalDateTime fecha = item.getFecha();
                    if (valor != null && fecha != null) {
                        displayText += " - "+item.getUnidad()+": " + valor + " - Fecha: " + fecha.toLocalDate();
                    }

                    setText(displayText);
                } else {
                    setText(null);
                }
            }
        });
        huellaComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Huella item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    String displayText = "Actividad: " + item.getIdActividad().getNombre();
                    BigDecimal valor = item.getValor();
                    LocalDateTime fecha = item.getFecha();
                    if (valor != null && fecha != null) {
                        displayText += " - "+item.getUnidad()+": " + valor + " - Fecha: " + fecha.toLocalDate();
                    }

                    setText(displayText);
                } else {
                    setText(null);
                }
            }
        });
    }


    private void rellenarCamposConHuellaSeleccionada() {
        Huella huellaSeleccionada = huellaComboBox.getValue();
        if (huellaSeleccionada != null) {
            valor.setText(huellaSeleccionada.getValor().toString());
            unidad.setText("Unidad: " + huellaSeleccionada.getUnidad());

            // Buscar la categoría en la lista del ComboBox
            Categoria categoriaSeleccionada = categoria.getItems().stream()
                    .filter(c -> c.getId().equals(huellaSeleccionada.getIdActividad().getIdCategoria().getId()))
                    .findFirst()
                    .orElse(null);
            categoria.setValue(categoriaSeleccionada);

            // Buscar la actividad en la lista del ComboBox
            Actividad actividadSeleccionada = actividad.getItems().stream()
                    .filter(a -> a.getId().equals(huellaSeleccionada.getIdActividad().getId()))
                    .findFirst()
                    .orElse(null);
            actividad.setValue(actividadSeleccionada);
            fecha.setValue(huellaSeleccionada.getFecha().toLocalDate());

        }
    }

    @FXML
    private void closeWindow(Event event) {
        Categoria selectedCategoria = categoria.getValue();
        Actividad selectedActividad = actividad.getValue();
        LocalDate selectedDate = fecha.getValue();
        String valorInput = valor.getText();

        if (selectedCategoria == null || selectedActividad == null || selectedDate == null || valorInput.isEmpty()) {
            throw new IllegalArgumentException("Todos los campos deben estar completos.");
        }

        BigDecimal valorNumerico;
        try {
            valorNumerico = new BigDecimal(valorInput);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El valor debe ser un número válido.");
        }

        LocalDateTime fechaCompleta = selectedDate.atStartOfDay();

        Huella nuevaHuella = new Huella();
        if (huellaComboBox.getValue() != null) {
            nuevaHuella.setId(huellaComboBox.getValue().getId());
        }
        nuevaHuella.setValor(valorNumerico);
        nuevaHuella.setUnidad(selectedCategoria.getUnidad());
        nuevaHuella.setIdActividad(selectedActividad);
        nuevaHuella.setFecha(fechaCompleta);
        nuevaHuella.setIdUsuario(usuario);

        huellaController.actualizarHuella(nuevaHuella);

        // Cerrar ventana
        ((Node) (event.getSource())).getScene().getWindow().hide();
    }
}
