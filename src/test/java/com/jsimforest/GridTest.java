package com.jsimforest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GridTest extends AbstractTest {
    @Test
    public void testGrille() throws Exception {
        Grid myGrid = new Grid(5, 8);
        assertEquals(myGrid.getHeight(),myGrid.getMatrix().size());
        for(int i=0; i< myGrid.getWidth();i++){
            assertEquals(myGrid.getWidth(), myGrid.getMatrix().get(i).size());
        }
    }
    @ParameterizedTest(name= "Setting negative width {0} or negative height {1} should throws an IllegalArgumentException")
    @CsvSource({"-2, 2", "2, -2", "-2, 2", "-2, -2"})
    public void WrongHeightOrWidth_ShouldThrow_IllegalArgumentException(int gridWidth, int gridHeight){
        assertThrows(IllegalArgumentException.class, () -> new Grid(gridWidth, gridHeight));
    }
}