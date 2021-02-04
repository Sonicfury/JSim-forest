package com.jsimforest;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import static java.lang.Integer.parseInt;

public class Client extends Application implements PropertyChangeListener {
    private ArrayList densities = new ArrayList(Arrays.asList(
            new Text("Densité en arbre : 0"),
                new Text("Densité en jeune pousse : 0"),
                new Text("Densité en plante : 0"),
                new Text("Densité en infecté : 0"),
                new Text("Densité en brûlé : 0"),
                new Text("Densité en cendre : 0")));
    private final Pane gridPane = new Pane();
    private ResultSet allSims;
    private Button activeButton;
    private boolean locker = false;
    public VBox densitiesBox = new VBox(10);

    public void setLocker(boolean locker) {
        this.locker = locker;
    }


    private ResultSet allConfigs;

    /**
     * refresh the configs from the database
     */
    public void refreshConfigs() {
        try {
            this.allConfigs = Configuration.selectAllConfigurations();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Refresh the sims from the database
     */
    public void refreshSims(){
        try{
            this.allSims = Simulation.selectAllSimulations();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private int step;

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    /**
     * This is executed at each step of the simulation
     */
    public void propertyChange(PropertyChangeEvent evt) {
        refreshDensities();
        this.setStep((int) evt.getNewValue());
        System.out.println("step");
        refreshGrid(this.gridPane);
    }

    public static void startUI() {
        launch();
    }

    private Configuration simulationConfig = new Configuration();
    private Simulation simulation = new Simulation(this.simulationConfig);
    private Simulation initialState;

    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));

    /**
     * generate a new grid in the gridroot pane
     * @param gridRootPane The grid root pane (pane that conatains the cell)
     */
    private void generateGrid(Pane gridRootPane) {
        for (int i = 0; i < this.simulationConfig.getGridWidth(); i++) {
            for (int j = 0; j < this.simulationConfig.getGridHeight(); j++) {
                Pane newCell = new Pane();
                newCell.setLayoutX(i * 20);
                newCell.setLayoutY(j * 20);
                newCell.setMinSize(20, 20);
                newCell.getStyleClass().addAll(Arrays.asList("cell", "null", "ok"));
                int yCoord = i;
                int xCoord = j;
                newCell.setOnMouseClicked((event) -> {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        String actualState = newCell.getStyleClass().get(1);
                        newCell.getStyleClass().remove(actualState);
                        newCell.getStyleClass().add(1, this.cycleTypes(actualState, xCoord, yCoord));
                        newCell.getStyleClass().set(2, "ok");
                    } else {
                        String currentHealth = newCell.getStyleClass().get(2);
                        newCell.getStyleClass().remove(currentHealth);
                        newCell.getStyleClass().add(2, cycleHealth(Health.valueOf(currentHealth), xCoord, yCoord));
                    }
                });
                gridRootPane.getChildren().add(newCell);
            }
        }
    }

    /**
     * Regenerate a fresh grid with new dimensions
     * @param gridRootPane The grid root pane (pane that conatains the cell)
     */
    private void changeGridSize(Pane gridRootPane) {
        try {
            gridRootPane.getChildren().clear();
            this.generateGrid(gridRootPane);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Change the active mod for the config and the active skin for the button
     * @param newButton the new button to reskin
     * @param newMode new mode for the simulation
     */
    private void changeActiveMode(Button newButton, Mode newMode) {
        if (this.step == 0) {
            this.activeButton.getStyleClass().remove("active");
            newButton.getStyleClass().add("active");
            this.activeButton = newButton;
            this.simulationConfig.setMode(newMode);
            this.healGrid();
//            this.generateGrid(this.gridPane);
        }
    }

    public void healGrid() {
        for(int i = 0; i < this.simulationConfig.getGridHeight(); i++){
            for(int j = 0; j < this.simulationConfig.getGridWidth(); j++){
                this.simulation.getGrid().getMatrix().get(i).get(j).setHealth(Health.ok);
            }
        }
        refreshGrid(this.gridPane);
    }

    /**
     * Refresh the grid of the GUI
     * @param gridRootPane The grid root pane (pane that conatains the cell)
     */
    private void refreshGrid(Pane gridRootPane) {
        Density density = this.simulation.getDensities().get(this.step - 1);
//        Density density = new Density(0.1, 0, 0, 0, 0, 0);
        System.out.println(density.getTreeDensity());
        this.densities = new ArrayList(Arrays.asList(
                new Text("Densité en arbre : " + Double.toString(density.getTreeDensity())),
                new Text("Densité en jeune pousse" + density.getYoungTreeDensity()),
                new Text("Densité en plante" + density.getPlantDensity()),
                new Text("Densité en infecté" + density.getInsectDensity()),
                new Text("Densité en brûlé" + density.getBurningDensity()),
                new Text("Densité en cendre" + density.getAshDensity())));
        densitiesBox.getChildren().addAll(densities);
        System.out.println();
        gridRootPane.getChildren().clear();
        for (int i = 0; i < this.simulationConfig.getGridHeight(); i++) {
            for (int j = 0; j < this.simulationConfig.getGridWidth(); j++) {
                Pane newCell = new Pane();
                newCell.setLayoutX(i * 20);
                newCell.setLayoutY(j * 20);
                newCell.setMinSize(20, 20);
                newCell.getStyleClass().addAll(Arrays.asList("cell", this.simulation.getGrid().getMatrix().get(i).get(j).getCellType().getName(), this.simulation.getGrid().getMatrix().get(i).get(j).getHealth().name()));
                gridRootPane.getChildren().add(newCell);
                int yCoord = i;
                int xCoord = j;
                newCell.setOnMouseClicked((event) -> {
                    if (this.simulation.isPause()) {
                        if (event.getButton().equals(MouseButton.PRIMARY)) {
                            String actualState = newCell.getStyleClass().get(1);
                            newCell.getStyleClass().remove(actualState);
                            newCell.getStyleClass().add(1, this.cycleTypes(actualState, xCoord, yCoord));
                            newCell.getStyleClass().set(2, "ok");
                        } else {
                            String currentHealth = newCell.getStyleClass().get(2);
                            newCell.getStyleClass().remove(currentHealth);
                            newCell.getStyleClass().add(2, cycleHealth(Health.valueOf(currentHealth), xCoord, yCoord));
                        }
                    }

                });
            }
        }
    }

    /**
     * Find the next type of the clicked cell
     * @param currentType current type of the cell
     * @param x x xCoordinate of the treated cell
     * @param y y yCoordinate of the treated cell
     * @return the next type of the clicked cell
     */
    public String cycleTypes(String currentType, int x, int y) {
        String newType;
        CellType nullType = new CellType("null", "white");
        CellType plantType = new CellType("plant", "lightGreen");
        CellType youngTreeType = new CellType("youngTree", "mediumGreen");
        CellType treeType = new CellType("tree", "green");
        switch (currentType) {
            case "null", "empty" -> {
                newType = "plant";
                this.simulation.getGrid().editCell(x, y, plantType);
            }
            case "plant" -> {
                newType = "youngTree";
                this.simulation.getGrid().editCell(x, y, youngTreeType);
            }
            case "youngTree" -> {
                newType = "tree";
                this.simulation.getGrid().editCell(x, y, treeType);
            }
            case "tree" -> {
                newType = "null";
                this.simulation.getGrid().editCell(x, y, nullType);
            }
            default -> throw new IllegalArgumentException();
        }
        return newType;
    }

    /**
     * Find the next health of the clicked cell
     * @param currentHealth current health of the treated cell
     * @param x xCoordinate of the treated cell
     * @param y yCoordinate of the treated cell
     * @return The next health state for the trated cell
     */
    public String cycleHealth(Health currentHealth, int x, int y) {
        Health newHealth;
        if (this.simulationConfig.getMode().equals(Mode.fire)) {
            switch (currentHealth) {
                case ok -> {
                    this.simulation.getGrid().editCell(x, y, Health.burned);
                    newHealth = Health.burned;
                }
                case burned -> {
                    this.simulation.getGrid().editCell(x, y, Health.ash);
                    newHealth = Health.ash;
                }
                case ash -> {
                    this.simulation.getGrid().editCell(x, y, Health.ok);
                    newHealth = Health.ok;
                }
                default -> {
                    newHealth = Health.ok;
                }

            }
        } else if (this.simulationConfig.getMode().equals(Mode.insect)) {
            if (currentHealth.equals(Health.ok)) {
                this.simulation.getGrid().editCell(x, y, Health.infected);
                newHealth = Health.infected;
            } else {
                this.simulation.getGrid().editCell(x, y, Health.ok);
                newHealth = Health.ok;
            }
        } else {
            newHealth = currentHealth;
        }
        return newHealth.name();
    }

    /**
     * Start the GUI for the client
     * @param stage primary window of the gui
     */
    public void start(Stage stage) {
        //Default config
        this.simulationConfig = new Configuration();
        this.simulation = new Simulation(simulationConfig);

        // alerts

        Alert error = new Alert(Alert.AlertType.ERROR);
        error.setTitle("Erreur");
        error.setHeaderText("");
        error.setContentText("Une erreur est survenue pendant l'envoi de la configuration");
        Alert successful = new Alert(Alert.AlertType.INFORMATION);
        successful.setTitle("Succés");
        successful.setHeaderText("");
        successful.setContentText("Votre configuration est sauvegardée");

        // Getting screen dimension
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        double winHeight = bounds.getHeight();
        double winWidth = bounds.getWidth();

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
            if (!newValue.equals("") && parseInt(newValue) >= 3 && !this.locker) {
                pause.setOnFinished(event -> {
                    this.simulationConfig.setGridWidth(parseInt(newValue));
                    this.simulation.newGrid();
                    changeGridSize(this.gridPane);
                });
                pause.playFromStart();
            }

        });
        gridHeightField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals("") && parseInt(newValue) >= 3 && !this.locker)
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
            if (!newValue.equals("")) {
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
            if (!newValue.equals("")) {
                this.simulationConfig.setStepsPerSecond(parseInt(newValue));
            }
        });

