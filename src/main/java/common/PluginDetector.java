package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PluginDetector {
    private static final Map<String, String> PLUGIN_PATTERNS = new HashMap<>();

    static {
        PLUGIN_PATTERNS.put("ViaVersion", "ViaVersion");
        PLUGIN_PATTERNS.put("ViaBackwards", "ViaBackwards");
        PLUGIN_PATTERNS.put("ViaRewind", "ViaRewind");
        PLUGIN_PATTERNS.put("ViaRewind-Legacy-Support", "ViaRewind-Legacy");
    }

    public static Map<String, String> detectInstalledPlugins(String pluginsFolder) {
        Map<String, String> detectedPlugins = new HashMap<>();
        File pluginsDir = new File(pluginsFolder);

        if (!pluginsDir.exists() || !pluginsDir.isDirectory()) {
            return detectedPlugins;
        }

        File[] files = pluginsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (files == null) {
            return detectedPlugins;
        }

        for (File file : files) {
            String fileName = file.getName();
            for (Map.Entry<String, String> entry : PLUGIN_PATTERNS.entrySet()) {
                String pluginName = entry.getKey();
                String configKey = entry.getValue();

                if (fileName.contains(pluginName)) {
                    detectedPlugins.put(configKey, fileName);
                    break;
                }
            }
        }

        return detectedPlugins;
    }

    public static void updateConfigForDetectedPlugins(String configPath, Map<String, String> detectedPlugins) {
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(configPath), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            String[] lines = content.toString().split("\n");
            StringBuilder result = new StringBuilder();

            String currentPlugin = null;
            boolean inPluginSection = false;

            for (String line : lines) {
                if (line.matches("^\\s*[A-Za-z-]+:\\s*$")) {
                    currentPlugin = line.trim().replace(":", "");
                    inPluginSection = PLUGIN_PATTERNS.containsValue(currentPlugin);
                }

                if (inPluginSection && currentPlugin != null) {
                    if (line.matches("^\\s*enabled:\\s*true\\s*$")) {
                        if (!detectedPlugins.containsKey(currentPlugin)) {
                            line = line.replace("true", "false");
                        }
                    } else if (line.matches("^\\s*enabled:\\s*false\\s*$")) {
                        if (detectedPlugins.containsKey(currentPlugin)) {
                            line = line.replace("false", "true");
                        }
                    } else if (line.matches("^\\s*fileName:\\s*\"[^\"]*\"\\s*$")) {
                        if (detectedPlugins.containsKey(currentPlugin)) {
                            String fileName = detectedPlugins.get(currentPlugin);
                            line = line.replaceAll("\"[^\"]*\"", "\"" + fileName + "\"");
                        }
                    }
                }

                result.append(line).append("\n");
            }

            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(configPath), StandardCharsets.UTF_8)) {
                writer.write(result.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateTomlConfigForDetectedPlugins(String configPath, Map<String, String> detectedPlugins) {
        try {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(configPath), StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            String[] lines = content.toString().split("\n");
            StringBuilder result = new StringBuilder();

            String currentPlugin = null;
            boolean inPluginSection = false;

            for (String line : lines) {
                if (line.matches("^\\s*\\[[A-Za-z-]+\\]\\s*$")) {
                    currentPlugin = line.trim().replaceAll("[\\[\\]]", "");
                    inPluginSection = PLUGIN_PATTERNS.containsValue(currentPlugin);
                }

                if (inPluginSection && currentPlugin != null) {
                    if (line.matches("^\\s*enabled\\s*=\\s*true\\s*$")) {
                        if (!detectedPlugins.containsKey(currentPlugin)) {
                            line = line.replace("true", "false");
                        }
                    } else if (line.matches("^\\s*enabled\\s*=\\s*false\\s*$")) {
                        if (detectedPlugins.containsKey(currentPlugin)) {
                            line = line.replace("false", "true");
                        }
                    } else if (line.matches("^\\s*fileName\\s*=\\s*\"[^\"]*\"\\s*$")) {
                        if (detectedPlugins.containsKey(currentPlugin)) {
                            String fileName = detectedPlugins.get(currentPlugin);
                            line = line.replaceAll("\"[^\"]*\"", "\"" + fileName + "\"");
                        }
                    }
                }

                result.append(line).append("\n");
            }

            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(configPath), StandardCharsets.UTF_8)) {
                writer.write(result.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
