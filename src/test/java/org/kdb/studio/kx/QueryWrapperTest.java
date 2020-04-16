package org.kdb.studio.kx;

import com.intellij.openapi.util.io.StreamUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class QueryWrapperTest {

    @Test
    public void parseQueryComments() throws Exception {
        try (InputStream is = QueryWrapperTest.class.getResourceAsStream("/comments_example_1.txt")) {
            String query = StreamUtil.readText(is, StandardCharsets.UTF_8);
            Assert.assertEquals(" {\"title\": {\"text\": \"A fancy plot\", \"font\": {\"size\": 20}}, \"domainAxis\": {\"label\": { \"show\": true, \"text\": Price\"}}} ", QueryWrapper.toComments(query));
        }
        try (InputStream is = QueryWrapperTest.class.getResourceAsStream("/comments_example_2.txt")) {
            String query = StreamUtil.readText(is, StandardCharsets.UTF_8);
            Assert.assertEquals(" {\"title\": {\"text\": \"A fancy plot\", \"font\": {\"size\": 20}},      \"domainAxis\": {\"label”:           { \"show\": true, \"text\": \"Price\"}}} ", QueryWrapper.toComments(query));
        }
        try (InputStream is = QueryWrapperTest.class.getResourceAsStream("/comments_example_3.txt")) {
            String query = StreamUtil.readText(is, StandardCharsets.UTF_8);
            Assert.assertEquals("{\"title\": {\"text\": \"A fancy plot\", \"font\": {\"size\": 20}}, \"domainAxis\": {\"label”: { \"show\": true, \"text\": \"Price\"}}} ", QueryWrapper.toComments(query));
        }

    }
}