        // Mode buttons
        HBox simMode = new HBox();
        simMode.setAlignment(Pos.CENTER);
        simMode.getStyleClass().add("modeButtons");

        SVGPath forestSVG = new SVGPath();
        forestSVG.setContent("M378.31 378.49L298.42 288h30.63c9.01 0 16.98-5 20.78-13.06 3.8-8.04 2.55-17.26-3.28-24.05L268.42 160h28.89c9.1 0 17.3-5.35 20.86-13.61 3.52-8.13 1.86-17.59-4.24-24.08L203.66 4.83c-6.03-6.45-17.28-6.45-23.32 0L70.06 122.31c-6.1 6.49-7.75 15.95-4.24 24.08C69.38 154.65 77.59 160 86.69 160h28.89l-78.14 90.91c-5.81 6.78-7.06 15.99-3.27 24.04C37.97 283 45.93 288 54.95 288h30.63L5.69 378.49c-6 6.79-7.36 16.09-3.56 24.26 3.75 8.05 12 13.25 21.01 13.25H160v24.45l-30.29 48.4c-5.32 10.64 2.42 23.16 14.31 23.16h95.96c11.89 0 19.63-12.52 14.31-23.16L224 440.45V416h136.86c9.01 0 17.26-5.2 21.01-13.25 3.8-8.17 2.44-17.47-3.56-24.26z");
        forestSVG.setScaleY(0.1);
        forestSVG.setScaleX(0.1);
        forestSVG.setFill(Color.GREEN);
        Button forestModeButton = new Button("", forestSVG);
        this.activeButton = forestModeButton;

