package com.github.dangelcrack.controller;

import com.github.dangelcrack.model.dao.CategoriaDAO;
import com.github.dangelcrack.model.dao.HuellaDAO;
import com.github.dangelcrack.model.dao.RecomendacionDAO;
import com.github.dangelcrack.model.entity.Categoria;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Recomendacion;
import com.github.dangelcrack.model.entity.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ImpactsController extends Controller implements Initializable {

    /** FXML Table Views and Columns */
    @FXML
    private TableView<Huella> tableViewhuellas;
    @FXML
    private TableColumn<Huella, String> actividadColumnhuellas;
    @FXML
    private TableColumn<Huella, Integer> valorColumnhuellas;
    @FXML
    private TableColumn<Huella, String> unidadColumnhuellas;
    @FXML
    private TableColumn<Huella, String> fechaColumnhuellas;
    @FXML
    private TableColumn<Huella, String> cantidadEmitida;

    @FXML
    private String ultimaRecomendacion = "";

    @FXML
    private TableView<Categoria> tableViewCalculo;
    @FXML
    private TableColumn<Categoria, String> categoriaColumnCalculo;
    @FXML
    private TableColumn<Categoria, String> totalColumnCalculo;
    @FXML
    private TableColumn<Categoria, String> mediaDiariaColumnCalculo;
    @FXML
    private TableColumn<Categoria, String> mediaSemanalColumnCalculo;
    @FXML
    private TableColumn<Categoria, String> mediaMensualColumnCalculo;

    @FXML
    private Button btnAccion;
    @FXML
    private Text textRecomendacion;

    /** Observable Lists to store data */
    private ObservableList<Huella> huellas;
    private ObservableList<Categoria> categorias;

    /**
     * This method is triggered when the view is opened for a user.
     * It loads the user's "huellas" and associated categories.
     * @param usuario the user whose data is being displayed
     * @param input any additional input, currently unused
     * @throws IOException in case of an error during the loading process
     */
    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        List<Huella> huellasList = HuellaDAO.build().obtenerHuellasPorUsuario(usuario);
        this.huellas = FXCollections.observableArrayList(huellasList);
        tableViewhuellas.setItems(this.huellas);

        List<Categoria> categoriaList = cargarCategoriasRelacionadasConActividades(huellasList);
        this.categorias = FXCollections.observableArrayList(categoriaList);
        tableViewCalculo.setItems(this.categorias);
    }

    /** Method called when the view is closed, currently no actions required */
    @Override
    public void onClose(Object output) {}

    /**
     * This method loads the categories related to the activities in the given list of "huellas".
     * It filters categories that have a matching "unidad" with any of the huellas.
     * @param huellas the list of huellas
     * @return a filtered list of categories related to the huellas
     */
    private List<Categoria> cargarCategoriasRelacionadasConActividades(List<Huella> huellas) {
        List<Categoria> todasLasCategorias = CategoriaDAO.build().listar();
        return todasLasCategorias.stream()
                .filter(categoria -> huellas.stream().anyMatch(huella ->
                        huella.getUnidad() != null &&
                                huella.getUnidad().equalsIgnoreCase(categoria.getUnidad())
                ))
                .toList();
    }

    /**
     * This method calculates the carbon footprint (Huella) for a given "huella" and its emission factor.
     * @param factorEmision the emission factor for the category
     * @param huella the specific huella data to calculate
     * @return the calculated carbon footprint
     */
    private double calcularHuella(BigDecimal factorEmision, Huella huella) {
        if (factorEmision == null || huella.getValor() == null) {
            return 0;
        }
        return factorEmision.doubleValue() * huella.getValor().doubleValue();
    }

    /** Initialize the table columns and actions when the controller is loaded */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnasTablaHuellas();  // Set up the columns for the Huella table
        configurarColumnasTablaCalculo();  // Set up the columns for the Calculation table

        btnAccion.setOnAction(event -> handleRecomendacion());  // Action handler for recommendation button
        tableViewhuellas.getStylesheets().add(getClass().getResource("/com/github/dangelcrack/css/styles.css").toExternalForm());  // Add styles to table
        tableViewCalculo.getStylesheets().add(getClass().getResource("/com/github/dangelcrack/css/styles.css").toExternalForm());  // Add styles to table
    }

    /**
     * Configures the columns of the Huella table to display the necessary data.
     * This includes activity, value, unit, date, and emission quantity.
     */
    private void configurarColumnasTablaHuellas() {
        actividadColumnhuellas.setCellValueFactory(huella ->
                new SimpleStringProperty(huella.getValue().getIdActividad() != null ? huella.getValue().getIdActividad().getNombre() : ""));
        valorColumnhuellas.setCellValueFactory(new PropertyValueFactory<>("valor"));
        unidadColumnhuellas.setCellValueFactory(huella -> new SimpleStringProperty(huella.getValue().getUnidad()));
        fechaColumnhuellas.setCellValueFactory(huella -> new SimpleStringProperty(
                huella.getValue().getFecha() != null ? huella.getValue().getFecha().toLocalDate().toString() : ""));
        cantidadEmitida.setCellValueFactory(huella -> {
            Categoria categoriaAsociada = categorias.stream()
                    .filter(categoria -> huella.getValue().getUnidad().equalsIgnoreCase(categoria.getUnidad()))
                    .findFirst()
                    .orElse(null);

            BigDecimal factorEmision = (categoriaAsociada != null) ? categoriaAsociada.getFactorEmision() : BigDecimal.ZERO;
            double cantidad = calcularHuella(factorEmision, huella.getValue());

            return new SimpleStringProperty(String.format("%.2f kg CO₂", cantidad));
        });
    }

    /**
     * Configures the columns of the Categoria calculation table.
     * This table calculates the total impact, and average daily, weekly, and monthly emissions for each category.
     */
    private void configurarColumnasTablaCalculo() {
        categoriaColumnCalculo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        totalColumnCalculo.setCellValueFactory(categoria -> {
            double totalImpacto = huellas.stream()
                    .filter(huella -> huella.getUnidad().equalsIgnoreCase(categoria.getValue().getUnidad()))
                    .mapToDouble(huella -> calcularHuella(categoria.getValue().getFactorEmision(), huella))
                    .sum();
            return new SimpleStringProperty(String.format("%.2f kg CO₂", totalImpacto));
        });
        mediaDiariaColumnCalculo.setCellValueFactory(categoria -> calcularMedia(categoria, 1));
        mediaSemanalColumnCalculo.setCellValueFactory(categoria -> calcularMedia(categoria, 7));
        mediaMensualColumnCalculo.setCellValueFactory(categoria -> calcularMedia(categoria, 30));
    }

    /**
     * Calculates the average carbon footprint for a given category over a specified number of days.
     * @param categoria the category to calculate the average for
     * @param dias the number of days for the average (1, 7, 30)
     * @return a SimpleStringProperty representing the calculated average
     */
    private SimpleStringProperty calcularMedia(TableColumn.CellDataFeatures<Categoria, String> categoria, int dias) {
        LocalDate hoy = LocalDate.now();
        List<Huella> huellasFiltradas = huellas.stream()
                .filter(huella -> huella.getUnidad().equalsIgnoreCase(categoria.getValue().getUnidad()))
                .collect(Collectors.toList());

        double totalImpacto = 0;
        double totalDias = 0;
        if (dias == 1) {
            var huellasPorDia = huellasFiltradas.stream()
                    .collect(Collectors.groupingBy(huella -> huella.getFecha().toLocalDate()));
            for (var entry : huellasPorDia.entrySet()) {
                List<Huella> huellasDelDia = entry.getValue();
                double impactoDelDia = huellasDelDia.stream()
                        .mapToDouble(huella -> calcularHuella(categoria.getValue().getFactorEmision(), huella))
                        .sum();
                double mediaDelDia = impactoDelDia / huellasDelDia.size();
                totalImpacto += mediaDelDia;
                totalDias += 1;
            }
        } else {
            for (Huella huella : huellasFiltradas) {
                long diasTranscurridos = hoy.toEpochDay() - huella.getFecha().toLocalDate().toEpochDay();
                if (diasTranscurridos <= dias) {
                    double impactoHuella = calcularHuella(categoria.getValue().getFactorEmision(), huella);
                    totalImpacto += impactoHuella;
                    totalDias += 1;
                }
            }
        }
        if (totalDias > 0) {
            double media = totalImpacto / totalDias;
            return new SimpleStringProperty(String.format("%.2f kg CO₂", media));
        } else {
            return new SimpleStringProperty("0.00 kg CO₂");
        }
    }

    /**
     * This method handles the recommendation action.
     * It selects a random recommendation based on the associated categories and displays it.
     */
    @FXML
    private void handleRecomendacion() {
        if (!huellas.isEmpty()) {
            List<Categoria> categoriasAsociadas = huellas.stream()
                    .map(huella -> huella.getUnidad())
                    .distinct()
                    .map(unidad -> categorias.stream()
                            .filter(categoria -> categoria.getUnidad().equalsIgnoreCase(unidad))
                            .findFirst()
                            .orElse(null))
                    .filter(categoria -> categoria != null)
                    .collect(Collectors.toList());
            if (!categoriasAsociadas.isEmpty()) {
                RecomendacionDAO recomendacionDAO = RecomendacionDAO.build();
                List<Recomendacion> recomendaciones = categoriasAsociadas.stream()
                        .flatMap(categoria -> recomendacionDAO.filtrarPorHuella(categoria.getId()).stream())
                        .collect(Collectors.toList());
                if (!recomendaciones.isEmpty()) {
                    Recomendacion recomendacion = null;
                    while (recomendacion == null || recomendacion.getDescripcion().equals(ultimaRecomendacion)) {
                        int indiceAleatorio = (int) (Math.random() * recomendaciones.size());
                        recomendacion = recomendaciones.get(indiceAleatorio);
                    }
                    ultimaRecomendacion = recomendacion.getDescripcion();
                    textRecomendacion.setText(ultimaRecomendacion);
                }
            }
        } else {
            textRecomendacion.setText("El usuario no tiene huellas asociadas.");
        }
    }

}
