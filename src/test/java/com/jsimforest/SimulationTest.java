package com.jsimforest;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest extends AbstractTest {

    @Test
    public void setGridInitialState_fromConfiguration() {
        CellType plantType = new CellType("plant", "lightGreen");
        CellType youngTreeType = new CellType("youngTree", "mediumGreen");
        CellType treeType = new CellType("tree", "green");

        Configuration config = new Configuration(1, 10, Mode.forest, 5, 5);

        Grid grid = new Grid(config.getGridWidth(), config.getGridHeight());

        grid.editCell(2, 3, plantType);
        grid.editCell(0, 2, plantType);
        grid.editCell(4, 1, plantType);
        grid.editCell(1, 1, plantType);
        grid.editCell(3, 1, plantType);
        grid.editCell(2, 0, youngTreeType);
        grid.editCell(4, 2, youngTreeType);
        grid.editCell(1, 0, youngTreeType);
        grid.editCell(3, 0, youngTreeType);
        grid.editCell(4, 3, youngTreeType);
        grid.editCell(4, 4, treeType);
        grid.editCell(0, 0, treeType);
        grid.editCell(1, 4, treeType);
        grid.editCell(3, 4, treeType);
        grid.editCell(3, 3, treeType);

        ArrayList<ArrayList<Cell>> expectedMatrix = createExpectedMatrix();

        for (int i = 0; i < expectedMatrix.size(); i++) {
            ArrayList<Cell> expectedRow = expectedMatrix.get(i);
            ArrayList<Cell> row = grid.getMatrix().get(i);
            for (int j = 0; j < expectedRow.size(); j++) {
                Cell expectedCell = expectedRow.get(j);
                Cell cell = row.get(j);

                assertTrue(expectedCell.equals(cell));
            }
        }

    }

    public ArrayList<ArrayList<Cell>> createExpectedMatrix() {
        CellType plantType = new CellType("plant", "lightGreen");
        CellType youngTreeType = new CellType("youngTree", "mediumGreen");
        CellType treeType = new CellType("tree", "green");

        ArrayList<Cell> row0 = new ArrayList<>(Arrays.asList(
                new Cell(treeType, 0, 0),
                new Cell(youngTreeType, 1, 0),
                new Cell(youngTreeType, 2, 0),
                new Cell(youngTreeType, 3, 0),
                new Cell(4, 0)
        ));
        ArrayList<Cell> row1 = new ArrayList<>(Arrays.asList(
                new Cell(0, 1),
                new Cell(plantType,1, 1),
                new Cell(2, 1),
                new Cell(plantType, 3, 1),
                new Cell(plantType, 4, 1)
        ));
        ArrayList<Cell> row2 = new ArrayList<>(Arrays.asList(
                new Cell(plantType, 0, 2),
                new Cell(1, 2),
                new Cell(2, 2),
                new Cell(3, 2),
                new Cell(youngTreeType, 4, 2)
        ));
        ArrayList<Cell> row3 = new ArrayList<>(Arrays.asList(
                new Cell(0, 3),
                new Cell(1, 3),
                new Cell(plantType, 2, 3),
                new Cell(treeType, 3, 3),
                new Cell(youngTreeType, 4, 3)
        ));
        ArrayList<Cell> row4 = new ArrayList<>(Arrays.asList(
                new Cell(0, 4),
                new Cell(treeType, 1, 4),
                new Cell(2, 4),
                new Cell(treeType, 3, 4),
                new Cell(treeType, 4, 4)
        ));

        return new ArrayList<>(Arrays.asList(
                row0, row1, row2, row3, row4
        ));
    }
}