package com.markineo.cminas.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.markineo.cminas.Minas;

public class FileManager {
	private final Minas plugin;
	
	private FileConfiguration mainConfig;
	private FileConfiguration messagesConfig;
	private FileConfiguration itensConfig;
	private FileConfiguration dbConfig;
	
	private boolean isLoaded;
	
	public FileManager(Minas plugin) {
		this.plugin = plugin;
	}
	
	public FileConfiguration getConfig(String fileName) {
		File configFile = new File(plugin.getDataFolder(), fileName);
		
		if (!configFile.exists()) {
			plugin.saveResource(fileName, false);
		}
		
		FileConfiguration config = new YamlConfiguration();
		
		try {
			config.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		return config;
	}
	
	private void loadConfigurations() {
	    if (!isLoaded) {
	        isLoaded = true;
	        messagesConfig = getConfig("messages.yml");
	        mainConfig = getConfig("config.yml");
	        itensConfig = getConfig("itens.yml");
	        dbConfig = getConfig("database.yml");
	    }
	}
	
	public void setConfig(FileConfiguration file, String fileName, String config, Object value) {
        file.set(config, value);
        saveConfig(file, fileName);
    }

    private void saveConfig(FileConfiguration config, String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ItemStack loadItem(FileConfiguration config, String path) {
    	if (!config.contains(path)) return null;
        
    	try {
    		return ItemStack.deserialize(config.getConfigurationSection(path).getValues(false));
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
    
    public Location loadLocation(FileConfiguration config, String path) {
        if (!config.contains(path)) return null;
        
    	try {
    		return Location.deserialize(config.getConfigurationSection(path).getValues(false));
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
	
	public void reloadConfigurations() {
		messagesConfig = null;
        mainConfig = null;
        itensConfig = null;
        dbConfig = null;
        
	    messagesConfig = getConfig("messages.yml");
        mainConfig = getConfig("config.yml");
        itensConfig = getConfig("itens.yml");
        dbConfig = getConfig("database.yml");
	}
	
	public FileConfiguration getDatabaseConfig() {
		loadConfigurations();
		return dbConfig;
	}

	public FileConfiguration getMessagesConfig() {
	    loadConfigurations();
	    return messagesConfig;
	}
	
	public FileConfiguration getItensConfig() {
	    loadConfigurations();
	    return itensConfig;
	}

	public FileConfiguration getMainConfig() {
	    loadConfigurations();
	    return mainConfig;
	}
	
	public String getMinasWorld() {
		loadConfigurations();
		
		return mainConfig.getString("minas_world");
	}
	
	public String getMessage(String message) {
		if (messagesConfig == null) messagesConfig = getConfig("messages.yml");
		
		return messagesConfig.getString(message).replace("&","§").replace("{linha}", "§f§n                                                                            \n§f \n");
	}
}
