package com.jsimforest;

import java.util.Objects;

public class Density {

    private final double plantDensity;
    private final double youngTreeDensity;
    private final double treeDensity;
    private double burningDensity;
    private double ashDensity;
    private double insectDensity;

    public Density(double plantDensity, double youngTreeDensity, double treeDensity) {
        this.plantDensity = plantDensity;
        this.youngTreeDensity = youngTreeDensity;
        this.treeDensity = treeDensity;
        this.burningDensity = 0;
        this.ashDensity = 0;
        this.insectDensity = 0;
    }

    public Density(
            double plantDensity,
            double youngTreeDensity,
            double treeDensity,
            double burningDensity,
            double ashDensity,
            double insectDensity
    ) {
        this.plantDensity = plantDensity;
        this.youngTreeDensity = youngTreeDensity;
        this.treeDensity = treeDensity;
        this.burningDensity = burningDensity;
        this.ashDensity = ashDensity;
        this.insectDensity = insectDensity;
    }

    public double getPlantDensity() {

        return plantDensity;
    }

    public double getYoungTreeDensity() {

        return youngTreeDensity;
    }

    public double getTreeDensity() {

        return treeDensity;
    }

    public double getBurningDensity() {

        return burningDensity;
    }

    public double getAshDensity() {

        return ashDensity;
    }

    public double getInsectDensity() {

        return insectDensity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Density density = (Density) o;
        return Double.compare(density.plantDensity, plantDensity) == 0 && Double.compare(density.youngTreeDensity, youngTreeDensity) == 0 && Double.compare(density.treeDensity, treeDensity) == 0 && Double.compare(density.burningDensity, burningDensity) == 0 && Double.compare(density.ashDensity, ashDensity) == 0 && Double.compare(density.insectDensity, insectDensity) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(plantDensity, youngTreeDensity, treeDensity, burningDensity, ashDensity, insectDensity);
    }
}
