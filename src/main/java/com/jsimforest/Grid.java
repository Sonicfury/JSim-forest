package com.jsimforest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;

public class Grid {

    public Grid(int width, int height) {
        setWidth(width);
        setHeight(height);
        ArrayList<ArrayList<Cell>> matrix = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            matrix.add(new ArrayList<>());
            ArrayList<Cell> line = matrix.get(i);
            for (int j = 0; j < width; j++) {
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
     * @param matrix new matrix for the grid
     * @throws IllegalArgumentException when the new matrix have wrong dimensions
     */
    public void setMatrix(ArrayList<ArrayList<Cell>> matrix) {
        if (matrix.size() == this.getHeight()) {
            for (ArrayList<Cell> line : matrix) {
                if (line.size() != this.getWidth()) {
                    throw new IllegalArgumentException();
                }
            }
            this.matrix = matrix;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Object clone() {
        ArrayList<ArrayList<Cell>> matrixClone = new ArrayList<>();
        for (ArrayList<Cell> line : this.getMatrix()) {
            matrixClone.add(new ArrayList<>());
            for (Cell cell : line) {
                matrixClone.get(matrixClone.size() - 1).add(cell.clone());
            }
        }
        return matrixClone;
    }

    public int getWidth() {
        return width;
    }

    /**
     * @param width number of columns of the simulation
     * @throws IllegalArgumentException when width value is negative
     */
    public void setWidth(int width) {
        if (width > 0) {
            this.width = width;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int getHeight() {
        return height;
    }

    /**
     * @param height number of lines of the simulation
     * @throws IllegalArgumentException when height value is negative
     */
    public void setHeight(int height) {
        if (height > 0) {
            this.height = height;
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param x        cell's x coordinate
     * @param y        cell's y coordinate
     * @param cellType cell's cellType
     */
    public void editCell(int x, int y, CellType cellType) {
        ArrayList<Cell> row = this.getMatrix().get(y);
        Cell cell = row.get(x);

        cell.setCellType(cellType);
    }

    public void editCell(int x, int y, CellType cellType, Health health) {
        ArrayList<Cell> row = this.getMatrix().get(y);
        Cell cell = row.get(x);
        cell.setCellType(cellType);
        cell.setHealth(health);
    }

    public void editCell(int x, int y, Health health) {
        ArrayList<Cell> row = this.getMatrix().get(y);
        Cell cell = row.get(x);
        cell.setHealth(health);
    }

    /**
     *
     */
    public int saveGrid() throws SQLException {
        String sql = MessageFormat.format(
                "INSERT INTO Grids (height, width) VALUES ( {0}, {1} )",
                this.height, this.width);

        int gridId = DataBaseInterface.insert(sql);
        sql = "INSERT INTO Cells (coordX, coordY, health, id_Types, id_Grids) VALUES ";
        StringBuilder insertingValues = new StringBuilder();
        Cell currentCell;
        int idType;
        for (int i = 0; i < this.getWidth(); i++) {
            for (int j = 0; j < this.getHeight(); j++) {
                currentCell = this.matrix.get(i).get(j);
                switch (currentCell.getCellType().getName()) {
                    case "plant" -> {
                        idType = 2;
                    }
                    case "youngTree" -> {
                        idType = 3;
                    }
                    case "null" -> {
                        idType = 1;
                    }
                    case "tree" -> {
                        idType = 4;
                    }
                    default -> {
                        System.out.println(currentCell.getCellType().getName());
                        throw new IllegalArgumentException("Le type fourni n'est pas correct");
                    }
                }
                insertingValues.append(MessageFormat.format(
                        "( {0}, {1}, {2}, {3}, {4} ), ",
                        currentCell.getCoordX(),
                        currentCell.getCoordY(),
                        "'" + currentCell.getHealth() + "'",
                        idType,
                        gridId
                ));
            }
        }
        DataBaseInterface.insert(sql + insertingValues.substring(0, insertingValues.length() - 2));
        return gridId;
    }

    public static ResultSet selectGridCells(int gridId) throws SQLException {

        String sql = MessageFormat.format("SELECT coordX, coordY, health, t.name, t.color FROM cells c JOIN types t on c.id_Types = t.id WHERE id_Grids = {0} ORDER BY coordX ASC, coordY ASC", gridId);
        return DataBaseInterface.select(sql);
    }
}
