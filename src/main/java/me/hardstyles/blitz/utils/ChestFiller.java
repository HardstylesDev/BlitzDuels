package me.hardstyles.blitz.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ChestFiller {
    private final Random r = new Random();
    private final HashMap<ItemStack, Integer> lootTable = new HashMap<>();
    private final int totalChance;

    public ChestFiller() {
        lootTable.put(new ItemStack(Material.WOOD_AXE, 1), 30);
        lootTable.put(new ItemStack(Material.STONE_AXE, 1), 20);
        lootTable.put(new ItemStack(Material.GOLD_SWORD, 1), 18);
        lootTable.put(new ItemStack(Material.WOOD_SWORD, 1), 17);
        lootTable.put(new ItemStack(Material.STONE_SWORD, 1), 10);

        lootTable.put(new ItemStack(Material.BOW, 1), 10);
        lootTable.put(new ItemStack(Material.ARROW, 12), 7);
        lootTable.put(new ItemStack(Material.ARROW, 16), 4);

        lootTable.put(new ItemStack(Material.FISHING_ROD, 1), 15);

        //ARMOUR
        lootTable.put(new ItemStack(Material.LEATHER_BOOTS, 1), 45);

        lootTable.put(new ItemBuilder(Material.LEATHER_LEGGINGS).name("&rBig Boy Pants").color(Color.fromRGB(0,255,255)).enchantment(Enchantment.WATER_WORKER, 10).make(), 20);
        lootTable.put(new ItemBuilder(Material.LEATHER_LEGGINGS).name("&rSexy Pants").color(Color.fromRGB(125,0,255)).enchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1).make(), 15);

        lootTable.put(new ItemStack(Material.LEATHER_CHESTPLATE, 1), 25);
        lootTable.put(new ItemStack(Material.LEATHER_HELMET, 1), 45);

        lootTable.put(new ItemStack(Material.GOLD_BOOTS, 1), 15);
        lootTable.put(new ItemStack(Material.GOLD_LEGGINGS, 1), 10);
        lootTable.put(new ItemStack(Material.GOLD_CHESTPLATE, 1), 7);
        lootTable.put(new ItemStack(Material.GOLD_HELMET, 1), 13);

        lootTable.put(new ItemStack(Material.CHAINMAIL_BOOTS, 1), 18);
        lootTable.put(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1), 8);
        lootTable.put(new ItemStack(Material.CHAINMAIL_HELMET, 1), 13);

        lootTable.put(new ItemStack(Material.IRON_BOOTS, 1), 9);
        lootTable.put(new ItemStack(Material.IRON_CHESTPLATE, 1), 3);
        lootTable.put(new ItemStack(Material.IRON_HELMET, 1), 10);

        //FOOD
        lootTable.put(new ItemStack(Material.GOLDEN_APPLE, 1), 1);

        lootTable.put(new ItemStack(Material.COOKED_FISH, 8), 15);
        lootTable.put(new ItemStack(Material.RAW_BEEF, 3), 20);
        lootTable.put(new ItemStack(Material.APPLE, 3), 20);
        lootTable.put(new ItemStack(Material.PORK, 3), 20);
        lootTable.put(new ItemStack(Material.RAW_FISH, 3), 20);
        lootTable.put(new ItemStack(Material.RAW_CHICKEN, 1), 10);
        lootTable.put(new ItemStack(Material.WHEAT, 3), 10);
        lootTable.put(new ItemStack(Material.ROTTEN_FLESH, 3), 9);





        //MISC
        lootTable.put(new ItemStack(Material.IRON_INGOT, 1), 4);
        lootTable.put(new ItemStack(Material.GOLD_INGOT, 8), 4);
        lootTable.put(new ItemStack(Material.DIAMOND, 1), 2);
        lootTable.put(new ItemStack(Material.STICK, 3), 15);
        lootTable.put(new ItemStack(Material.BOWL, 1), 8);

        lootTable.put(new ItemStack(Material.EXP_BOTTLE, 1), 10);
        lootTable.put(new ItemStack(Material.EXP_BOTTLE, 2), 5);

        int c = 0;
        for(int i : lootTable.values()) {
            c += i;
        }
        totalChance = c;
    }

    public void generateChestLoot(Inventory inventory, int minAmount) {
        int amt = r.nextInt(2) + minAmount;
        List<ItemStack> selectedItems = new ArrayList<>();
        for(int a = 0; a <= amt; a++) {
            int index = r.nextInt(totalChance);
            int counter = 0;
            for(ItemStack is : lootTable.keySet())
                if(lootTable.get(is) + counter > index) {
                    if(selectedItems.contains(is)) {
                        a--;
                        break;
                    }
                    selectedItems.add(is);
                    break;
                }else
                    counter += lootTable.get(is);
        }
        for(ItemStack is : selectedItems) {
            int random = r.nextInt(inventory.getSize());
            while(inventory.getItem(random) != null)
                random = r.nextInt(inventory.getSize());
            inventory.setItem(random, is);
        }
    }
}
