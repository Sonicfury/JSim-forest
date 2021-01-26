package com.jsimforest;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PCLStep {

    private int step;
    private PropertyChangeSupport support;

    public PCLStep(){
        this.support = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }

    public void setStep(int step) {
        support.firePropertyChange("step", this.step, step);
        this.step = step;
    }
}
