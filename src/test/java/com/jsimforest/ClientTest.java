package com.jsimforest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ClientTest extends AbstractTest{
    @Test
    public void CyclingVoidType(){
        Client tester = new Client();
        assertEquals("plant", tester.cycleTypes("null", 0, 0));
    }
    @Test
    public void CyclingPlantType(){
        Client tester = new Client();
        assertEquals("youngTree", tester.cycleTypes("plant", 0, 0));
    }
    @Test
    public void CyclingYoungTreeType(){
        Client tester = new Client();
        assertEquals("tree", tester.cycleTypes("youngTree", 0, 0));
    }
    @Test
    public void CyclingTreeType(){
        Client tester = new Client();
        assertEquals("null", tester.cycleTypes("tree", 0, 0));
    }

    @Test
    public void CyclingUnexistingType(){
        Client tester = new Client();
        assertThrows(IllegalArgumentException.class, () -> tester.cycleTypes("false", 0, 0));
    }
}
