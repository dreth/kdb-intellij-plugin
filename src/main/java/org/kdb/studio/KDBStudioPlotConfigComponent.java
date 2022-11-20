package org.kdb.studio;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.chart.PlotConfigManager;
import org.kdb.studio.chart.Plots;
import org.kdb.studio.ui.KDBToolbarUIManager;

@com.intellij.openapi.components.State(
        name = "KDBStudioPlotConfig",
        storages = @Storage("kdbstudio_plots.xml")
)
public class KDBStudioPlotConfigComponent implements PersistentStateComponent<Plots> {

    @Nullable
    @Override
    public Plots getState() {
        return ApplicationManager.getApplication().getService(PlotConfigManager.class).loadAvailable();
    }

    @Override
    public void loadState(Plots plotState) {
        ApplicationManager.getApplication().getService(PlotConfigManager.class).setState(plotState);
    }
}
