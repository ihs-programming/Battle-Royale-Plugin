package com.ihsprogramming.battleroyale;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Criterias;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class TaskRunner extends BukkitRunnable implements Listener {
	
    private final JavaPlugin plugin;
    
    protected static final int MIN_PLAYERS = 2;
    private static final int SHRINK_TIME = 180;
    private static final int SHRINK_PERIOD = 200;
    
	int ID = 0;
	
	public static int minX = -100;
	public static int maxX = 100;
	public static int minZ = -100;
	public static int maxZ = 100;
		
	private static int sidelength;
	
	public static ScoreboardManager scoreboardmanager;
	public static Scoreboard board;
	
	public static Objective kills;
	public static Objective health;

    public TaskRunner(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /*
     * waits for players to join
     */
	@EventHandler
	public void onStart(StartEvent event)
	{	
		// repeatedly check if enough players have joined
		BukkitScheduler scheduler = plugin.getServer().getScheduler();
        ID = scheduler.scheduleSyncRepeatingTask(plugin, new Runnable() {        	
            @Override
            public void run() {
            	if (plugin.getServer().getOnlinePlayers().size() >= MIN_PLAYERS) { // if enough players have connected, start the game
            		Bukkit.getScheduler().cancelTask(ID);
            		new TaskRunner(plugin).runTask(plugin);
            	}
            	plugin.getServer().broadcastMessage(ChatColor.GOLD+"Connected players: "+ChatColor.AQUA+plugin.getServer().getOnlinePlayers().size()+"/"+MIN_PLAYERS);
        		plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE+"Please wait, the battle will begin shortly!");
            }
        }, 0L, 100L);
	}
	
    @Override
    public void run() {
        // set time to day
    	Bukkit.getWorld("world").setTime(1000);
    	
    	// handle scoreboard stuff
    	board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
    	
    	kills = board.registerNewObjective("Kills", Criterias.PLAYER_KILLS);
    	kills.setDisplaySlot(DisplaySlot.SIDEBAR);
    	kills.setDisplayName("Kills");
    	
    	health = board.registerNewObjective("Health", Criterias.HEALTH);
    	health.setDisplaySlot(DisplaySlot.BELOW_NAME);
    	health.setDisplayName(ChatColor.RED + "�?�");
    	
    	// spawn all the players
    	Bukkit.getServer().getWorld("world").setSpawnLocation((minX+maxX)/2, 60, (minZ+maxZ)/2);
        Collection<? extends Player> list = (Bukkit.getOnlinePlayers());
		for(Player player:list) {
			SpawnEvent spawnevent = new SpawnEvent(player);
			Bukkit.getPluginManager().callEvent(spawnevent);
		}
		
		// create and shrink the storm
		WorldBorder worldborder = Bukkit.getServer().getWorld("world").getWorldBorder();
		sidelength = ((maxX-minX) > (maxZ-minZ)) ? maxX-minX : maxZ-minZ;
		worldborder.setSize(sidelength);
		worldborder.setCenter((minX+maxX)/2, (minZ+maxZ)/2);
		worldborder.setDamageBuffer(0);
		plugin.getServer().broadcastMessage(ChatColor.DARK_AQUA + "The storm has formed! It will shrink in " + ChatColor.GOLD + (SHRINK_PERIOD - SHRINK_TIME) + ChatColor.DARK_AQUA + " seconds!");
		ID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
		     @Override
		     public void run() {
		    	sidelength *= 0.5;		 		
		 		if (sidelength <= 10) {
		    		Bukkit.getServer().getScheduler().cancelTask(ID);
	    			plugin.getServer().broadcastMessage(ChatColor.DARK_AQUA + "The storm has stopped shrinking! Last person standing wins!");
		    	} else {
		    		Random Xrand = new Random();
			 		int x = Xrand.nextInt(maxX - minX - (sidelength)) + minX + sidelength/2;
			 		Random Zrand = new Random();
			 		int z = Zrand.nextInt(maxZ - minZ - (sidelength)) + minZ + sidelength/2;
			 		Bukkit.getServer().getWorld("world").setSpawnLocation(x, 60, z);
			 		worldborder.setCenter(x, z);
			 		worldborder.setSize(sidelength, SHRINK_TIME);
			 		maxX = x + sidelength / 2;
			 		minX = x - sidelength / 2;
			 		maxZ = z + sidelength / 2;
			 		minZ = z - sidelength / 2;
			 		if (sidelength <= 2*10) {
		 				plugin.getServer().broadcastMessage(ChatColor.DARK_AQUA + "The storm is shrinking for the" + ChatColor.ITALIC + " final time " + ChatColor.RESET + ChatColor.DARK_AQUA + "around X: " + ChatColor.GOLD + x + ChatColor.DARK_AQUA + " Z: " + ChatColor.GOLD + z + ChatColor.DARK_AQUA + " with a radius of "+ ChatColor.GOLD + sidelength/2 + ChatColor.DARK_AQUA + ".");
			 		} else {
			 			plugin.getServer().broadcastMessage(ChatColor.DARK_AQUA + "The storm is shrinking around X: " + ChatColor.GOLD + x + ChatColor.DARK_AQUA + " Z: " + ChatColor.GOLD + z + ChatColor.DARK_AQUA + " with a radius of "+ ChatColor.GOLD + sidelength/2 + ChatColor.DARK_AQUA + ".");
			 			plugin.getServer().broadcastMessage(ChatColor.DARK_AQUA + "The storm will shrink again in " + ChatColor.GOLD + (SHRINK_PERIOD - SHRINK_TIME) + ChatColor.DARK_AQUA + " seconds!");
			 		}
		    	}	
		     }
		}, 20 * SHRINK_PERIOD, 20 * SHRINK_PERIOD);
		
		
    }

}
