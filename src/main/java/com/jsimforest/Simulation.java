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

    public void pause(){
        this.pause = true;
    }

    public void resume(){
        this.pause = false;
    }

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

    public TypesAndHealthsList getCellsTypeAndHealth(int i, int j, ArrayList<ArrayList<Cell>> matrix) {
        List<String> cellTypesList = new ArrayList<>();
        List<Health> cellHealthList = new ArrayList<>();

        for (int k = i - 1; k <= i + 1; k++) {
            if (k >= 0 && k < matrix.size()) {
                for (int l = j - 1; l <= j + 1; l++) {
                    // do not add centralCell's cellType to the list
                    if (!(i == k && j == l) && l >= 0 && l < matrix.get(i).size()) {
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
            }
        }

        return new TypesAndHealthsList(cellTypesList, cellHealthList);
    }

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

        this.step += 1;
        this.stepObservable.setStep(this.step);
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

    public ResultSet selectOneSimulatiuon(int id) throws  SQLException {

        String sql = MessageFormat.format("SELECT * FROM configurations WHERE id = {0}", id);

        return DataBaseInterface.select(sql);
    }

    public ResultSet selectAllSimulations() throws  SQLException {

        String sql = "SELECT * FROM configurations ";

        return DataBaseInterface.select(sql);
    }
}
