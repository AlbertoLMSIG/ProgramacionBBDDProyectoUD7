module com.mycompany.empresaropa {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.instrument;
    requires java.persistence;
    requires java.sql;
    
    opens com.mycompany.empresaropa.entities;
    opens com.mycompany.empresaropa to javafx.fxml;
    
    exports com.mycompany.empresaropa;
}
