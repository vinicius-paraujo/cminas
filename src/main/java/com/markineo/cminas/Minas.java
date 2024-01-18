package com.markineo.cminas;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.markineo.cminas.commands.CMinasCommand;
import com.markineo.cminas.commands.MinaCommand;
import com.markineo.cminas.commands.MinasAdminCommand;
import com.markineo.cminas.commands.SetMinaCommand;
import com.markineo.cminas.control.MinasManager;
import com.markineo.cminas.events.BasicInventory;
import com.markineo.cminas.events.BlockBreak;
import com.markineo.cminas.events.OnCommand;
import com.markineo.cminas.events.PlayerInteract;
import com.markineo.cminas.events.PlayerMove;
import com.markineo.cminas.util.CommandManager;
import com.markineo.cminas.util.DatabaseManager;
import com.markineo.cminas.util.FileManager;
import com.markineo.cminas.util.ItemManager;
import com.markineo.cminas.util.PlayerManager;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Minas extends JavaPlugin {
	private FileManager fManager;
	private PlayerManager pManager;
	private ItemManager iManager;
	private MinasManager mManager;
	
	private Chat chat;
	private Economy econ;
	private Permission perms;
	
	private FileConfiguration messagesConfig;
	private FileConfiguration mainConfig;
	private FileConfiguration permissionsConfig;
	private FileConfiguration sqlConfig;
	
	private Connection conn;
	private int port;
	private String database;
	private String host;
	private String user;
	private String password;
	private String url;
	
	@Override
	public void onEnable() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		
		fManager = new FileManager(this);
		pManager = new PlayerManager(fManager);
		iManager = new ItemManager(fManager);
		mManager = new MinasManager(fManager);
		
		messagesConfig = fManager.getConfig("messages.yml");
		mainConfig = fManager.getConfig("config.yml");
		permissionsConfig = fManager.getConfig("permissions.yml");
		sqlConfig = fManager.getConfig("database.yml");
		
		this.setupChat();
		this.setupEconomy();
		this.setupPermissions();
		this.setupDatabase();
		
		mManager.updateBlockBreakDatabase();
		
		// commands & events
		CommandManager.registerCommands(this, new MinasAdminCommand(fManager), new SetMinaCommand(fManager), new MinaCommand(fManager, pManager, iManager, mManager), new CMinasCommand(fManager));
		//getServer().getPluginManager().registerEvents(new onPlayerJoin(this, fManager, protocolManager), this);
		//getServer().getPluginManager().registerEvents(new BasicInventory(fManager), this);
		//getServer().getPluginManager().registerEvents(new SpawnTeleport(fManager), this);
		getServer().getPluginManager().registerEvents(new BlockBreak(this, fManager, pManager, mManager), this);
		getServer().getPluginManager().registerEvents(new PlayerMove(fManager, iManager, mManager, pManager), this);
		getServer().getPluginManager().registerEvents(new PlayerInteract(fManager, iManager, mManager, this), this);
		getServer().getPluginManager().registerEvents(new OnCommand(fManager), this);
		getServer().getPluginManager().registerEvents(new BasicInventory(this, fManager, mManager, iManager), this);
		
		Bukkit.getConsoleSender().sendMessage("§7[§bcMinas§7] §fDesenvolvido por: Markineo.");
		
		new BukkitRunnable() {
            @Override
            public void run() {
                mManager.updateBlockBreakDatabase();
            }
        }.runTaskTimer(this, 1200, 1200);
	}
	
	private void setupDatabase() {	
		host = sqlConfig.getString("host");
		port = sqlConfig.getInt("port");
		user = sqlConfig.getString("user");
		database = sqlConfig.getString("database");
		password = sqlConfig.getString("password");
		url = "jdbc:mysql://"+host+":"+port+"/"+database+"?characterEncoding=UTF-8";
        DatabaseManager.configureDataSource(url, user, password);
		
		try {
			conn = DatabaseManager.getConnection();
			if (conn != null) Bukkit.getConsoleSender().sendMessage("§7[§bcMinas§7] Conectado com sucesso ao MySQL.");
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage(e.getMessage());
		} finally {
			try {
				if (conn != null && !conn.isClosed()) conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        
        econ = rsp.getProvider();
        return econ != null;
    }
	
	private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	
	public Economy getEconomy() {
        return econ;
    }
	
	public Chat getChat() {
		return chat;
	}
	
	@Override
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("§7[§bcMinas§7] O plugin foi desligado com sucesso.");
	}
}
