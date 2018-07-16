package org.kdb.studio;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.actions.ConnectionsBoxAction;
import org.kdb.studio.actions.RunAllAction;
import org.kdb.studio.actions.RunCodeAction;
import org.kdb.studio.actions.ToolbarControlAction;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.ui.KDBToolbarUIManager;

import java.util.Optional;

@com.intellij.openapi.components.State(
        name = "KDBStudio",
        storages = {@Storage("kdbstudio.xml")},
        additionalExportFile = "kdbstudio"
)
public class KDBStudioApplicationComponent implements ApplicationComponent, PersistentStateComponent<State> {


    private DefaultActionGroup kdbActionGroup;

    boolean initialEnabled = false;

    @Override
    public void initComponent() {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        RunCodeAction runCodeAction = new RunCodeAction(connectionManager);
        RunAllAction runAllAction = new RunAllAction(connectionManager);

        kdbActionGroup = new DefaultActionGroup("KDBToolbarActions", false);
        kdbActionGroup.add(new ConnectionsBoxAction(connectionManager));
        kdbActionGroup.add(runCodeAction);
        kdbActionGroup.add(runAllAction);
        KDBToolbarUIManager.initInstance(kdbActionGroup, initialEnabled);

        ActionManager am = ActionManager.getInstance();
        am.registerAction("KDB_run_line", runCodeAction);
        am.registerAction("KDB_run_all", runAllAction);

        DefaultActionGroup uiToggleGroup = (DefaultActionGroup) am.getAction("UIToggleActions");
        uiToggleGroup.add(new ToolbarControlAction());
    }

    @Override
    public void disposeComponent() {
        ActionManager am = ActionManager.getInstance();
        DefaultActionGroup windowM = (DefaultActionGroup) am.getAction("MainToolBar");
        windowM.remove(kdbActionGroup);
        ConnectionManager.getInstance().releaseAll();
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "KDBStudio";
    }

    @Nullable
    public State getState() {
        return State.create(ConnectionManager.getInstance(), KDBToolbarUIManager.getInstance() != null ? KDBToolbarUIManager.getInstance().isVisible() : initialEnabled);
    }

    public void loadState(State state) {
        state.apply(ConnectionManager.getInstance());
        if (KDBToolbarUIManager.getInstance() == null) {
            initialEnabled = Optional.ofNullable(state.getToolbarEnabled()).orElse(true);
        } else {
            KDBToolbarUIManager.getInstance().setState(Optional.ofNullable(state.getToolbarEnabled()).orElse(true));
        }
    }

}
