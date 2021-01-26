package com.jsimforest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SimulationTest extends AbstractTest {

    private static final CellType plantType = new CellType("plant", "lightGreen");
    private static final CellType youngTreeType = new CellType("youngTree", "mediumGreen");
    private static final CellType treeType = new CellType("tree", "green");

    @Test
    public void setGridInitialState_fromConfiguration() {
        Configuration config = new Configuration(1, 10, Mode.forest, 5, 5);
        Simulation simulation = new Simulation(config);
        Grid grid = simulation.getGrid();

        grid.editCell(2, 3, plantType);
        grid.editCell(0, 2, plantType);
        grid.editCell(4, 1, plantType);
        grid.editCell(1, 1, plantType);
        grid.editCell(3, 1, plantType);
        grid.editCell(2, 0, youngTreeType);
        grid.editCell(4, 2, youngTreeType);
        grid.editCell(1, 0, youngTreeType);
        grid.editCell(3, 0, youngTreeType);
        grid.editCell(4, 3, youngTreeType);
        grid.editCell(4, 4, treeType);
        grid.editCell(0, 0, treeType);
        grid.editCell(1, 4, treeType);
        grid.editCell(3, 4, treeType);
        grid.editCell(3, 3, treeType);

        ArrayList<ArrayList<Cell>> expectedMatrix = createMatrix();

        for (int i = 0; i < expectedMatrix.size(); i++) {
            ArrayList<Cell> expectedRow = expectedMatrix.get(i);
            ArrayList<Cell> row = grid.getMatrix().get(i);
            for (int j = 0; j < expectedRow.size(); j++) {
                Cell expectedCell = expectedRow.get(j);
                Cell cell = row.get(j);

                assertTrue(expectedCell.equals(cell));
            }
        }
    }

    @Test
    public void observeGridChanges() {
        Configuration config = new Configuration(10,2, Mode.forest, 3, 3);
        Simulation simulation = new Simulation(config);

        Client clientObserver = new Client();
        simulation.stepObservable.addPropertyChangeListener(clientObserver);


        simulation.step();
        assertEquals(1, clientObserver.getStep());
        simulation.step();
        assertEquals(2, clientObserver.getStep());
    }

    @Test
    public void runSimulation() {
        int stepsNumber = 10;
        int stepsPerSecond = 1;
        ArrayList<ArrayList<Cell>> matrix = createMatrix();

        Configuration config = new Configuration(stepsPerSecond, stepsNumber, Mode.forest, 5, 5);
        Simulation simulation = new Simulation(config);
        simulation.getGrid().setMatrix(matrix);
        int expectedElapsedTime = (stepsNumber / stepsPerSecond) * 1000;

        ArrayList<ArrayList<Cell>> copyMatrix = createMatrix();
        Simulation copySimulation = new Simulation(config);
        copySimulation.getGrid().setMatrix(copyMatrix);

        Grid copyGrid = copySimulation.getGrid();

        try {
            simulation.run();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        assertEquals(stepsNumber, simulation.getStep());
        assertFalse(copyGrid.equals(simulation.getGrid()));
        assertEquals(expectedElapsedTime, simulation.getElapsedTime());
    }

    @ParameterizedTest
    @MethodSource({
            "littleMatrixStream_fromNull"
    })
    public void evolveEmpty(ArrayList<ArrayList<Cell>> matrix) {
        Configuration config = new Configuration(1, 1, Mode.forest, 3, 3);
        Simulation simulation = new Simulation(config);
        simulation.getGrid().setMatrix(matrix);

        simulation.step();

        ArrayList<ArrayList<Cell>> simMatrix = simulation.getGrid().getMatrix();
        Cell simCell = simMatrix.get(1).get(1);

        assertTrue(simCell.getCellType().equals(plantType));
    }

    @ParameterizedTest
    @MethodSource({
            "littleMatrixStream_fromPlant_shouldEvolve"
    })
    public void evolvePlant(ArrayList<ArrayList<Cell>> matrix) {
        Configuration config = new Configuration(1, 1, Mode.forest, 3, 3);
        Simulation simulation = new Simulation(config);

        simulation.getGrid().setMatrix(matrix);

        simulation.step();

        ArrayList<ArrayList<Cell>> simMatrix = simulation.getGrid().getMatrix();
        Cell simCell = simMatrix.get(1).get(1);

        assertTrue(simCell.getCellType().equals(youngTreeType));
    }

    @ParameterizedTest
    @MethodSource({
            "littleMatrixStream_fromPlant_shouldNotEvolve"
    })
    public void evolvePlant_shouldNotEvolve(ArrayList<ArrayList<Cell>> matrix) {
        Configuration config = new Configuration(1, 1, Mode.forest, 3, 3);
        Simulation simulation = new Simulation(config);

        simulation.getGrid().setMatrix(matrix);

        simulation.step();

        ArrayList<ArrayList<Cell>> simMatrix = simulation.getGrid().getMatrix();
        Cell simCell = simMatrix.get(1).get(1);

        assertTrue(simCell.getCellType().equals(plantType));
    }

    @Test
    public void evolveYoungTree() {
        Configuration config = new Configuration(1, 3, Mode.forest, 3, 3);
        Simulation simulation = new Simulation(config);
        ArrayList<ArrayList<Cell>> matrix = createLittleMatrix_fromYoungTree();

        simulation.getGrid().setMatrix(matrix);

        try {
            simulation.run();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        ArrayList<ArrayList<Cell>> simMatrix = simulation.getGrid().getMatrix();
        Cell simCell = simMatrix.get(1).get(1);

        assertTrue(simCell.getCellType().equals(treeType));
    }

    private static ArrayList<ArrayList<Cell>> createMatrix() {
        ArrayList<Cell> row0 = new ArrayList<>(Arrays.asList(
                new Cell(treeType, 0, 0),
                new Cell(youngTreeType, 1, 0),
                new Cell(youngTreeType, 2, 0),
                new Cell(youngTreeType, 3, 0),
                new Cell(4, 0)
        ));
        ArrayList<Cell> row1 = new ArrayList<>(Arrays.asList(
                new Cell(0, 1),
                new Cell(plantType, 1, 1),
                new Cell(2, 1),
                new Cell(plantType, 3, 1),
                new Cell(plantType, 4, 1)
        ));
        ArrayList<Cell> row2 = new ArrayList<>(Arrays.asList(
                new Cell(plantType, 0, 2),
                new Cell(1, 2),
                new Cell(2, 2),
                new Cell(3, 2),
                new Cell(youngTreeType, 4, 2)
        ));
        ArrayList<Cell> row3 = new ArrayList<>(Arrays.asList(
                new Cell(0, 3),
                new Cell(1, 3),
                new Cell(plantType, 2, 3),
                new Cell(treeType, 3, 3),
                new Cell(youngTreeType, 4, 3)
        ));
        ArrayList<Cell> row4 = new ArrayList<>(Arrays.asList(
                new Cell(0, 4),
                new Cell(treeType, 1, 4),
                new Cell(2, 4),
                new Cell(treeType, 3, 4),
                new Cell(treeType, 4, 4)
        ));

        return new ArrayList<>(Arrays.asList(
                row0, row1, row2, row3, row4
        ));
    }

    private static ArrayList<ArrayList<Cell>> createLittleMatrix_fromNull_twoTrees() {
        ArrayList<Cell> row0 = new ArrayList<>(Arrays.asList(
                new Cell(0, 0),
                new Cell(treeType, 1, 0),
                new Cell(2, 0)
        ));
        ArrayList<Cell> row1 = new ArrayList<>(Arrays.asList(
                new Cell(0, 1),
                new Cell(1, 1),
                new Cell(treeType, 2, 1)
        ));
        ArrayList<Cell> row2 = new ArrayList<>(Arrays.asList(
                new Cell(0, 2),
                new Cell(1, 2),
                new Cell(2, 2)
        ));

        return new ArrayList<>(Arrays.asList(
                row0, row1, row2
        ));
    }

    private static ArrayList<ArrayList<Cell>> createLittleMatrix_fromNull_threeYoungTrees() {
        ArrayList<Cell> row0 = new ArrayList<>(Arrays.asList(
                new Cell(0, 0),
                new Cell(1, 0),
                new Cell(2, 0)
        ));
        ArrayList<Cell> row1 = new ArrayList<>(Arrays.asList(
                new Cell(youngTreeType, 0, 1),
                new Cell(1, 1),
                new Cell(2, 1)
        ));
        ArrayList<Cell> row2 = new ArrayList<>(Arrays.asList(
                new Cell(youngTreeType, 0, 2),
                new Cell(youngTreeType, 1, 2),
                new Cell(2, 2)
        ));

        return new ArrayList<>(Arrays.asList(
                row0, row1, row2
        ));
    }

    private static ArrayList<ArrayList<Cell>> createLittleMatrix_fromNull_oneTreeTwoYoungTrees() {
        ArrayList<Cell> row0 = new ArrayList<>(Arrays.asList(
                new Cell(0, 0),
                new Cell(1, 0),
                new Cell(2, 0)
        ));
        ArrayList<Cell> row1 = new ArrayList<>(Arrays.asList(
                new Cell(0, 1),
                new Cell(1, 1),
                new Cell(treeType, 2, 1)
        ));
        ArrayList<Cell> row2 = new ArrayList<>(Arrays.asList(
                new Cell(youngTreeType, 0, 2),
                new Cell(youngTreeType, 1, 2),
                new Cell(2, 2)
        ));

        return new ArrayList<>(Arrays.asList(
                row0, row1, row2
        ));
    }

    private static Stream<ArrayList<ArrayList<Cell>>> littleMatrixStream_fromNull() {
        return Stream.of(
                createLittleMatrix_fromNull_twoTrees(),
                createLittleMatrix_fromNull_threeYoungTrees(),
                createLittleMatrix_fromNull_oneTreeTwoYoungTrees()
        );
    }

    private static ArrayList<ArrayList<Cell>> createLittleMatrix_fromPlant_twoTrees() {
        ArrayList<Cell> row0 = new ArrayList<>(Arrays.asList(
                new Cell(0, 0),
                new Cell(treeType, 1, 0),
                new Cell(2, 0)
        ));
        ArrayList<Cell> row1 = new ArrayList<>(Arrays.asList(
                new Cell(0, 1),
                new Cell(plantType, 1, 1),
                new Cell(treeType, 2, 1)
        ));
        ArrayList<Cell> row2 = new ArrayList<>(Arrays.asList(
                new Cell(0, 2),
                new Cell(1, 2),
                new Cell(2, 2)
        ));

        return new ArrayList<>(Arrays.asList(
                row0, row1, row2
        ));
    }

    private static ArrayList<ArrayList<Cell>> createLittleMatrix_fromPlant_oneTreeTwoYoungTrees() {
        ArrayList<Cell> row0 = new ArrayList<>(Arrays.asList(
                new Cell(0, 0),
                new Cell(1, 0),
                new Cell(2, 0)
        ));
        ArrayList<Cell> row1 = new ArrayList<>(Arrays.asList(
                new Cell(0, 1),
                new Cell(plantType, 1, 1),
                new Cell(treeType, 2, 1)
        ));
        ArrayList<Cell> row2 = new ArrayList<>(Arrays.asList(
                new Cell(youngTreeType, 0, 2),
                new Cell(youngTreeType, 1, 2),
                new Cell(2, 2)
        ));

        return new ArrayList<>(Arrays.asList(
                row0, row1, row2
        ));
    }

    private static ArrayList<ArrayList<Cell>> createLittleMatrix_fromPlant_fourTrees() {
        ArrayList<Cell> row0 = new ArrayList<>(Arrays.asList(
                new Cell(treeType, 0, 0),
                new Cell(1, 0),
                new Cell(2, 0)
        ));
        ArrayList<Cell> row1 = new ArrayList<>(Arrays.asList(
                new Cell(0, 1),
                new Cell(plantType, 1, 1),
                new Cell(treeType, 2, 1)
        ));
        ArrayList<Cell> row2 = new ArrayList<>(Arrays.asList(
                new Cell(treeType, 0, 2),
                new Cell(treeType, 1, 2),
                new Cell(2, 2)
        ));

        return new ArrayList<>(Arrays.asList(
                row0, row1, row2
        ));
    }

    private static ArrayList<ArrayList<Cell>> createLittleMatrix_fromPlant_threeYoungTreesOneTree() {
        ArrayList<Cell> row0 = new ArrayList<>(Arrays.asList(
                new Cell(treeType, 0, 0),
                new Cell(1, 0),
                new Cell(2, 0)
        ));
        ArrayList<Cell> row1 = new ArrayList<>(Arrays.asList(
                new Cell(0, 1),
                new Cell(plantType, 1, 1),
                new Cell(youngTreeType, 2, 1)
        ));
        ArrayList<Cell> row2 = new ArrayList<>(Arrays.asList(
                new Cell(youngTreeType, 0, 2),
                new Cell(youngTreeType, 1, 2),
                new Cell(2, 2)
        ));

        return new ArrayList<>(Arrays.asList(
                row0, row1, row2
        ));
    }

    private static Stream<ArrayList<ArrayList<Cell>>> littleMatrixStream_fromPlant_shouldEvolve() {
        return Stream.of(
                createLittleMatrix_fromPlant_twoTrees(),
                createLittleMatrix_fromPlant_oneTreeTwoYoungTrees()
        );
    }

    private static Stream<ArrayList<ArrayList<Cell>>> littleMatrixStream_fromPlant_shouldNotEvolve() {
        return Stream.of(
                createLittleMatrix_fromPlant_fourTrees(),
                createLittleMatrix_fromPlant_threeYoungTreesOneTree()
        );
    }

    private static ArrayList<ArrayList<Cell>> createLittleMatrix_fromYoungTree() {
        ArrayList<Cell> row0 = new ArrayList<>(Arrays.asList(
                new Cell(treeType, 0, 0),
                new Cell(1, 0),
                new Cell(2, 0)
        ));
        ArrayList<Cell> row1 = new ArrayList<>(Arrays.asList(
                new Cell(0, 1),
                new Cell(youngTreeType, 1, 1),
                new Cell(youngTreeType, 2, 1)
        ));
        ArrayList<Cell> row2 = new ArrayList<>(Arrays.asList(
                new Cell(youngTreeType, 0, 2),
                new Cell(youngTreeType, 1, 2),
                new Cell(2, 2)
        ));

        return new ArrayList<>(Arrays.asList(
                row0, row1, row2
        ));
    }
}