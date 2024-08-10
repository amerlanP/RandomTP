package org.amerlan.randomTP;

import org.amerlan.randomTP.commands.RTP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Logger;

public final class RandomTP extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Logger logger = Bukkit.getLogger();
        logger.info("RandomTP Plugin Enabled");

        // Get values from config and instantiate RTP object
        saveDefaultConfig();
        FileConfiguration config = getConfig();
        Map<String, Object> bounds = config.getConfigurationSection("bounds").getValues(false);
        int cooldown = config.get("cooldown-sec") != null ? (int) config.get("cooldown-sec") : 30;
        int minX = (int) bounds.get("minX");
        int maxX = (int) bounds.get("maxX");
        int minZ = (int) bounds.get("minZ");
        int maxZ = (int) bounds.get("maxZ");
        RTP rtp = new RTP(minX, maxX, minZ, maxZ, cooldown);

        // Assign command /wild to rtp
        getCommand("wild").setExecutor(rtp);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("RandomTP Plugin Disabled");
    }
}
