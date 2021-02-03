package com.jsimforest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    @Test
    public void testSaveConfiguration() {

        Configuration config = new Configuration();

        String name = "test sauvegarde configuration";

        assertDoesNotThrow(()-> config.saveConfiguration(name));

    }

    @Test
    public void testSelectOneConfiguration() throws SQLException {

        Configuration config = new Configuration();

        int id = 1;

        ResultSet result = config.selectOneConfiguration(id);

        String name = null;
        int exectSpeed = 0;
        int stepNumber = 0;
        int gridWith = 0;
        int gridHeight = 0;

        while(result.next()) {

            name = result.getString("name");
            exectSpeed = result.getInt("execSpeed");
            stepNumber = result.getInt("stepNumber");
            gridWith = result.getInt("gridWidth");
            gridHeight = result.getInt("gridHeight");
        }

        assertEquals("test sauvegarde",  name);
        assertEquals(1,  exectSpeed);
        assertEquals(10,  stepNumber);
        assertEquals(100,  gridWith);
        assertEquals(100,  gridHeight);


    }

    @Test
    public void testSelectAllConfigurations() throws SQLException {

        Configuration config = new Configuration();

        ResultSet rs = config.selectAllConfigurations();

        // collect column names
        List<String> columnNames = new ArrayList<>();
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            columnNames.add(rsmd.getColumnLabel(i));
        }

        int rowIndex = 0;
        while (rs.next()) {
            rowIndex++;
            // collect row data as objects in a List
            List<Object> rowData = new ArrayList<>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                rowData.add(rs.getObject(i));
            }
            // for test purposes, dump contents to check our results
            // (the real code would pass the "rowData" List to some other routine)
            System.out.printf("Row %d%n", rowIndex);
            for (int colIndex = 0; colIndex < rsmd.getColumnCount(); colIndex++) {
                String objType = "null";
                String objString = "";
                Object columnObject = rowData.get(colIndex);
                if (columnObject != null) {
                    objString = columnObject.toString() + " ";
                    objType = columnObject.getClass().getName();
                }
                System.out.printf("  %s: %s(%s)%n",
                        columnNames.get(colIndex), objString, objType);
            }
        }

    }
}