package org.kdb.studio.chart;

import org.kdb.studio.chart.entity.Plot;

import java.util.ArrayList;
import java.util.List;

public class Plots {

    public List<Plot> plots = new ArrayList<>();

    public String defaultId;

    public List<Plot> getPlots() {
        return plots;
    }

    public String getDefaultId() {
        return defaultId;
    }

    public void setDefaultId(String defaultId) {
        this.defaultId = defaultId;
    }
}
