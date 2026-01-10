package common;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class BuildYml {

    public static String file;

    public static void createYamlFile(String folder, boolean isProxy) {
        file = folder + "/versions.yml";
        Path filePath = Paths.get(file);

        if (!Files.exists(filePath)) {
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

            Yaml yaml = new Yaml(options);

            Map<String, Integer> initialData = new java.util.LinkedHashMap<>();
            if (isProxy) {
                initialData.put("ViaVersion", -1);
                initialData.put("ViaVersion-Dev", -1);
                initialData.put("ViaVersion-Java8", -1);
                initialData.put("ViaBackwards", -1);
                initialData.put("ViaBackwards-Dev", -1);
                initialData.put("ViaBackwards-Java8", -1);
                initialData.put("ViaRewind", -1);
                initialData.put("ViaRewind-Dev", -1);
                initialData.put("ViaRewind-Java8", -1);
            } else {
                initialData.put("ViaVersion", -1);
                initialData.put("ViaVersion-Dev", -1);
                initialData.put("ViaVersion-Java8", -1);
                initialData.put("ViaBackwards", -1);
                initialData.put("ViaBackwards-Dev", -1);
                initialData.put("ViaBackwards-Java8", -1);
                initialData.put("ViaRewind", -1);
                initialData.put("ViaRewind-Dev", -1);
                initialData.put("ViaRewind-Java8", -1);
                initialData.put("ViaRewind%20Legacy%20Support", -1);
                initialData.put("ViaRewind%20Legacy%20Support%20DEV", -1);
            }

            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                yaml.dump(initialData, writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateBuildNumber(String key, int newBuildNumber) {
        try {
            Path filePath = Paths.get(file);
            Map<String, Integer> data = readYamlFile(filePath);

            if (data.containsKey(key)) {
                data.put(key, newBuildNumber);
                writeYamlFile(filePath, data);
                if (newBuildNumber != -1) {
                    LoggerUtil.info(LanguageManager.getInstance().getMessage("yaml.build_updated", "key", key,
                            "build", String.valueOf(newBuildNumber)));
                }
            } else {
                LoggerUtil.info(LanguageManager.getInstance().getMessage("yaml.key_not_found", "key", key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getDownloadedBuild(String key) {
        try {
            Path filePath = Paths.get(file);
            Map<String, Integer> data = readYamlFile(filePath);

            if (data.containsKey(key)) {
                return data.get(key);
            } else {
                LoggerUtil.info(LanguageManager.getInstance().getMessage("yaml.key_not_found", "key", key));
                return -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Integer> readYamlFile(Path filePath) throws IOException {
        Yaml yaml = new Yaml();
        try {
            Object obj = yaml.load(Files.newBufferedReader(filePath));
            if (obj instanceof Map) {
                return (Map<String, Integer>) obj;
            } else {
                throw new RuntimeException(LanguageManager.getInstance().getMessage("yaml.invalid_format"));
            }
        } catch (IOException e) {
            throw new IOException(LanguageManager.getInstance().getMessage("yaml.read_error"), e);
        }
    }

    private static void writeYamlFile(Path filePath, Map<String, Integer> data) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        Yaml yaml = new Yaml(options);

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            yaml.dump(data, writer);
        }
    }
}