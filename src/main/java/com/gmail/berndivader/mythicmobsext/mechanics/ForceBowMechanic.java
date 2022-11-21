package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

import java.io.File;

@ExternalAnnotation(name = "forcebow", author = "BerndiVader")
public class ForceBowMechanic extends SkillMechanic implements ITargetedEntitySkill {
	boolean debug;

	public ForceBowMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = line;
		this.debug = mlc.getBoolean("debug", false);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity entity) {
		if (this.debug)
			System.err.println("Using forcebow");
		Volatile.handler.forceBowDraw((LivingEntity) data.getCaster().getEntity().getBukkitEntity(),
				(LivingEntity) entity.getBukkitEntity(), this.debug);
		return SkillResult.SUCCESS;
	}
}
