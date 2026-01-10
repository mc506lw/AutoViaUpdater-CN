package common;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {
    private static LanguageManager instance;
    private Map<String, String> messages;
    private String currentLanguage;
    private String dataFolder;

    private LanguageManager(String dataFolder, String language) {
        this.dataFolder = dataFolder;
        this.currentLanguage = language;
        loadLanguage(language);
    }

    public static void init(String dataFolder, String language) {
        instance = new LanguageManager(dataFolder, language);
    }

    public static LanguageManager getInstance() {
        return instance;
    }

    public void loadLanguage(String language) {
        this.currentLanguage = language;
        String fileName = "lang/" + language + ".yml";
        Path filePath = Paths.get(dataFolder, fileName);

        if (!Files.exists(filePath)) {
            createLanguageFile(filePath, language);
        }

        messages = new HashMap<>();
        try {
            Yaml yaml = new Yaml();
            Object data = yaml.load(Files.newBufferedReader(filePath));
            if (data instanceof Map) {
                flattenMap((Map<?, ?>) data, "");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void flattenMap(Map<?, ?> map, String prefix) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                String key = prefix.isEmpty() ? entry.getKey().toString() : prefix + "." + entry.getKey().toString();
                if (entry.getValue() instanceof Map) {
                    flattenMap((Map<?, ?>) entry.getValue(), key);
                } else {
                    messages.put(key, entry.getValue().toString());
                }
            }
        }
    }

    private void createLanguageFile(Path filePath, String language) {
        try {
            Files.createDirectories(filePath.getParent());
            String resourceName = "/lang/" + language + ".yml";
            try (InputStream in = getClass().getResourceAsStream(resourceName)) {
                if (in != null) {
                    Files.copy(in, filePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMessage(String key) {
        return messages.getOrDefault(key, key);
    }

    public String getMessage(String key, String... placeholders) {
        String message = getMessage(key);
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                message = message.replace("{" + placeholders[i] + "}", placeholders[i + 1]);
            }
        }
        return message;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }
}
