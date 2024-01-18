package com.markineo.cminas.util;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemManager {
	private FileManager fManager;
	
	public ItemManager(FileManager fManager) {
		this.fManager = fManager;
	}
	
	private FileConfiguration itensConfig;
	
	private ItemStack item;
	private ItemMeta meta;
	
	public ItemStack createPickaxeItem(int pLevel) {
		itensConfig = fManager.getItensConfig();
		item = new ItemStack(Material.GOLD_PICKAXE, 1);
		
		meta = item.getItemMeta();
		
		int effBase = itensConfig.getInt("pickaxe_data.efficiency_base");
		double multipler = itensConfig.getDouble("pickaxe_data.level_multipler");		
		int effFinal = (int) (effBase * Math.pow(multipler, pLevel-1));
				
		meta.addEnchant(Enchantment.DIG_SPEED, effFinal, true);
		meta.addEnchant(Enchantment.DURABILITY, 999, true);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		int A = pLevel;
		int B = itensConfig.getInt("pickaxe_data.max_level");
		
		int totalCharacters = 10;
		double percentageA = (double) A/B;

		int charactersA = (int) Math.round(percentageA * totalCharacters);
		int charactersB = totalCharacters - charactersA;;

		String color = "§b";

		StringBuilder lineBuilder = new StringBuilder(color);
		for (int i = 0; i < charactersA; i++) {
		    lineBuilder.append("|");
		}
		lineBuilder.append("§7");
		for (int i = 0; i < charactersB; i++) {
		    lineBuilder.append("|");
		}
		lineBuilder.append(color);

		String lLine = lineBuilder.toString();
		
		meta.setDisplayName(itensConfig.getString("pickaxe_data.name").replace("&", "§"));
		List<String> lore = itensConfig.getStringList("pickaxe_data.description")
                .stream()
                .map(line -> line.replace("&", "§")
                		.replace("{level}", lLine))
                .collect(Collectors.toList());
		meta.setLore(lore);
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public boolean isPickaxeItem(ItemStack item) {
		itensConfig = fManager.getItensConfig();
		
		return (item.hasItemMeta() &&
				item.getItemMeta().getDisplayName().equals(itensConfig.getString("pickaxe_data.name")) &&
				item.getItemMeta().getItemFlags().contains(ItemFlag.HIDE_ENCHANTS) &&
				item.getType().equals(Material.GOLD_PICKAXE)
				);
				
	}
	
	public boolean havePickaxeItem(Player player) {
        ItemStack[] inventoryContents = player.getInventory().getContents();

        for (ItemStack itemStack : inventoryContents) {
            if (itemStack != null && isPickaxeItem(itemStack)) {
                return true;
            }
        }

        return false;	    
	}
	
	public ItemStack getPickaxeItemInInventory(Player player) {
		ItemStack[] inventoryContents = player.getInventory().getContents();

        for (ItemStack itemStack : inventoryContents) {
            if (itemStack != null && isPickaxeItem(itemStack)) {
                return itemStack;
            }
        }

        return null;	
	}
	
}
