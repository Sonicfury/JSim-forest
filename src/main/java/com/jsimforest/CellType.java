package com.jsimforest;

public class CellType {

    private final String name;
    private final String color;

    /**
     * Configuration CellType - default
     */
    public CellType(){
        this.name = "null";
        this.color = "null";
    }

    /**
     * Overloading CellType Constructor - full
     * @param name name of Cell Type
     * @param color color of Cell
     */
    public CellType(String name, String color){
        this.name = name;
        this.color = color;
    }

    public String getName() {

        return name;
    }

    public String getColor() {

        return color;
    }

    public boolean equals(CellType cellType){

        return this.getName().equals(cellType.getName()) && this.getColor().equals(cellType.getColor());
    }
}
