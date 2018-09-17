package org.kdb.studio.chart;

import org.kdb.studio.chart.entity.Plot;
import org.kdb.studio.ui.KTableModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class PlotConfigManager {

    private static final PlotConfigManager INSTANCE = new PlotConfigManager();

    public static PlotConfigManager getInstance() {
        return INSTANCE;
    }

    private Plots plots = new Plots();

    public List<String> listAllPlots(boolean includeDefault) {
        List<String> ids = Optional.ofNullable(plots).orElse(new Plots()).getPlots().stream().map(Plot::getId).collect(Collectors.toList());
        if (includeDefault) {
            ids.add("<<DEFAULT>>");
        }
        return ids;
    }

    public List<String> listAllPlots() {
        return listAllPlots(true);
    }

    public Plot byName(String id) {
        if (id.equals("<<DEFAULT>>")) {
            return defaultConfig();
        } else {
            return Optional.ofNullable(plots).orElse(new Plots()).getPlots().stream().filter(plot -> id.equals(plot.getId())).findFirst().orElse(null);
        }
    }

    public Plots loadAvailable() {
        return this.plots;
    }

    public Plot forModel(KTableModel table) {
        if (plots.getPlots().size() > 0) {
            return plots.getPlots().get(0);
        } else {
            return defaultConfig();
        }
    }

    public Plot defaultConfig() {
        try {
            return ChartConfigLoader.load(PlotConfigManager.class.getResourceAsStream("/default-plot.json"));
        } catch (Exception e) {
            return null;
        }
    }

    public void setState(Plots plots) {
        this.plots = plots;
    }

    public Plots getState() {
        return this.plots;
    }
}
