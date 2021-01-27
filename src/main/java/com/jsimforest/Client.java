package com.jsimforest;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

public class Client extends Application implements PropertyChangeListener {
    public static double winWidth;
    public static double winHeight;

    private int step;

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        this.setStep((int) evt.getNewValue());
    }

    public static void startUI(){
        launch();
    }

    public void start(Stage stage){
        // Getting screen dimension
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        winHeight = bounds.getHeight();
        winWidth = bounds.getWidth();

        // Setting up root
        HBox root = new HBox();
        root.getStyleClass().add("mainHbox");
        root.setSpacing(100);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setMinWidth(winWidth);
        root.setMinHeight(winHeight);
        Scene scene = new Scene(root, winHeight, winWidth);
        scene.getStylesheets().add(getClass().getResource("/com/sim.css").toExternalForm());

        // Setting grid Scene
        Pane gridPane = new Pane();
        root.getChildren().add(gridPane);
        int simHeight = 50;
        int simWidth = 50;

        for(int i = 0; i < simHeight;i++){
            for (int j = 0;j < simWidth; j++){
                Pane newCell = new Pane();
                newCell.setLayoutX(i*20);
                newCell.setLayoutY(j*20);
                newCell.setMinSize(20, 20);
                newCell.getStyleClass().addAll(Arrays.asList("null", "cell"));
                gridPane.getChildren().add(newCell);
            }
        }

        //Config interface
        VBox rectDivConfig = new VBox();
        rectDivConfig.setId("configDiv");
        GridPane formular = new GridPane();
        formular.setVgap(25);
        formular.setAlignment(Pos.BASELINE_CENTER);

        // Titre de la zone de configuration
        Text configTitle = new Text("Configurez votre simulation");
        configTitle.getStyleClass().add("configTitle");
        configTitle.setTextAlignment(TextAlignment.CENTER);
        configTitle.setWrappingWidth(600);
        configTitle.setLayoutY(0);
        rectDivConfig.getChildren().add(configTitle);

        //Champ de largeur grille
        Label gridWidthLabel = new Label("Largeur de la grille : ");
        gridWidthLabel.getStyleClass().add("label");
        formular.add(gridWidthLabel, 0, 1);
        TextField gridWidthField = new TextField();
        gridWidthField.getStyleClass().add("field");
        formular.add(gridWidthField, 1, 1);

        // Champ de hauteur de grille
        Label gridHeightLabel = new Label("Hauteur de la grille : ");
        gridHeightLabel.getStyleClass().add("label");
        formular.add(gridHeightLabel, 0, 2);
        TextField gridHeightField = new TextField();
        gridHeightField.getStyleClass().add("field");
        formular.add(gridHeightField, 1, 2);

        //Champ de nombre de pas
        Label simulationStepLabel = new Label("Nombre de pas dans la grille : ");
        simulationStepLabel.getStyleClass().add("label");
        formular.add(simulationStepLabel, 0, 3);
        TextField simulationStepField = new TextField();
        simulationStepField.getStyleClass().add("field");
        formular.add(simulationStepField, 1, 3);

        //Champ de la vitesse d'éxécution
        Label simulationSpeedLabel = new Label("Nombre de pas dans la grille : ");
        simulationSpeedLabel.getStyleClass().add("label");
        formular.add(simulationSpeedLabel, 0, 4);
        TextField simulationSpeedField = new TextField();
        simulationSpeedField.getStyleClass().add("field");
        formular.add(simulationSpeedField, 1, 4);
        rectDivConfig.getChildren().add(formular);

        // Boutons configuration
        GridPane configButtons = new GridPane();
        configButtons.setAlignment(Pos.CENTER);
        configButtons.setPadding(new Insets(50, 0, 0, 0));
        configButtons.getStyleClass().add("buttonsGrid");
        configButtons.setVgap(20);
        configButtons.setHgap(20);

        Button saveConfig = new Button("Sauvegarder configuration");
        saveConfig.getStyleClass().add("button");
        Button loadConfig = new Button("Charger configuration");
        loadConfig.getStyleClass().add("button");
        Button exportGrid = new Button("Exporter la grille");
        exportGrid.getStyleClass().add("button");
        Button importGrid = new Button("Importer la grille");
        importGrid.getStyleClass().add("button");

        configButtons.add(saveConfig, 0, 0);
        configButtons.add(loadConfig, 0, 1);
        configButtons.add(exportGrid, 1, 0);
        configButtons.add(importGrid, 1, 1);

        //Simulation control
        HBox controlButtons = new HBox();
        controlButtons.setPadding(new Insets(400, 15, 0, 15));
        controlButtons.getStyleClass().add("controlButtons");
        controlButtons.setAlignment(Pos.CENTER);
        controlButtons.setSpacing(15);

        // Play button
        SVGPath playSVG = new SVGPath();
        playSVG.setContent("M424.4 214.7L72.4 6.6C43.8-10.3 0 6.1 0 47.9V464c0 37.5 40.7 60.1 72.4 41.3l352-208c31.4-18.5 31.5-64.1 0-82.6z");
        Button playButton = new Button("", playSVG);
        playSVG.setScaleX(0.1);
        playSVG.setScaleY(0.1);
        playSVG.setFill(Color.WHITE);
        playButton.getStyleClass().add("playButton");
        controlButtons.getChildren().add(playButton);

        // Pause button
        SVGPath pauseSVG = new SVGPath();
        pauseSVG.setContent("M144 479H48c-26.5 0-48-21.5-48-48V79c0-26.5 21.5-48 48-48h96c26.5 0 48 21.5 48 48v352c0 26.5-21.5 48-48 48zm304-48V79c0-26.5-21.5-48-48-48h-96c-26.5 0-48 21.5-48 48v352c0 26.5 21.5 48 48 48h96c26.5 0 48-21.5 48-48z");
        Button pauseButton = new Button("", pauseSVG);
        pauseSVG.setScaleX(0.1);
        pauseSVG.setScaleY(0.1);
        pauseSVG.setFill(Color.WHITE);
        pauseButton.getStyleClass().add("playButton");
        controlButtons.getChildren().add(pauseButton);

        // step back button
        SVGPath stebBackSVG = new SVGPath();
        stebBackSVG.setContent("M64 468V44c0-6.6 5.4-12 12-12h48c6.6 0 12 5.4 12 12v176.4l195.5-181C352.1 22.3 384 36.6 384 64v384c0 27.4-31.9 41.7-52.5 24.6L136 292.7V468c0 6.6-5.4 12-12 12H76c-6.6 0-12-5.4-12-12z");
        Button stepBack = new Button("", stebBackSVG);
        stebBackSVG.setScaleX(0.1);
        stebBackSVG.setScaleY(0.1);
        stebBackSVG.setFill(Color.WHITE);
        stepBack.getStyleClass().add("playButton");
        controlButtons.getChildren().add(stepBack);

        // stop button
        SVGPath stopSVG = new SVGPath();
        stopSVG.setContent("M400 32H48C21.5 32 0 53.5 0 80v352c0 26.5 21.5 48 48 48h352c26.5 0 48-21.5 48-48V80c0-26.5-21.5-48-48-48z");
        Button stopButton = new Button("", stopSVG);
        stopSVG.setScaleX(0.1);
        stopSVG.setScaleY(0.1);
        stopSVG.setFill(Color.WHITE);
        stopButton.getStyleClass().add("playButton");
        controlButtons.getChildren().add(stopButton);

        // step forward button
        SVGPath stepForwardSVG = new SVGPath();
        stepForwardSVG.setContent("M384 44v424c0 6.6-5.4 12-12 12h-48c-6.6 0-12-5.4-12-12V291.6l-195.5 181C95.9 489.7 64 475.4 64 448V64c0-27.4 31.9-41.7 52.5-24.6L312 219.3V44c0-6.6 5.4-12 12-12h48c6.6 0 12 5.4 12 12z");
        Button stepForward = new Button("", stepForwardSVG);
        stepForwardSVG.setScaleX(0.1);
        stepForwardSVG.setScaleY(0.1);
        stepForwardSVG.setFill(Color.WHITE);
        stepForward.getStyleClass().add("playButton");
        controlButtons.getChildren().add(stepForward);
        rectDivConfig.getChildren().add(configButtons);
        rectDivConfig.getChildren().add(controlButtons);

        root.getChildren().add(rectDivConfig);

        // Rendering stage
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setTitle("Simulation JSIMForest");
        stage.show();
    }
}
