package com.markineo.cminas.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import com.markineo.cminas.Minas;
import com.markineo.cminas.control.MinasManager;
import com.markineo.cminas.util.FileManager;
import com.markineo.cminas.util.ItemManager;

import net.milkbowl.vault.economy.Economy;

public class BasicInventory implements Listener {
	private Minas plugin;
	private FileManager fManager;
	private MinasManager mManager;
	private ItemManager iManager;
	
	private FileConfiguration mainConfig;
	private FileConfiguration itensConfig;
	
	public BasicInventory(Minas plugin, FileManager fManager, MinasManager mManager, ItemManager iManager) {
		this.plugin = plugin;
		this.mManager = mManager;
		this.fManager = fManager;
		this.iManager = iManager;
	}
	
	private Economy economy;
	private OfflinePlayer oPlayer;
	private double playerBalance;
	private int blocksBroken;
	
	private String title;
	private Player player;
	
	@EventHandler
	public void onInventoryOpen(InventoryClickEvent event) {
		if (!(event.getInventory().getHolder() instanceof Player)) return;
		
		mainConfig = fManager.getMainConfig();
		title = mainConfig.getString("upgrade_menu.menu_title").replace("&", "§");
		player = (Player) event.getInventory().getHolder();
		
		Bukkit.getConsoleSender().sendMessage(event.getAction().toString());
		
		if (!event.getInventory().getName().equals(title)) return;
		if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) { event.setCancelled(true); }
		
		ItemStack clickedItem = event.getCurrentItem();
		
		if (clickedItem != null && clickedItem.getType() == Material.WOOL) {
            short data = clickedItem.getDurability();
            
            switch(data) {
	            case 14:
	            	player.closeInventory();
	            	break;
	            	
	            case 5:
	            	player.closeInventory();
	            	
	            	economy = plugin.getEconomy();
	            	oPlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
	            	playerBalance = economy.getBalance(oPlayer);
	            	blocksBroken = mManager.getBlocksBreaks(player.getUniqueId());
	            		
	            	itensConfig = fManager.getItensConfig();
	            	int pLevel = mManager.getPlayerPickaxeLevel(player);
	            	
	            	if ((pLevel + 1) > itensConfig.getInt("pickaxe_data.max_level")) { player.sendMessage(fManager.getMessage("err_messages.9")); return; }	
	        		double precoBase = mainConfig.getDouble("upgrade_menu.preco_base");
	        		int blocosBase = mainConfig.getInt("upgrade_menu.blocks_break_base");
	        		double multipler = mainConfig.getDouble("upgrade_menu.level_multipler");
	        		
	        		double precoFinal = precoBase * Math.pow(multipler, pLevel-1); 
	        		double tBlocosFinal = blocosBase * Math.pow(multipler, pLevel-1);
	        		
	        		if (!(playerBalance >= precoFinal && blocksBroken >= tBlocosFinal)) { player.sendMessage(fManager.getMessage("err_messages.8")); return; }
	        		economy.withdrawPlayer(oPlayer, precoFinal);
	        		mManager.resetBlockBreak(player);
	        		mManager.updatePickaxeLevel(player, pLevel + 1);
	        		if (iManager.havePickaxeItem(player)) {
	        			ItemStack pItem = iManager.getPickaxeItemInInventory(player);
	        			player.getInventory().remove(pItem);
	        			
	        			pItem = iManager.createPickaxeItem(pLevel+1);
	        			player.getInventory().addItem(pItem);
	        		}
	            	
	            	player.sendMessage(fManager.getMessage("minas_messages.4"));
	            	break;
            }
        }
		
		event.setCancelled(true);
		
	}
}
