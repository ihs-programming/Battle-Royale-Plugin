package com.ihsprogramming.battleroyale;

import org.bukkit.plugin.java.JavaPlugin;

public class BattleRoyale extends JavaPlugin {
	
    // Fired when plugin is first enabled
    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(new SpawnListener(), this);
    	getServer().getPluginManager().registerEvents(new TaskRunner(this), this);
    	this.getCommand("startgame").setExecutor(new Commander(this));	
    }
    
    // Fired when plugin is disabled
    @Override
    public void onDisable() {

    }
    
}
