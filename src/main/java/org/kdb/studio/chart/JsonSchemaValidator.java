package org.kdb.studio.chart;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

/**
 * Validate incoming json against configured json-schema
 */
public class JsonSchemaValidator {

    public static Schema defaultSchema;

    static {
        try (InputStream inputStream = JsonSchemaValidator.class.getResourceAsStream("/plot-schema.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
            defaultSchema = SchemaLoader.load(rawSchema);
        } catch (IOException ignore) {

        }
    }

    public static List<String> validate(InputStream stream) {

        try (Reader reader = new InputStreamReader(stream)) {
            defaultSchema.validate(toJsonObject(parseStrict(reader)));
        } catch (JsonSyntaxException e) {
            return Collections.singletonList("Unexpected json syntax");
        } catch (ValidationException e) {
            List<String> problems = new ArrayList<>();
            retrieveMessages(e, problems);
            return problems;
        } catch (Exception e) {
            return Collections.singletonList(Optional.ofNullable(e.getMessage()).orElse(e.toString()));
        }
        return Collections.emptyList();
    }

    protected static Object toJsonObject(JsonElement element) {
        if (element.isJsonArray()) {
            return new JSONArray(new JSONTokener(element.toString()));
        } else {
            return new JSONObject(new JSONTokener(element.toString()));
        }
    }

    protected static void retrieveMessages(ValidationException exception, List<String> problems) {
        if (!exception.getCausingExceptions().isEmpty()) {
            exception.getCausingExceptions().stream().forEach(cause -> retrieveMessages(cause, problems));
        } else {
            problems.add(exception.getMessage());
        }
    }

    public static JsonElement parseStrict(Reader reader) {
        try (JsonReader jsonReader = new JsonReader(reader)) {
            jsonReader.setStrictness(Strictness.STRICT);
            JsonElement element = JsonParser.parseReader(jsonReader);
            if (!element.isJsonNull() && jsonReader.peek() != JsonToken.END_DOCUMENT) {
                throw new JsonSyntaxException("Did not consume the entire document.");
            }
            return element;
        } catch (MalformedJsonException e) {
            throw new JsonSyntaxException(e);
        } catch (IOException e) {
            throw new JsonIOException(e);
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException(e);
        } catch (StackOverflowError e) {
            throw new JsonParseException("Failed parsing JSON source to Json", e);
        } catch (OutOfMemoryError e) {
            throw new JsonParseException("Failed parsing JSON source to Json", e);
        }
    }


}
