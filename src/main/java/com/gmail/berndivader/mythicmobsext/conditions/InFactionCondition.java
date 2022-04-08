package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "infaction", author = "BerndiVader")
public class InFactionCondition extends AbstractCustomCondition implements IEntityCondition {
	private String[] factions;

	public InFactionCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		this.factions = mlc.getString("faction").split(",");
	}

	@Override
	public boolean check(AbstractEntity t) {
		Entity target = t.getBukkitEntity();
		ActiveMob am = Utils.mobmanager.getMythicMobInstance(target);
		if (am != null)
			return SameFactionCondition.checkFactions(am, am, t, this.factions);
		return false;
	}
}
