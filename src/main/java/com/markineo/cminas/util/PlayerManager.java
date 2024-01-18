package com.markineo.cminas.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class PlayerManager {
	private FileManager fManager;
	
	public PlayerManager(FileManager fManager) {
		this.fManager = fManager;
	}
	
	private FileConfiguration mConfig;
	
	public double getPlayerBonus(Player player) {
		double playerBonus = 0.0;
	    mConfig = fManager.getMainConfig();

	    for (String key : mConfig.getConfigurationSection("bonus").getKeys(false)) {
	        String permission = mConfig.getString("bonus." + key + ".permission");

	        if (player.hasPermission(permission)) {
	            playerBonus = mConfig.getDouble("bonus." + key + ".bonus");
	            break;
	        }
	    }

	    return playerBonus;
	}
	
	public int getPlayerMinaId(Player player) {
		int minaId = -1;
		
		mConfig = fManager.getMainConfig();

	    for (String key : mConfig.getConfigurationSection("minas").getKeys(false)) {
	        String permission = mConfig.getString("minas." + key + ".permission");

	        if (player.hasPermission(permission)) {
	            minaId = Integer.parseInt(key);
	            break;
	        }
	    }
		
		return minaId;
	}
	
	
}
