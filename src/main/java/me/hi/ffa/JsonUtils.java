package me.hi.ffa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Method to load JSON data
    public static JsonNode loadJson(File FILE) {
        try {
            return objectMapper.readTree(FILE);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to load JSON data.");
            return null;
        }
    }

    // Method to save JSON data
    public static void saveJson(File FILE, JsonNode root) {
        try {
            objectMapper.writeValue(FILE, root);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to save JSON data.");
        }
    }
}
