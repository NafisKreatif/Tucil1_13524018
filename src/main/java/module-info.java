module stima {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;

    opens stima to javafx.fxml;
    exports stima;
}
