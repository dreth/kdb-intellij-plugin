package org.kdb.studio.actions;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.BrowserHyperlinkListener;
import com.intellij.ui.awt.RelativePoint;
import org.kdb.studio.KDBResourceBundle;
import org.kdb.studio.chart.entity.PlotOverride;

import javax.swing.*;
import java.awt.*;

public class PlotOverrideAction extends AnAction {

    private PlotOverride lastState;

    private Component component;

    public void setComponent(Component component) {
        this.component = component;
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        if (lastState != null && lastState.messageType != null) {
            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder(lastState.details, lastState.messageType, BrowserHyperlinkListener.INSTANCE)
                    .setAnimationCycle(200)
                    .setBorderInsets(new Insets(5, 5, 5, 5))
                    .createBalloon()
                    .show(new RelativePoint(component, new Point(15, 15)), Balloon.Position.below);
        }
    }

    public void setPlotOverride(PlotOverride override) {
        lastState = override;
        if (override.messageType == MessageType.INFO) {
            getTemplatePresentation().setVisible(true);
            getTemplatePresentation().setIcon(AllIcons.General.BalloonInformation);
            return;
        }
        if (override.messageType == MessageType.WARNING) {
            getTemplatePresentation().setVisible(true);
            getTemplatePresentation().setIcon(AllIcons.General.BalloonWarning);
            return;
        }
        getTemplatePresentation().setIcon(null);
        getTemplatePresentation().setVisible(false);
    }
}
