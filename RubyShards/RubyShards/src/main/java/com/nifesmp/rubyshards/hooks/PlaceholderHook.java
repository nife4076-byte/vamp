package com.nifesmp.rubyshards.hooks;

import com.nifesmp.rubyshards.RubyShards;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderHook extends PlaceholderExpansion {

    private final RubyShards plugin;

    public PlaceholderHook(RubyShards plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "rubies";
    }

    @Override
    public @NotNull String getAuthor() {
        return "NifeSMP";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Required so PAPI doesn't unregister it
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) return "0";

        // %rubies_count% - returns the player's ruby count
        if (identifier.equals("count")) {
            return String.valueOf(plugin.getRubyManager().getRubies(player.getUniqueId()));
        }

        // %rubies_next_claim_block% - rubies needed until next claim block
        if (identifier.equals("next_claim_block")) {
            long rubies = plugin.getRubyManager().getRubies(player.getUniqueId());
            int perBlock = plugin.getConfig().getInt("rubies-per-claim-block", 10);
            long nextBlock = perBlock - (rubies % perBlock);
            return String.valueOf(nextBlock);
        }

        // %rubies_claim_blocks_earned% - total claim blocks earned from rubies
        if (identifier.equals("claim_blocks_earned")) {
            long rubies = plugin.getRubyManager().getRubies(player.getUniqueId());
            int perBlock = plugin.getConfig().getInt("rubies-per-claim-block", 10);
            return String.valueOf(rubies / perBlock);
        }

        return null;
    }
}
