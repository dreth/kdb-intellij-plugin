package org.kdb.studio;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.actions.*;
import org.kdb.studio.db.AuthenticationDriverManager;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.ui.ColorAndFontManager;
import org.kdb.studio.ui.KDBToolbarUIManager;

import java.util.Optional;

@com.intellij.openapi.components.State(
        name = "KDBStudio",
        storages = {@Storage("kdbstudio.xml")},
        additionalExportFile = "kdbstudio"
)
public class KDBStudioApplicationComponent implements ApplicationComponent, PersistentStateComponent<State> {

    boolean initialEnabled = false;

    @Override
    public void initComponent() {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        AuthenticationDriverManager authenticationDriverManager = AuthenticationDriverManager.getInstance();
        RunCodeAction runCodeAction = new RunCodeAction(connectionManager);
        RunAllAction runAllAction = new RunAllAction(connectionManager);
        ReRunAction reRunAction = new ReRunAction(connectionManager);
        ExcelAction excelAction = new ExcelAction();
        ExportAction exportAction = new ExportAction();
        ChartAction chartAction = new ChartAction();
        PlotConfigManagementAction plotConfigManagementAction = new PlotConfigManagementAction();

        KDBToolbarUIManager.initInstance(initialEnabled);

        ActionManager am = ActionManager.getInstance();
        DefaultActionGroup kdbActionGroup = (DefaultActionGroup) am.getAction("KDBToolbarActions");
        kdbActionGroup.add(new ConnectionsBoxAction(connectionManager, authenticationDriverManager));
        kdbActionGroup.add(runCodeAction);
        kdbActionGroup.add(runAllAction);
        kdbActionGroup.addSeparator();
        kdbActionGroup.add(reRunAction);
        kdbActionGroup.addSeparator();
        kdbActionGroup.add(excelAction);
        kdbActionGroup.add(exportAction);
        kdbActionGroup.add(chartAction);

        am.registerAction("KDB_1_run_line", runCodeAction);
        am.registerAction("KDB_2_run_all", runAllAction);
        am.registerAction("KDB_3_run_refresh", reRunAction);
        am.registerAction("KDB_4_open_excel", excelAction);
        am.registerAction("KDB_5_open_export", exportAction);
        am.registerAction("KDB_6_open_chart", chartAction);
        am.registerAction("KDB_7_open_as_html", new ShowAsHTMLAction());
        am.registerAction("KDB_8_plot_config", plotConfigManagementAction);

        DefaultActionGroup uiToggleGroup = (DefaultActionGroup) am.getAction("UIToggleActions");
        uiToggleGroup.add(new ToolbarControlAction());

        DefaultActionGroup kdbStudioGroup = new DefaultActionGroup("KDB+ Studio Config", true);
        kdbStudioGroup.add(plotConfigManagementAction);
        kdbStudioGroup.add(new FontConfigManagementAction());
        kdbStudioGroup.add(new EnableFormattingSupportAction(ColorAndFontManager.getInstance()));

        DefaultActionGroup viewMenuGroup = (DefaultActionGroup) am.getAction("ViewMenu");
        viewMenuGroup.add(kdbStudioGroup);
    }

    @Override
    public void disposeComponent() {
        ConnectionManager.getInstance().releaseAll();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "KDBStudio";
    }

    @Nullable
    public State getState() {
        return State.create(ConnectionManager.getInstance(), AuthenticationDriverManager.getInstance(), ColorAndFontManager.getInstance(), KDBToolbarUIManager.getInstance() != null ? KDBToolbarUIManager.getInstance().isVisible() : initialEnabled);
    }

    public void loadState(State state) {
        state.apply(ConnectionManager.getInstance(), AuthenticationDriverManager.getInstance());
        state.apply(ColorAndFontManager.getInstance());
        if (KDBToolbarUIManager.getInstance() == null) {
            initialEnabled = Optional.ofNullable(state.getToolbarEnabled()).orElse(true);
        } else {
            KDBToolbarUIManager.getInstance().setState(Optional.ofNullable(state.getToolbarEnabled()).orElse(true));
        }
    }

}
