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
import org.kdb.studio.db.ConnectionManager;

@com.intellij.openapi.components.State(
        name = "KDBStudio",
        storages = {@Storage("kdbstudio.xml")},
        additionalExportFile = "kdbstudio"
)
public class KDBStudioApplicationComponent implements ApplicationComponent, PersistentStateComponent<State> {

    public static final String MAIN_TOOL_BAR = "MainToolBar";

    private DefaultActionGroup kdbActionGroup;

    @Override
    public void initComponent() {
        ConnectionManager connectionManager = ConnectionManager.getInstance();
        kdbActionGroup = new DefaultActionGroup();
        kdbActionGroup.add(new ConnectionsBoxAction(connectionManager));
        kdbActionGroup.add(new RunCodeAction(connectionManager));
        kdbActionGroup.add(new RunAllAction(connectionManager));

        ActionManager am = ActionManager.getInstance();
        DefaultActionGroup windowM = (DefaultActionGroup) am.getAction(MAIN_TOOL_BAR);

        windowM.add(kdbActionGroup);

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
        return State.create(ConnectionManager.getInstance());
    }

    public void loadState(State state) {
        state.apply(ConnectionManager.getInstance());
    }

}
