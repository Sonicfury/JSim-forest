package com.jsimforest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.ArrayList;
import java.util.Arrays;


public class GridTest extends AbstractTest {
    @ParameterizedTest(name = "Generating grid with correct width and height")
    @CsvSource("10, 10")
    public void GoodMatrixDimension_ShouldNotThrow_IllegalArgumentException(int gridWidth,int gridHeight) {
        assertDoesNotThrow(() -> new Grid(gridWidth, gridHeight));

    }

    @ParameterizedTest(name = "Setting negative width {0} or negative height {1} should throws an IllegalArgumentException")
    @CsvSource({"-2, 2", "2, -2", "-2, 2", "-2, -2"})
    public void WrongHeightOrWidth_ShouldThrow_IllegalArgumentException(int gridWidth, int gridHeight){
        assertThrows(IllegalArgumentException.class, () -> new Grid(gridWidth, gridHeight));
    }

    @ParameterizedTest(name = "Setting a matrix with uncorrect width {0} and height {1}")
    @CsvSource({"2, 2, 4, 4", "6, 6, 4, 4", "2, 2, 2, 4"})
    public void WrongMatrixDimension_ShouldThrow_IllegalArgumentException(int matrixLines, int matrixColumns, int gridHeight, int gridWidth){
        Grid testGrid = new Grid(gridWidth, gridHeight);
        ArrayList<ArrayList<Cell>> newMatrix = new ArrayList<>();
        for(int i = 0;i < matrixLines; i++){
            newMatrix.add(new ArrayList<>());
            for (int j = 0; j < matrixColumns; j++){
                newMatrix.get(i).add(new Cell(j, i));
            }
        }
        assertThrows(IllegalArgumentException.class, () -> testGrid.setMatrix(newMatrix));
    }

    @Test
    public void SetMatrix(){
        Grid gridTest = new Grid(2, 2);
        ArrayList<ArrayList<Cell>> newMatrix = new ArrayList<>();
        newMatrix.add(new ArrayList<>(Arrays.asList(new Cell(0, 0), new Cell(0, 1))));
        newMatrix.add(new ArrayList<>(Arrays.asList(new Cell(1, 0), new Cell(1, 1))));
        gridTest.setMatrix(newMatrix);
        assertEquals(newMatrix, gridTest.getMatrix());
    }
}