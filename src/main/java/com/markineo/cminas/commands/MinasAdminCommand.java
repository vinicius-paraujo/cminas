package com.markineo.cminas.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.markineo.cminas.util.FileManager;

public class MinasAdminCommand implements CommandExecutor {
	private FileManager fManager;
	
	public MinasAdminCommand(FileManager fManager) {
		this.fManager = fManager;
	}

	private Player player;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage("§cEsse comando deve ser executado por um jogador.");
			return true;
		}
		
		player = (Player) sender;
		
		if (!player.hasPermission("celestial.admin")) { player.sendMessage("§cVocê não tem permissão para executar esse comando"); return true; }
		String sintaxe = "§6§lcMinas - AJUDA\n§c/cminas reload";
		if (args.length < 1) { player.sendMessage(sintaxe); return true; }
		if (args[0].equals("reload")) {
			fManager.reloadConfigurations();
			
			player.sendMessage("§aAs configurações foram recarregadas a partir dos arquivos locais.");
			return true;
		}
		
		return true;
	}
}