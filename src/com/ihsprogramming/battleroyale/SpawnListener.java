package com.ihsprogramming.battleroyale;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Score;

public class SpawnListener implements Listener {
	
	/*
	 * This code handles player death.
	 */
	@EventHandler
    public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player){ // check to see if damaged entity is a player
	        Player player = (Player) event.getEntity();
	        if(player.getHealth() - event.getDamage() <= 0) {  // detects if damage would kill player
	        	if (event instanceof EntityDamageByEntityEvent){
	        		// if player was shot by an arrow, use the entity who shot that arrow for scoring purposes
	        		if(event.getCause() == DamageCause.PROJECTILE) {
	        		    Arrow a = (Arrow) ((EntityDamageByEntityEvent) event).getDamager();
	        		    if (a.getShooter() instanceof Entity) { // check if arrow shooter was entity (consider that arrows can be shot by dispensers or summoned using commands)
	        		    	Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + " was eliminated by " + ChatColor.GOLD + ((Entity) a.getShooter()).getName());
	        		    	// if a player shot the arrow, increase their score by one
	        		    	if (a.getShooter() instanceof Player) {
	    	    				Score kills = TaskRunner.kills.getScore(((Player) a.getShooter()).getName());
	    	    				kills.setScore(kills.getScore() + 1);
	    	    			}
	        		    } else {
	    	    			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + " was eliminated by " + ChatColor.GOLD + event.getCause().toString().toLowerCase().replaceAll("_", " ")); // use generic death message
	    	    		}
	        		} else {
	        			// if player was killed by an entity that isn't an arrow, use the entity's name for death message
	        			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + " was eliminated by " + ChatColor.GOLD + ((EntityDamageByEntityEvent) event).getDamager().getName());
	        			// if player was killed in PvP combat, get and increase the killer's score
	        			if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player) { // check if damager was player
		    				Score kills = TaskRunner.kills.getScore(((EntityDamageByEntityEvent) event).getDamager().getName());
		    				kills.setScore(kills.getScore() + 1);
		    			}
	        		}	    			
	    		} else {
	    			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + " was eliminated by " + ChatColor.GOLD + event.getCause().toString().toLowerCase().replaceAll("_", " ")); // use generic death message
	    		}
	            event.setCancelled(true); // cancel damage event, to prevent death
	            try {
	            	// drop the contents of the player's inventory upon death
		            for (ItemStack itemStack : player.getInventory().getContents()) {
		            	if (!itemStack.isSimilar(compass())) { // don't drop the storm compass
		            		player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
		            	}
		            }
		            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
		                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
		            }
	            } catch (Exception e) {
	            	
	            }
	    		player.setGameMode(GameMode.SPECTATOR); // changes the player's gamemode to spectator
	        }
		}
	}
	
	/*
	 * handles player spawning
	 */
	@EventHandler
	public void onSpawn(SpawnEvent event)
    {	
		Player player = event.getPlayer();
		
		// reset the player to defaults
		player.getInventory().clear();
		player.setGameMode(GameMode.SURVIVAL);
		player.setScoreboard(TaskRunner.board);
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setSaturation(5);
		player.setExhaustion(0);
		player.setTotalExperience(0);
		
		Score kills = TaskRunner.kills.getScore(player.getName());
		kills.setScore(0);
		
		int x;
		int z;
		int maxX = TaskRunner.maxX;
		int minX = TaskRunner.minX;
		int maxZ = TaskRunner.maxZ;
		int minZ = TaskRunner.minZ;
		boolean teleported = false;
		
		// teleport player to a random safe location
		while(!teleported) {
			Random Xrand = new Random();
			x = Xrand.nextInt(maxX - minX) + minX;
			Random Zrand = new Random();
			z = Zrand.nextInt(maxZ - minZ) + minZ;
			int y = Bukkit.getServer().getWorld("world").getHighestBlockAt(x,z).getY();
			Location loc = new Location(player.getLocation().getWorld(), x, y, z);
			loc.getChunk().load();
			Chunk c = loc.getChunk();
			player.getLocation().getWorld().loadChunk(c);
			loc.setY(loc.getWorld().getHighestBlockYAt(loc));
			
			if (!checkLocation(player, loc)) {
				
			} else {
				player.teleport(loc);
				player.sendMessage(ChatColor.DARK_AQUA + "Teleported to: X: " + ChatColor.GOLD + x + ChatColor.DARK_AQUA + " Z: " + ChatColor.GOLD + z);
				teleported = true;
			}
		}
		
		PlayerInventory playerinventory = player.getInventory();
        playerinventory.addItem(compass());
        
    }
	
	/*
	 * checks if location is safe for spawning
	 */
	public static boolean checkLocation(Player player, Location loc)
	{
		if (loc.getBlock().getType() != Material.AIR && loc.getBlock().getType() != null) {
			return false;
		}		
		return true;
	}
	
	/*
	 * creates a storm compass
	 */
	public ItemStack compass(){
		ItemStack is;
		ItemMeta im;
		is = new ItemStack(Material.COMPASS);
		im = is.getItemMeta();
		im.setDisplayName("Storm Compass");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.ITALIC + "Points to the center of the storm.");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;     
	}
}
