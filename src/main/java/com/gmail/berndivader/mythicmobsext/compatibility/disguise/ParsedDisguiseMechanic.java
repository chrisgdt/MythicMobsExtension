package com.gmail.berndivader.mythicmobsext.compatibility.disguise;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;

public class ParsedDisguiseMechanic extends SkillMechanic implements ITargetedEntitySkill {

	PlaceholderString disguise;

	public ParsedDisguiseMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.disguise = mlc.getPlaceholderString(new String[] { "disguise", "d" }, "Notch");
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (CompatibilityManager.LibsDisguises != null) {
			String d = disguise.get(data, target);
			switch (d.toUpperCase()) {
			case "STEVE":
			case "ALEX":
				break;
			default:
				CompatibilityManager.LibsDisguises.setDisguise((ActiveMob) data.getCaster(), d);
			}
			return SkillResult.SUCCESS;
		}
		return SkillResult.ERROR;
	}
}
