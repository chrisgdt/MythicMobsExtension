package com.gmail.berndivader.mythicmobsext.mechanics;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;

import java.io.File;

@ExternalAnnotation(name = "cleartravelpoints", author = "BerndiVader")
public class ClearTravelPoints extends SkillMechanic implements INoTargetSkill {
	boolean remove_points;

	public ClearTravelPoints(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;

		this.remove_points = mlc.getBoolean("removeagain", true);
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		Volatile.handler.clearTravelPoints(data.getCaster().getEntity().getBukkitEntity());
		return SkillResult.SUCCESS;
	}
}
