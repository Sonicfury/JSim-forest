package com.jsimforest;

import java.util.ArrayList;

public class Grid {
    public Grid(int width, int height) {
        setWidth(width);
        setHeight(height);
        ArrayList<ArrayList<Cell>> matrix = new ArrayList<>();
        for(int i=0;i<height;i++){
            matrix.add(new ArrayList<>());
            ArrayList<Cell> line = matrix.get(i);
            for(int j=0;j<width;j++){
                line.add(new Cell(j, i));
            }
        }
        setMatrix(matrix);
    }

    private int width;
    private int height;
    private ArrayList<ArrayList<Cell>> matrix;


    public ArrayList<ArrayList<Cell>> getMatrix() {
        return matrix;
    }

    /**
     *
     * @param matrix new matrix for the grid
     * @throws IllegalArgumentException when the new matrix have wrong dimensions
     */
    public void setMatrix(ArrayList<ArrayList<Cell>> matrix) {
        if(matrix.size() == this.getHeight()){
            for(ArrayList<Cell> line: matrix){
                if(line.size() != this.getWidth()){
                    throw new IllegalArgumentException();
                }
            }
            this.matrix = matrix;
        }else{
            throw new IllegalArgumentException();
        }
    }

    public int getWidth() {
        return width;
    }

    /**
     *
     * @param width number of columns of the simulation
     * @throws IllegalArgumentException when width value is negative
     */
    public void setWidth(int width) {
        if(width > 0){
            this.width = width;
        }else{
            throw new IllegalArgumentException();
        }
    }

    public int getHeight() {
        return height;
    }

    /**
     *
     * @param height number of lines of the simulation
     * @throws IllegalArgumentException when height value is negative
     */
    public void setHeight(int height) {
        if(height > 0){
            this.height = height;
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param x cell's x coordinate
     * @param y cell's y coordinate
     * @param cellType cell's cellType
     */
    public void editCell(int x, int y, CellType cellType){
        ArrayList<Cell> row = this.getMatrix().get(y);
        Cell cell = row.get(x);

        cell.setCellType(cellType);
    }
}
