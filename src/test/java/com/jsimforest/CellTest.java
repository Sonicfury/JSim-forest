package com.jsimforest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class cellTest extends AbstractTest {

        @Test
        public void setCell(){
            Cell cell = new Cell();

            CellType cellType = new CellType();
            cellType.setColor("vert");
            cellType.setName("type");

            int coordX = 1;
            int coordY = 1;
            Health health = Health.ok;

            cell.setCellType(cellType);
            cell.setCoordX(coordX);
            cell.setCoordY(coordY);
            cell.setHealth(health);

            assertEquals(cellType, cell.getCellType());
            assertEquals(coordX, cell.getCoordX());
            assertEquals(coordY, cell.getCoordY());
            assertEquals(health, cell.getHealth());


        }



}