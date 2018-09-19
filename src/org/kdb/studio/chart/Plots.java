package org.kdb.studio.chart;

import org.kdb.studio.chart.entity.Plot;

import java.util.*;

public class Plots {

    public Set<Plot> plots = new LinkedHashSet<>();

    public Map<PlotDefaultType, String> defaultId = new TreeMap<>();

    public Set<Plot> getPlots() {
        return plots;
    }

    public String getDefaultId(PlotDefaultType plotDefaultType) {
        return defaultId.get(plotDefaultType);
    }

    public void setDefaultId(PlotDefaultType plotDefaultType, String id) {
        for (Iterator<Map.Entry<PlotDefaultType, String>> it = defaultId.entrySet().iterator(); it.hasNext(); ) {
            if (id.equals(it.next().getValue())) {
                it.remove();
            }
        }
        if (plotDefaultType != null) {
            this.defaultId.put(plotDefaultType, id);
        }
    }

    public void remove(String plotId) {
        for (Iterator<Plot> it = plots.iterator(); it.hasNext();) {
            if (plotId.equals(it.next().getId())) {
                it.remove();
            }
        }
        for (Iterator<Map.Entry<PlotDefaultType, String>> it = defaultId.entrySet().iterator(); it.hasNext();) {
            if (plotId.equals(it.next().getValue())) {
                it.remove();
            }
        }
    }

    public PlotDefaultType typeFor(String plotId) {
        return defaultId.entrySet().stream().filter(entry -> plotId.equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
    }

}
