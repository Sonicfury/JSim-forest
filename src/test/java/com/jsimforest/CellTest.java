package com.jsimforest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class CellTest extends AbstractTest {

    @ParameterizedTest
    @EnumSource(Health.class)
    public void setCell(Health health){
        Cell cell = new Cell();

        CellType cellType = new CellType();
        cellType.setColor("green");
        cellType.setName("void");

        int coordX = 1;
        int coordY = 1;

        cell.setCellType(cellType);
        cell.setCoordX(coordX);
        cell.setCoordY(coordY);
        cell.setHealth(health);

        assertEquals(cellType, cell.getCellType());
        assertEquals(coordX, cell.getCoordX());
        assertEquals(coordY, cell.getCoordY());
        assertEquals(health, cell.getHealth());
    }

    @ParameterizedTest(name = "setting coordinates with coordX = {0} and  coordY = {1} " +
            "should throw an IllegalArgumentException")
    @CsvSource({"-1, -1"})
    public void wrongCoordValue_shouldThrow_IllegalArgumentException(int coordX, int coordY) {
        Cell cell = new Cell();

        assertThrows(IllegalArgumentException.class, () -> cell.setCoordX(coordX));
        assertThrows(IllegalArgumentException.class, () -> cell.setCoordY(coordY));
    }
}
