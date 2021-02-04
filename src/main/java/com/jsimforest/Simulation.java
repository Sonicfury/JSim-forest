package com.jsimforest;

import java.sql.ResultSet;

import javafx.application.Platform;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;

public class Simulation {
    private final Configuration configuration;
    private Grid grid;

    private int step;
    private int elapsedTime;
    private final ArrayList<Density> densities = new ArrayList<>();

    public boolean isPause() {
        return pause;
    }

    private boolean pause;
    public PCLStep stepObservable;
    private static final CellType emptyType = new CellType();
    private static final CellType plantType = new CellType("plant", "lightGreen");
    private static final CellType youngTreeType = new CellType("youngTree", "mediumGreen");
    private static final CellType treeType = new CellType("tree", "green");
    String env = new JsimProp("config.properties").loadPropertiesFile("environment");

    public Simulation(Configuration config) {
        this.configuration = config;
        this.grid = new Grid(config.getGridWidth(), config.getGridHeight());
        this.step = 0;
        this.stepObservable = new PCLStep();
    }

    /**
     * Resets the grid
     */
    public void newGrid() {
        this.grid = new Grid(this.configuration.getGridWidth(), this.configuration.getGridHeight());
    }

    public Grid getGrid() {

        return this.grid;
    }

    public int getStep() {

        return this.step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getElapsedTime() {

        return elapsedTime;
    }

    public ArrayList<Density> getDensities() {

        return densities;
    }

    /**
     * Pauses the simulation
     */
    public void pause() {
        this.pause = true;
    }

    /**
     * Resumes the simulation
     */
    public void resume() {
        this.pause = false;
    }

    /**
     * Runs steps of the simulation.
     */
    public void run() {
        double stepsPerSecond = this.configuration.getStepsPerSecond();
        int interval = (int) (1000 / stepsPerSecond);
        if (env.equals("prod")) {
            new Thread(() -> {
                while (this.step < this.configuration.getStepsNumber()) {
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> {
                        if (!this.pause) {
                            this.step(); // this.step incremented in step()
                            this.elapsedTime = this.step * interval;
                        }
                    });
                }
                this.pause = true;
            }).start();
        } else {
            while (this.step < this.configuration.getStepsNumber()) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (!this.pause) {
                    this.step(); // this.step incremented in step()
                    this.elapsedTime = this.step * interval;
                }
            }
        }
    }

    /**
     *
     * @param i
     * @param j
     * @param k
     * @param l
     * @param matrix
     * @param cellTypesList
     * @param cellHealthList
     */
    public void addToHealthAndTypesList(int i, int j, int k, int l, ArrayList<ArrayList<Cell>> matrix, List<String> cellTypesList, List<Health> cellHealthList){
        if (!(i == k && j == l) && l >= 0 && l < matrix.get(i).size()) { // do not add centralCell's cellType to the list
            Cell c = matrix.get(k).get(l);
            cellTypesList.add(c.getCellType().getName());

            if (this.configuration.getMode().equals(Mode.fire)) {
                cellHealthList.add(c.getHealth());
            } else if (
                    this.configuration.getMode().equals(Mode.insect)
                            && (
                            (k == i - 1 && l == j)
                                || (k == i && l == j - 1)
                                || (k == i + 1 && l == j)
                                || (k == i && l == j + 1)
                    )
            ) {
                cellHealthList.add(c.getHealth());
            }
        }
    }

    /**
     *
     * @param i
     * @param j
     * @param matrix
     * @return
     */
    public TypesAndHealthsList getCellsTypeAndHealth(int i, int j, ArrayList<ArrayList<Cell>> matrix) {
        List<String> cellTypesList = new ArrayList<>();
        List<Health> cellHealthList = new ArrayList<>();

        for (int k = i - 1; k <= i + 1; k++) {
            if (k >= 0 && k < matrix.size()) {
                for (int l = j - 1; l <= j + 1; l++) {

                    addToHealthAndTypesList(i, j, k, l, matrix, cellTypesList, cellHealthList);
                }
            }
        }

        return new TypesAndHealthsList(cellTypesList, cellHealthList);
    }

    /**
     * Walks through each list to evolve related cells
     * @param toReset
     * @param toAsh
     * @param toBurning
     * @param toInfected
     * @param toPlant
     * @param toYoungTree
     * @param toTree
     */
    public void evolveCells(
            ArrayList<Cell> toReset,
            ArrayList<Cell> toAsh,
            ArrayList<Cell> toBurning,
            ArrayList<Cell> toInfected,
            ArrayList<Cell> toPlant,
            ArrayList<Cell> toYoungTree,
            ArrayList<Cell> toTree
    ) {
        for (Cell cell : toReset) {
            cell.reset();
        }
        for (Cell cell : toAsh) {
            cell.setHealth(Health.ash);
            cell.setAge(0);
        }
        for (Cell cell : toBurning) {
            cell.setFire();
            cell.setAge(0);
        }
        for (Cell cell : toInfected) {
            cell.infect();
            cell.setAge(0);
        }
        for (Cell cell : toPlant) {
            cell.setCellType(plantType);
            cell.setAge(0);
        }
        for (Cell cell : toYoungTree) {
            cell.setCellType(youngTreeType);
            cell.setAge(0);
        }
        for (Cell cell : toTree) {
            cell.setCellType(treeType);
            cell.setAge(0);
        }
    }

    /**
     * Tells wether a cell should evolve and in what type/health
     * @param centralCell
     * @param cellTypesList
     * @param cellHealthList
     * @param toReset
     * @param toAsh
     * @param toBurning
     * @param toInfected
     * @param toPlant
     * @param toYoungTree
     * @param toTree
     */
    public void checkCentralCellForEvolution(
            Cell centralCell,
            List<String> cellTypesList,
            List<Health> cellHealthList,
            ArrayList<Cell> toReset,
            ArrayList<Cell> toAsh,
            ArrayList<Cell> toBurning,
            ArrayList<Cell> toInfected,
            ArrayList<Cell> toPlant,
            ArrayList<Cell> toYoungTree,
            ArrayList<Cell> toTree
    ) {
        if (centralCell.getHealth().equals(Health.burned)) {
            toAsh.add(centralCell);

            return;
        }

        if (centralCell.getHealth().equals(Health.ash) || centralCell.getHealth().equals(Health.infected)) {
            toReset.add(centralCell);

            return;
        }

        int youngTreeTypeCount = Collections.frequency(cellTypesList, youngTreeType.getName());
        int treeTypeCount = Collections.frequency(cellTypesList, treeType.getName());

        int fireCount = Collections.frequency(cellHealthList, Health.burned);
        int infectedCount = Collections.frequency(cellHealthList, Health.infected);

        if (
                this.configuration.getMode().equals(Mode.fire)
                        && centralCell.getHealth().equals(Health.ok)
                        && !centralCell.getCellType().getName().equals(emptyType.getName())
                        && fireCount >= 1
        ) {
            toBurning.add(centralCell);
        } else if (
                this.configuration.getMode().equals(Mode.insect)
                        && centralCell.getHealth().equals(Health.ok)
                        && !centralCell.getCellType().getName().equals(emptyType.getName())
                        && infectedCount >= 1
        ) {
            toInfected.add(centralCell);
        } else {
            if (centralCell.getCellType().getName().equals(youngTreeType.getName()) && centralCell.getAge() == 2) {
                toTree.add(centralCell);
            }
            if (centralCell.getCellType().getName().equals(plantType.getName()) && (treeTypeCount + youngTreeTypeCount) <= 3) {
                toYoungTree.add(centralCell);
            }
            if (centralCell.getCellType().getName().equals(emptyType.getName()) && (
                    treeTypeCount >= 2 || youngTreeTypeCount >= 3 || (treeTypeCount == 1 && youngTreeTypeCount == 2)
            )) {
                toPlant.add(centralCell);
            }
        }
    }

    /**
     * One step
     */
    public void step() {
        ArrayList<ArrayList<Cell>> matrix = this.grid.getMatrix();

        ArrayList<Cell> toPlant = new ArrayList<>();
        ArrayList<Cell> toYoungTree = new ArrayList<>();
        ArrayList<Cell> toTree = new ArrayList<>();

        ArrayList<Cell> toBurning = new ArrayList<>();
        ArrayList<Cell> toInfected = new ArrayList<>();
        ArrayList<Cell> toAsh = new ArrayList<>();
        ArrayList<Cell> toReset = new ArrayList<>();

        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                Cell centralCell = matrix.get(i).get(j);

                TypesAndHealthsList typesAndHealthsList = getCellsTypeAndHealth(i, j, matrix);

                List<String> cellTypesList = typesAndHealthsList.getCellTypesList();
                List<Health> cellHealthList = typesAndHealthsList.getCellHealthList();

                centralCell.setAge(centralCell.getAge() + 1);
                checkCentralCellForEvolution(centralCell, cellTypesList, cellHealthList, toReset, toAsh, toBurning, toInfected, toPlant, toYoungTree, toTree);
            }
        }

        evolveCells(toReset, toAsh, toBurning, toInfected, toPlant, toYoungTree, toTree);
        calculateDensity(matrix);

        this.step += 1;
        this.stepObservable.setStep(this.step);
    }

    /**
     * Calculates instant density for a step and adds it to the Simulation.densities for history.
     * @param matrix
     */
    public void calculateDensity(ArrayList<ArrayList<Cell>> matrix) {
        int plantTypeTotalCount = 0;
        int youngTreeTypeTotalCount = 0;
        int treeTypeTotalCount = 0;

        int burningTotalCount = 0;
        int ashTotalCount = 0;
        int infectedTotalCount = 0;

        for (ArrayList<Cell> cells : matrix) {
            for (Cell centralCell : cells) {
                if (plantType.equals(centralCell.getCellType())) {
                    plantTypeTotalCount++;
                } else if (youngTreeType.equals(centralCell.getCellType())) {
                    youngTreeTypeTotalCount++;
                } else if (treeType.equals(centralCell.getCellType())) {
                    treeTypeTotalCount++;
                }

                if (Health.burned.equals(centralCell.getHealth())) {
                    burningTotalCount++;
                } else if (Health.ash.equals(centralCell.getHealth())) {
                    ashTotalCount++;
                } else if (Health.infected.equals(centralCell.getHealth())) {
                    infectedTotalCount++;
                }
            }
        }

        int cellsCount = this.grid.getHeight() * this.grid.getWidth();

        double plantDensity = (double) plantTypeTotalCount / (double) cellsCount;
        double youngTreeDensity = (double) youngTreeTypeTotalCount / (double) cellsCount;
        double treeDensity = (double) treeTypeTotalCount / (double) cellsCount;

        double burningDensity = (double) burningTotalCount / (double) cellsCount;
        double ashDensity = (double) ashTotalCount / (double) cellsCount;
        double infectedDensity = (double) infectedTotalCount / (double) cellsCount;

        this.densities.add(
                new Density(plantDensity, youngTreeDensity, treeDensity, burningDensity, ashDensity, infectedDensity, this.step)
        );
    }

    /**
     * @throws SQLException sql exception
     */
    public void saveSimulation(String name, int idGrid, int idConfiguration) throws SQLException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        String creationDate = "'" + format.format(new java.util.Date()) + "'";

        String nameReformat = "'" + name + "'";

        String sql = MessageFormat.format(

                "INSERT INTO Simulations (name, steps, creationDate, id_Grids, id_Configurations) VALUES ( {0}, {1}, {2}, {3}, {4}  )",
                nameReformat, this.step, creationDate, idGrid, idConfiguration);

        DataBaseInterface.insert(sql);
    }

    /**
     * Static class containing a list of healths and a list of types (for the neighborhood).
     */
    static class TypesAndHealthsList {
        List<String> cellTypesList;
        List<Health> cellHealthList;

        public TypesAndHealthsList(List<String> cellTypesList, List<Health> cellHealthList) {
            this.cellTypesList = cellTypesList;
            this.cellHealthList = cellHealthList;
        }

        public List<String> getCellTypesList() {
            return cellTypesList;
        }

        public List<Health> getCellHealthList() {
            return cellHealthList;
        }
    }

    public static ResultSet selectOneSimulation(int id) throws  SQLException {

        String sql = MessageFormat.format("SELECT s.id, c.id, id_Grids, s.name, steps, s.creationDate, execSpeed, stepNumber, gridWidth, gridHeight, configMode FROM simulations s JOIN configurations c on s.id_Configurations = c.id WHERE s.id = {0}", id);

        return DataBaseInterface.select(sql);
    }

    public static ResultSet selectAllSimulations() throws  SQLException {

        String sql = "SELECT s.id, c.id, id_Grids, s.name, steps, s.creationDate, execSpeed, stepNumber, gridWidth, gridHeight, configMode FROM simulations s JOIN configurations c on s.id_Configurations = c.id";

        return DataBaseInterface.select(sql);
    }
}
