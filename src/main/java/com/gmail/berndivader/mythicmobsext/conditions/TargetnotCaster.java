package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;

// TODO FIX : getPlugin() in io.lumine.mythic.core.skills.SkillCondition cannot implement getPlugin() in io.lumine.mythic.api.skills.ISkillMechanic
@ExternalAnnotation(name = "targetnotcaster", author = "BerndiVader")
public class TargetnotCaster {//extends AbstractCustomCondition implements ITargetedEntitySkill {

	public TargetnotCaster(String line, MythicLineConfig mlc) {
		//super(line, mlc);
	}

	//@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity entity) {
		if (data.getCaster().getEntity().getBukkitEntity().getEntityId() != entity.getBukkitEntity().getEntityId()) {
			return SkillResult.SUCCESS;
		}
		return SkillResult.ERROR;
	}
}
