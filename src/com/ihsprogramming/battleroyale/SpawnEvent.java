package com.ihsprogramming.battleroyale;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpawnEvent extends Event {
	
	private final Player player;
	
    public SpawnEvent(Player player) {
        this.player = player;
    }
    
	private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    public Player getPlayer() {
        return this.player;
    }

}
