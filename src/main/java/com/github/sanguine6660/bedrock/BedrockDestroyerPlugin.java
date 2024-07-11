/*
 * Copyright (c) 2024 - present | sanguine6660 <sanguine6660@gmail.com>
 */

package com.github.sanguine6660.bedrock;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BedrockDestroyerPlugin extends JavaPlugin {
    public static LegacyComponentSerializer lcs;
    public ItemStack destroyer;

    @Override
    public void onEnable() {
        lcs = LegacyComponentSerializer.builder().character('&').build();

        destroyer = new ItemStack(Material.NETHERITE_PICKAXE);
        destroyer.setAmount(1);
        ItemMeta destroyerMeta = destroyer.getItemMeta();
        destroyerMeta.setUnbreakable(true);
        destroyerMeta.displayName(lcs.deserialize("&l&5Bedrock Destroyer"));
        destroyerMeta.lore(List.of(
                lcs.deserialize("&7Klicke auf einen Bedrock Block um ihn zu zerstören.")
        ));
        destroyer.setItemMeta(destroyerMeta);

        ShapedRecipe recipe = new ShapedRecipe(NamespacedKey.fromString("bedrock_destroyer", this), destroyer);
        recipe.shape("XXX", " Y ", " Y ");
        recipe.setIngredient('X', Material.NETHERITE_BLOCK);
        recipe.setIngredient('Y', Material.STICK);
        recipe.setGroup(this.getName().toLowerCase());
        Bukkit.getServer().addRecipe(recipe);

        Bukkit.getServer().getPluginManager().registerEvents(new BedrockDestroyListener(this), this);
        this.getLogger().info("Bedrock can now be destroyed XD");
    }

    public ItemStack getDestroyer() {
        return destroyer;
    }
}
