package de.hshannover.dqgui.framework.serialization;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Backbone of our serialization.
 *
 * @author Marc Herschel
 *
 */
public final class JSONOperations {
    private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

    private JSONOperations() {}

    /**
     * Dump as JSON (with expose annotations)
     * @param dump object of choice.
     * @param path destination.
     * @throws IOException Upsy daisy.
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public static void toJSON(Object dump, Path path) throws IOException {
        if(!Files.exists(path)) {
            if(!Files.exists(path.getParent()))
                Files.createDirectories(path.getParent());
            Files.createFile(path);
        }
        try(BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(dump, writer);
        } catch(IOException e) {
            throw e;
        }
    }

    /**
     * Recover from JSON (with expose annotations)
     * @param toLoad class structure of Object.
     * @param path of serialized JSON.
     * @return Object of object (must be cast)
     * @throws IOException if not existing.
     */
    public static Object fromJSON(Class<?> toLoad, Path path) throws IOException {
        try(BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, toLoad);
        } catch(IOException e) {
            throw e;
        }
    }

}