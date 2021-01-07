package com.jsimforest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTypeTest extends AbstractTest {

    @Test
    public void setEmptyCellType() {
        CellType cellType = new CellType();

        String name = "null";
        String color = "null";

        assertEquals(name, cellType.getName());
        assertEquals(color, cellType.getColor());
    }

    @Test
    public void setCellType() {

        String name = "tree";
        String color = "green";

        CellType cellType = new CellType(name, color);

        assertEquals(name, cellType.getName());
        assertEquals(color, cellType.getColor());
    }

}