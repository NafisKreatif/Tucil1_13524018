module stima {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens stima to javafx.fxml;
    exports stima;
}
