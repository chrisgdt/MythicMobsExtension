package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.externals.*;

import java.io.File;

@ExternalAnnotation(name = "despawning", author = "BerndiVader")
public class SetDespawnMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
	boolean set;

	public SetDespawnMechanic(SkillExecutor manager, File file, String line, MythicLineConfig mlc) {
		super(manager, file, line, mlc);
		this.line = line;
		set = mlc.getBoolean("set", true);
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		this.castAtEntity(data, data.getCaster().getEntity());
		return SkillResult.SUCCESS;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isLiving()) {
			LivingEntity entity = (LivingEntity) target.getBukkitEntity();
			entity.setRemoveWhenFarAway(set);
			return SkillResult.SUCCESS;
		}
		return SkillResult.ERROR;
	}
}
