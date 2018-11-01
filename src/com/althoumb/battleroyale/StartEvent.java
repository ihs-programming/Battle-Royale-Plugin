package com.althoumb.battleroyale;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class StartEvent extends Event {
    
	private static final HandlerList HANDLERS = new HandlerList();

    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
}