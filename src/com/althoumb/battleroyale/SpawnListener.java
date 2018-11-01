package com.althoumb.battleroyale;

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
	
	@EventHandler
    public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player){
	        Player player = (Player) event.getEntity();
	        if(player.getHealth() - event.getDamage() <= 0) {
	        	if (event instanceof EntityDamageByEntityEvent){
	        		if(event.getCause() == DamageCause.PROJECTILE) {
	        		    Arrow a = (Arrow) ((EntityDamageByEntityEvent) event).getDamager();
	        		    if (a.getShooter() instanceof Entity) {
	        		    	Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + " was eliminated by " + ChatColor.GOLD + ((Entity) a.getShooter()).getName());
	        		    	if (a.getShooter() instanceof Player) {
	    	    				Score kills = TaskRunner.kills.getScore(((Player) a.getShooter()).getName());
	    	    				kills.setScore(kills.getScore() + 1);
	    	    			}
	        		    } else {
	    	    			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + " was eliminated by " + ChatColor.GOLD + event.getCause().toString().toLowerCase().replaceAll("_", " "));
	    	    		}
	        		} else {
	        			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + " was eliminated by " + ChatColor.GOLD + ((EntityDamageByEntityEvent) event).getDamager().getName());
	        			if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player) {
		    				Score kills = TaskRunner.kills.getScore(((EntityDamageByEntityEvent) event).getDamager().getName());
		    				kills.setScore(kills.getScore() + 1);
		    			}
	        		}	    			
	    		} else {
	    			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + ChatColor.RED + " was eliminated by " + ChatColor.GOLD + event.getCause().toString().toLowerCase().replaceAll("_", " "));
	    		}
	            event.setCancelled(true);
	            try {
		            for (ItemStack itemStack : player.getInventory().getContents()) {
		            	if (!itemStack.isSimilar(compass())) {
		            		player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
		            	}
		            }
		            for (ItemStack itemStack : player.getInventory().getArmorContents()) {
		                player.getWorld().dropItemNaturally(player.getLocation(), itemStack);
		            }
	            } catch (Exception e) {
	            	
	            }
	    		player.setGameMode(GameMode.SPECTATOR);
	        }
		}
	}
	
	@EventHandler
	public void onSpawn(SpawnEvent event)
    {	
		Player player = event.getPlayer();
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
				player.sendMessage(ChatColor.GOLD + "Couldn't find a safe place to teleport you! Trying again!");
			} else {
				player.teleport(loc);
				player.sendMessage(ChatColor.DARK_AQUA + "Teleported to: X: " + ChatColor.GOLD + x + ChatColor.DARK_AQUA + " Z: " + ChatColor.GOLD + z);
				teleported = true;
			}
		}
		
		PlayerInventory playerinventory = player.getInventory();
        playerinventory.addItem(compass());
        
    }
		
	public static boolean checkLocation(Player player, Location loc)
	{
		if (loc.getBlock().getType() != Material.AIR && loc.getBlock().getType() != null) {
			return false;
		}		
		return true;
	}
	
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
