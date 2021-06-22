package me.hardstyles.blitz.kits;

import me.hardstyles.blitz.Core;
import me.hardstyles.blitz.utils.ItemBuilder;
import me.hardstyles.blitz.utils.ItemUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;

public class IItemManager {
    private final Core core;
    HashMap<Integer, ItemStack> items;
    public ArrayList<IItem> potions;
    public ArrayList<IItem> helmets;
    public ArrayList<IItem> chestplates;
    public ArrayList<IItem> leggings;
    public ArrayList<IItem> boots;
    public ArrayList<IItem> weapons;
    public ArrayList<IItem> projectiles;
    public ArrayList<IItem> arrows;
    public ArrayList<IItem> bows;
    public ArrayList<IItem> mobs;

    public IItemManager(Core core) {
        this.core = core;
        this.items = new HashMap<>();
        this.helmets = new ArrayList<>();
        this.potions = new ArrayList<>();
        this.chestplates = new ArrayList<>();
        this.leggings = new ArrayList<>();
        this.boots = new ArrayList<>();
        this.weapons = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.arrows = new ArrayList<>();
        this.bows = new ArrayList<>();
        this.mobs = new ArrayList<>();

        //  this.items.put(35, new ItemBuilder(Material.DIAMOND_BOOTS).name("&rWolftamer's Diamond Boots (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,4).amount(1).make());
        //  this.items.put(25, new ItemBuilder(Material.DIAMOND_BOOTS).name("&rHorsetamer's Diamond Boots (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL,2).amount(1).make());
        helmets.add(new IItem(0, new ItemStack(Material.BARRIER), 0));
        helmets.add(new IItem(1, new ItemBuilder(Material.LEATHER_HELMET).name("&rLeather Helmet").amount(1).make(), 6));
        helmets.add(new IItem(2, new ItemBuilder(Material.GOLD_HELMET).name("&rGold Helmet").amount(1).make(), 10));
        helmets.add(new IItem(3, new ItemBuilder(Material.CHAINMAIL_HELMET).name("&rChain Helmet").amount(1).make(), 10));
        helmets.add(new IItem(4, new ItemBuilder(Material.IRON_HELMET).name("&rPaladin's Iron Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).amount(1).make(), 14));
        helmets.add(new IItem(5, new ItemBuilder(Material.GOLD_HELMET).name("&rPigman's Gold Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).amount(1).make(), 20));
        helmets.add(new IItem(6, new ItemBuilder(Material.IRON_HELMET).name("&rViking's Iron Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).amount(1).make(), 24));

        helmets.add(new IItem(7, new ItemBuilder(Material.DIAMOND_HELMET).name("&rArcher's Diamond Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).amount(1).make(), 27));
        helmets.add(new IItem(8, new ItemBuilder(Material.DIAMOND_HELMET).name("&rMeatmaster's Diamond Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).amount(1).make(), 32));


        chestplates.add(new IItem(0, new ItemStack(Material.BARRIER), 0));
        chestplates.add(new IItem(1, new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&rLeather Chestplate").amount(1).make(), 10));
        chestplates.add(new IItem(2, new ItemBuilder(Material.GOLD_CHESTPLATE).name("&rGolden Chestplate").amount(1).make(), 14));
        chestplates.add(new IItem(3, new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).name("&rChain Chestplate").amount(1).make(), 14));
        chestplates.add(new IItem(4, new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&rLeather Chestplate").color(Color.GREEN).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).amount(1).make(), 21));
        chestplates.add(new IItem(5, new ItemBuilder(Material.IRON_CHESTPLATE).name("&rIron Chestplate").amount(1).make(), 25));
        chestplates.add(new IItem(6, new ItemBuilder(Material.DIAMOND_CHESTPLATE).name("&rCreepertamer's Diamond Chestplate").enchantment(Enchantment.PROTECTION_EXPLOSIONS, 10).amount(1).make(), 31));


        leggings.add(new IItem(0, new ItemStack(Material.BARRIER), 0));
        leggings.add(new IItem(1, new ItemBuilder(Material.LEATHER_LEGGINGS).name("&rLeather Leggings").color(Color.AQUA).enchantment(Enchantment.WATER_WORKER, 10).amount(1).make(), 7));
        leggings.add(new IItem(2, new ItemBuilder(Material.GOLD_LEGGINGS).name("&rGold Leggings").amount(1).make(), 11));
        leggings.add(new IItem(3, new ItemBuilder(Material.CHAINMAIL_LEGGINGS).name("&rChain Leggings").amount(1).make(), 14));
        leggings.add(new IItem(4, new ItemBuilder(Material.LEATHER_LEGGINGS).name("&rLeather Leggings").color(Color.BLUE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).amount(1).make(), 18));
        leggings.add(new IItem(5, new ItemBuilder(Material.IRON_LEGGINGS).name("&rFarmer's Iron Leggings (X)").amount(1).make(), 25));
        leggings.add(new IItem(6, new ItemBuilder(Material.DIAMOND_LEGGINGS).name("&rTim's Diamond Leggings (X)").amount(1).make(), 29));


        boots.add(new IItem(0, new ItemStack(Material.BARRIER), 0));
        boots.add(new IItem(1, new ItemBuilder(Material.GOLD_BOOTS).name("&rGold Boots").amount(1).make(), 7));
        boots.add(new IItem(2, new ItemBuilder(Material.CHAINMAIL_BOOTS).name("&rChain Boots").amount(1).make(), 7));
        boots.add(new IItem(3, new ItemBuilder(Material.IRON_BOOTS).name("&rIron Boots").amount(1).make(), 12));
        boots.add(new IItem(4, new ItemBuilder(Material.IRON_BOOTS).name("&rMeatmaster's Iron Boots (X)").amount(1).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).make(), 14));
        boots.add(new IItem(5, new ItemBuilder(Material.DIAMOND_BOOTS).name("&rRanger's Diamond Boots (X)").amount(1).make(), 21));
        boots.add(new IItem(6, new ItemBuilder(Material.DIAMOND_BOOTS).name("&rHorsetamer's Diamond Boots (X)").amount(1).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).make(), 26));
        boots.add(new IItem(7, new ItemBuilder(Material.DIAMOND_BOOTS).name("&rWolftamer's Diamond Boots (X)").amount(1).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).make(), 30));

        weapons.add(new IItem(0, new ItemStack(Material.BARRIER), 0));
        weapons.add(new IItem(1, new ItemBuilder(Material.WOOD_SWORD).name("&rWood Sword").amount(1).make(), 5));
        weapons.add(new IItem(2, new ItemBuilder(Material.STONE_AXE).name("&rStone Axe").amount(1).make(), 5));
        weapons.add(new IItem(3, new ItemBuilder(Material.STONE_SWORD).name("&rStone Sword").amount(1).make(), 9));
        weapons.add(new IItem(4, new ItemBuilder(Material.IRON_AXE).name("&rIron Axe").amount(1).make(), 9));

        weapons.add(new IItem(5, new ItemBuilder(Material.STONE_SWORD).name("&rStone Sword").enchantment(Enchantment.DAMAGE_ALL, 1).amount(1).make(), 15));
        weapons.add(new IItem(6, new ItemBuilder(Material.IRON_SWORD).name("&rIron Sword").enchantment(Enchantment.DURABILITY, 1).amount(1).make(), 15));
        weapons.add(new IItem(7, new ItemBuilder(Material.DIAMOND_AXE).name("&rDiamond Axe").amount(1).make(), 15));
        weapons.add(new IItem(8, new ItemBuilder(Material.DIAMOND_SPADE).name("&rDiamond Shovel").enchantment(Enchantment.DAMAGE_ALL, 2).amount(1).make(), 15));
        weapons.add(new IItem(8, new ItemBuilder(Material.GOLD_SWORD).name("&rGolden Sword").enchantment(Enchantment.DAMAGE_ALL, 2).amount(1).make(), 15));
        weapons.add(new IItem(9, new ItemBuilder(Material.DIAMOND_PICKAXE).name("&rDiamond Pickaxe").enchantment(Enchantment.DAMAGE_ALL, 1).amount(1).make(), 15));
        weapons.add(new IItem(10, new ItemBuilder(Material.DIAMOND_SWORD).name("&rDiamond Sword").enchantment(Enchantment.DURABILITY, 1).amount(1).make(), 40));
        weapons.add(new IItem(10, new ItemBuilder(Material.DIAMOND_SWORD).name("&rDiamond Sword").enchantment(Enchantment.DAMAGE_ALL, 1).amount(1).make(), 80));


        projectiles.add(new IItem(0, new ItemStack(Material.BARRIER), 0));
        projectiles.add(new IItem(1, new ItemBuilder(Material.FISHING_ROD).amount(1).make(), 6));
        projectiles.add(new IItem(2, new ItemBuilder(Material.EGG).amount(32).make(), 8));
        projectiles.add(new IItem(3, new ItemBuilder(Material.SNOW_BALL).amount(32).make(), 8));
        projectiles.add(new IItem(3, new ItemBuilder(Material.FLINT_AND_STEEL).durability(Material.FLINT_AND_STEEL.getMaxDurability() - 4).make(), 15));

        bows.add(new IItem(0, new ItemStack(Material.BARRIER), 0));
        bows.add(new IItem(1, new ItemStack(Material.BOW), 10));
        bows.add(new IItem(2, new ItemBuilder(Material.BOW).name("&rArcher's Bow (V)").enchantment(Enchantment.ARROW_DAMAGE, 1).amount(1).make(), 10));
        bows.add(new IItem(3, new ItemBuilder(Material.BOW).name("&rArcher's Bow (X)").enchantment(Enchantment.ARROW_DAMAGE, 2).amount(1).make(), 15));
        bows.add(new IItem(4, new ItemBuilder(Material.BOW).name("&rRanger's Bow (X)").enchantment(Enchantment.ARROW_DAMAGE, 1).enchantment(Enchantment.ARROW_KNOCKBACK).amount(1).make(), 20));

        arrows.add(new IItem(0, new ItemStack(Material.BARRIER), 0));
        arrows.add(new IItem(1, new ItemStack(Material.ARROW, 8), 5));
        arrows.add(new IItem(2, new ItemStack(Material.ARROW, 16), 10));
        arrows.add(new IItem(3, new ItemStack(Material.ARROW, 24), 15));

        mobs.add(new IItem(0, new ItemStack(Material.BARRIER),0));
        mobs.add(new IItem(1, new ItemBuilder(Material.MONSTER_EGG).durability(52).name("&rSpider Spawn Egg").amount(5).make(),17));
        mobs.add(new IItem(2, new ItemBuilder(Material.MONSTER_EGG).durability(61).name("&rBlaze Spawn Egg").amount(3).make(),23));
        mobs.add(new IItem(3, new ItemBuilder(Material.MONSTER_EGG).durability(50).name("&rCreeper Spawn Egg").amount(4).make(),13));
        mobs.add(new IItem(4, new ItemBuilder(Material.MONSTER_EGG).durability(100).name("&rHorse Spawn Egg").make(),10));
        mobs.add(new IItem(5, new ItemBuilder(Material.MONSTER_EGG).durability(90).name("&rPig Spawn Egg").amount(1).make(),10));
        mobs.add(new IItem(6, new ItemBuilder(Material.MONSTER_EGG).durability(96).name("&rMooshroom Spawn Egg").amount(4).make(),8));
        mobs.add(new IItem(7, new ItemBuilder(Material.MONSTER_EGG).durability(54).name("&rZombie Spawn Egg").amount(3).make(),17));
        mobs.add(new IItem(8, new ItemBuilder(Material.MONSTER_EGG).durability(51).name("&rSkeleton Spawn Egg").amount(3).make(),17));
        mobs.add(new IItem(9, new ItemBuilder(Material.MONSTER_EGG).durability(62).name("&rMagma Cube Spawn Egg").amount(4).make(),14));
        mobs.add(new IItem(10, new ItemBuilder(Material.MONSTER_EGG).durability(55).name("&rSlime Spawn Egg").amount(4).make(),14));
        mobs.add(new IItem(11,new ItemBuilder(Material.MONSTER_EGG).durability(999).name("&rSnowman Spawn Egg").amount(4).make(),18));
        mobs.add(new IItem(12,new ItemBuilder(Material.MONSTER_EGG).durability(95).name("&rWolf Spawn Egg").amount(5).make(),17));
        mobs.add(new IItem(13,new ItemBuilder(Material.MONSTER_EGG).durability(66).name("&rWitch Spawn Egg").amount(1).make(),16));


        PotionEffect[] warrior = new PotionEffect[]{new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 0), new PotionEffect(PotionEffectType.SPEED, 20 * 8, 0)};
        PotionEffect[] baker = new PotionEffect[]{new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 0), new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 8, 0)};
        PotionEffect[] reaper = new PotionEffect[]{new PotionEffect(PotionEffectType.WITHER, 20 * 6, 2), new PotionEffect(PotionEffectType.SLOW, 20 * 6, 0)};

        ItemStack warriorPot = ItemUtils.buildPotion(warrior, (short) 16450);
        warriorPot.setAmount(3);

        ItemStack reaperPot = ItemUtils.buildPotion(reaper, (short) 16424);
        reaperPot.setAmount(3);

        ItemStack bakerPot = ItemUtils.buildPotion(baker, (short) 16385);
        bakerPot.setAmount(3);



        potions.add(new IItem(0, new ItemStack(Material.BARRIER), 0));
        potions.add(new IItem(1, new ItemStack(Material.APPLE, 8), 2));
        potions.add(new IItem(2, new ItemStack(Material.COOKED_BEEF, 5), 4));
        potions.add(new IItem(3, new ItemStack(Material.GOLDEN_CARROT, 4), 6));
        potions.add(new IItem(4, new ItemStack(Material.GOLDEN_APPLE), 10));
        potions.add(new IItem(5, warriorPot, 15));
        potions.add(new IItem(6, reaperPot, 15));
        potions.add(new IItem(6, bakerPot, 15));
        potions.add(new IItem(7, ItemUtils.buildPotion(PotionEffectType.SPEED, 20 * 16, 2, (short) 8194, 4), 15));
        potions.add(new IItem(8, ItemUtils.buildPotion(PotionEffectType.SLOW, 20 * 8, 2, (short) 16426, 4), 10));
        potions.add(new IItem(9, ItemUtils.buildPotion(PotionEffectType.BLINDNESS, 20 * 7, 3, (short) 16460, 3), 15));
        potions.add(new IItem(10, ItemUtils.buildPotion(PotionEffectType.HARM, 1, 1, (short) 16460, 2), 12));
        potions.add(new IItem(11, ItemUtils.buildPotion(PotionEffectType.POISON, 20 * 6, 2, (short) 16420, 5), 17));
        potions.add(new IItem(12, ItemUtils.buildPotion(PotionEffectType.REGENERATION, 20 * 8, 2, (short) 8193, 3), 18));

    }

    public IItem next(ArrayList<IItem> items, ItemStack itemStack) {
        int index = 0;
        for (IItem potion : items) {
            if (itemStack.isSimilar(potion.item)) {
                return (index == (items.size() - 1) ? items.get(0) : items.get(index + 1));
            }
            index++;
        }
        return null;
    }

    public IItem previous(ArrayList<IItem> items, ItemStack itemStack) {
        int index = 0;
        for (IItem potion : items) {
            if (itemStack.isSimilar(potion.item)) {
                return (index == 0 ? items.get(items.size() - 1) : items.get(index - 1));
            }
            index++;
        }
        return null;
    }
}
