package com.ihsprogramming.battleroyale;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Commander implements CommandExecutor {
	
	public Commander(JavaPlugin plugin) {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("startgame")) {
			StartEvent startevent = new StartEvent();
			Bukkit.getServer().getPluginManager().callEvent(startevent);
			return true;
		}
		return false; 
		// implementation exactly as before...
	}
}
