package me.hardstyles.blitz.kits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hardstyles.blitz.utils.ItemBuilder;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

@Getter
public enum IItem {
    //0
    BLANK(new ItemBuilder(Material.BARRIER).make(), 0),

    //1-8
    LEATHER_HELMET(new ItemBuilder(Material.LEATHER_HELMET).name("&rLeather Helmet").make(), 6),
    GOLD_HELMET(new ItemBuilder(Material.GOLD_HELMET).name("&rGold Helmet").make(), 10),
    CHAIN_HELMET(new ItemBuilder(Material.CHAINMAIL_HELMET).name("&rChain Helmet").make(), 10),
    PALADIN_HELMET(new ItemBuilder(Material.IRON_HELMET).name("&rPaladin's Iron Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).make(), 14),
    PIGMAN_HELMET(new ItemBuilder(Material.GOLD_HELMET).name("&rPigman's Gold Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).make(), 20),
    VIKING_HELMET(new ItemBuilder(Material.IRON_HELMET).name("&rViking's Iron Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3).make(), 24),
    ARCHER_HELMET(new ItemBuilder(Material.DIAMOND_HELMET).name("&rArcher's Diamond Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).make(), 27),
    MEATMASTER_HELMET(new ItemBuilder(Material.DIAMOND_HELMET).name("&rMeatmaster's Diamond Helmet (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).make(), 32),

    //9-14
    LEATHER_CHESTPLATE(new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&rLeather Chestplate").make(), 10),
    GOLDEN_CHESTPLATE(new ItemBuilder(Material.GOLD_CHESTPLATE).name("&rGolden Chestplate").make(), 14),
    CHAIN_CHESTPLATE(new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).name("&rChain Chestplate").make(), 14),
    ARMORER_CHESTPLATE(new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&rArmorer's Leather Chestplate (X)").color(Color.GREEN).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).make(), 21),
    PALADIN_CHESTPLATE(new ItemBuilder(Material.IRON_CHESTPLATE).name("&rPaladin's Iron Chestplate (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).make(), 25),
    CREEPERTAMER_CHESTPLATE(new ItemBuilder(Material.DIAMOND_CHESTPLATE).name("&rCreepertamer's Diamond Chestplate (X)").enchantment(Enchantment.PROTECTION_EXPLOSIONS, 10).make(), 31),

    //15-20
    BIG_BOY_PANTS(new ItemBuilder(Material.LEATHER_LEGGINGS).name("&rLeather Leggings").color(Color.AQUA).enchantment(Enchantment.WATER_WORKER, 10).make(), 7),
    GOLD_LEGGINGS(new ItemBuilder(Material.GOLD_LEGGINGS).name("&rGold Leggings").make(), 11),
    CHAIN_LEGGINGS(new ItemBuilder(Material.CHAINMAIL_LEGGINGS).name("&rChain Leggings").make(), 14),
    ARMORER_LEGGINGS(new ItemBuilder(Material.LEATHER_LEGGINGS).name("&r&rArmorer's Leather Leggings (X)").color(Color.BLUE).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).make(), 18),
    FARMER_LEGGINGS(new ItemBuilder(Material.IRON_LEGGINGS).name("&rFarmer's Iron Leggings (X)").make(), 25),
    TIM_LEGGINGS(new ItemBuilder(Material.DIAMOND_LEGGINGS).name("&rTim's Diamond Leggings (X)").make(), 29),

    //21-27
    GOLD_BOOTS(new ItemBuilder(Material.GOLD_BOOTS).name("&rGold Boots").make(), 7),
    CHAIN_BOOTS(new ItemBuilder(Material.CHAINMAIL_BOOTS).name("&rChain Boots").make(), 7),
    IRON_BOOTS(new ItemBuilder(Material.IRON_BOOTS).name("&rIron Boots").make(), 12),
    MEATMASTER_BOOTS(new ItemBuilder(Material.IRON_BOOTS).name("&rMeatmaster's Iron Boots (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).make(), 14),
    RANGER_BOOTS(new ItemBuilder(Material.DIAMOND_BOOTS).name("&rRanger's Diamond Boots (X)").make(), 21),
    HORSETAMER_BOOTS(new ItemBuilder(Material.DIAMOND_BOOTS).name("&rHorsetamer's Diamond Boots (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2).make(), 26),
    WOLFTAMER_BOOTS(new ItemBuilder(Material.DIAMOND_BOOTS).name("&rWolftamer's Diamond Boots (X)").enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4).make(), 30),

    //28-39
    STONE_AXE(new ItemBuilder(Material.STONE_AXE).name("&rStone Axe").make(), 5),
    WOOD_SWORD(new ItemBuilder(Material.WOOD_SWORD).name("&rWooden Sword").make(), 5),
    IRON_AXE(new ItemBuilder(Material.IRON_AXE).name("&rIron Axe").make(), 9),
    STONE_SWORD(new ItemBuilder(Material.STONE_SWORD).name("&rStone Sword").make(), 9),
    NECROMANCER_SHOVEL(new ItemBuilder(Material.DIAMOND_SPADE).name("&rAstronaut's Diamond Shovel (X)").enchantment(Enchantment.DAMAGE_ALL, 1).make(), 12),
    DIAMOND_AXE(new ItemBuilder(Material.DIAMOND_AXE).name("&rDiamond Axe").make(), 15),
    IRON_SWORD(new ItemBuilder(Material.IRON_SWORD).name("&rIron Sword").enchantment(Enchantment.DURABILITY, 1).make(), 15),
    SPELEOLOGIST_PICKAXE(new ItemBuilder(Material.DIAMOND_PICKAXE).name("&rDiamond Pickaxe").enchantment(Enchantment.DAMAGE_ALL, 1).make(), 15),
    SHARP_STONE_SWORD(new ItemBuilder(Material.STONE_SWORD).name("&rStone Sword").enchantment(Enchantment.DAMAGE_ALL, 1).make(), 15),
    PIGMAN_SWORD(new ItemBuilder(Material.GOLD_SWORD).name("&rPigman's Golden Sword (X)").enchantment(Enchantment.DAMAGE_ALL, 2).make(), 17),
    DIAMOND_SWORD(new ItemBuilder(Material.DIAMOND_SWORD).name("&rDiamond Sword").make(), 30),
    SHARP_DIAMOND_SWORD(new ItemBuilder(Material.DIAMOND_SWORD).name("&rDiamond Sword").enchantment(Enchantment.DAMAGE_ALL, 1).make(), 50),

    //40-43
    ROD(new ItemBuilder(Material.FISHING_ROD).make(), 6),
    EGG(new ItemBuilder(Material.EGG).amount(32).make(), 8),
    SNOWBALL(new ItemBuilder(Material.SNOW_BALL).amount(32).make(), 8),
    FLINT_AND_STEEL(new ItemBuilder(Material.FLINT_AND_STEEL).durability(60).make(), 15),

    //44-47
    BOW(new ItemStack(Material.BOW), 10),
    ARCHER_BOW_V(new ItemBuilder(Material.BOW).name("&rArcher's Bow (V)").enchantment(Enchantment.ARROW_DAMAGE, 1).make(), 10),
    ARCHER_BOW_X(new ItemBuilder(Material.BOW).name("&rArcher's Bow (X)").enchantment(Enchantment.ARROW_DAMAGE, 2).make(), 15),
    RANGER_BOW(new ItemBuilder(Material.BOW).name("&rRanger's Bow (X)").enchantment(Enchantment.ARROW_DAMAGE, 1).enchantment(Enchantment.ARROW_KNOCKBACK).make(), 20),

    //48-50
    ARROW_8(new ItemStack(Material.ARROW, 8), 5),
    ARROW_16(new ItemStack(Material.ARROW, 16), 10),
    ARROW_24(new ItemStack(Material.ARROW, 24), 15),

    //51-63
    SPIDER_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(52).name("&rSpider Spawn Egg").amount(5).make(), 17),
    BLAZE_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(61).name("&rBlaze Spawn Egg").amount(3).make(), 23),
    CREEPER_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(50).name("&rCreeper Spawn Egg").amount(4).make(), 13),
    HORSE_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(100).name("&rHorse Spawn Egg").make(), 10),
    PIG_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(90).name("&rPig Spawn Egg").make(), 10),
    MOOSHROOM_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(96).name("&rMooshroom Spawn Egg").amount(4).make(), 8),
    ZOMBIE_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(54).name("&rZombie Spawn Egg").amount(3).make(), 17),
    SKELETON_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(51).name("&rSkeleton Spawn Egg").amount(3).make(),17),
    MAGMA_CUBE_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(62).name("&rMagma Cube Spawn Egg").amount(4).make(), 14),
    SLIME_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(55).name("&rSlime Spawn Egg").amount(4).make(), 14),
    SNOWMAN_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(999).name("&rSnowman Spawn Egg").amount(4).make(), 18),
    WOLF_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(95).name("&rWolf Spawn Egg").amount(5).make(), 17),
    WITCH_EGG(new ItemBuilder(Material.MONSTER_EGG).durability(66).name("&rWitch Spawn Egg").make(), 16),

    //64-76
    APPLE(new ItemStack(Material.APPLE, 8), 2),
    STEAK(new ItemStack(Material.COOKED_BEEF, 5), 4),
    CARROT(new ItemStack(Material.GOLDEN_CARROT, 4), 6),
    GAPPLE(new ItemStack(Material.GOLDEN_APPLE), 10),
    SLOW_POTS(new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 8, 1)).durability(16426).amount(4).make(), 10),
    HARM_POTS(new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.HARM, 1, 0)).durability(16460).amount(2).make(), 12),
    WARRIOR_POT(new ItemBuilder(Material.POTION).name("&fWarrior Potion").addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 0)).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 8, 0)).durability(16386).amount(3).make(), 15),
    REAPER_POT(new ItemBuilder(Material.POTION).name("&fReaper Potion").addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 6, 2)).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 6, 0)).durability(16424).amount(3).make(), 15),
    BAKER_POT(new ItemBuilder(Material.POTION).name("&fBaker Potion").addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 0)).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 8, 0)).durability(16449).amount(3).make(), 15),
    SPEED_POTS(new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 16, 1)).durability(8226).amount(4).make(), 15),
    BLIND_POTS(new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 7, 2)).durability(16460).amount(3).make(), 15),
    POISON_POTS(new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 6, 1)).durability(16420).amount(5).make(), 17),
    REGEN_POTS(new ItemBuilder(Material.POTION).addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 8, 1)).durability(8193).amount(3).make(), 18);

    private final ItemStack item;
    private final int price;

    IItem(ItemStack itemStack, int price) {
        this.price = price;
        ItemMeta meta = itemStack.getItemMeta();
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        itemStack.setItemMeta(meta);
        this.item = itemStack;
    }

    public static IItem[] getItems(Type type) {
        IItem[] items = new IItem[type.getSize() + 1];
        items[0] = BLANK;

        int index = 1;
        for (int i = type.getIndexStart(); i < type.getIndexEnd(); i++) {
            items[index++] = IItem.values()[i];
        }
        return items;
    }

    public IItem previous(Type type) {
        List<IItem> items = Arrays.asList(type == null ? new IItem[]{} : IItem.getItems(type));
        int index = Math.max(0, items.indexOf(this));
        return (index <= 0 ? items.get(items.size() - 1) : items.get(index - 1));
    }

    public IItem next(Type type) {
        List<IItem> items = Arrays.asList(type == null ? new IItem[]{} : IItem.getItems(type));
        int index = Math.max(0, items.indexOf(this));
        return (index >= items.size() - 1 ? items.get(0) : items.get(index + 1));
    }

    @AllArgsConstructor
    @Getter
    public enum Type {
        HELMET(1, 9),
        CHESTPLATE(9, 15),
        LEGGINGS(15, 21),
        BOOTS(21, 28),
        WEAPON(28, 40),
        PROJECTILE(40, 44),
        BOW(44, 48),
        ARROW(48, 51),
        MOB(51, 64),
        CONSUMABLE(64, 77);

        private final int indexStart, indexEnd;

        public int getSize() {
            return indexEnd - indexStart;
        }
    }
}
