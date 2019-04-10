package org.kdb.studio;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.ex.ActionManagerEx;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.keymap.KeymapExtension;
import com.intellij.openapi.keymap.KeymapGroup;
import com.intellij.openapi.keymap.KeymapGroupFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class KDBKeymapExtension implements KeymapExtension {
    @Nullable
    @Override
    public KeymapGroup createGroup(Condition<AnAction> condition, @Nullable Project project) {

        final KeymapGroup result = KeymapGroupFactory.getInstance().createGroup("KDB Studio plugin", IconLoader.findIcon("/icons/kx-kdb-logo.png"));

        final ActionManagerEx actionManager = ActionManagerEx.getInstanceEx();
        final String[] ids = actionManager.getActionIds("KDB_");
        Arrays.sort(ids);

        if (project != null) {
            ApplicationManager.getApplication().runReadAction(() -> {
                for (final String id : ids) {
                    result.addActionId(id);
                }
            });
        }

        return result;

    }
}
