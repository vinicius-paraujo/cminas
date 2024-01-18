package com.markineo.cminas.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.markineo.cminas.util.FileManager;

public class CMinasCommand implements CommandExecutor {
	private FileManager fManager;
	
	public CMinasCommand(FileManager fManager) {
		this.fManager = fManager;
	}
	
	private FileConfiguration itensConfig;

	private Player player;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			Bukkit.getConsoleSender().sendMessage("§cEsse comando deve ser executado por um jogador.");
			return true;
		}
		
		player = (Player) sender;
		
		if (!player.hasPermission("celestial.admin")) { player.sendMessage("§cVocê não tem permissão para executar esse comando"); return true; }
		String sintaxe = "§6§lcMinas - AJUDA\n§c/cminas reload\n§c/cminas additem (porcentagem)";
		if (args.length < 1) { player.sendMessage(sintaxe); return true; }
		
		switch (args[0].toLowerCase()) {
		    case "reload":
		        fManager.reloadConfigurations();
		        player.sendMessage("§aAs configurações foram recarregadas a partir dos arquivos locais.");
		        return true;
	
		    case "additem":
		    	itensConfig = fManager.getItensConfig();
		    	if (args.length < 2) { player.sendMessage("§cUse: /cminas additem [chance]."); return true; }
		    	if (!args[1].matches("\\d+(\\.\\d+)?")) { player.sendMessage("§cPor favor, forneça uma porcentagem válida."); return true; }
		    	if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR)) { player.sendMessage("§cVocê não tem nenhum item na mão para adicionar."); return true; }
		    	
		    	int id = getNextItemId(itensConfig);
		    	fManager.setConfig(itensConfig, "itens.yml", "itens." + id + ".data", player.getItemInHand().serialize());
		    	fManager.setConfig(itensConfig, "itens.yml", "itens." + id + ".porcentagem", Double.parseDouble(args[1])/100);
				
				player.sendMessage(fManager.getMessage("minas_messages.3").replace("{porcentagem}", ""  + Double.parseDouble(args[1])));
		        return true;
		}
		
		return true;
	}
	
	private int getNextItemId(FileConfiguration config) {
	    int nextItemId = 1;
	    if (config.contains("itens")) {
	        for (String key : config.getConfigurationSection("itens").getKeys(false)) {
	            int itemId = Integer.parseInt(key);
	            nextItemId = Math.max(nextItemId, itemId + 1);
	        }
	    }

	    return nextItemId;
	}
}
