package org.kdb.studio.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kdb.studio.chart.entity.Plot;

import java.io.InputStream;

public class ChartConfigLoader {

    public static Plot load(InputStream is) throws Exception {
        return new ObjectMapper().readValue(is, Plot.class);
    }
}
