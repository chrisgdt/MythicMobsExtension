package com.gmail.berndivader.mythicmobsext.compatibility.factions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.conditions.AbstractCustomCondition;

public class PlayerInFactionsCondition extends AbstractCustomCondition implements IEntityCondition {
	String[] factions;

	public PlayerInFactionsCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		factions = mlc.getString(new String[] { "factions", "faction", "f" }, "").toLowerCase().split(",");
	}

	@Override
	public boolean check(AbstractEntity entity) {
		if (entity.isPlayer()) {
			return FactionsSupport.playersFaction((Player) entity.getBukkitEntity(), factions);
		}
		return false;
	}
}