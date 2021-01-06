package com.jsimforest;

public class Cell {

    private CellType cellType;
    private int coordX;
    private int coordY;
    private Health health;

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public int getCoordX() {
        return coordX;
    }

    /**
     *
     * @param coordX coordinate of cell on the axe X
     * @throws IllegalArgumentException if the coordinate X is inferior or equal to 0
     */
    public void setCoordX(int coordX) {
        if(coordX >= 0){
            this.coordX = coordX;
        }
        else{
            throw new IllegalArgumentException("coordX must be superior or equal to 0");
        }
    }


    public int getCoordY() {
        return coordY;
    }

    /**
     *
     * @param coordY  coordinate of cell on the axe Y
     */
    public void setCoordY(int coordY) {
        if(coordY >= 0) {
            this.coordY = coordY;
        }
        else {
            throw new IllegalArgumentException("coordY must be superior or equal to 0");
        }
    }

    public Health getHealth() {
        return health;
    }

    /**
     *
     * @param health state of cell with a enum
     */
    public void setHealth(Health health) {
        this.health = health;
    }
}
