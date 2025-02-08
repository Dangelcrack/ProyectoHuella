package com.github.dangelcrack.controller;

import com.github.dangelcrack.model.connection.Connection;
import com.github.dangelcrack.model.dao.UsuarioDAO;
import com.github.dangelcrack.model.entity.Huella;
import com.github.dangelcrack.model.entity.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.hibernate.Hibernate;
import org.hibernate.Session;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Controlador para la pantalla de ranking de usuarios.
 * Muestra un ranking basado en las huellas de carbono de los usuarios.
 */
public class RankingController extends Controller implements Initializable {

    /** Contenedor para la interfaz de usuario */
    @FXML
    private VBox vBox;

    /** Tabla para mostrar los usuarios en el ranking */
    @FXML
    private TableView<Usuario> tableView;

    /** Columna para mostrar el nombre del usuario */
    @FXML
    private TableColumn<Usuario, String> nameColumn;

    /** Columna para mostrar la posición en el ranking */
    @FXML
    private TableColumn<Usuario, String> posicionColumn;

    /** Columna para mostrar la media de huellas del usuario */
    @FXML
    private TableColumn<Usuario, String> cantidadColumn;
    @FXML
    private TableColumn<Usuario, String> categoriaColumn;

    /** Columna para mostrar la cantidad de huellas del usuario */
    @FXML
    private TableColumn<Usuario, String> cantidadDeHuellas;

    /** Lista observable de usuarios */
    private ObservableList<Usuario> usuarios;

    /** Número de días para calcular la huella de carbono reciente */
    private final static int dias = 30;

    /**
     * Método que se ejecuta al abrir la ventana de este controlador.
     * Carga la lista de usuarios con sus detalles y asigna posiciones.
     *
     * @param usuario El usuario que abre la ventana.
     * @param input Entrada adicional (no utilizada en este caso).
     * @throws IOException Si ocurre un error al abrir la ventana.
     */
    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        // Obtiene la lista de usuarios con sus huellas
        List<Usuario> usuariosList = UsuarioDAO.build().fetchUsuariosWithDetails();

        // Ordena y asigna las posiciones a los usuarios
        ordenarYAsignarPosiciones(usuariosList);

        // Convierte la lista de usuarios en una lista observable para la tabla
        usuarios = FXCollections.observableArrayList(usuariosList);

        // Asigna los datos a la tabla
        tableView.setItems(usuarios);
    }

    /**
     * Método que se ejecuta al cerrar la ventana de este controlador.
     *
     * @param output Salida adicional (no utilizada en este caso).
     */
    @Override
    public void onClose(Object output) {
        // Optional: Any cleanup can be added here
    }

    /**
     * Método que inicializa la interfaz de usuario al cargar la ventana.
     * Configura las columnas de la tabla.
     *
     * @param url La URL del archivo FXML (no utilizada en este caso).
     * @param resourceBundle El recurso de idioma (no utilizado en este caso).
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Añade la hoja de estilos para la tabla
        tableView.getStylesheets().add(getClass().getResource("/com/github/dangelcrack/css/styles.css").toExternalForm());

        // Configura la política de redimensionamiento de las columnas
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Configura las columnas de la tabla
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        posicionColumn.setCellValueFactory(usuario -> new SimpleStringProperty(String.valueOf(usuarios.indexOf(usuario.getValue()) + 1)));
        cantidadColumn.setCellValueFactory(usuario -> calcularMedia(usuario.getValue()));
        cantidadDeHuellas.setCellValueFactory(usuario -> new SimpleStringProperty(String.valueOf(usuario.getValue().getHuellas().size())));
        categoriaColumn.setCellValueFactory(usuario -> {
            // Obtiene la lista de huellas del usuario
            List<Huella> huellas = usuario.getValue().getHuellas();

            // Si no hay huellas, devuelve "No registrado"
            if (huellas.isEmpty()) {
                return new SimpleStringProperty("No registrado");
            }

            // Obtiene todas las categorías únicas de las huellas
            String categorias = huellas.stream()
                    .map(huella -> huella.getIdActividad().getIdCategoria().getNombre())
                    .distinct() // Elimina duplicados
                    .collect(Collectors.joining(", ")); // Une las categorías con comas

            return new SimpleStringProperty(categorias);
        });
    }

    /**
     * Calcula la media de huellas de un usuario durante los últimos 30 días.
     *
     * @param usuario El usuario cuyo impacto se va a calcular.
     * @return Un objeto SimpleStringProperty con la media de huellas en formato de cadena.
     */
    private SimpleStringProperty calcularMedia(Usuario usuario) {
        LocalDate hoy = LocalDate.now();

        // Filtra las huellas que están dentro de los últimos 30 días
        List<Huella> huellasFiltradas = usuario.getHuellas();

        // Calcula el total de impacto de las huellas filtradas
        double totalImpacto = huellasFiltradas.stream()
                .filter(huella -> hoy.toEpochDay() - huella.getFecha().toLocalDate().toEpochDay() <= dias)
                .mapToDouble(this::calcularHuella)
                .sum();

        // Calcula la media de huellas, considerando que si no tiene huellas, es 0
        double media = huellasFiltradas.isEmpty() ? 0.0 : totalImpacto / huellasFiltradas.size();
        return new SimpleStringProperty(huellasFiltradas.isEmpty() ? "No registrado" : String.format("%.2f kg CO₂", media));
    }

    /**
     * Calcula la huella de carbono basada en una huella específica.
     *
     * @param huella La huella que se va a calcular.
     * @return El valor calculado de la huella de carbono.
     */
    private double calcularHuella(Huella huella) {
        // Si no hay factor de emisión o valor, se considera 0
        if (huella.getIdActividad().getIdCategoria().getFactorEmision() == null || huella.getValor() == null) {
            return 0;
        }
        // Calcula la huella multiplicando el factor de emisión por el valor
        return huella.getIdActividad().getIdCategoria().getFactorEmision().doubleValue() * huella.getValor().doubleValue();
    }

    /**
     * Ordena a los usuarios basándose en el tamaño de las huellas, el impacto de las huellas en los últimos 30 días y el nombre del usuario.
     * Asigna también las posiciones en el ranking.
     *
     * @param usuariosList La lista de usuarios a ordenar.
     */
    private void ordenarYAsignarPosiciones(List<Usuario> usuariosList) {
        usuariosList.sort(Comparator.comparingInt((Usuario usuario) -> {
                    // Ordena por la cantidad de huellas del usuario (de mayor a menor)
                    List<Huella> huellas = usuario.getHuellas();
                    return -huellas.size();
                }).thenComparingDouble(usuario -> {
                    // Luego ordena por el total de impacto de las huellas en los últimos 30 días
                    List<Huella> huellas = usuario.getHuellas();
                    return huellas.stream()
                            .filter(huella -> LocalDate.now().toEpochDay() - huella.getFecha().toLocalDate().toEpochDay() <= dias)
                            .mapToDouble(this::calcularHuella)
                            .sum();
                })
                .thenComparing(Usuario::getNombre)); // Finalmente, ordena por el nombre del usuario
    }
}
