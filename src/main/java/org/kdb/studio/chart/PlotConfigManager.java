package org.kdb.studio.chart;

import org.kdb.studio.chart.entity.Plot;
import org.kdb.studio.kx.type.*;
import org.kdb.studio.ui.KTableModel;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class PlotConfigManager {

    public static String DEFAULT_ID = "<<DEFAULT>>";

    private static final PlotConfigManager INSTANCE = new PlotConfigManager();

    public static PlotConfigManager getInstance() {
        return INSTANCE;
    }

    private Plots plots = new Plots();

    public List<String> listAllPlots(boolean includeDefault) {
        List<String> ids = Optional.ofNullable(plots).orElse(new Plots()).getPlots().stream().map(Plot::getId).sorted().collect(Collectors.toList());
        if (includeDefault) {
            ids.add(DEFAULT_ID);
        }
        return ids;
    }

    public List<String> listAllPlots() {
        return listAllPlots(true);
    }

    public Plot byName(String id) {
        if (id == null || id.equals(DEFAULT_ID)) {
            return defaultConfig();
        } else {
            return Optional.ofNullable(plots).orElse(new Plots()).getPlots().stream().filter(plot -> id.equals(plot.getId())).findFirst().orElse(null);
        }
    }

    public Plots loadAvailable() {
        return this.plots;
    }

    public Plot forModel(KTableModel table) {
        PlotDefaultType type = typeFor(table);
        if (type == null) {
            return defaultConfig();
        }
        String defaultId = plots.getDefaultId(type);
        return byName(defaultId);
    }

    protected PlotDefaultType typeFor(KTableModel table) {
        if (table.getColumnCount() > 0) {
            Class klass = table.getColumnClass(0);
            if (Arrays.asList(KTimestampVector.class, KTimespanVector.class, KDateVector.class, KTimeVector.class, KMonthVector.class, KMinuteVector.class, KSecondVector.class, KDatetimeVector.class).contains(klass)) {
                return PlotDefaultType.TimeSeriesChart;
            } else if (Arrays.asList(KDoubleVector.class, KFloatVector.class, KShortVector.class, KIntVector.class, KLongVector.class).contains(klass)) {
                return PlotDefaultType.XYLineChart;
            }
        }
        return null;

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
