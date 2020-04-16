package org.kdb.studio.kx;

import com.google.gson.Gson;
import org.kdb.studio.chart.entity.Plot;

public class JsonParserUtil {

    public static String loadLastJsonFromText(String text) {
        StringBuilder json = new StringBuilder();
        boolean jsonEndReached = false;
        int deep = 0;
        for (int i = text.length() -1; i > 0; i--) {
            char c = text.charAt(i);
            switch (c) {
                case '}':
                    if (!jsonEndReached) {
                        jsonEndReached = true;
                    }
                    deep++;
                    break;
                case '{':
                    if (jsonEndReached) {
                        deep --;
                    }
                    break;
            }
            if (jsonEndReached) {
                json.append(c);
            }
            if (jsonEndReached && deep == 0) {
                break;
            }
        }
        return json.reverse().toString();
    }

    public static Plot loadLastJsonAsPlot(String text) {
        String json = loadLastJsonFromText(text);
        if (json.isEmpty() && !text.isEmpty()) {
            return null;
        }
        return new Gson().fromJson(json, Plot.class);

    }
}
