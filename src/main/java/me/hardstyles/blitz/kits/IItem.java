package me.hardstyles.blitz.kits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public enum IItem {
    BLANK(Type.BLANK, new ItemBuilder(Material.BARRIER).make(), 0),

    LEATHER_HELMET(Type.HELMET, new ItemBuilder(Material.LEATHER_HELMET).name("&rLeather Helmet").make(), 6),
    GOLD_HELMET(Type.HELMET, new ItemBuilder(Material.GOLD_HELMET).name("&rGold Helmet").make(), 10),
    CHAIN_HELMET(Type.HELMET, new ItemBuilder(Material.CHAINMAIL_HELMET).name("&rChain Helmet").make(), 10),
    ARMORER_HELMET(Type.HELMET, new ItemBuilder(Material.LEATHER_HELMET).name("&rArmorer's Leather Helmet &7(X)").color(Color.YELLOW).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.PROTECTION_PROJECTILE, 10).make(), 14),
    PALADIN_HELMET(Type.HELMET, new ItemBuilder(Material.IRON_HELMET).name("&rPaladin's Iron Helmet &7(X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).make(), 15),
    PIGMAN_HELMET(Type.HELMET, new ItemBuilder(Material.GOLD_HELMET).name("&rPigman's Gold Helmet &7(X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).make(), 18),
    VIKING_HELMET(Type.HELMET, new ItemBuilder(Material.IRON_HELMET).name("&rViking's Iron Helmet &7(X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).make(), 21),
    ARCHER_HELMET(Type.HELMET, new ItemBuilder(Material.DIAMOND_HELMET).name("&rArcher's Diamond Helmet &7(X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).make(), 26),
    MEATMASTER_HELMET(Type.HELMET, new ItemBuilder(Material.DIAMOND_HELMET).name("&rMeatmaster's Diamond Helmet &7(X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).make(), 30),

    LEATHER_CHESTPLATE(Type.CHESTPLATE, new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&rLeather Chestplate").make(), 10),
    GOLDEN_CHESTPLATE(Type.CHESTPLATE, new ItemBuilder(Material.GOLD_CHESTPLATE).name("&rGolden Chestplate").make(), 14),
    CHAIN_CHESTPLATE(Type.CHESTPLATE, new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).name("&rChain Chestplate").make(), 14),
    PALADIN_CHESTPLATE(Type.CHESTPLATE, new ItemBuilder(Material.IRON_CHESTPLATE).name("&rPaladin's Iron Chestplate &7(X)").make(), 18),
    ARMORER_CHESTPLATE(Type.CHESTPLATE, new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&rArmorer's Leather Chestplate &7(X)").color(Color.GREEN).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).enchantment(Enchantment.PROTECTION_EXPLOSIONS, 10).make(), 21),
    PALADIN_CHESTPLATE_P2(Type.CHESTPLATE, new ItemBuilder(Material.IRON_CHESTPLATE).name("&6Paladin's Iron Chestplate (⭐⭐)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).make(), 25),
    CREEPERTAMER_CHESTPLATE(Type.CHESTPLATE, new ItemBuilder(Material.DIAMOND_CHESTPLATE).name("&rCreepertamer's Diamond Chestplate &7(X)").enchantment(Enchantment.PROTECTION_EXPLOSIONS, 10).make(), 31),

    BIG_BOY_PANTS(Type.LEGGINGS, new ItemBuilder(Material.LEATHER_LEGGINGS).name("&rLeather Leggings").color(Color.AQUA).enchantment(Enchantment.WATER_WORKER, 10).make(), 7),
    GOLD_LEGGINGS(Type.LEGGINGS, new ItemBuilder(Material.GOLD_LEGGINGS).name("&rGold Leggings").make(), 11),
    CHAIN_LEGGINGS(Type.LEGGINGS, new ItemBuilder(Material.CHAINMAIL_LEGGINGS).name("&rChain Leggings").make(), 14),
    FARMER_LEGGINGS(Type.LEGGINGS, new ItemBuilder(Material.IRON_LEGGINGS).name("&rFarmer's Iron Leggings &7(X)").make(), 16),
    FLORIST_LEGGINGS(Type.LEGGINGS, new ItemBuilder(Material.CHAINMAIL_LEGGINGS).name("&rFlorist's Chain Leggings &7(X)").enchantment(Enchantment.THORNS, 2).enchantment(Enchantment.DURABILITY, 10).make(), 18),
    REAPER_LEGGINGS(Type.LEGGINGS, new ItemBuilder(Material.CHAINMAIL_LEGGINGS).name("&rReaper's Chain Leggings &7(X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).make(), 18),
    ARMORER_LEGGINGS(Type.LEGGINGS, new ItemBuilder(Material.LEATHER_LEGGINGS).name("&rArmorer's Leather Leggings &7(X)").color(Color.BLUE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).enchantment(Enchantment.PROTECTION_FIRE, 10).make(), 23),
    TIM_LEGGINGS(Type.LEGGINGS, new ItemBuilder(Material.DIAMOND_LEGGINGS).name("&rTim's Diamond Leggings &7(X)").make(), 26),

    GOLD_BOOTS(Type.BOOTS, new ItemBuilder(Material.GOLD_BOOTS).name("&rGold Boots").make(), 7),
    CHAIN_BOOTS(Type.BOOTS, new ItemBuilder(Material.CHAINMAIL_BOOTS).name("&rChain Boots").make(), 7),
    IRON_BOOTS(Type.BOOTS, new ItemBuilder(Material.IRON_BOOTS).name("&rIron Boots").make(), 10),
    ARMORER_BOOTS(Type.BOOTS, new ItemBuilder(Material.LEATHER_BOOTS).name("&rArmorer's Leather Boots &7(X)").color(Color.WHITE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchantment(Enchantment.PROTECTION_FALL, 4).make(), 13),
    MEATMASTER_BOOTS(Type.BOOTS, new ItemBuilder(Material.IRON_BOOTS).name("&rMeatmaster's Iron Boots &7(X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).make(), 14),
    FLORIST_BOOTS(Type.BOOTS, new ItemBuilder(Material.IRON_BOOTS).name("&rFlorist's Iron Boots &7(X)").enchantment(Enchantment.THORNS, 2).enchantment(Enchantment.DURABILITY, 10).make(), 18),
    RANGER_BOOTS(Type.BOOTS, new ItemBuilder(Material.DIAMOND_BOOTS).name("&rRanger's Diamond Boots &7(X)").make(), 20),
    HORSETAMER_BOOTS(Type.BOOTS, new ItemBuilder(Material.DIAMOND_BOOTS).name("&rHorsetamer's Diamond Boots &7(X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).make(), 25),
    WOLFTAMER_BOOTS(Type.BOOTS, new ItemBuilder(Material.DIAMOND_BOOTS).name("&rWolftamer's Diamond Boots &7(X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).make(), 30),

    STONE_AXE(Type.WEAPON, new ItemBuilder(Material.STONE_AXE).name("&rStone Axe").make(), 5),
    WOOD_SWORD(Type.WEAPON, new ItemBuilder(Material.WOOD_SWORD).name("&rWooden Sword").make(), 5),
    IRON_AXE(Type.WEAPON, new ItemBuilder(Material.IRON_AXE).name("&rIron Axe").make(), 9),
    STONE_SWORD(Type.WEAPON, new ItemBuilder(Material.STONE_SWORD).name("&rStone Sword").make(), 9),
    NECROMANCER_SHOVEL(Type.WEAPON, new ItemBuilder(Material.DIAMOND_SPADE).name("&rNecromancer's Diamond Shovel &7(X)").enchantment(Enchantment.DAMAGE_ALL, 1).make(), 12),
    DIAMOND_AXE(Type.WEAPON, new ItemBuilder(Material.DIAMOND_AXE).name("&rDiamond Axe").make(), 15),
    IRON_SWORD(Type.WEAPON, new ItemBuilder(Material.IRON_SWORD).name("&rIron Sword").enchantment(Enchantment.DURABILITY, 1).make(), 15),
    SPELEOLOGIST_PICKAXE(Type.WEAPON, new ItemBuilder(Material.DIAMOND_PICKAXE).name("&rDiamond Pickaxe").enchantment(Enchantment.DAMAGE_ALL, 1).make(), 15),
    SHARP_STONE_SWORD(Type.WEAPON, new ItemBuilder(Material.STONE_SWORD).name("&rStone Sword").enchantment(Enchantment.DAMAGE_ALL, 1).make(), 15),
    REAPER_SCYTHE(Type.WEAPON, new ItemBuilder(Material.DIAMOND_HOE).name("&rReaper's Scythe &7(X)").enchantment(Enchantment.DAMAGE_ALL, 5).make(), 15),
    PIGMAN_SWORD(Type.WEAPON, new ItemBuilder(Material.GOLD_SWORD).name("&rPigman's Golden Sword &7(X)").enchantment(Enchantment.DAMAGE_ALL, 2).enchantment(Enchantment.DURABILITY, 10).make(), 17),
    DIAMOND_SWORD(Type.WEAPON, new ItemBuilder(Material.DIAMOND_SWORD).name("&rDiamond Sword").make(), 30),
    SHARP_DIAMOND_SWORD(Type.WEAPON, new ItemBuilder(Material.DIAMOND_SWORD).name("&rDiamond Sword").enchantment(Enchantment.DAMAGE_ALL, 1).make(), 50),

    ROD(Type.PROJECTILE, new ItemBuilder(Material.FISHING_ROD).make(), 6),
    EGG(Type.PROJECTILE, new ItemBuilder(Material.EGG).amount(32).make(), 8),
    SNOWBALL(Type.PROJECTILE, new ItemBuilder(Material.SNOW_BALL).amount(32).make(), 8),
    FLINT_AND_STEEL(Type.PROJECTILE, new ItemBuilder(Material.FLINT_AND_STEEL).make(), 15),
    FISHERMAN_ROD(Type.PROJECTILE, new ItemBuilder(Material.FISHING_ROD).enchantment(Enchantment.DAMAGE_ALL, 5).enchantment(Enchantment.LUCK, 3).enchantment(Enchantment.DURABILITY, 1).make(), 20),

    BOW(Type.BOW, new ItemStack(Material.BOW), 10),
    ARCHER_BOW_V(Type.BOW, new ItemBuilder(Material.BOW).name("&rArcher's Bow &7(V)").enchantment(Enchantment.ARROW_DAMAGE, 1).make(), 10),
    ARCHER_BOW_X(Type.BOW, new ItemBuilder(Material.BOW).name("&rArcher's Bow &7(X)").enchantment(Enchantment.ARROW_DAMAGE, 2).make(), 15),
    RANGER_BOW(Type.BOW, new ItemBuilder(Material.BOW).name("&rRanger's Bow &7(X)").enchantment(Enchantment.ARROW_DAMAGE, 1).enchantment(Enchantment.ARROW_KNOCKBACK).make(), 20),

    ARROW_8(Type.ARROW, new ItemStack(Material.ARROW, 8), 5),
    ARROW_16(Type.ARROW, new ItemStack(Material.ARROW, 16), 10),
    ARROW_24(Type.ARROW, new ItemStack(Material.ARROW, 24), 15),

    SPIDER_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(52).name("&rSpider Spawn Egg").amount(5).make(), 17),
    BLAZE_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(61).name("&rBlaze Spawn Egg").amount(3).make(), 23),
    CREEPER_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(50).name("&rCreeper Spawn Egg").amount(4).make(), 13),
    HORSE_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(100).name("&rHorse Spawn Egg").make(), 10),
    PIG_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(90).name("&rPig Spawn Egg").make(), 10),
    MOOSHROOM_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(96).name("&rMooshroom Spawn Egg").amount(4).make(), 8),
    ZOMBIE_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(54).name("&rZombie Spawn Egg").amount(3).make(), 17),
    SKELETON_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(51).name("&rSkeleton Spawn Egg").amount(3).make(),17),
    MAGMA_CUBE_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(62).name("&rMagma Cube Spawn Egg").amount(4).make(), 14),
    SLIME_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(55).name("&rSlime Spawn Egg").amount(4).make(), 14),
    SNOWMAN_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(999).name("&rSnowman Spawn Egg").amount(4).make(), 18),
    WOLF_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(95).name("&rWolf Spawn Egg").amount(5).make(), 17),
    WITCH_EGG(Type.MOB, new ItemBuilder(Material.MONSTER_EGG).durability(66).name("&rWitch Spawn Egg").make(), 16),

    APPLE(Type.CONSUMABLE, new ItemStack(Material.APPLE, 8), 2),
    STEAK(Type.CONSUMABLE, new ItemStack(Material.COOKED_BEEF, 5), 4),
    CARROT(Type.CONSUMABLE, new ItemStack(Material.GOLDEN_CARROT, 4), 6),
    GAPPLE(Type.CONSUMABLE, new ItemStack(Material.GOLDEN_APPLE), 10),
    TNT(Type.CONSUMABLE, new ItemStack(Material.TNT), 10),
    SLOW_POTS(Type.CONSUMABLE, new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 8, 1)).durability(16426).amount(4).make(), 10),
    HARM_POTS(Type.CONSUMABLE, new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 0)).durability(16460).amount(2).make(), 12),
    WARRIOR_POT(Type.CONSUMABLE, new ItemBuilder(Material.POTION).name("&fWarrior Potion").addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 0)).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 8, 0)).durability(16386).amount(3).make(), 15),
    REAPER_POT(Type.CONSUMABLE, new ItemBuilder(Material.POTION).name("&fReaper Potion").addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 6, 2)).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 6, 0)).durability(16424).amount(3).make(), 15),
    BAKER_POT(Type.CONSUMABLE, new ItemBuilder(Material.POTION).name("&fBaker Potion").addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 0)).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 8, 0)).durability(16449).amount(3).make(), 15),
    SPEED_POTS(Type.CONSUMABLE, new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 16, 1)).durability(8226).amount(4).make(), 15),
    BLIND_POTS(Type.CONSUMABLE, new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 7, 2)).durability(16460).amount(3).make(), 15),
    POISON_POTS(Type.CONSUMABLE, new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 6, 1)).durability(16420).amount(5).make(), 17),
    REGEN_POTS(Type.CONSUMABLE, new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 1)).durability(8193).amount(3).make(), 18)
    ;

    private final Type type;
    private final ItemStack item;
    private final int price;

    public static List<IItem> getItems(Type type) {
        List<IItem> items = new ArrayList<>();
        items.add(BLANK);
        for (IItem item : values()) {
            if (item.getType() == type) {
                items.add(item);
            }
        }
        return items;
    }

    public IItem previous(Type type) {
        List<IItem> items = IItem.getItems(type);
        int index = Math.max(0, items.indexOf(this));
        return (index <= 0 ? items.get(items.size() - 1) : items.get(index - 1));
    }

    public IItem next(Type type) {
        List<IItem> items = IItem.getItems(type);
        int index = Math.max(0, items.indexOf(this));
        return (index >= items.size() - 1 ? items.get(0) : items.get(index + 1));
    }

    public enum Type {
        BLANK,
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        WEAPON,
        PROJECTILE,
        BOW,
        ARROW,
        MOB,
        CONSUMABLE
    }
}
