package stima;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import stima.Solver.Solution;
import stima.Solver.VisualizationOption;
import stima.Validation.ValidationResult;

public class IndexController {
    public static IndexController instance;

    @FXML
    Canvas visualCanvas;
    private GraphicsContext visualContext;
    private double borderWidth = 2;

    // inputs
    @FXML
    TextArea inputTextArea;
    @FXML
    Label inputError;
    @FXML
    Button inputTxt;

    // outputs
    @FXML
    TextArea outputTextArea;
    @FXML
    Label outputError;

    // grids and solving
    @FXML
    CheckBox useVisualization;
    @FXML
    Label totalIteration;
    @FXML
    Label totalCase;

    private Image crownImage;
    private Color[] gameColors = {
            Color.web("#bba3e2"),
            Color.web("#ffc992"),
            Color.web("#96beff"),
            Color.web("#b3dfa0"),
            Color.web("#dfdfdf"),
            Color.web("#ff7b60"),
            Color.web("#e6f388"),
            Color.web("#b9b29e"),
            Color.web("#dfa0bf"),
            Color.web("#a3d2d8"),
            Color.web("#62efea"),
            Color.web("#ff93f3"),
            Color.web("#8acc6d"),
            Color.web("#729aec"),
            Color.web("#c387e0"),
            Color.web("#ffe04b"),
            Color.web("#a64210"),
            Color.web("#F4A900"),
            Color.web("#7033f2"),
            Color.web("#497E76"),
            Color.web("#ff6666"),
            Color.web("#66ff66"),
            Color.web("#ffff66"),
            Color.web("#ff66ff"),
            Color.web("#66ffff"),
            Color.web("#6666ff"),
    };

    private String defaultInput = "" +
            "AAABBCCCD\n" +
            "ABBBBCECD\n" +
            "ABBBDCECD\n" +
            "AAABDCCCD\n" +
            "BBBBDDDDD\n" +
            "FGGGDDHDD\n" +
            "FGIGDDHDD\n" +
            "FGIGDDHDD\n" +
            "FGGGDDHHH";

    private boolean isSolved = false;
    public boolean isOnLiveUpdate = false;
    public Thread liveUpdateThread = null;