        forestModeButton.getStyleClass().add("modeButton");
        forestModeButton.getStyleClass().add("active");
        forestModeButton.setOnMouseClicked((event) -> {
            if (this.activeButton != forestModeButton && this.simulation.getStep() == 0) {
                changeActiveMode(forestModeButton, Mode.forest);
            }
        });
        simMode.getChildren().add(forestModeButton);

        SVGPath fireSVG = new SVGPath();
        fireSVG.setContent("M192 0C79.7 101.3 0 220.9 0 300.5 0 425 79 512 192 512s192-87 192-211.5c0-79.9-80.2-199.6-192-300.5zm0 448c-56.5 0-96-39-96-94.8 0-13.5 4.6-61.5 96-161.2 91.4 99.7 96 147.7 96 161.2 0 55.8-39.5 94.8-96 94.8z");
        fireSVG.setFill(Color.RED);
        fireSVG.setScaleY(0.1);
        fireSVG.setScaleX(0.1);
        Button fireModeButton = new Button("", fireSVG);
        fireModeButton.getStyleClass().add("modeButton");
        fireModeButton.setOnMouseClicked((event) -> {
            if (this.activeButton != fireModeButton && this.simulation.getStep() == 0) {
                changeActiveMode(fireModeButton, Mode.fire);
            }
        });

