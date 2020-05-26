package org.kdb.studio.chart;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.kdb.studio.chart.entity.Plot;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class ChartConfigLoader {

    public static Plot load(InputStream is) throws Exception {
        try (Reader reader = new InputStreamReader(is)){
            return new Gson().fromJson(reader, Plot.class);
        }
    }

    public static Plot clone(Plot original) {
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(original);
        return gson.fromJson(jsonElement, Plot.class);
    }
}
