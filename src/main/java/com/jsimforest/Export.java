package com.jsimforest;

import com.opencsv.CSVWriter;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Export {

    static List<String[]> createCsvDataSimple(Simulation simulation) {

        List<String[]> list = new ArrayList<>();

        String[] header = {"Pas", "Jeune pousse", "Arbuste", "Arbres", "Incendie", "Cendre", "infecté"};

        list.add(header);

        for (Density density : simulation.getDensities()
             ) {

            String[] record = {Integer.toString(density.getStep()), Double.toString(density.getPlantDensity()), Double.toString(density.getYoungTreeDensity()), Double.toString(density.getTreeDensity()),
                    Double.toString(density.getBurningDensity()), Double.toString(density.getAshDensity()), Double.toString(density.getInsectDensity())};
            list.add(record);
        }

        return list;
    }

    public static EventHandler<? super MouseEvent> export(Simulation simulation) throws IOException {

        List<String[]> csvData = Export.createCsvDataSimple(simulation);

        try (CSVWriter writer = new CSVWriter(new FileWriter("/Users/jbloup/Documents/Projects/Java/JSim-forest/test.csv"))) {
            writer.writeAll(csvData);
        }
        return null;
    }

}
