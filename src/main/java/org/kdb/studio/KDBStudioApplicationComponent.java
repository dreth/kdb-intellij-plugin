package org.kdb.studio;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.Nullable;
import org.kdb.studio.db.AuthenticationDriverManager;
import org.kdb.studio.db.ConnectionManager;
import org.kdb.studio.ui.ColorAndFontManager;
import org.kdb.studio.ui.KDBToolbarUIManager;

import java.util.Optional;

@com.intellij.openapi.components.State(
        name = "KDBStudio",
        storages = @Storage("kdbstudio.xml")
)
public class KDBStudioApplicationComponent implements PersistentStateComponent<State> {

    @Nullable
    public State getState() {
        KDBToolbarUIManager manager = ApplicationManager.getApplication().getService(KDBToolbarUIManager.class);
        return State.create(ConnectionManager.getInstance(), AuthenticationDriverManager.getInstance(), ColorAndFontManager.getInstance(), manager.isVisible());
    }

    public void loadState(State state) {
        state.apply(ConnectionManager.getInstance(), AuthenticationDriverManager.getInstance());
        state.apply(ColorAndFontManager.getInstance());
        ApplicationManager.getApplication().getService(KDBToolbarUIManager.class).setState(Optional.ofNullable(state.getToolbarEnabled()).orElse(true));
    }

}
