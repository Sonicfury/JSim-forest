package com.jsimforest;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import javafx.application.Platform;

import java.util.*;
import java.util.List;

public class Simulation {
    private final Configuration configuration;
    private Grid grid;
    private int step;
    private int elapsedTime;
    private boolean pause;
    public PCLStep stepObservable;

    public Simulation(Configuration config) {
        this.configuration = config;
        this.grid = new Grid(config.getGridWidth(), config.getGridHeight());
        this.step = 0;
        this.stepObservable = new PCLStep();
    }

    public void newGrid(){
        this.grid = new Grid(this.configuration.getGridWidth(), this.configuration.getGridHeight());
    }

    public Grid getGrid() {

        return this.grid;
    }

    public int getStep() {

        return this.step;
    }

    public int getElapsedTime() {

        return elapsedTime;
    }

    /*public void pause(){
        this.pause = true;
    }

    public void resume(){
        this.pause = false;
    }*/

    public void run() {
        double stepsPerSecond = this.configuration.getStepsPerSecond();
        int interval = (int) (1000 / stepsPerSecond);
        new Thread(() -> {
            while (this.step < this.configuration.getStepsNumber()) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e){
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
    }

    public void step() {
        CellType plantType = new CellType("plant", "lightGreen");
        CellType youngTreeType = new CellType("youngTree", "mediumGreen");
        CellType treeType = new CellType("tree", "green");

        ArrayList<ArrayList<Cell>> matrix = this.grid.getMatrix();
        ArrayList<Cell> toPlant = new ArrayList<>();
        ArrayList<Cell> toYoungTree = new ArrayList<>();
        ArrayList<Cell> toTree = new ArrayList<>();


        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                Cell centralCell = matrix.get(i).get(j);
                List<String> cellTypesList = new ArrayList<>();

                for (int k = i - 1; k <= i + 1; k++) {
                    if (k >= 0 && k < matrix.size()) {
                        for (int l = j - 1; l <= j + 1; l++) {
                            // do not add centralCell's cellType to the list
                            if (i == k && j == l) {
                                continue;
                            } else if (l >= 0 && l < matrix.get(i).size()) {
                                Cell c = matrix.get(k).get(l);
                                cellTypesList.add(c.getCellType().getName());
                            }
                        }
                    }
                }

                centralCell.setAge(centralCell.getAge() + 1);

                int plantTypeCount = Collections.frequency(cellTypesList, plantType.getName());
                int youngTreeTypeCount = Collections.frequency(cellTypesList, youngTreeType.getName());
                int treeTypeCount = Collections.frequency(cellTypesList, treeType.getName());

                if (centralCell.getCellType().getName().equals("youngTree") && centralCell.getAge() == 2) {
                    toTree.add(centralCell);
                }
                if (centralCell.getCellType().getName().equals("plant") && (treeTypeCount + youngTreeTypeCount) <= 3) {
                    toYoungTree.add(centralCell);
                }
                if (centralCell.getCellType().getName().equals("null") && (
                        treeTypeCount >= 2 || youngTreeTypeCount >= 3 || (treeTypeCount == 1 && youngTreeTypeCount == 2)
                )) {
                    toPlant.add(centralCell);
                }
            }
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

        this.step += 1;
        this.stepObservable.setStep(this.step);
    }

    /**
     *
     * @throws SQLException sql exception
     */
    public void saveSimulation(String name, int idGrid, int idConfiguration) throws SQLException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        String creationDate = "'" + format.format(new java.util.Date()) + "'" ;

        String nameReformat = "'" + name + "'" ;

        String sql = MessageFormat.format(

                "INSERT INTO Simulations (name, steps, creationDate, id_Grids, id_Configurations) VALUES ( {0}, {1}, {2}, {3}, {4}  )",
                nameReformat, this.step, creationDate, idGrid, idConfiguration);

        DataBaseInterface.insert(sql);
    }
}