    public void initialize() {
        instance = this;
        URL _url = App.class.getResource("/images/crown.png");
        crownImage = new Image(_url.toExternalForm());
        visualContext = visualCanvas.getGraphicsContext2D();

        inputTextArea.setText(defaultInput);
        inputTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                updateGridFromInput();
            }
        });

        outputTextArea.setEditable(false);

        updateGridFromInput();
    }

    private char[][] getInput() {
        String inputString = inputTextArea.getText();
        Scanner scanner = new Scanner(inputString);

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

        return inputGrids;
    }

    public void updateGridFromInput() {
        if (isOnLiveUpdate) {
            inputError.setText("Visualisasi sedang berjalan! Tunggu sampai selesai.");
            return;
        }
        inputError.setText("");
        outputError.setText("");

        isSolved = false;

        char[][] inputGrids = getInput();
        int n = inputGrids.length;

        ValidationResult validationResult = Validation.validateInput(inputGrids);
        switch (validationResult) {
            case Valid:
                inputError.setText("");
                outputError.setText("");
                setTotalCase(-1);
                setTotalIteration(-1);
                drawBoard(inputGrids);
                break;

            case InconsistentDimension:
                inputError.setText("Banyak kolom tidak konsisten dengan banyak baris!");
                break;

            case WrongNumberOfRegion:
                inputError.setText(
                        "Total daerah kurang dari " + n + "! Pastikan ada daerah dengan kode dari 'A' sampai 'Z'.");
                break;

            case InvalidColorCode:
                inputError.setText(
                        "Kode daerah tidak valid! Pastikan hanya ada daerah dengan kode dari 'A' sampai 'Z'.");
                break;

            case SeparatedRegion:
                inputError.setText(
                        "Daerah tidak boleh terpisah!");
                break;

            case TooBig:
                inputError.setText(
                        "Besar papan maksimal adalah " + Validation.maxGridSize + " 26.");
                break;

            case ZeroSize:
                inputError.setText(
                        "Besar papan harus lebih dari 0.");
                break;

            default:
                break;
        }

        outputTextArea.setText("");
    }

    public void onInputFromFile(Event event) {
        if (isOnLiveUpdate) {
            inputError.setText("Visualisasi sedang berjalan! Tunggu sampai selesai.");
            return;
        }
        inputError.setText("");
        outputError.setText("");

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("TEXT files (*.txt)", "*.txt"));
        fileChooser.setTitle("Open .txt");
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                Scanner sc = new Scanner(file);
                String inputString = "";
                boolean first = true;
                while (sc.hasNextLine()) {
                    if (first)
                        inputString += sc.nextLine();
                    else
                        inputString += "\n" + sc.nextLine();
                    first = false;
                }
                inputTextArea.setText(inputString);
                sc.close();

                updateGridFromInput();
            } catch (IOException e) {
                inputError.setText(e.getMessage());
            }
        }
    }

    public void onSaveAsTxt(Event event) {
        if (isOnLiveUpdate) {
            outputError.setText("Visualisasi sedang berjalan! Tunggu sampai selesai.");
            return;
        }
        if (!isSolved) {
            outputError.setText("Selesaikan suatu papan terlebih dahulu untuk menyimpan hasil.");
            return;
        }
        inputError.setText("");
        outputError.setText("");

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
        fileChooser.setInitialFileName("solution.txt");
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("TEXT files (*.txt)", "*.txt"));
        fileChooser.setTitle("Save as .txt");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                PrintStream writer = new PrintStream(file);
                writer.print(outputTextArea.getText());
                writer.close();
            } catch (IOException e) {
                outputError.setText(e.getMessage());
            }
        }
    }

    public void onSaveAsPng(Event event) {
        if (isOnLiveUpdate) {
            outputError.setText("Visualisasi sedang berjalan! Tunggu sampai selesai.");
            return;
        }
        if (!isSolved) {
            outputError.setText("Selesaikan suatu papan terlebih dahulu untuk menyimpan hasil.");
            return;
        }
        inputError.setText("");
        outputError.setText("");

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
        fileChooser.setInitialFileName("solution.png");
        fileChooser.setSelectedExtensionFilter(new ExtensionFilter("IMAGES files (*.png)", "*.png"));
        fileChooser.setTitle("Save as .png");
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                int width = (int) visualCanvas.getWidth();
                int height = (int) visualCanvas.getHeight();
                WritableImage writableImage = new WritableImage(width, height);
                visualCanvas.snapshot(null, writableImage);
                PixelReader pixelReader = writableImage.getPixelReader();
                BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int argb = pixelReader.getArgb(x, y);
                        bufferedImage.setRGB(x, y, argb);
                    }
                }
                ImageIO.write(bufferedImage, "png", file);
            } catch (IOException e) {
                outputError.setText(e.getMessage());
            }
        }
    }

    public void solveGrid() {
        if (isOnLiveUpdate) {
            outputError.setText("Visualisasi sedang berjalan! Tunggu sampai selesai.");
            return;
        }
        if (Validation.validateInput(getInput()) != ValidationResult.Valid) {
            return;
        }
        inputError.setText("");
        outputError.setText("");
        outputTextArea.setText("");

        Solution solution;
        char[][] inputGrids = getInput();
        if (useVisualization.isSelected()) {
            Solver.solve(inputGrids, VisualizationOption.Live);
        } else {
            solution = Solver.solve(inputGrids, VisualizationOption.None);
            outputSolution(solution);
        }
    }

    public void outputSolution(Solution solution) {
        isSolved = true;

        String outputString = new String();
        if (solution.isSolved) {
            int n = solution.grid.length;
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    outputString += solution.grid[i][j];
                }
                outputString += '\n';
            }
            outputString += '\n';
            outputString += "Solution found.\n";
        } else {
            outputString += "Solution not found.\n";
        }
        outputString += "Execution time: " + solution.time_ms + " ms\n";
        outputString += "Total iteration: " + solution.totalIteration + "\n";
        outputString += "Total case: " + solution.totalCase + "\n";

        outputTextArea.setText(outputString);
    }

    public void resetGrid() {
        updateGridFromInput();
    }

    public void drawBoard() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                double width = visualCanvas.getWidth();
                double height = visualCanvas.getHeight();
                visualContext.setFill(Color.BLACK);
                visualContext.fillRect(0, 0, width, height);
                double cellWidth = (width - 6 * borderWidth) / 5.0;
                for (int i = 0; i < 5; i++) {
                    for (int j = 0; j < 5; j++) {
                        visualContext.setFill(Color.WHITE);
                        visualContext.fillRect(
                                borderWidth * (j + 1) + cellWidth * j,
                                borderWidth * (i + 1) + cellWidth * i,
                                cellWidth,
                                cellWidth);
                    }
                }
            }
        });
    }

    public void drawBoard(char[][] input) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                double width = visualCanvas.getWidth();
                double height = visualCanvas.getHeight();
                visualContext.setFill(Color.BLACK);
                visualContext.fillRect(0, 0, width, height);
                double n = input.length;
                double cellWidth = (width - (n + 1) * borderWidth) / n;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        visualContext.setFill(gameColors[input[i][j] - 'A']);
                        visualContext.fillRect(
                                borderWidth * (j + 1) + cellWidth * j,
                                borderWidth * (i + 1) + cellWidth * i,
                                cellWidth,
                                cellWidth);
                    }
                }
            }
        });
    }

    public void drawBoard(char[][] input, char[][] output) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                double width = visualCanvas.getWidth();
                double n = input.length;
                double cellWidth = (width - (n + 1) * borderWidth) / n;

                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        visualContext.setFill(gameColors[input[i][j] - 'A']);
                        visualContext.fillRect(
                                borderWidth * (j + 1) + cellWidth * j,
                                borderWidth * (i + 1) + cellWidth * i,
                                cellWidth,
                                cellWidth);

                        if (output[i][j] == '#') {
                            double queenWidth = cellWidth / 2;
                            visualContext.setFill(Color.BLACK);
                            visualContext.drawImage(crownImage,
                                    borderWidth * (j + 1) + cellWidth * j + queenWidth / 2,
                                    borderWidth * (i + 1) + cellWidth * i + queenWidth / 2,
                                    queenWidth,
                                    queenWidth);
                        }
                    }
                }
            }
        });
    }

    public void drawCell(int i, int j, int n, char code) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                double width = visualCanvas.getWidth();
                double cellWidth = (width - (n + 1) * borderWidth) / n;

                if (code == '#') {
                    double queenWidth = cellWidth / 2;
                    visualContext.setFill(Color.BLACK);
                    visualContext.drawImage(crownImage,
                            borderWidth * (j + 1) + cellWidth * j + queenWidth / 2,
                            borderWidth * (i + 1) + cellWidth * i + queenWidth / 2,
                            queenWidth,
                            queenWidth);
                } else {
                    visualContext.setFill(gameColors[code - 'A']);
                    visualContext.fillRect(
                            borderWidth * (j + 1) + cellWidth * j,
                            borderWidth * (i + 1) + cellWidth * i,
                            cellWidth,
                            cellWidth);
                }
            }
        });
    }

    public void setTotalIteration(int n) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                totalIteration.setText("Total iteration: " + (n == -1 ? "-" : n));
            }
        });
    }

    public synchronized void setTotalCase(int n) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                totalCase.setText("Total case: " + (n == -1 ? "-" : n));
            }
        });
    }
}
