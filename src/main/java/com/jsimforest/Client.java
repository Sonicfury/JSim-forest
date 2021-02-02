package com.jsimforest;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static java.lang.Integer.parseInt;

public class Client extends Application implements PropertyChangeListener {
    public static double winWidth;
    public static double winHeight;
    private Pane gridPane = new Pane();

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
        refreshGrid(this.gridPane);
    }

    public static void startUI(){
        launch();
    }

    private Configuration simulationConfig = new Configuration();
    private Simulation simulation = new Simulation(this.simulationConfig);
    private Simulation initialState;

    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));

    private void generateGrid(Pane gridRootPane){
        for(int i = 0; i < this.simulationConfig.getGridWidth();i++){
            for (int j = 0;j < this.simulationConfig.getGridHeight(); j++){
                Pane newCell = new Pane();
                newCell.setLayoutX(i*20);
                newCell.setLayoutY(j*20);
                newCell.setMinSize(20, 20);
                newCell.getStyleClass().addAll(Arrays.asList("cell", "null"));
                int yCoord = i;
                int xCoord = j;
                newCell.setOnMouseClicked(event -> {
                    String actualState = newCell.getStyleClass().get(1);
                    newCell.getStyleClass().remove(actualState);
                    newCell.getStyleClass().add(this.cycleTypes(actualState, xCoord, yCoord));
                });
                gridRootPane.getChildren().add(newCell);
            }
        }
    }

    private void changeGridSize(Pane gridRootPane){
        try{
            gridRootPane.getChildren().clear();
            this.generateGrid(gridRootPane);
        }catch(NumberFormatException e){
            System.out.println(e.getMessage());
        }
    }

    private void refreshGrid(Pane gridRootPane){
        gridRootPane.getChildren().clear();
        for(int i = 0; i < this.simulationConfig.getGridHeight(); i++){
            for(int j = 0; j < this.simulationConfig.getGridWidth(); j++){
                Pane newCell = new Pane();
                newCell.setLayoutX(i*20);
                newCell.setLayoutY(j*20);
                newCell.setMinSize(20, 20);
                newCell.getStyleClass().addAll(Arrays.asList("cell", this.simulation.getGrid().getMatrix().get(i).get(j).getCellType().getName()));
                gridRootPane.getChildren().add(newCell);
            }
        }
    }

    public String cycleTypes(String currentType, int x, int y) {
        String newType;
        CellType nullType = new CellType("null", "white");
        CellType plantType = new CellType("plant", "lightGreen");
        CellType youngTreeType = new CellType("youngTree", "mediumGreen");
        CellType treeType = new CellType("tree", "green");
        switch (currentType){
            case "null":
                newType = "plant";
                this.simulation.getGrid().editCell(x, y, plantType);
                break;
            case "plant":
                newType = "youngTree";
                this.simulation.getGrid().editCell(x, y, youngTreeType);
                break;
            case "youngTree":
                newType = "tree";
                this.simulation.getGrid().editCell(x, y, treeType);
                break;
            case "tree":
                newType = "null";
                this.simulation.getGrid().editCell(x, y, nullType);
                break;
            default:
                throw new IllegalArgumentException();
        }
        return newType;
    }

    public void start(Stage stage){
        //Default config
        this.simulationConfig = new Configuration();
        this.simulation = new Simulation(simulationConfig);

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
        Scene scene = new Scene(root, winWidth, winHeight);
        scene.getStylesheets().add(getClass().getResource("/com/sim.css").toExternalForm());

        // Setting grid Scene
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("scroller");
        root.getChildren().add(scrollPane);
        this.gridPane.getStyleClass().add("gridPane");


        //Config interface
        VBox rectDivConfig = new VBox();
        rectDivConfig.setId("configDiv");
        GridPane formulary = new GridPane();
        formulary.setVgap(25);
        formulary.setAlignment(Pos.BASELINE_CENTER);

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
        formulary.add(gridWidthLabel, 0, 1);
        TextField gridWidthField = new TextField();
        gridWidthField.getStyleClass().add("field");
        gridWidthField.setText("100");
        formulary.add(gridWidthField, 1, 1);

        // Champ de hauteur de grille
        Label gridHeightLabel = new Label("Hauteur de la grille : ");
        gridHeightLabel.getStyleClass().add("label");
        formulary.add(gridHeightLabel, 0, 2);
        TextField gridHeightField = new TextField();
        gridHeightField.getStyleClass().add("field");
        gridHeightField.setText("100");
        formulary.add(gridHeightField, 1, 2);

        // Génération de la grille et rechargement de la grille
        generateGrid(this.gridPane);
        scrollPane.setContent(this.gridPane);
        gridWidthField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals("") && parseInt(newValue) >= 3){
                pause.setOnFinished(event -> {
                    this.simulationConfig.setGridWidth(parseInt(newValue));
                    this.simulation.newGrid();
                    changeGridSize(this.gridPane);
                });
                pause.playFromStart();
            }

        });
        gridHeightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals("") && parseInt(newValue) >= 3)
            pause.setOnFinished(event -> {
                this.simulationConfig.setGridHeight(parseInt(newValue));
                this.simulation.newGrid();
                changeGridSize(this.gridPane);
            });
            pause.playFromStart();
        });

        //Champ de nombre de pas
        Label simulationStepLabel = new Label("Nombre de pas : ");
        simulationStepLabel.getStyleClass().add("label");
        formulary.add(simulationStepLabel, 0, 3);
        TextField simulationStepField = new TextField();
        simulationStepField.setText("20");
        simulationStepField.getStyleClass().add("field");
        formulary.add(simulationStepField, 1, 3);
        simulationStepField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals("")){
                this.simulationConfig.setStepsNumber(parseInt(newValue));
            }
        });

        //Champ de la vitesse d'éxécution
        Label simulationSpeedLabel = new Label("Vitesse d'éxecution de la grille : ");
        simulationSpeedLabel.getStyleClass().add("label");
        formulary.add(simulationSpeedLabel, 0, 4);
        TextField simulationSpeedField = new TextField();
        simulationSpeedField.setText("1");
        simulationSpeedField.getStyleClass().add("field");
        formulary.add(simulationSpeedField, 1, 4);
        rectDivConfig.getChildren().add(formulary);
        simulationSpeedField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.equals("")){
                this.simulationConfig.setStepsPerSecond(parseInt(newValue));
            }
        });

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
        playButton.setOnMouseClicked((event) -> {
            if(!this.simulation.isPause() && this.simulation.getStep() == 0){
                this.initialState = new Simulation(this.simulationConfig);
                this.initialState.getGrid().setMatrix((ArrayList<ArrayList<Cell>>) this.simulation.getGrid().clone());
                gridHeightField.setDisable(true);
                gridWidthField.setDisable(true);
                simulationSpeedField.setDisable(true);
                simulationStepField.setDisable(true);
                this.simulation.stepObservable.addPropertyChangeListener(this);
                simulation.run();
            }else{
                this.simulation.resume();
            }

        });
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
        pauseButton.setOnMouseClicked((event) -> {
            this.simulation.pause();
        });

        // reset button
        SVGPath resetSVG = new SVGPath();
        resetSVG.setContent("M212.333 224.333H12c-6.627 0-12-5.373-12-12V12C0 5.373 5.373 0 12 0h48c6.627 0 12 5.373 12 12v78.112C117.773 39.279 184.26 7.47 258.175 8.007c136.906.994 246.448 111.623 246.157 248.532C504.041 393.258 393.12 504 256.333 504c-64.089 0-122.496-24.313-166.51-64.215-5.099-4.622-5.334-12.554-.467-17.42l33.967-33.967c4.474-4.474 11.662-4.717 16.401-.525C170.76 415.336 211.58 432 256.333 432c97.268 0 176-78.716 176-176 0-97.267-78.716-176-176-176-58.496 0-110.28 28.476-142.274 72.333h98.274c6.627 0 12 5.373 12 12v48c0 6.627-5.373 12-12 12z");
        Button resetButton = new Button("", resetSVG);
        resetSVG.setScaleX(0.1);
        resetSVG.setScaleY(0.1);
        resetSVG.setFill(Color.WHITE);
        resetButton.setOnMouseClicked((event -> {
            if((this.getStep() == 0 || this.simulation.isPause()) && this.initialState != null){
                gridHeightField.setDisable(false);
                gridWidthField.setDisable(false);
                simulationSpeedField.setDisable(false);
                simulationStepField.setDisable(false);
                this.simulation = new Simulation(this.simulationConfig);
                Collections.copy(this.simulation.getGrid().getMatrix(), this.initialState.getGrid().getMatrix());
                refreshGrid(this.gridPane);
                this.simulation.setStep(0);
                this.step = 0;
            }
        }));

        resetButton.getStyleClass().add("playButton");
        controlButtons.getChildren().add(resetButton);

        // clear button
        SVGPath clearSVG = new SVGPath();
        clearSVG.setContent("M32 464a48 48 0 0 0 48 48h288a48 48 0 0 0 48-48V128H32zm272-256a16 16 0 0 1 32 0v224a16 16 0 0 1-32 0zm-96 0a16 16 0 0 1 32 0v224a16 16 0 0 1-32 0zm-96 0a16 16 0 0 1 32 0v224a16 16 0 0 1-32 0zM432 32H312l-9.4-18.7A24 24 0 0 0 281.1 0H166.8a23.72 23.72 0 0 0-21.4 13.3L136 32H16A16 16 0 0 0 0 48v32a16 16 0 0 0 16 16h416a16 16 0 0 0 16-16V48a16 16 0 0 0-16-16z");
        Button clearButton = new Button("", clearSVG);
        clearSVG.setScaleX(0.1);
        clearSVG.setScaleY(0.1);
        clearButton.setOnMouseClicked((event -> {
            if(this.step == 0 || this.simulation.isPause()){
                this.simulation = new Simulation(this.simulationConfig);
                gridHeightField.setDisable(false);
                gridWidthField.setDisable(false);
                simulationSpeedField.setDisable(false);
                simulationStepField.setDisable(false);
                this.simulation.setStep(0);
                this.step = 0;
                generateGrid(this.gridPane);
            }
        }));
        clearSVG.setFill(Color.WHITE);
        clearButton.getStyleClass().add("playButton");

        controlButtons.getChildren().add(clearButton);

        // step forward button
        SVGPath stepForwardSVG = new SVGPath();
        stepForwardSVG.setContent("M384 44v424c0 6.6-5.4 12-12 12h-48c-6.6 0-12-5.4-12-12V291.6l-195.5 181C95.9 489.7 64 475.4 64 448V64c0-27.4 31.9-41.7 52.5-24.6L312 219.3V44c0-6.6 5.4-12 12-12h48c6.6 0 12 5.4 12 12z");
        Button stepForward = new Button("", stepForwardSVG);
        stepForwardSVG.setScaleX(0.1);
        stepForwardSVG.setScaleY(0.1);
        stepForwardSVG.setFill(Color.WHITE);
        stepForward.getStyleClass().add("playButton");
        stepForward.setOnMouseClicked((event -> {
            gridHeightField.setDisable(true);
            gridWidthField.setDisable(true);
            simulationSpeedField.setDisable(true);
            simulationStepField.setDisable(true);
            if(this.step == 0){
                this.initialState = new Simulation(this.simulationConfig);
                this.initialState.getGrid().setMatrix((ArrayList<ArrayList<Cell>>) this.simulation.getGrid().clone());
            }
            if(this.step == 0 || this.simulation.isPause()){
                this.simulation.step();
                this.refreshGrid(this.gridPane);
                if(!this.simulation.isPause()){
                    System.out.println(pause);
                    this.simulation.pause();
                }
            }


        }));
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
