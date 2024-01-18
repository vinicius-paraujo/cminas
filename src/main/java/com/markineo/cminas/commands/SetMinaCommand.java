package com.markineo.cminas.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.markineo.cminas.util.FileManager;

public class SetMinaCommand implements CommandExecutor {
	private FileManager fManager;
	
	public SetMinaCommand(FileManager fManager) {
		this.fManager = fManager;
	}

	private FileConfiguration mainConfig;
	private Player player;
	
	private int minaId;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage("§cEsse comando deve ser executado por um jogador.");
			return true;
		}
		
		player = (Player) sender;
		
		if (!player.hasPermission("celestial.admin")) { player.sendMessage("§cVocê não tem permissão para executar esse comando"); return true; }
		if (args.length < 1) { player.sendMessage("§cUse: /setmina [id]."); return true; }
		if (!args[0].matches("\\d+")) { player.sendMessage("§cPor favor, forneça um número de ID válido para a máquina."); return true; }
		
		minaId = Integer.parseInt(args[0]);
		mainConfig = fManager.getMainConfig();
		fManager.setConfig(mainConfig, "config.yml", "minas." + minaId + ".location", player.getLocation().serialize());
		
		player.sendMessage(fManager.getMessage("minas_messages.2"));
		
		return true;
	}
}