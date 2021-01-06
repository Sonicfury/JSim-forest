package com.jsimforest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CellTypeTest extends AbstractTest {

    @Test
    public void setCellType(){
       CellType cellType = new CellType();

        String name = "tree";
        String color = "green";

        cellType.setName(name);
        cellType.setColor(color);

        assertEquals(name, cellType.getName());
        assertEquals(color, cellType.getColor());
    }


}