        simMode.getChildren().add(fireModeButton);

        SVGPath bugSVG = new SVGPath();
        bugSVG.setContent("M511.988 288.9c-.478 17.43-15.217 31.1-32.653 31.1H424v16c0 21.864-4.882 42.584-13.6 61.145l60.228 60.228c12.496 12.497 12.496 32.758 0 45.255-12.498 12.497-32.759 12.496-45.256 0l-54.736-54.736C345.886 467.965 314.351 480 280 480V236c0-6.627-5.373-12-12-12h-24c-6.627 0-12 5.373-12 12v244c-34.351 0-65.886-12.035-90.636-32.108l-54.736 54.736c-12.498 12.497-32.759 12.496-45.256 0-12.496-12.497-12.496-32.758 0-45.255l60.228-60.228C92.882 378.584 88 357.864 88 336v-16H32.666C15.23 320 .491 306.33.013 288.9-.484 270.816 14.028 256 32 256h56v-58.745l-46.628-46.628c-12.496-12.497-12.496-32.758 0-45.255 12.498-12.497 32.758-12.497 45.256 0L141.255 160h229.489l54.627-54.627c12.498-12.497 32.758-12.497 45.256 0 12.496 12.497 12.496 32.758 0 45.255L424 197.255V256h56c17.972 0 32.484 14.816 31.988 32.9zM257 0c-61.856 0-112 50.144-112 112h224C369 50.144 318.856 0 257 0z");
        bugSVG.setFill(Color.PURPLE);
        bugSVG.setScaleY(0.1);
        bugSVG.setScaleX(0.1);
        Button bugModeButton = new Button("", bugSVG);
        bugModeButton.setOnMouseClicked((event) -> {
            if (this.activeButton != bugModeButton && this.simulation.getStep() == 0) {
                changeActiveMode(bugModeButton, Mode.insect);
            }
        });
        bugModeButton.getStyleClass().add("modeButton");
        bugModeButton.setId("bugMode");
        simMode.getChildren().add(bugModeButton);

        formulary.add(simMode, 0, 5);

        // Boutons configuration

        GridPane configButtons = new GridPane();
        configButtons.setAlignment(Pos.CENTER);
        configButtons.setPadding(new Insets(50, 0, 0, 0));
        configButtons.getStyleClass().add("buttonsGrid");
        configButtons.setVgap(20);
        configButtons.setHgap(20);

