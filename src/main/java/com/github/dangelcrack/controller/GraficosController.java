package com.github.dangelcrack.controller;

import com.github.dangelcrack.model.dao.CategoriaDAO;
import com.github.dangelcrack.model.dao.HuellaDAO;
import com.github.dangelcrack.model.dao.RecomendacionDAO;
import com.github.dangelcrack.model.entity.Categoria;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Recomendacion;
import com.github.dangelcrack.model.entity.Usuario;
import com.github.dangelcrack.model.services.HuellaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class GraficosController extends Controller implements Initializable {

    @FXML
    private String ultimaRecomendacion = "";

    @FXML
    private Button btnAccion;
    @FXML
    private Button btnMediaDiaria;
    @FXML
    private Button btnMediaSemanal;
    @FXML
    private Button btnMediaMensual;
    @FXML
    private Text textRecomendacion;

    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private PieChart pieChart;

    private ObservableList<Huella> huellas;
    private ObservableList<Categoria> categorias;

    private HuellaService huellaService = new HuellaService(); // Creamos una instancia del servicio

    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        // Obtener huellas y categorías relacionadas usando el servicio
        huellas = FXCollections.observableArrayList(huellaService.obtenerHuellasPorUsuario(usuario));
        categorias = FXCollections.observableArrayList(cargarCategoriasRelacionadasConActividades(huellas));

        // Actualizar gráficos
        actualizarGraficos();
    }

    @Override
    public void onClose(Object output) {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnAccion.setOnAction(event -> handleRecomendacion());
        btnMediaDiaria.setOnAction(event -> handleMediaDiaria());
        btnMediaSemanal.setOnAction(event -> handleMediaSemanal());
        btnMediaMensual.setOnAction(event -> handleMediaMensual());
    }

    /**
     * Carga las categorías relacionadas con las huellas del usuario.
     *
     * @param huellas Lista de huellas del usuario
     * @return Lista de categorías relacionadas
     */
    private List<Categoria> cargarCategoriasRelacionadasConActividades(List<Huella> huellas) {
        List<Categoria> todasLasCategorias = CategoriaDAO.build().listar();
        List<Categoria> categoriasRelacionadas = new ArrayList<>();

        for (Categoria categoria : todasLasCategorias) {
            for (Huella huella : huellas) {
                if (huella.getUnidad() != null && huella.getUnidad().equalsIgnoreCase(categoria.getUnidad())) {
                    categoriasRelacionadas.add(categoria);
                    break;
                }
            }
        }

        return categoriasRelacionadas;
    }

    /**
     * Calcula la huella de carbono para una huella específica.
     *
     * @param factorEmision El factor de emisión asociado
     * @param huella La huella a calcular
     * @return El valor calculado de la huella
     */
    private double calcularHuella(BigDecimal factorEmision, Huella huella) {
        if (factorEmision == null || huella.getValor() == null) {
            return 0;
        }
        return factorEmision.doubleValue() * huella.getValor().doubleValue();
    }

    /**
     * Actualiza los gráficos de barras y circular.
     */
    private void actualizarGraficos() {
        barChart.getData().clear();
        pieChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Huella de Carbono");
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        for (Categoria categoria : categorias) {
            double totalHuella = 0;
            for (Huella huella : huellas) {
                if (huella.getUnidad().equalsIgnoreCase(categoria.getUnidad())) {
                    totalHuella += calcularHuella(categoria.getFactorEmision(), huella);
                }
            }
            series.getData().add(new XYChart.Data<>(categoria.getNombre(), totalHuella));
            pieChartData.add(new PieChart.Data(categoria.getNombre(), totalHuella));
        }
        barChart.getData().add(series);
        pieChart.setData(pieChartData);
    }

    /**
     * Muestra una recomendación basada en las huellas del usuario.
     */
    @FXML
    private void handleRecomendacion() {
        if (huellas.isEmpty()) {
            textRecomendacion.setText("El usuario no tiene huellas asociadas.");
            return;
        }

        // Obtener categorías asociadas a las huellas
        Set<String> unidades = new HashSet<>();
        for (Huella huella : huellas) {
            unidades.add(huella.getUnidad());
        }

        List<Categoria> categoriasAsociadas = new ArrayList<>();
        for (String unidad : unidades) {
            for (Categoria categoria : categorias) {
                if (categoria.getUnidad().equalsIgnoreCase(unidad)) {
                    categoriasAsociadas.add(categoria);
                    break;
                }
            }
        }

        if (categoriasAsociadas.isEmpty()) {
            textRecomendacion.setText("No hay recomendaciones disponibles.");
            return;
        }

        // Obtener recomendaciones
        RecomendacionDAO recomendacionDAO = RecomendacionDAO.build();
        List<Recomendacion> recomendaciones = new ArrayList<>();
        for (Categoria categoria : categoriasAsociadas) {
            recomendaciones.addAll(recomendacionDAO.filtrarPorHuella(categoria.getId()));
        }

        if (recomendaciones.isEmpty()) {
            textRecomendacion.setText("No hay recomendaciones disponibles.");
            return;
        }

        // Seleccionar una recomendación aleatoria
        Recomendacion recomendacion;
        do {
            int indiceAleatorio = (int) (Math.random() * recomendaciones.size());
            recomendacion = recomendaciones.get(indiceAleatorio);
        } while (recomendacion.getDescripcion().equals(ultimaRecomendacion));

        // Mostrar la recomendación
        ultimaRecomendacion = recomendacion.getDescripcion();
        textRecomendacion.setText(ultimaRecomendacion);
    }

    /**
     * Muestra la media diaria de la huella de carbono.
     */
    @FXML
    private void handleMediaDiaria() {
        double mediaDiaria = calcularMedia(1); // 1 día
        textRecomendacion.setText(String.format("Media Diaria: %.2f kg CO₂", mediaDiaria));

        // Actualizar gráficos
        actualizarGraficos();
    }

    /**
     * Muestra la media semanal de la huella de carbono.
     */
    @FXML
    private void handleMediaSemanal() {
        double mediaSemanal = calcularMedia(7); // 7 días
        textRecomendacion.setText(String.format("Media Semanal: %.2f kg CO₂", mediaSemanal));

        // Actualizar gráficos
        actualizarGraficos();
    }

    /**
     * Muestra la media mensual de la huella de carbono.
     */
    @FXML
    private void handleMediaMensual() {
        double mediaMensual = calcularMedia(30); // 30 días
        textRecomendacion.setText(String.format("Media Mensual: %.2f kg CO₂", mediaMensual));

        // Actualizar gráficos
        actualizarGraficos();
    }

    /**
     * Calcula la media de la huella de carbono para un período de tiempo específico.
     *
     * @param dias Número de días para calcular la media
     * @return La media de la huella de carbono
     */
    private double calcularMedia(int dias) {
        if (huellas.isEmpty()) {
            return 0;
        }

        double totalHuella = 0;
        for (Huella huella : huellas) {
            totalHuella += calcularHuella(huella.getIdActividad().getIdCategoria().getFactorEmision(), huella);
        }

        return totalHuella / dias;
    }
}