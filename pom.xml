package com.nifesmp.rubyshards.commands;

import com.nifesmp.rubyshards.RubyShards;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RubiesCommand implements CommandExecutor {

    private final RubyShards plugin;

    public RubiesCommand(RubyShards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            // Check own balance
            if (!(sender instanceof Player player)) {
                sender.sendMessage("Console must specify a player: /rubies <player>");
                return true;
            }

            long rubies = plugin.getRubyManager().getRubies(player.getUniqueId());
            String msg = plugin.getConfig().getString("messages.balance-self", "&6Your Rubies: &e{rubies} 💎")
                    .replace("{rubies}", String.valueOf(rubies));
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));

        } else {
            // Check another player's balance
            if (!sender.hasPermission("rubyshards.check.others")) {
                String msg = plugin.getConfig().getString("messages.no-permission", "&cYou don't have permission.");
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                // Try offline player UUID lookup from our data
                String msg = plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found.");
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
                return true;
            }

            long rubies = plugin.getRubyManager().getRubies(target.getUniqueId());
            String msg = plugin.getConfig().getString("messages.balance-other", "&6{player}'s Rubies: &e{rubies} 💎")
                    .replace("{rubies}", String.valueOf(rubies))
                    .replace("{player}", target.getName());
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
        }

        return true;
    }
}
