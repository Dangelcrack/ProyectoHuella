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

public class RankingController extends Controller implements Initializable {
    @FXML
    private VBox vBox;
    @FXML
    private TableView<Usuario> tableView;
    @FXML
    private TableColumn<Usuario, String> nameColumn;
    @FXML
    private TableColumn<Usuario, String> posicionColumn;
    @FXML
    private TableColumn<Usuario, String> cantidadColumn;
    @FXML
    private TableColumn<Usuario, String> cantidadDeHuellas;

    private ObservableList<Usuario> usuarios;
    private final static int dias = 30;

    @Override
    public void onOpen(Usuario usuario, Object input) throws IOException {
        List<Usuario> usuariosList = UsuarioDAO.build().fetchUsuariosWithDetails();
        ordenarYAsignarPosiciones(usuariosList);
        usuarios = FXCollections.observableArrayList(usuariosList);
        tableView.setItems(usuarios);
    }

    @Override
    public void onClose(Object output) {
        // Optional: Any cleanup can be added here
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tableView.getStylesheets().add(getClass().getResource("/com/github/dangelcrack/css/styles.css").toExternalForm());
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Configurar columnas
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        posicionColumn.setCellValueFactory(usuario -> new SimpleStringProperty(String.valueOf(usuarios.indexOf(usuario.getValue()) + 1)));
        cantidadColumn.setCellValueFactory(usuario -> calcularMedia(usuario.getValue()));
        cantidadDeHuellas.setCellValueFactory(usuario -> new SimpleStringProperty(String.valueOf(usuario.getValue().getHuellas().size())));
    }

    private SimpleStringProperty calcularMedia(Usuario usuario) {
        LocalDate hoy = LocalDate.now();
        List<Huella> huellasFiltradas = usuario.getHuellas();

        double totalImpacto = huellasFiltradas.stream()
                .filter(huella -> hoy.toEpochDay() - huella.getFecha().toLocalDate().toEpochDay() <= dias)
                .mapToDouble(this::calcularHuella)
                .sum();

        double media = huellasFiltradas.isEmpty() ? 0.0 : totalImpacto / huellasFiltradas.size();
        return new SimpleStringProperty(huellasFiltradas.isEmpty() ? "No registrado" : String.format("%.2f kg CO₂", media));
    }


    private double calcularHuella(Huella huella) {
        if (huella.getIdActividad().getIdCategoria().getFactorEmision() == null || huella.getValor() == null) {
            return 0;
        }
        return huella.getIdActividad().getIdCategoria().getFactorEmision().doubleValue() * huella.getValor().doubleValue();
    }

    private void ordenarYAsignarPosiciones(List<Usuario> usuariosList) {
        usuariosList.sort(Comparator.comparingInt((Usuario usuario) -> {
                    List<Huella> huellas = usuario.getHuellas();
                    return -huellas.size();
                }).thenComparingDouble(usuario -> {
                    List<Huella> huellas = usuario.getHuellas();
                    return huellas.stream()
                            .filter(huella -> LocalDate.now().toEpochDay() - huella.getFecha().toLocalDate().toEpochDay() <= dias)
                            .mapToDouble(this::calcularHuella)
                            .sum();
                })
                .thenComparing(Usuario::getNombre));
    }


}
