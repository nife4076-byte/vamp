package com.nifesmp.rubyshards.commands;

import com.nifesmp.rubyshards.RubyShards;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RubyAdminCommand implements CommandExecutor {

    private final RubyShards plugin;

    public RubyAdminCommand(RubyShards plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("rubyshards.admin")) {
            String msg = plugin.getConfig().getString("messages.no-permission", "&cYou don't have permission.");
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("Usage: /rubyadmin <give|take|set> <player> <amount>");
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            String msg = plugin.getConfig().getString("messages.player-not-found", "&cPlayer not found.");
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            String msg = plugin.getConfig().getString("messages.invalid-amount", "&cPlease enter a valid amount.");
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
            return true;
        }

        switch (action) {
            case "give" -> {
                plugin.getRubyManager().addRubies(target.getUniqueId(), amount);
                String msg = plugin.getConfig().getString("messages.admin-give", "&aGave &e{amount} &arubies to &e{player}&a.")
                        .replace("{amount}", String.valueOf(amount))
                        .replace("{player}", target.getName());
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
            }
            case "take" -> {
                plugin.getRubyManager().removeRubies(target.getUniqueId(), amount);
                String msg = plugin.getConfig().getString("messages.admin-take", "&cTook &e{amount} &crubies from &e{player}&c.")
                        .replace("{amount}", String.valueOf(amount))
                        .replace("{player}", target.getName());
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
            }
            case "set" -> {
                plugin.getRubyManager().setRubies(target.getUniqueId(), amount);
                String msg = plugin.getConfig().getString("messages.admin-set", "&aSet &e{player}&a's rubies to &e{amount}&a.")
                        .replace("{amount}", String.valueOf(amount))
                        .replace("{player}", target.getName());
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(msg));
            }
            default -> sender.sendMessage("Usage: /rubyadmin <give|take|set> <player> <amount>");
        }

        return true;
    }
}
