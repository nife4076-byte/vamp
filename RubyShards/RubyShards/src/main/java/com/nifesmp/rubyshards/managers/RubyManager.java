package com.nifesmp.rubyshards.managers;

import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.nifesmp.rubyshards.RubyShards;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RubyManager {

    private final RubyShards plugin;
    private final Map<UUID, Long> rubyData = new HashMap<>();
    private final File dataFile;
    private FileConfiguration dataConfig;
    private GriefPrevention griefPrevention;
    private final int rubiesPerClaimBlock;

    public RubyManager(RubyShards plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        this.rubiesPerClaimBlock = plugin.getConfig().getInt("rubies-per-claim-block", 10);

        // Hook into GriefPrevention
        if (plugin.getServer().getPluginManager().getPlugin("GriefPrevention") != null) {
            griefPrevention = GriefPrevention.instance;
            plugin.getLogger().info("GriefPrevention hooked successfully!");
        } else {
            plugin.getLogger().warning("GriefPrevention not found! Claim block bonuses will not work.");
        }

        loadData();
    }

    public long getRubies(UUID uuid) {
        return rubyData.getOrDefault(uuid, 0L);
    }

    public long getRubies(Player player) {
        return getRubies(player.getUniqueId());
    }

    public void setRubies(UUID uuid, long amount) {
        long oldRubies = getRubies(uuid);
        rubyData.put(uuid, Math.max(0, amount));
        updateClaimBlocks(uuid, oldRubies, Math.max(0, amount));
    }

    public void addRubies(UUID uuid, long amount) {
        long oldRubies = getRubies(uuid);
        long newRubies = oldRubies + amount;
        rubyData.put(uuid, newRubies);
        updateClaimBlocks(uuid, oldRubies, newRubies);
    }

    public void removeRubies(UUID uuid, long amount) {
        long current = getRubies(uuid);
        long newAmount = Math.max(0, current - amount);
        long oldRubies = current;
        rubyData.put(uuid, newAmount);
        updateClaimBlocks(uuid, oldRubies, newAmount);
    }

    private void updateClaimBlocks(UUID uuid, long oldRubies, long newRubies) {
        if (griefPrevention == null) return;

        // Calculate how many claim blocks they should have from rubies
        long oldBlocks = oldRubies / rubiesPerClaimBlock;
        long newBlocks = newRubies / rubiesPerClaimBlock;
        long diff = newBlocks - oldBlocks;

        if (diff == 0) return;

        // Update GriefPrevention claim blocks
        PlayerData playerData = griefPrevention.dataStore.getPlayerData(uuid);
        if (playerData != null) {
            int currentBonus = playerData.getBonusClaimBlocks();
            playerData.setBonusClaimBlocks((int) Math.max(0, currentBonus + diff));
        }
    }

    private void loadData() {
        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml!");
                return;
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        if (dataConfig.contains("players")) {
            for (String uuidStr : dataConfig.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    long rubies = dataConfig.getLong("players." + uuidStr);
                    rubyData.put(uuid, rubies);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in data.yml: " + uuidStr);
                }
            }
        }

        plugin.getLogger().info("Loaded ruby data for " + rubyData.size() + " players.");
    }

    public void saveAll() {
        dataConfig = new YamlConfiguration();
        for (Map.Entry<UUID, Long> entry : rubyData.entrySet()) {
            dataConfig.set("players." + entry.getKey().toString(), entry.getValue());
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data.yml! " + e.getMessage());
        }
    }
}
