module com.github.dangelcrack {
    requires java.naming;
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires java.xml.bind;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires jakarta.persistence;

    // Abre los paquetes para JavaFX y Hibernate
    opens com.github.dangelcrack to javafx.fxml;
    opens com.github.dangelcrack.controller to javafx.fxml; // Abre el paquete donde está LoginController
    opens com.github.dangelcrack.model.entity to org.hibernate.orm.core, javafx.base;

    // Exporta los paquetes para que otros módulos puedan utilizarlos
    exports com.github.dangelcrack;
}
