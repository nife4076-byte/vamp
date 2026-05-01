package com.nifesmp.rubyshards;

import com.nifesmp.rubyshards.commands.RubyAdminCommand;
import com.nifesmp.rubyshards.commands.RubiesCommand;
import com.nifesmp.rubyshards.hooks.PlaceholderHook;
import com.nifesmp.rubyshards.managers.RubyManager;
import com.nifesmp.rubyshards.tasks.RubyTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RubyShards extends JavaPlugin {

    private static RubyShards instance;
    private RubyManager rubyManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        rubyManager = new RubyManager(this);

        // Register commands
        getCommand("rubies").setExecutor(new RubiesCommand(this));
        getCommand("rubyadmin").setExecutor(new RubyAdminCommand(this));

        // Start the ruby earning task (runs every 60 seconds)
        new RubyTask(this).runTaskTimer(this, 1200L, 1200L);

        // Register PlaceholderAPI hook if available
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderHook(this).register();
            getLogger().info("PlaceholderAPI hook registered!");
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholders will not work.");
        }

        getLogger().info("RubyShards enabled! NifeSMP ready to earn rubies.");
    }

    @Override
    public void onDisable() {
        if (rubyManager != null) {
            rubyManager.saveAll();
        }
        getLogger().info("RubyShards disabled. All data saved.");
    }

    public static RubyShards getInstance() {
        return instance;
    }

    public RubyManager getRubyManager() {
        return rubyManager;
    }
}
