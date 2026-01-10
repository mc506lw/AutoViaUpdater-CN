package bungeecord;

import common.CronScheduler;
import common.LanguageManager;
import common.PluginDetector;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static common.BuildYml.createYamlFile;
import static common.BuildYml.updateBuildNumber;
import static common.UpdateVias.updateVia;

public final class AutoViaUpdater extends Plugin {

    private Configuration config;
    private final java.util.concurrent.atomic.AtomicBoolean isChecking = new java.util.concurrent.atomic.AtomicBoolean(
            false);
    public boolean isViaVersionEnabled;
    public boolean isViaVersionDev;
    public boolean isViaVersionSnapshot;
    public boolean isViaVersionJava8;
    public String viaVersionFileName;
    public boolean isViaBackwardsEnabled;
    public boolean isViaBackwardsDev;
    public boolean isViaBackwardsSnapshot;
    public boolean isViaBackwardsJava8;
    public String viaBackwardsFileName;
    public boolean isViaRewindEnabled;
    public boolean isViaRewindDev;
    public boolean isViaRewindSnapshot;
    public boolean isViaRewindJava8;
    public String viaRewindFileName;
    private boolean isFirstStartup;

    @Override
    public void onEnable() {
        new Metrics(this, 18605);
        common.LoggerUtil.setLogger(getLogger());
        isFirstStartup = !new File(getDataFolder(), "config.yml").exists();
        saveDefaultConfig();
        loadConfiguration();
        createYamlFile(getDataFolder().getAbsolutePath(), true);

        if (isFirstStartup) {
            String language = config.getString("Language", "zh-CN");
            LanguageManager.init(getDataFolder().getAbsolutePath(), language);
            detectAndConfigurePlugins();
            reloadSettings();
        }

        reloadSettings();
        if (!isFirstStartup) {
            String language = config.getString("Language", "zh-CN");
            LanguageManager.init(getDataFolder().getAbsolutePath(), language);
        }
        updateChecker();
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new UpdateCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new AutoViaUpdaterCommand());
    }

    private void detectAndConfigurePlugins() {
        String pluginsFolder = getDataFolder().getParent();
        Map<String, String> detectedPlugins = PluginDetector.detectInstalledPlugins(pluginsFolder);

        if (!detectedPlugins.isEmpty()) {
            String configPath = getDataFolder().getAbsolutePath() + File.separator + "config.yml";
            PluginDetector.updateConfigForDetectedPlugins(configPath, detectedPlugins);

            for (Map.Entry<String, String> entry : detectedPlugins.entrySet()) {
                String pluginName = entry.getKey();
                String fileName = entry.getValue();
                String displayName = pluginName.replace("-", " ");
                getLogger().info(LanguageManager.getInstance().getMessage("plugin.detected", "plugin", displayName,
                        "filename", fileName));
            }
        }
    }

    public void updateChecker() {
        String cronExpression = config.getString("Cron-Expression", "");
        long interval = config.getLong("Check-Interval");
        long delay = config.getLong("Delay");

        if (!cronExpression.isEmpty()) {
            CronScheduler scheduler = new CronScheduler(cronExpression);
            getProxy().getScheduler().schedule(this, () -> scheduler.runIfDue(v -> checkUpdateVias()), delay, 1,
                    TimeUnit.SECONDS);
        } else {
            getProxy().getScheduler().schedule(this, this::checkUpdateVias, delay, interval * 60, TimeUnit.SECONDS);
        }
    }

    private void saveDefaultConfig() {
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (InputStream in = getClass().getResourceAsStream("/config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadConfiguration() {
        File file = new File(getDataFolder(), "config.yml");
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void reloadSettings() {
        loadConfiguration();
        isViaVersionEnabled = config.getBoolean("ViaVersion.enabled", true);
        isViaVersionSnapshot = config.getBoolean("ViaVersion.snapshot", true);
        isViaVersionDev = config.getBoolean("ViaVersion.dev", false);
        isViaVersionJava8 = config.getBoolean("ViaVersion.java8", false);
        viaVersionFileName = config.getString("ViaVersion.fileName", "");
        isViaBackwardsEnabled = config.getBoolean("ViaBackwards.enabled", true);
        isViaBackwardsSnapshot = config.getBoolean("ViaBackwards.snapshot", true);
        isViaBackwardsDev = config.getBoolean("ViaBackwards.dev", false);
        isViaBackwardsJava8 = config.getBoolean("ViaBackwards.java8", false);
        viaBackwardsFileName = config.getString("ViaBackwards.fileName", "");
        isViaRewindEnabled = config.getBoolean("ViaRewind.enabled", true);
        isViaRewindSnapshot = config.getBoolean("ViaRewind.snapshot", true);
        isViaRewindDev = config.getBoolean("ViaRewind.dev", false);
        isViaRewindJava8 = config.getBoolean("ViaRewind.java8", false);
        viaRewindFileName = config.getString("ViaRewind.fileName", "");
    }

    public void checkUpdateVias() {
        if (!isChecking.compareAndSet(false, true))
            return;
        try {
            reloadSettings();
            if (getProxy().getPluginManager().getPlugin("ViaVersion") == null) {
                updateBuildNumber("ViaVersion", -1);
            }
            if (getProxy().getPluginManager().getPlugin("ViaBackwards") == null) {
                updateBuildNumber("ViaBackwards", -1);
            }
            if (getProxy().getPluginManager().getPlugin("ViaRewind") == null) {
                updateBuildNumber("ViaRewind", -1);
            }
            boolean shouldRestart = false;
            if (isViaVersionEnabled
                    && updatePlugin("ViaVersion", isViaVersionSnapshot, isViaVersionDev, isViaVersionJava8,
                            viaVersionFileName)) {
                shouldRestart = true;
            }
            if (isViaBackwardsEnabled
                    && updatePlugin("ViaBackwards", isViaBackwardsSnapshot, isViaBackwardsDev, isViaBackwardsJava8,
                            viaBackwardsFileName)) {
                shouldRestart = true;
            }
            if (isViaRewindEnabled
                    && updatePlugin("ViaRewind", isViaRewindSnapshot, isViaRewindDev, isViaRewindJava8,
                            viaRewindFileName)) {
                shouldRestart = true;
            }
            if (shouldRestart && config.getBoolean("AutoRestart")) {
                String raw = config.getString("AutoRestart-Message");
                String colored = net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&',
                        raw == null ? "" : raw);
                getProxy().broadcast(new TextComponent(colored));
                getProxy().getScheduler().schedule(this, () -> getProxy().stop(), config.getLong("AutoRestart-Delay"),
                        TimeUnit.SECONDS);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            isChecking.set(false);
        }
    }

    private boolean updatePlugin(String pluginName, boolean isSnapshot, boolean isDev, boolean isJava8,
            String customFileName)
            throws IOException {
        return updateVia(pluginName, getDataFolder().getParent(), isSnapshot, isDev, isJava8, customFileName);
    }

    public class UpdateCommand extends Command {

        public UpdateCommand() {
            super("updatevias", "autoviaupdater.admin");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            sender.sendMessage(
                    new TextComponent(ChatColor.YELLOW + LanguageManager.getInstance().getMessage("command.checking")));
            getProxy().getScheduler().runAsync(AutoViaUpdater.this, () -> {
                checkUpdateVias();
                sender.sendMessage(new TextComponent(
                        ChatColor.AQUA + LanguageManager.getInstance().getMessage("command.completed")));
            });
        }
    }

    public class AutoViaUpdaterCommand extends Command {

        public AutoViaUpdaterCommand() {
            super("autoviaupdater", "autoviaupdater.reload");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (args.length == 0) {
                sender.sendMessage(new TextComponent(
                        ChatColor.YELLOW + LanguageManager.getInstance().getMessage("command.usage")));
                return;
            }

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("autoviaupdater.reload")) {
                    sender.sendMessage(new TextComponent(
                            ChatColor.RED + LanguageManager.getInstance().getMessage("command.no_permission")));
                    return;
                }

                sender.sendMessage(new TextComponent(
                        ChatColor.YELLOW + LanguageManager.getInstance().getMessage("command.reloading")));
                getProxy().getScheduler().runAsync(AutoViaUpdater.this, () -> {
                    loadConfiguration();
                    reloadSettings();
                    String language = config.getString("Language", "zh-CN");
                    LanguageManager.init(getDataFolder().getAbsolutePath(), language);

                    sender.sendMessage(new TextComponent(
                            ChatColor.GREEN + LanguageManager.getInstance().getMessage("command.reloaded")));
                });
                return;
            }

            sender.sendMessage(
                    new TextComponent(ChatColor.YELLOW + LanguageManager.getInstance().getMessage("command.usage")));
        }
    }

}
