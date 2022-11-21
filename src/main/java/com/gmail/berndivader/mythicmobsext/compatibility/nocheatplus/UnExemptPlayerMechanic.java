package com.gmail.berndivader.mythicmobsext.compatibility.nocheatplus;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;

import java.io.File;

public class UnExemptPlayerMechanic extends ExemptPlayerMechanic implements INoTargetSkill, ITargetedEntitySkill {

	public UnExemptPlayerMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity e) {
		return SkillResult.MISSING_COMPATIBILITY;
	}
}
