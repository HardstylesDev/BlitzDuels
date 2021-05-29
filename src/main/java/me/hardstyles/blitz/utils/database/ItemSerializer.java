package me.hardstyles.blitz.utils.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.hardstyles.blitz.Core;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ItemSerializer {
    final private Core core;
    public ItemSerializer(Core core){
        this.core =core;
    }

    public String getStringFromItem(ItemStack itemStack) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("material", itemStack.getType().toString());
        if (itemStack.getItemMeta().hasDisplayName()) {
            jsonObject.addProperty("name", itemStack.getItemMeta().getDisplayName());
        }
        jsonObject.addProperty("durability", itemStack.getDurability());
        jsonObject.addProperty("amount", itemStack.getAmount());
        if ((itemStack.getType() + "").toLowerCase().contains("leather")) {
            jsonObject.addProperty("color", ((LeatherArmorMeta) itemStack.getItemMeta()).getColor().asRGB());
        }
        if (itemStack.getType() == Material.POTION) {
            JsonObject effects = new JsonObject();
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            for (PotionEffect customEffect : potionMeta.getCustomEffects()) {
                JsonObject eff = new JsonObject();
                eff.addProperty("type", customEffect.getType().getName());
                eff.addProperty("duration", customEffect.getDuration());
                eff.addProperty("amplifier", customEffect.getAmplifier());
                effects.add(customEffect.getType().getName(), eff);
            }
            jsonObject.add("effects", effects);
        }
        if(!itemStack.getEnchantments().isEmpty()){
            JsonObject ench = new JsonObject();
            itemStack.getEnchantments().forEach((enchantment, integer) -> {
                ench.addProperty(enchantment.getName(), integer);
            });
            jsonObject.add("enchantments", ench);
        }
        return new Gson().toJson(jsonObject);
    }

    public ItemStack getItemFromString(String a){
        JsonObject json = new Gson().fromJson(a, JsonObject.class);
        ItemStack itemStack = new ItemStack(Material.valueOf(json.get("material").getAsString()), json.get("amount").getAsInt());
        ItemMeta itemMeta = itemStack.getItemMeta();

        if(json.has("name")){
            itemMeta.setDisplayName(json.get("name").getAsString());
        }
        itemStack.setItemMeta(itemMeta);
        itemStack.setDurability(json.get("durability").getAsShort());

        if(json.has("enchantments")){
           JsonObject enchantments =  json.get("enchantments").getAsJsonObject();
            for (Enchantment value : Enchantment.values()) {
                if(enchantments.has(value.getName())){
                //    itemStack.addEnchantment(value, enchantments.get(value.getName()).getAsInt(),3);
                    itemMeta.addEnchant(value,enchantments.get(value.getName()).getAsInt(),true);
                }
            }
        }
        itemStack.setItemMeta(itemMeta);
        if(json.has("effects")){
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            potionMeta.clearCustomEffects();
            JsonObject effects =  json.get("effects").getAsJsonObject();
            for (PotionEffectType type : PotionEffectType.values()) {
                if(type == null) continue;
                System.out.println(type.getName());
                if(effects.has(type.getName())){
                    PotionEffect effect = new PotionEffect(type,effects.get(type.getName()).getAsJsonObject().get("duration").getAsInt(),effects.get(type.getName()).getAsJsonObject().get("amplifier").getAsInt());
                    potionMeta.addCustomEffect(effect, false);
                }
            }
            itemStack.setItemMeta(potionMeta);
        }
        if(json.has("color")){
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            armorMeta.setColor(Color.fromRGB(json.get("color").getAsInt()));
            itemStack.setItemMeta(armorMeta);
        }



        return itemStack;

    }
}
