package me.hardstyles.blitz.kits;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class PointShop {
    private final Core core;
    HashMap<Integer, ItemStack> items;
    public PointShop(Core core){
        this.core = core;
        this.items = new HashMap<>();

        this.items.put(10, new ItemBuilder(Material.DIAMOND_BOOTS).name("&rWolftamer's Diamond Boots (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,4).amount(1).make());
        this.items.put(10, new ItemBuilder(Material.DIAMOND_HELMET).name("&rMeatmaster's Diamond Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,4).amount(1).make());
    }

}
