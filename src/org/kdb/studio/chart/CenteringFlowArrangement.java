package org.kdb.studio.chart;

import org.jfree.chart.block.Block;
import org.jfree.chart.block.BlockContainer;
import org.jfree.chart.block.FlowArrangement;
import org.jfree.chart.block.RectangleConstraint;
import org.jfree.chart.ui.Size2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CenteringFlowArrangement extends FlowArrangement {

    @Override
    public Size2D arrange(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
        Size2D size2D = super.arrange(container, g2, constraint);
        //find max height in each y group;
        Map<Double, Double> maxInGroup = new HashMap<>();

        for(Block block: (List<Block>)container.getBlocks()) {
            Rectangle2D bounds = block.getBounds();
            if (bounds instanceof Rectangle2D.Double) {
                Rectangle2D.Double d = Rectangle2D.Double.class.cast(bounds);
                double max = maxInGroup.computeIfAbsent(d.y, key -> 0.);
                maxInGroup.put(d.y, Math.max(max, d.height));
            }
        }
        for(Block block: (List<Block>)container.getBlocks()) {
            Rectangle2D bounds = block.getBounds();
            if (bounds instanceof Rectangle2D.Double) {
                Rectangle2D.Double d = Rectangle2D.Double.class.cast(bounds);
                double max = maxInGroup.get(d.y);
                d.y = d.y + (max - d.height) / 2;
            }
        }
        return size2D;
    }
}
