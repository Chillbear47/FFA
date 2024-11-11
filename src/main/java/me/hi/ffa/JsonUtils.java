package me.hi.ffa;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;

public class JsonUtils {

    private static final String JSON_FILE_PATH = Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/FFA_info.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Method to load JSON data
    public static JsonNode loadJson() {
        try {
            return objectMapper.readTree(new File(JSON_FILE_PATH));
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to load JSON data.");
            return null;
        }
    }

    // Method to save JSON data
    public static void saveJson(JsonNode root) {
        try {
            objectMapper.writeValue(new File(JSON_FILE_PATH), root);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Failed to save JSON data.");
        }
    }
}
