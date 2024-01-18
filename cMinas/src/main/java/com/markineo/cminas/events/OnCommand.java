package com.markineo.cminas.events;

import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.markineo.cminas.util.FileManager;

public class OnCommand implements Listener {
	private FileManager fManager;
	
	public OnCommand(FileManager fManager) {
		this.fManager = fManager;
	}
	
	private FileConfiguration mainConfig;
	private Player player;
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		player = event.getPlayer();
		mainConfig = fManager.getMainConfig();
		
        if (!(player.getLocation().getWorld().getName().equals(fManager.getMinasWorld()))) return;
        if (player.hasPermission("celestial.admin")) return;
        
        String command = event.getMessage().toLowerCase();
        List<String> allowedCommands = mainConfig.getStringList("allowed_commands");

        if (!allowedCommands.contains(command)) {
            player.sendMessage(fManager.getMessage("err_messages.7"));
            event.setCancelled(true);
        }
        
	}
	
}