        HBox askConfigNameBox = new HBox(50);
        Stage exportConfigStage = new Stage();
        Scene exportConfigScene = new Scene(askConfigNameBox, 750, 50);

        TextField askConfigName = new TextField();
        askConfigName.setMinWidth(250);

        Label askConfigNameLabel = new Label("Entrez un nom pour votre nouvelle configuration : ");
        askConfigNameLabel.setMinWidth(300);
        askConfigNameLabel.setLabelFor(askConfigName);
        askConfigNameLabel.setId("popupLabel");

        Button submitConfigName = new Button("Confirmer");
        submitConfigName.setMinWidth(100);

        submitConfigName.setOnMouseClicked((event) -> {
            if (!askConfigName.getText().equals("")) {
                try {
                    this.simulationConfig.saveConfiguration(askConfigName.getText());
                    exportConfigStage.close();
                    successful.show();
                } catch (SQLException throwables) {
                    error.show();
                }
            }
        });

        askConfigNameBox.getChildren().addAll(Arrays.asList(askConfigNameLabel, askConfigName, submitConfigName));

        exportConfigStage.setScene(exportConfigScene);
        exportConfigStage.setTitle("Nouvelle configuration");

        Button saveConfig = new Button("Sauvegarder configuration");
        saveConfig.setOnMouseClicked((event) -> {
            exportConfigStage.show();
        });

        Stage importConfig = new Stage();
        ScrollPane configImporterScroll = new ScrollPane();
        VBox importConfigsRoot = new VBox(50);
        GridPane configs = new GridPane();
        configImporterScroll.setContent(configs);
        configs.setHgap(30);
        configs.setVgap(50);
        importConfigsRoot.getChildren().add(configImporterScroll);
        Scene importConfigScene = new Scene(importConfigsRoot, 800, 300);

        importConfig.setScene(importConfigScene);


        saveConfig.getStyleClass().add("button");
        Button loadConfig = new Button("Charger configuration");
        loadConfig.setOnMouseClicked((event) -> {
            refreshConfigs();
            configs.getChildren().clear();
            int configNumber = 1;
            try {
                ResultSet allConfigs = Configuration.selectAllConfigurations();
                ResultSetMetaData configMD = allConfigs.getMetaData();
                for (int i = 1; i <= configMD.getColumnCount(); i++) {
                    configs.add(new Text(configMD.getColumnLabel(i)), i - 1, 0);
                }
                while (allConfigs.next()) {
                    int line_id = allConfigs.getInt(1);
                    for (int i = 1; i < configMD.getColumnCount() + 1; i++) {
                        Text property = new Text(allConfigs.getString(configMD.getColumnName(i)));
                        property.setTextAlignment(TextAlignment.CENTER);
                        configs.add(property, i - 1, configNumber);
                    }
                    Button importButton = new Button("Importer");
                    importButton.setOnMouseClicked((importButtonClick -> {
                        try {
                            ResultSet importedConfig = Configuration.selectOneConfiguration(line_id);
                            while (importedConfig.next()) {
                                simulationSpeedField.setText(importedConfig.getString(2));
                                simulationStepField.setText(importedConfig.getString(3));
                                gridWidthField.setText(importedConfig.getString(4));
                                gridHeightField.setText(importedConfig.getString(5));
                                switch (Mode.valueOf(importedConfig.getString(6))) {
                                    case fire -> {
                                        changeActiveMode(fireModeButton, Mode.fire);
                                    }
                                    case forest -> {
                                        changeActiveMode(forestModeButton, Mode.forest);
                                    }
                                    case insect -> {
                                        changeActiveMode(bugModeButton, Mode.insect);
                                    }
                                }
                                this.simulationConfig = new Configuration(
                                        importedConfig.getDouble(2),
                                        importedConfig.getInt(3),
                                        Mode.valueOf(importedConfig.getString(6)),
                                        importedConfig.getInt(4),
                                        importedConfig.getInt(5)
                                );
                                this.simulation = new Simulation(this.simulationConfig);
                                generateGrid(this.gridPane);
                                importConfig.close();
                            }

                        } catch (SQLException throwables) {
                            System.out.println(throwables.getMessage());
                            error.show();
                        }

                    }));
                    importButton.setMinWidth(100);
                    configs.add(importButton, 8, configNumber);
                    configNumber++;
                }
            } catch (SQLException e) {
                refreshConfigs();
                error.show();
            }
            importConfig.show();
        });
        loadConfig.getStyleClass().add("button");

