package com.jsimforest;

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

    public void run() throws InterruptedException {
        double stepsPerSecond = this.configuration.getStepsPerSecond();
        int interval = (int) (1000 / stepsPerSecond);

        while (this.step < this.configuration.getStepsNumber()) {
            // this.step incremented in step()
            if (!this.pause) {
                this.step();
                Thread.sleep(interval);
                this.elapsedTime = this.step * interval;
            }
        }
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
}
