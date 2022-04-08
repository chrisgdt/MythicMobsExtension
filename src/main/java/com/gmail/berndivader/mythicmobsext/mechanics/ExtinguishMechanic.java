package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "extinguish", author = "BerndiVader")
public class ExtinguishMechanic extends SkillMechanic implements ITargetedEntitySkill {

	public ExtinguishMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		Entity e = target.getBukkitEntity();
		e.setFireTicks(-1);
		return SkillResult.SUCCESS;
	}
}
