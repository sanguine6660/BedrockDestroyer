/*
 * Copyright (c) 2024 - present | sanguine6660 <sanguine6660@gmail.com>
 */

package com.github.sanguine6660.bedrock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class BedrockDestroyListener implements Listener {
    private final BedrockDestroyerPlugin plugin;

    public BedrockDestroyListener(BedrockDestroyerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.BEDROCK)) return;
        if (!event.getPlayer().getInventory().getItemInMainHand().equals(plugin.getDestroyer())) return;

        Block target = event.getClickedBlock();
        BlockBreakEvent testBuild = new BlockBreakEvent(target, event.getPlayer());
        Bukkit.getServer().getPluginManager().callEvent(testBuild);

        if (testBuild.isCancelled()) {
            event.getPlayer().sendMessage(BedrockDestroyerPlugin.lcs.deserialize("&cDer Block befindet sich in einer geschützen Region."));
            return;
        }

        target.setType(Material.AIR);
        event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        event.getPlayer().sendMessage(BedrockDestroyerPlugin.lcs.deserialize("&aBedrock zerstört."));

    }

}
