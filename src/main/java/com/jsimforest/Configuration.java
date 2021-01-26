package com.jsimforest;

public class Configuration {

    private double stepsPerSecond;
    private int stepsNumber;
    private Mode mode;
    private int gridWidth;
    private int gridHeight;

    /**
     * Configuration Constructor - default
     */
    public Configuration() {
        this.stepsPerSecond = 1;
        this.stepsNumber = 20;
        this.mode = Mode.forest;
        this.gridWidth = 100;
        this.gridHeight = 100;
    }

    /**
     * Overloading Configuration Constructor - full
     *
     * @param stepsPerSecond the number of steps per second for the simulation
     * @param stepsNumber    the number of steps for the simulation
     * @param mode           the simulation's mode to run (forest -- default, insect, fire)
     * @param gridWidth      how many Cells width is the grid
     * @param gridHeight     how many Cells height is the grid
     */
    public Configuration(double stepsPerSecond, int stepsNumber, Mode mode, int gridWidth, int gridHeight) {
        this.stepsPerSecond = stepsPerSecond;
        this.stepsNumber = stepsNumber;
        this.mode = mode;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
    }

    public double getStepsPerSecond() {
        return stepsPerSecond;
    }

    /**
     * stepsPerSecond should always be at least 1 to properly run the simulation
     *
     * @param stepsPerSecond the number of steps per second for the simulation
     * @throws IllegalArgumentException if the number of steps per second is inferior or equal to 0
     */
    public void setStepsPerSecond(double stepsPerSecond) {
        if (stepsPerSecond > 0.001) {
            this.stepsPerSecond = stepsPerSecond;
        } else {
            throw new IllegalArgumentException("The number of steps per second must be superior to 0");
        }
    }

    public int getStepsNumber() {
        return stepsNumber;
    }

    /**
     * stepNumber should always be at least 1 to properly run a simulation
     *
     * @param stepNumber the number of steps for the simulation
     * @throws IllegalArgumentException if the number or steps is inferior to 1
     */
    public void setStepsNumber(int stepNumber) {
        if (stepNumber > 0) {
            this.stepsNumber = stepNumber;
        } else {
            throw new IllegalArgumentException("The number of steps must be at least 1");
        }
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    /**
     * The user has to draw a minimum of 5 cells to run a simulation ; therefore, the grid must be at least 3x3
     *
     * @param gridWidth how many Cells width is the grid
     * @throws IllegalArgumentException if the width is inferior to 3
     */
    public void setGridWidth(int gridWidth) {
        if (gridWidth >= 3) {
            this.gridWidth = gridWidth;
        } else {
            throw new IllegalArgumentException("The grid width must be at least 3");
        }
    }

    public int getGridHeight() {
        return gridHeight;
    }

    /**
     * The user has to draw a minimum of 5 cells to run a simulation ; therefore, the grid must be at least 3x3
     *
     * @param gridHeight how many Cells height is the grid
     * @throws IllegalArgumentException if the height is inferior to 3
     */
    public void setGridHeight(int gridHeight) {
        if (gridHeight >= 3) {
            this.gridHeight = gridHeight;
        } else {
            throw new IllegalArgumentException("The grid height must be at least 3");
        }
    }
}
