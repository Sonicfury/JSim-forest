package com.jsimforest;

public class CellType {

    private String name;
    private String color;

    /**
     * Configuration CellType - default
     */
    public CellType(){
        this.name = "tree";
        this.color = "green";
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

    /**
     *
     * @param name name type of cell
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    /**
     *
     * @param color color of type
     */
    public void setColor(String color) {
        this.color = color;
    }
}
