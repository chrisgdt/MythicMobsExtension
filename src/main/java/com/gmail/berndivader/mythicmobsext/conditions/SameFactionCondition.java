package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.core.mobs.ActiveMob;

import org.bukkit.entity.Entity;

@ExternalAnnotation(name = "samefaction", author = "BerndiVader")
public class SameFactionCondition extends AbstractCustomCondition implements IEntityComparisonCondition {
	private String[] factions;

	public SameFactionCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		this.factions = mlc.getString("faction").split(",");
	}

	@Override
	public boolean check(AbstractEntity c, AbstractEntity t) {
		Entity caster = c.getBukkitEntity();
		Entity target = t.getBukkitEntity();
		if (caster.getEntityId() == target.getEntityId())
			return false;
		ActiveMob am = Utils.mobmanager.getMythicMobInstance(caster);
		ActiveMob tam = Utils.mobmanager.getMythicMobInstance(target);
		if (am == null || tam == null)
			return false;
		return (checkFactions(am, am, t, this.factions) && checkFactions(am, tam, t, this.factions));
	}

	public static boolean checkFactions(SkillCaster caster, ActiveMob am, AbstractEntity target, String[] factions) {
		for (int a = 0; a < factions.length; a++) {
			String f = Utils.parseMobVariables(factions[a], caster, target, null);
			if (am.hasFaction() && am.getFaction().equals(f)) {
				return true;
			}
		}
		return false;
	}
}
