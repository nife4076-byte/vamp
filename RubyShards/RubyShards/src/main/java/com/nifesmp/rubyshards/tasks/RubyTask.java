package com.nifesmp.rubyshards.tasks;

import com.nifesmp.rubyshards.RubyShards;
import com.nifesmp.rubyshards.managers.RubyManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RubyTask extends BukkitRunnable {

    private final RubyShards plugin;
    private final RubyManager rubyManager;
    private final String afkWorld;
    private final int normalRate;
    private final int afkRate;

    public RubyTask(RubyShards plugin) {
        this.plugin = plugin;
        this.rubyManager = plugin.getRubyManager();
        this.afkWorld = plugin.getConfig().getString("afk-world", "afk");
        this.normalRate = plugin.getConfig().getInt("rubies-per-minute-normal", 1);
        this.afkRate = plugin.getConfig().getInt("rubies-per-minute-afk", 5);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean isInAfkWorld = player.getWorld().getName().equalsIgnoreCase(afkWorld);
            int amount = isInAfkWorld ? afkRate : normalRate;

            rubyManager.addRubies(player.getUniqueId(), amount);

            long total = rubyManager.getRubies(player.getUniqueId());

            // Send earn message
            String earnMsg = plugin.getConfig().getString("messages.rubies-earned",
                    "&aYou earned &e{amount} &aruby! &7(Total: &e{total}&7)");
            earnMsg = earnMsg
                    .replace("{amount}", String.valueOf(amount))
                    .replace("{total}", String.valueOf(total));

            Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(earnMsg);
            player.sendMessage(message);
        }

        // Auto-save every cycle
        rubyManager.saveAll();
    }
}
