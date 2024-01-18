package com.markineo.cminas.events;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.markineo.cminas.Minas;
import com.markineo.cminas.control.MinasManager;
import com.markineo.cminas.util.FileManager;
import com.markineo.cminas.util.ItemManager;

import net.milkbowl.vault.economy.Economy;

public class PlayerInteract implements Listener {
	private Minas plugin;
	private FileManager fManager;
	private ItemManager iManager;
	private MinasManager mManager;
	
	public PlayerInteract(FileManager fManager, ItemManager iManager, MinasManager mManager, Minas plugin) {
		this.fManager = fManager;
		this.iManager = iManager;
		this.mManager = mManager;
		this.plugin = plugin;
	}
	
	private Economy economy;
	private OfflinePlayer oPlayer;
	private double playerBalance;
	private int blocksBroken;
	private FileConfiguration mainConfig;
	
	private Player player;

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR) && !(event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
		if (!iManager.isPickaxeItem(event.getItem())) return;
		
		player = event.getPlayer();
		int rows = 3;
		
		mainConfig = fManager.getMainConfig();
		String title = mainConfig.getString("upgrade_menu.menu_title");
		
		Inventory inventory = Bukkit.createInventory(player, rows * 9, title.replace("&", "§"));
		
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(player.getName());
		meta.setDisplayName(mainConfig.getString("upgrade_menu.title").replace("&", "§").replace("{player}", player.getName()));
		
		int pLevel = mManager.getPlayerPickaxeLevel(player);
		double precoBase = mainConfig.getDouble("upgrade_menu.preco_base");
		int blocosBase = mainConfig.getInt("upgrade_menu.blocks_break_base");
		double multipler = mainConfig.getDouble("upgrade_menu.level_multipler");
		
		int precoFinal = (int) (precoBase * Math.pow(multipler, pLevel-1)); 
		int tBlocosFinal = (int) (blocosBase * Math.pow(multipler, pLevel-1));
		
		economy = plugin.getEconomy();
    	oPlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
    	playerBalance = economy.getBalance(oPlayer);
    	blocksBroken = mManager.getBlocksBreaks(player.getUniqueId());
		
		List<String> lore = mainConfig.getStringList("upgrade_menu.description")
				.stream()
                .map(line -> line.replace("&", "§")
                		.replace("{preço}", "" + formatarPreco(precoFinal))
                		.replace("{blocos}", "" + tBlocosFinal)
                		.replace("{status}", !(playerBalance >= precoFinal && blocksBroken >= tBlocosFinal)?"§cVocê ainda não pode evoluir...":"§aVocê já pode evoluir!")
                		)
                .collect(Collectors.toList());;
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		inventory.setItem(13, item);
		
		item = new ItemStack(Material.WOOL, 1, (short) 5);
		ItemMeta meta2 = item.getItemMeta();
		meta2.setDisplayName("§a§lCONFIRMAR");
		lore = Arrays.asList(
        		"§f ",
        		"§eClique com esquerdo para confirmar."
        );
		meta2.setLore(lore);
		item.setItemMeta(meta2);
		
		inventory.setItem(7 + (9), item);
		
		item = new ItemStack(Material.WOOL, 1, (short) 14);
		meta2 = item.getItemMeta();
		meta2.setDisplayName("§4§lCANCELAR");
		lore = Arrays.asList(
        		"§f ",
        		"§cClique com esquerdo para cancelar."
        );
		meta2.setLore(lore);
		item.setItemMeta(meta2);
		inventory.setItem(10, item);
		
		player.openInventory(inventory);
	}
	
	public String formatarPreco(double preco) {
	    String[] sufixos = {"", "K", "M", "B", "T"};
	    int indice = 0;

	    while (preco >= 1000 && indice < sufixos.length - 1) {
	        preco /= 1000.0;
	        indice++;
	    }

	    return String.format("%.0f%s", preco, sufixos[indice]);
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		player = event.getPlayer();
		
		if (!(player.getLocation().getWorld().getName().equals(fManager.getMinasWorld()))) return;
		if (player.hasPermission("celestial.admin")) return;
		
		event.setCancelled(true);
	}
	
}