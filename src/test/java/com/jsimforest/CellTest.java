package com.jsimforest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CellTest extends AbstractTest {

    @Test
    public void setCell(){
        int coordX = 1;
        int coordY = 1;
        int age = 0;
        CellType cellType = new CellType();
        Health health = Health.ok;

        Cell cell = new Cell(coordX, coordY);

        assertEquals(cellType.getName(), cell.getCellType().getName());
        assertEquals(cellType.getColor(),cell.getCellType().getColor());
        assertEquals(coordX, cell.getCoordX());
        assertEquals(coordY, cell.getCoordY());
        assertEquals(age, cell.getAge());
        assertEquals(health, cell.getHealth());
    }

    @Test
    public void setCell_withCellType(){
        int coordX = 1;
        int coordY = 1;
        int age = 0;
        CellType cellType = new CellType("tree", "green");
        Health health = Health.ok;

        Cell cell = new Cell(cellType, coordX, coordY);

        assertEquals(cellType.getName(), cell.getCellType().getName());
        assertEquals(cellType.getColor(),cell.getCellType().getColor());
        assertEquals(coordX, cell.getCoordX());
        assertEquals(coordY, cell.getCoordY());
        assertEquals(age, cell.getAge());
        assertEquals(health, cell.getHealth());
    }

    @Test
    public void setCell_withSetters(){
        int coordX = 1;
        int coordY = 1;
        int age = 2;
        CellType cellType = new CellType("tree", "green");
        Health health = Health.ok;

        Cell cell = new Cell();
        cell.setCellType(cellType);
        cell.setHealth(health);
        cell.setCoordX(coordX);
        cell.setCoordY(coordY);
        cell.setAge(2);

        assertEquals(cellType.getName(), cell.getCellType().getName());
        assertEquals(cellType.getColor(),cell.getCellType().getColor());
        assertEquals(coordX, cell.getCoordX());
        assertEquals(coordY, cell.getCoordY());
        assertEquals(age, cell.getAge());
        assertEquals(health, cell.getHealth());
    }

    @ParameterizedTest(name = "setting coordinates with coordX = {0} and  coordY = {1} " +
            "should throw an IllegalArgumentException")
    @CsvSource({"-1, -1"})
    public void wrongCoordValue_shouldThrow_IllegalArgumentException(int coordX, int coordY) {
        Cell cell = new Cell(coordX, coordY);

        assertThrows(IllegalArgumentException.class, () -> cell.setCoordX(coordX));
        assertThrows(IllegalArgumentException.class, () -> cell.setCoordY(coordY));
    }

    @Test
    public void wrongAge_shouldThrow_IllegalArgumentException(){
        int age = -2;
        Cell cell = new Cell();

        assertThrows(IllegalArgumentException.class, () -> cell.setAge(age));
    }

    @Test
    public void testSaveCell() {

        Cell cell = new Cell();

        assertDoesNotThrow(()-> cell.saveCell(1, 1));
    }
}
