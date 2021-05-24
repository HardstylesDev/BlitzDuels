package me.hardstyles.blitz.kits;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.utils.ItemBuilder;
import me.hardstyles.blitz.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class PointShop {
    private final Core core;
    HashMap<Integer, ItemStack> items;
    HashMap<Integer, ItemStack> helmets;
    HashMap<Integer, ItemStack> potions;
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



        this.helmets.put(27, new ItemBuilder(Material.DIAMOND_HELMET).name("&rMeatmaster's Diamond Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,4).amount(1).make());
        this.helmets.put(19, new ItemBuilder(Material.DIAMOND_HELMET).name("&rArcher's Diamond Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,2).amount(1).make());


        PotionEffect[] warrior = new PotionEffect[]{new PotionEffect(PotionEffectType.REGENERATION,20*10,0),new PotionEffect(PotionEffectType.SPEED,20*8,0) };
        PotionEffect[] reaper = new PotionEffect[]{new PotionEffect(PotionEffectType.WITHER,20*6,2),new PotionEffect(PotionEffectType.SLOW,20*6,0) };
        potions.put(20, ItemUtils.buildPotion(warrior, (short) 16450));
        potions.put(25, ItemUtils.buildPotion(reaper, (short) 16424));
        potions.put(25, ItemUtils.buildPotion(PotionEffectType.SPEED,20*16,2, (short) 8194,4));
        potions.put(15, ItemUtils.buildPotion(PotionEffectType.SLOW,20*8,2, (short) 16426,4));
        potions.put(25, ItemUtils.buildPotion(PotionEffectType.BLINDNESS,20*7,3, (short) 16460,3));
        potions.put(15, ItemUtils.buildPotion(PotionEffectType.HARM,1,1, (short) 16460,2));

        potions.put(25, ItemUtils.buildPotion(PotionEffectType.POISON,20*6,2, (short) 16420,5));
        potions.put(25, ItemUtils.buildPotion(PotionEffectType.REGENERATION,20*8,2, (short) 8193,3));


    }

}
