package org.kdb.studio;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.chart.PlotConfigManager;
import org.kdb.studio.chart.Plots;

@com.intellij.openapi.components.State(
        name = "KDBStudioPlotConfig",
        storages = {@Storage("kdbstudio_plots.xml")},
        additionalExportFile = "kdbstudio_plots"
)
public class KDBStudioPlotConfigComponent implements PersistentStateComponent<Plots> {

    @Nullable
    @Override
    public Plots getState() {
        return PlotConfigManager.getInstance().loadAvailable();
    }

    @Override
    public void loadState(Plots plotState) {
        PlotConfigManager.getInstance().setState(plotState);
    }
}
