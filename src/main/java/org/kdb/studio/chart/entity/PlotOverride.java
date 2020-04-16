package org.kdb.studio.chart.entity;

import com.intellij.openapi.ui.MessageType;

public class PlotOverride {

    public MessageType messageType;
    public String details;

    public PlotOverride(MessageType messageType, String details) {
        this.messageType = messageType;
        this.details = details;
    }

}
