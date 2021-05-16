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

        this.items.put(35, new ItemBuilder(Material.DIAMOND_BOOTS).name("&rWolftamer's Diamond Boots (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,4).amount(1).make());
        this.items.put(25, new ItemBuilder(Material.DIAMOND_BOOTS).name("&rHorsetamer's Diamond Boots (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,2).amount(1).make());
        this.items.put(35, new ItemBuilder(Material.DIAMOND_HELMET).name("&rMeatmaster's Diamond Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,4).amount(1).make());
        this.items.put(25, new ItemBuilder(Material.IRON_HELMET).name("&rSnowman's Iron Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,3).amount(1).make());
        this.items.put(35, new ItemBuilder(Material.IRON_HELMET).name("&rViking's Iron Boots (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,3).amount(1).make());

        this.items.put(30, new ItemBuilder(Material.DIAMOND_CHESTPLATE).name("&rCreepertamer's Diamond Chestplate (X)").enchantment(Enchantment.PROTECTION_EXPLOSIONS,10).amount(1).make());
        this.items.put(30, new ItemBuilder(Material.DIAMOND_CHESTPLATE).name("&rTim's Diamond Leggings (X)").amount(1).make());
        this.items.put(15, new ItemBuilder(Material.CHAINMAIL_LEGGINGS).name("&rChain Leggings").amount(1).make());
        this.items.put(15, new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).name("&rChain Chestplate").amount(1).make());
        this.items.put(15, new ItemBuilder(Material.GOLD_CHESTPLATE).name("&rGold Chestplate").amount(1).make());
        this.items.put(15, new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&rLeather Chestplate").amount(1).make());





    }

}
