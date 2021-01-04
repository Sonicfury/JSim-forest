package com.jsimforest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest extends AbstractTest {

    @Test
    public void setEmptyConfiguration(){
        Configuration config = new Configuration();

        double stepsPerSecond = 1;
        int stepsNumber = 20;
        Mode mode = Mode.forest;
        int gridWidth = 100;
        int gridHeight = 100;

        assertEquals(config.getStepsPerSecond(), stepsPerSecond);
        assertEquals(config.getStepsNumber(), stepsNumber);
        assertEquals(config.getMode(), mode);
        assertEquals(config.getGridWidth(), gridWidth);
        assertEquals(config.getGridHeight(), gridHeight);
    }

    @Test
    public void setConfigurationWithConstructor(){
        double stepsPerSecond = 1;
        int stepsNumber = 20;
        Mode mode = Mode.forest;
        int gridWidth = 100;
        int gridHeight = 100;

        Configuration config = new Configuration(stepsPerSecond, stepsNumber, mode, gridWidth, gridHeight);

        assertEquals(config.getStepsPerSecond(), stepsPerSecond);
        assertEquals(config.getStepsNumber(), stepsNumber);
        assertEquals(config.getMode(), mode);
        assertEquals(config.getGridWidth(), gridWidth);
        assertEquals(config.getGridHeight(), gridHeight);
    }

    @ParameterizedTest(name = "setting Configuration in {0} mode")
    @EnumSource(Mode.class)
    public void setConfiguration(Mode mode) {

        double stepsPerSecond = 2.5;
        int stepsNumber = 30;
        int gridWidth = 50;
        int gridHeight = 50;

        Configuration config = new Configuration();

        config.setStepsPerSecond(stepsPerSecond);
        config.setStepsNumber(stepsNumber);
        config.setMode(mode);
        config.setGridWidth(gridWidth);
        config.setGridHeight(gridHeight);

        assertEquals(config.getStepsPerSecond(), stepsPerSecond);
        assertEquals(config.getStepsNumber(), stepsNumber);
        assertEquals(config.getMode(), mode);
        assertEquals(config.getGridWidth(), gridWidth);
        assertEquals(config.getGridHeight(), gridHeight);
    }

    @ParameterizedTest(name = "setting Configuration with {0} cell grid height and {1} cell grid width " +
            "should throw an IllegalArgumentException")
    @CsvSource({"1, 1", "-1, -1"})
    public void wrongGridValues_shouldThrow_IllegalArgumentException(int gridHeight, int gridWidth) {
        Configuration config = new Configuration();

        assertThrows(IllegalArgumentException.class, () -> config.setGridWidth(gridWidth));
        assertThrows(IllegalArgumentException.class, () -> config.setGridHeight(gridHeight));
    }

    @ParameterizedTest(name = "setting Configuration with {0} cell grid height and {1} cell grid width " +
            "should throw an IllegalArgumentException")
    @CsvSource({"-1.25, -1"})
    public void wrongStepValues_shouldThrow_IllegalArgumentException(double stepsPerSecond, int stepsNumber) {
        Configuration config = new Configuration();

        assertThrows(IllegalArgumentException.class, () -> config.setStepsPerSecond(stepsPerSecond));
        assertThrows(IllegalArgumentException.class, () -> config.setStepsNumber(stepsNumber));
    }
}