        GridPane gridButtons = new GridPane();
        gridButtons.setAlignment(Pos.CENTER);
        gridButtons.setPadding(new Insets(50, 0, 0, 0));
        gridButtons.getStyleClass().add("buttonsGrid");
        gridButtons.setVgap(20);
        gridButtons.setHgap(20);



        HBox askSimulationNameBox = new HBox(50);
        Stage exportSimulationStage = new Stage();
        exportSimulationStage.setTitle("Sauvegarder simulation");
        Scene exportSimulationScene = new Scene(askSimulationNameBox, 750, 50);
        exportSimulationStage.setScene(exportSimulationScene);

        TextField askSimulationName = new TextField();
        askSimulationName.setMinWidth(350);
        Label askSimulationNameLabel = new Label("Quel est le nom de votre grille : ");
        askSimulationNameLabel.setMinWidth(150);
        Button submitGridName = new Button("Confirmer");
        submitGridName.setOnMouseClicked((event) -> {
            if (!askSimulationName.getText().equals("")) {
                try {
                    int gridId = this.simulation.getGrid().saveGrid();
                    int configId = this.simulationConfig.saveConfiguration(askSimulationName.getText());
                    this.simulation.saveSimulation(askSimulationName.getText(), gridId, configId);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                exportSimulationStage.close();
            }
        });
        submitGridName.setMinWidth(100);
        askSimulationNameBox.getChildren().addAll(Arrays.asList(askSimulationNameLabel, askSimulationName, submitGridName));


        askConfigName.setMinWidth(250);

        Button saveSimulation = new Button("Sauvegarder la simulation");
        saveSimulation.setOnMouseClicked((event) -> {
            exportSimulationStage.show();
        });
        saveSimulation.getStyleClass().add("button");


        Stage loadSimulationStage = new Stage();
        ScrollPane SimulationLoaderScroll = new ScrollPane();
        VBox saveSimulationRoot = new VBox(50);
        GridPane simulationsList = new GridPane();
        SimulationLoaderScroll.setContent(simulationsList);
        simulationsList.setHgap(30);
        simulationsList.setVgap(50);
        saveSimulationRoot.getChildren().add(SimulationLoaderScroll);
        Scene loadSimulationScene = new Scene(saveSimulationRoot, 900, 300);
        loadSimulationStage.setScene(loadSimulationScene);
        Button loadSimulation = new Button("Charger une simulation");

        loadSimulation.setOnMouseClicked((event) -> {
            try {
                refreshSims();
                simulationsList.getChildren().clear();
                ResultSetMetaData simulationsMD = allSims.getMetaData();

                for (int column = 4; column <= simulationsMD.getColumnCount(); column++) {

                    simulationsList.add(new Text(simulationsMD.getColumnLabel(column)), column - 4, 0);
                }

                int simulationRow = 1;

                while (allSims.next()) {

                    int loadingSimuId = allSims.getInt(1);
                    Button loadThisSimButton = new Button("Charger cette simulation");

                    for (int column = 4; column <= simulationsMD.getColumnCount(); column++) {

                        simulationsList.add(new Text(allSims.getString(column)), column - 4, simulationRow);
                    }

                    simulationsList.add(loadThisSimButton, 8, simulationRow);

                    loadThisSimButton.setOnMouseClicked((loadSim) -> {
                        onLoadSimButtonClicked(
                                loadingSimuId,
                                simulationSpeedField,
                                simulationStepField,
                                gridWidthField,
                                gridHeightField,
                                forestModeButton,
                                fireModeButton,
                                bugModeButton,
                                loadSimulationStage
                        );
                    });

                    simulationRow++;
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            loadSimulationStage.show();
        });
        loadSimulation.getStyleClass().add("button");

        configButtons.add(saveConfig, 0, 0);
        configButtons.add(loadConfig, 0, 1);
        configButtons.add(saveSimulation, 1, 0);
        configButtons.add(loadSimulation, 1, 1);



        //Simulation control
        HBox controlButtons = new HBox();
        controlButtons.setPadding(new Insets(200, 15, 0, 15));
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
        playButton.getStyleClass().add("controlButton");
        playButton.setOnMouseClicked((event) -> {
            if (!this.simulation.isPause() && this.simulation.getStep() == 0) {
                this.initialState = new Simulation(this.simulationConfig);
                this.initialState.getGrid().setMatrix((ArrayList<ArrayList<Cell>>) this.simulation.getGrid().clone());
                gridHeightField.setDisable(true);
                gridWidthField.setDisable(true);
                simulationSpeedField.setDisable(true);
                simulationStepField.setDisable(true);
                this.simulation.stepObservable.addPropertyChangeListener(this);
                simulation.run();
            } else {
                this.simulation.resume();
            }
            if (this.simulation.getStep() == (this.simulationConfig.getStepsNumber())) {
                this.simulation.setStep(0);
                simulation.run();
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
        pauseButton.getStyleClass().add("controlButton");
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
            if ((this.getStep() == 0 || this.simulation.isPause()) && this.initialState != null) {
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

        resetButton.getStyleClass().add("controlButton");
        controlButtons.getChildren().add(resetButton);

        // clear button
        SVGPath clearSVG = new SVGPath();
        clearSVG.setContent("M32 464a48 48 0 0 0 48 48h288a48 48 0 0 0 48-48V128H32zm272-256a16 16 0 0 1 32 0v224a16 16 0 0 1-32 0zm-96 0a16 16 0 0 1 32 0v224a16 16 0 0 1-32 0zm-96 0a16 16 0 0 1 32 0v224a16 16 0 0 1-32 0zM432 32H312l-9.4-18.7A24 24 0 0 0 281.1 0H166.8a23.72 23.72 0 0 0-21.4 13.3L136 32H16A16 16 0 0 0 0 48v32a16 16 0 0 0 16 16h416a16 16 0 0 0 16-16V48a16 16 0 0 0-16-16z");
        Button clearButton = new Button("", clearSVG);
        clearSVG.setScaleX(0.1);
        clearSVG.setScaleY(0.1);
        clearButton.setOnMouseClicked((event -> {
            if (this.step == 0 || this.simulation.isPause()) {
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
        clearButton.getStyleClass().add("controlButton");

        controlButtons.getChildren().add(clearButton);

        // step forward button
        SVGPath stepForwardSVG = new SVGPath();
        stepForwardSVG.setContent("M384 44v424c0 6.6-5.4 12-12 12h-48c-6.6 0-12-5.4-12-12V291.6l-195.5 181C95.9 489.7 64 475.4 64 448V64c0-27.4 31.9-41.7 52.5-24.6L312 219.3V44c0-6.6 5.4-12 12-12h48c6.6 0 12 5.4 12 12z");
        Button stepForward = new Button("", stepForwardSVG);
        stepForwardSVG.setScaleX(0.1);
        stepForwardSVG.setScaleY(0.1);
        stepForwardSVG.setFill(Color.WHITE);
        stepForward.getStyleClass().add("controlButton");
        stepForward.setOnMouseClicked((event -> {
            gridHeightField.setDisable(true);
            gridWidthField.setDisable(true);
            simulationSpeedField.setDisable(true);
            simulationStepField.setDisable(true);
            if (this.step == 0) {
                this.initialState = new Simulation(this.simulationConfig);
                this.initialState.getGrid().setMatrix((ArrayList<ArrayList<Cell>>) this.simulation.getGrid().clone());
            }
            if (this.step == 0 || this.simulation.isPause()) {
                this.simulation.stepObservable.addPropertyChangeListener(this);
                this.simulation.pause();
                this.simulation.run();
                this.simulation.step();
                this.step++;
                this.refreshGrid(this.gridPane);
                if (!this.simulation.isPause()) {
                    this.simulation.pause();
                }
                if (this.step >= this.simulationConfig.getStepsNumber()) {
                    gridHeightField.setDisable(false);
                    gridWidthField.setDisable(false);
                    simulationSpeedField.setDisable(false);
                    simulationStepField.setDisable(false);
                }
            }


        }));
        controlButtons.getChildren().add(stepForward);
        rectDivConfig.getChildren().add(configButtons);

        //Densities
        VBox densitiesBox = new VBox(10);
        densitiesBox.setPadding(new Insets(50, 0,0, 0));
        rectDivConfig.getChildren().add(densitiesBox);
        densitiesBox.getChildren().addAll(densities);
        rectDivConfig.getChildren().add(controlButtons);
        root.getChildren().add(rectDivConfig);
        refreshDensities();
        // Rendering stage
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setTitle("Simulation JSIMForest");
        stage.show();
    }

    public void refreshDensities(){


    }

    public void editSimulationCells(ResultSet gridCells) {
        try {
            while (gridCells.next()) {
                int i = gridCells.getInt(1);
                int j = gridCells.getInt(2);
                String cellTypeName = gridCells.getString(4);
                String cellTypeColor = gridCells.getString(5);
                String cellHealth = gridCells.getString(3);
                CellType newCellType = new CellType(cellTypeName, cellHealth);
                Health health = Health.valueOf(cellHealth);

                this.simulation.getGrid().editCell(i, j, newCellType, health);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onLoadSimButtonClicked(
            int loadingSimu,
            TextField simulationSpeedField,
            TextField simulationStepField,
            TextField gridWidthField,
            TextField gridHeightField,
            Button forestModeButton,
            Button fireModeButton,
            Button bugModeButton,
            Stage loadSimulationStage
    ) {
        try {
            ResultSet importedSimulationConfig = Simulation.selectOneSimulation(loadingSimu);

            if (importedSimulationConfig.next()) {
                setLocker(true);
                simulationSpeedField.setText(importedSimulationConfig.getString(7));
                simulationStepField.setText(importedSimulationConfig.getString(8));
                gridWidthField.setText(importedSimulationConfig.getString(9));
                gridHeightField.setText(importedSimulationConfig.getString(10));
                setLocker(false);
                switch (Mode.valueOf(importedSimulationConfig.getString(11))) {
                    case fire -> {
                        changeActiveMode(fireModeButton, Mode.fire);
                    }
                    case forest -> {
                        changeActiveMode(forestModeButton, Mode.forest);
                    }
                    case insect -> {
                        changeActiveMode(bugModeButton, Mode.insect);
                    }
                }

                this.simulationConfig = new Configuration(
                        importedSimulationConfig.getDouble(7),
                        importedSimulationConfig.getInt(8),
                        Mode.valueOf(importedSimulationConfig.getString(11)),
                        importedSimulationConfig.getInt(9),
                        importedSimulationConfig.getInt(10)
                );

                this.simulation = new Simulation(this.simulationConfig);
            }

            ResultSet gridCells = Grid.selectGridCells(importedSimulationConfig.getInt(3));

            editSimulationCells(gridCells);

            this.gridPane.getChildren().clear();

            loadSimulationStage.close();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            refreshGrid(this.gridPane);
        }

    }
}
