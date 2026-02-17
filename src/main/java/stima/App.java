package stima;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class App extends Application {
    public static App Instance;
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/index.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root, 1080, 720);
        stage.setTitle("Permainan Queens LinkedIn");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void startFromTerminal() {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> inputs = new ArrayList<String>();
        while (scanner.hasNext()) {
            String input = scanner.nextLine();
            inputs.add(input);
        }
        scanner.close();

        int n = inputs.size();
        char[][] inputGrids = new char[n][];
        for (int i = 0; i < n; i++) {
            inputGrids[i] = inputs.get(i).toCharArray();
        }

        System.out.println(Validation.validateInput(inputGrids));
        Solver.Solution solution = Solver.solve(inputGrids);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(solution.grid[i][j]);
            }
            System.out.println();
        }
        System.out.println("Total case: " + solution.totalCase);
        System.out.println("Execution time (ms): " + solution.time_ms);
    }
}