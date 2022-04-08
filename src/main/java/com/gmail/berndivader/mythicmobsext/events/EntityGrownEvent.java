package com.gmail.berndivader.mythicmobsext.events;

import java.util.Optional;

import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EntityGrownEvent extends Event {
	static final HandlerList handlers = new HandlerList();
	final Optional<ActiveMob> am;
	final Entity entity;

	public EntityGrownEvent(Entity entity, ActiveMob am) {
		this.am = Optional.ofNullable(am);
		this.entity = entity;
	}

	public Entity getEntity() {
		return this.entity;
	}

	public boolean isActiveMob() {
		return am.isPresent();
	}

	public ActiveMob getActiveMob() {
		return am.isPresent() ? am.get() : null;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
