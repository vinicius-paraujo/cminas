package com.markineo.cminas.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import com.markineo.cminas.Minas;
import com.markineo.cminas.control.MinasManager;
import com.markineo.cminas.util.FileManager;
import com.markineo.cminas.util.PlayerManager;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;

import net.milkbowl.vault.economy.Economy;
import xyz.tozymc.spigot.api.title.TitleApi;

public class BlockBreak implements Listener {
	private Minas plugin;
	private FileManager fManager;
	private PlayerManager pManager;
	private MinasManager mManager;

	public BlockBreak(Minas plugin, FileManager fManager, PlayerManager pManager, MinasManager mManager) {
		this.plugin = plugin;
		this.fManager = fManager;
		this.pManager = pManager;
		this.mManager = mManager;
	}
	
	private FileConfiguration mainConfig;
	private FileConfiguration itensConfig;
	
	private Economy economy;
	
	private Player player;
	private OfflinePlayer oPlayer;
	private String playerMina;
	private double playerBonus;
	
	private Material blockMaterial;
	private Block blockBroken;
	
	private ItemStack tItem;
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		player = event.getPlayer();
		
		if (!(player.getLocation().getWorld().getName().equals(fManager.getMinasWorld()))) return;
		if (event.isCancelled()) return;
		
		WorldGuardPlugin worldGuard = WorldGuardPlugin.inst();
		if (!worldGuard.canBuild(player, event.getBlock())) { event.setCancelled(true); return; }
		
		mainConfig = fManager.getMainConfig();
		ConfigurationSection blocks = mainConfig.getConfigurationSection("blocks");
		
		for (String key : blocks.getKeys(false)) {
			economy = plugin.getEconomy();
			oPlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
			
			blockBroken = event.getBlock();
			blockMaterial = blockBroken.getType();
			
			ConfigurationSection bData = blocks.getConfigurationSection(key);;
			Material bMaterial = Material.valueOf(bData.getString("material"));
			
			if (bMaterial.equals(blockMaterial)) {
				event.setCancelled(true);
				blockBroken.setType(Material.AIR);
				
				int blockBreak = mManager.getBlocksBreaks(player.getUniqueId());
				mManager.updateBlockBreak(player, blockBreak+1);
				
				itensConfig = fManager.getItensConfig();
				
				int itemId = getItemIdByPercentage(itensConfig.getConfigurationSection("itens"));
				if (itemId >= 1) giveTeasureItem(itemId);
				
	            double blockPrice = blocks.getDouble(key + ".price");
	            playerBonus = pManager.getPlayerBonus(player);
	           
	            int finalPrice = (int) (blockPrice + (blockPrice * playerBonus));
	            economy.depositPlayer(oPlayer, finalPrice);
	            
	            String msg = fManager.getMessage("action_bar_messages.block_break").replace("{valor}", "" + finalPrice).replace("{bonus}", "" + playerBonus);
	            TitleApi.sendActionbar(player, msg);
	            
	            break;
	        }
		}
	}
	
	private void giveTeasureItem(int id) {
		tItem = fManager.loadItem(itensConfig, "itens." + id + ".data");
		player.getInventory().addItem(tItem);
	}
	
	private int getItemIdByPercentage(ConfigurationSection itemsSection) {
	    if (itemsSection == null) return 0;

	    List<String> itemKeys = new ArrayList<>(itemsSection.getKeys(false));
	    Collections.shuffle(itemKeys);

	    double randomValue = Math.random();
	    double cumulativeProbability = 0.0;

	    for (String key : itemKeys) {
	        ConfigurationSection itemData = itemsSection.getConfigurationSection(key);
	        if (itemData != null) {
	            double probability = itemData.getDouble("porcentagem");

	            cumulativeProbability += probability;

	            if (randomValue <= cumulativeProbability) {
	                return Integer.parseInt(key);
	            }
	        }
	    }

	    return 0;
	}

}
