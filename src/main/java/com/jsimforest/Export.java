package com.jsimforest;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public static void export(Simulation simulation, String path) throws IOException {

        List<String[]> csvData = Export.createCsvDataSimple(simulation);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyy-hhmmss");
        String dateStr = simpleDateFormat.format(new Date());

        try (CSVWriter writer = new CSVWriter(
                new FileWriter(
                        MessageFormat.format("{0}/{1}_{2}.csv",path,"JsimForest", dateStr)
                )
        )) {
            writer.writeAll(csvData);
        }
    }

}
