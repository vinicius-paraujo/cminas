package com.markineo.cminas.events;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import com.markineo.cminas.control.MinasManager;
import com.markineo.cminas.util.FileManager;
import com.markineo.cminas.util.ItemManager;
import com.markineo.cminas.util.PlayerManager;

public class PlayerMove implements Listener {
	private FileManager fManager;
	private ItemManager iManager;
	private MinasManager mManager;
	private PlayerManager pManager;
	
	public PlayerMove(FileManager fManager, ItemManager iManager, MinasManager mManager, PlayerManager pManager) {
		this.fManager = fManager;
		this.iManager = iManager;
		this.mManager = mManager;
		this.pManager = pManager;
	}
	
	private FileConfiguration mainConfig;
	
	private Player player;
	private ItemStack item;
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		player = event.getPlayer();
		
		if (!mManager.isPlayerRegistred(player)) mManager.registerPlayer(player);
        if (!player.hasPermission("celestial.admin") && iManager.havePickaxeItem(player)) {
        	item = iManager.getPickaxeItemInInventory(player);
        	player.getInventory().remove(item);
        }
	}
	
	private Location mLocation;
	private int mId;
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
	    Player player = event.getPlayer();
	    if (!(player.getLocation().getWorld().getName().equals(fManager.getMinasWorld()))) return;
	    if (player.hasPermission("celestial.admin")) return;
	    
	    mainConfig = fManager.getMainConfig();
		mId = pManager.getPlayerMinaId(player);
	    mLocation = fManager.loadLocation(mainConfig, "minas." + mId + ".location");
	    
	    int MAX_DISTANCE = 200; 
	    Location playerLocation = player.getLocation();

	    double distanceX = Math.abs(playerLocation.getX() - mLocation.getX());
	    double distanceY = Math.abs(playerLocation.getY() - mLocation.getY());
	    double distanceZ = Math.abs(playerLocation.getZ() - mLocation.getZ());

	    if (distanceX >= MAX_DISTANCE || distanceY >= MAX_DISTANCE || distanceZ >= MAX_DISTANCE) {
	        if (mLocation != null) {
	            player.teleport(mLocation);
	            player.sendMessage("§eVocê se afastou demais da mina.");
	        }
	    }
	}
	
	@EventHandler
	public void onChangeWorld(PlayerChangedWorldEvent event) {
		player = event.getPlayer();
		
		if (!player.hasPermission("celestial.admin") && iManager.havePickaxeItem(player) && !(player.getLocation().getWorld().getName().equals(fManager.getMinasWorld()))) {
			item = iManager.getPickaxeItemInInventory(player);
			player.getInventory().remove(item);
		}
	}
}
