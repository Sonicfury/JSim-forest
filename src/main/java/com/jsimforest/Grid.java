package com.jsimforest;

import javafx.geometry.*;
import java.util.ArrayList;

public class Grid {
    public Grid(int width, int height) {
        setWidth(width);
        setHeight(height);
        ArrayList<ArrayList<Cell>> matrix = new ArrayList<ArrayList<Cell>>();
        setJFXRoot(width, height);
        for(int i=0;i<height;i++){
            matrix.add(new ArrayList<Cell>());
            ArrayList<Cell> line = matrix.get(i);
            for(int j=0;j<width;j++){
                line.add(new Cell());
            }
        }
        setMatrix(matrix);
    }

    private int width;
    private int height;
    private ArrayList<ArrayList<Cell>> matrix;
    private javafx.geometry.BoundingBox JFXRoot;

    
    public ArrayList<ArrayList<Cell>> getMatrix() {
        return matrix;
    }

    public void setMatrix(ArrayList<ArrayList<Cell>> matrix) {
        this.matrix = matrix;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public BoundingBox getJFXRoot() {
        return JFXRoot;
    }

    public void setJFXRoot(BoundingBox JFXRoot) {
        this.JFXRoot = JFXRoot;
    }

    public void setJFXRoot(int width, int height) {
        this.JFXRoot = new BoundingBox(0, 0, width, height);
    }
}
