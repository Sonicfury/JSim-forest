package com.jsimforest;

import java.sql.SQLException;
import java.text.MessageFormat;

public class Cell {

    private CellType cellType;
    private int coordX;
    private int coordY;
    private int age = 0;
    private Health health;

    public Cell() {
    }

    /**
     * CellType constructor
     *
     * @param coordX X axis coordinate
     * @param coordY Y axis coordinate
     */
    public Cell(int coordX, int coordY) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.cellType = new CellType();
        this.health = Health.ok;
    }

    /**
     * Cell Constructor - overload
     *
     * @param cellType type of Cell
     * @param coordX   coordinate of cell on the axe X
     * @param coordY   coordinate of cell on the axe Y
     */
    public Cell(CellType cellType, int coordX, int coordY) {
        this.cellType = cellType;
        this.coordX = coordX;
        this.coordY = coordY;
        this.health = Health.ok;
    }

    /**
     *
     * @param cellType type of Cell
     * @param health health of cell
     * @param coordX coordinate of cell on the axe X
     * @param coordY coordinate of cell on the axe Y
     */
    public Cell(CellType cellType, Health health, int coordX, int coordY) {
        this.cellType = cellType;
        this.coordX = coordX;
        this.coordY = coordY;
        this.health = health;
    }

    public CellType getCellType() {

        return cellType;
    }

    @Override
    public Cell clone(){
        return new Cell(this.cellType, this.coordX, this.coordY);
    }

    /**
     * @param cellType type of Cell
     */
    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public int getCoordX() {

        return coordX;
    }

    /**
     * @param coordX coordinate of cell on the X axis
     * @throws IllegalArgumentException if the coordinate X is inferior to 0
     */
    public void setCoordX(int coordX) {
        if (coordX >= 0) {
            this.coordX = coordX;
        } else {
            throw new IllegalArgumentException("coordX must be superior or equal to 0");
        }
    }

    public int getCoordY() {

        return coordY;
    }

    /**
     * @param coordY coordinate of cell on the Y axis
     * @throws IllegalArgumentException if the coordinate Y is inferior to 0
     */
    public void setCoordY(int coordY) {
        if (coordY >= 0) {
            this.coordY = coordY;
        } else {
            throw new IllegalArgumentException("coordY must be superior or equal to 0");
        }
    }

    /**
     *
     * @param age How many steps has lived the cell
     */
    public void setAge(int age){
        if (age >= 0) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("Cell age must be superior or equal to 0");
        }
    }

    public int getAge() {

        return this.age;
    }

    public Health getHealth() {

        return health;
    }

    /**
     * @param health state of cell with a enum
     */
    public void setHealth(Health health) {
        this.health = health;
    }

    public boolean equals(Cell cell) {

        return this.cellType.equals(cell.getCellType()) && this.coordX == cell.getCoordX() && this.coordY == cell.getCoordY();
    }

    /**
     *
     * @throws SQLException sql exception
     */

    public void saveCell(int idType, int idGrid) throws SQLException {

        String sql = MessageFormat.format(
                "INSERT INTO Cells (coordX, coordY, health, id_Types, id_Grids) VALUES ( {0}, {1}, {2}, {3}, {4} )",
                this.coordX, this.coordY, this.health, idType, idGrid);

        DataBaseInterface.insert(sql);
    }

    public void setFire(){
        int randInt = (int) (Math.random() * 100);

        switch (this.cellType.getName()){
            case "plant":
                if (randInt <= 25){
                    this.setHealth(Health.burned);
                }
                break;
            case "youngTree":
                if (randInt <= 50){
                    this.setHealth(Health.burned);
                }
                break;
            case "tree":
                if (randInt <= 75){
                    this.setHealth(Health.burned);
                }
                break;
            default:
                break;
        }
    }

    public void infect(){
        int randInt = (int) (Math.random() * 100);

        switch (this.cellType.getName()){
            case "plant":
                if (randInt <= 75){
                    this.setHealth(Health.infected);
                }
                break;
            case "youngTree":
                if (randInt <= 50){
                    this.setHealth(Health.infected);
                }
                break;
            case "tree":
                if (randInt <= 25){
                    this.setHealth(Health.infected);
                }
                break;
            default:
                break;
        }
    }

    public void reset(){
        this.setCellType(new CellType());
        this.setHealth(Health.ok);
        this.setAge(0);
    }
}
