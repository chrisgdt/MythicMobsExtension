package com.gmail.berndivader.mythicmobsext.compatibility.factions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.conditions.AbstractCustomCondition;

public class PlayerInHomeFactionCondition extends AbstractCustomCondition implements IEntityCondition {
	public PlayerInHomeFactionCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
	}

	@Override
	public boolean check(AbstractEntity entity) {
		if (entity.isPlayer()) {
			return FactionsSupport.playerInHomeFaction((Player) entity.getBukkitEntity());
		}
		return false;
	}
}