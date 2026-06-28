module com.example.warcaby {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens com.example.warcaby to javafx.fxml;
    exports com.example.warcaby;
}