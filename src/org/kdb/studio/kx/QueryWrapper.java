package org.kdb.studio.kx;

import org.kdb.studio.kx.type.KBase;
import org.kdb.studio.kx.type.KCharacterVector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class QueryWrapper {

    public static KBase toRequest(String query, boolean enableMultilineComments) {
        if (enableMultilineComments) {
            return new KCharacterVector(wrapMultilineComments(query));
        }
        return new KCharacterVector(query);
    }

    /**
     *   Multiline comment
     *   As first and only non-whitespace char on a line:
     *       / starts a multiline comment
     *       \ terminates a multiline comment or, if not in a comment, comments to end of script
     */
    private static String wrapMultilineComments(String query) {
        StringBuilder wrapperQuery = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new StringReader(query))) {
            String line;
            boolean insideMultiLineComment = false;
            boolean commentsToEnd = false;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("/") && line.trim().length() == 1) {
                    insideMultiLineComment = true;
                } else if (line.startsWith("\\") && line.trim().length() == 1) {
                    if (insideMultiLineComment) {
                        insideMultiLineComment = false;
                        wrapperQuery.append("/");
                    } else {
                        commentsToEnd = true;
                    }
                }
                if (insideMultiLineComment || commentsToEnd) {
                    wrapperQuery.append("/");
                }
                wrapperQuery.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return wrapperQuery.toString();
    }
}
