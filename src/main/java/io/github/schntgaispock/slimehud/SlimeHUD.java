package io.github.schntgaispock.slimehud;

import javax.annotation.Nonnull;

import io.github.schntgaispock.slimehud.placeholder.PlaceholderManager;
import io.github.schntgaispock.slimehud.translation.TranslationManager;
import io.github.schntgaispock.slimehud.waila.HudController;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.schntgaispock.slimehud.command.CommandManager;
import io.github.schntgaispock.slimehud.waila.WAILAManager;

import java.io.File;
import java.util.logging.Level;

public class SlimeHUD extends JavaPlugin {

    private FileConfiguration playerData;
    private File playerDataFile;

    private static SlimeHUD instance;
    private HudController hudController;
    private TranslationManager translationManager;

    public static SlimeHUD getInstance() {
        return instance;
    }

    public FileConfiguration getPlayerData() {
        return playerData;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        getLogger().info("#=================================#");
        getLogger().info("#    SlimeHUD by SchnTgaiSpock    #");
        getLogger().info("#=================================#");

        loadPlayerData();

        final Metrics metrics = new Metrics(this, 15883);
        metrics.addCustomChart(
            new SimplePie("disabled", () -> getConfig().getBoolean("waila.disabled") + "")
        );
        metrics.addCustomChart(
            new SimplePie("waila_location", () -> getConfig().getString("waila.location"))
        );

        WAILAManager.setup();
        CommandManager.setup();
        PlaceholderManager.setup();
        hudController = new HudController();
        translationManager = new TranslationManager();
    }

    @Override
    public void onDisable() {
        savePlayerData();
        instance = null;
    }

    private void loadPlayerData() {
        playerDataFile = new File(getDataFolder(), "player.yml");
        if (!playerDataFile.exists()) {
            saveResource("player.yml", false);
        }
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public void savePlayerData() {
        try {
            playerData.save(playerDataFile);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Could not save player.yml!", e);
        }
    }

    public static HudController getHudController() {
        return instance.hudController;
    }

    public static TranslationManager getTranslationManager() {
        return instance.translationManager;
    }

    public static NamespacedKey newNamespacedKey(@Nonnull String name) {
        return new NamespacedKey(SlimeHUD.getInstance(), name);
    }

    public static void log(Level level, String... messages) {
        for (String msg : messages) {
            getInstance().getLogger().log(level, msg);
        }
    }

    public String getPluginVersion() {
        return getDescription().getVersion();
    }
}
