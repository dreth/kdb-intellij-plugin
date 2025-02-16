package org.kdb.studio.actions;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.kdb.studio.chart.entity.PlotOverride;

import java.awt.*;

public class PlotOverrideAction extends AnAction {

    private PlotOverride lastState;

    private Component component;

    public void setComponent(Component component) {
        this.component = component;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        if (lastState != null && lastState.messageType != null) {
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(lastState.details, lastState.messageType, BrowserHyperlinkListener.INSTANCE)
                    .setAnimationCycle(200)
                    .setBorderInsets(JBUI.insets(5))
                    .createBalloon()
                    .show(new RelativePoint(component, new Point(15, 15)), Balloon.Position.below);
        }
    }

    public void setPlotOverride(PlotOverride override) {
        // The thing returned by getTemplatePresentation() is immutable, do not apply "set*" methods on it.
        lastState = override;
        // Instead of updating the template, wait for IntelliJ action system to trigger update sometime later.
        // There is now a small delay after a style is selected in the dropdown,
        // and when the new selection appears as text. I will leave it as is for now.
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (lastState != null) {
            if (lastState.messageType == MessageType.INFO) {
                e.getPresentation().setVisible(true);
                e.getPresentation().setIcon(AllIcons.General.BalloonInformation);
                return;
            } else if (lastState.messageType == MessageType.WARNING) {
                e.getPresentation().setVisible(true);
                e.getPresentation().setIcon(AllIcons.General.BalloonWarning);
                return;
            }
        }
        // Hide the action if there is no override, or if message type is not known.
        e.getPresentation().setIcon(null);
        e.getPresentation().setVisible(false);
    }

}
