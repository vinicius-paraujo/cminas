package com.markineo.cminas.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.markineo.cminas.control.MinasManager;
import com.markineo.cminas.util.FileManager;
import com.markineo.cminas.util.ItemManager;
import com.markineo.cminas.util.PlayerManager;

import xyz.tozymc.spigot.api.title.TitleApi;

public class MinaCommand implements CommandExecutor {
	private FileManager fManager;
	private PlayerManager pManager;
	private ItemManager iManager;
	private MinasManager mManager;
	
	public MinaCommand(FileManager fManager, PlayerManager pManager, ItemManager iManager, MinasManager mManager) {
		this.fManager = fManager;
		this.pManager = pManager;
		this.iManager = iManager;
		this.mManager = mManager;
	}
	
	private FileConfiguration mainConfig;
	
	private Player player;
	private int mId;
	private Location mLocation;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) { sender.sendMessage("§cO comando deve ser executado por um jogador."); return true; }
		
		player = (Player) sender;
		
		mId = pManager.getPlayerMinaId(player);
		if (mId < 0) { player.sendMessage(fManager.getMessage("err_messages.4")); return true; }
		
		mainConfig = fManager.getMainConfig();
		mLocation = fManager.loadLocation(mainConfig, "minas." + mId + ".location");
		
		if (mLocation == null) { player.sendMessage(fManager.getMessage("err_messages.6")); return true; }
		if (!isInventoryClear(player)) { player.sendMessage(fManager.getMessage("err_messages.5")); return true; }
		
		player.teleport(mLocation);
		
		int pLevel = mManager.getPlayerPickaxeLevel(player);
		ItemStack pickaxeItem = iManager.createPickaxeItem(pLevel);
		player.getInventory().addItem(pickaxeItem);
		
		String title = fManager.getMessage("minas_messages.title");
		String subtitle = fManager.getMessage("minas_messages.subtitle");
		TitleApi.sendTitle(player, title, subtitle, 10, 40, 10);
		
		if (player.hasPermission("celestial.vip")) player.setAllowFlight(true);
		player.sendMessage("§aTeleportado com sucesso para a mina.");
		
		return true;
	}
	
	private boolean isInventoryClear(Player player) {
        ItemStack[] inventoryContents = player.getInventory().getContents();

        for (ItemStack itemStack : inventoryContents) {
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                return false;
            }
        }

        return true;
    }
	
}
