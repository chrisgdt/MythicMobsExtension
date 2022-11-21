package com.gmail.berndivader.mythicmobsext.cachedowners;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import java.io.File;

public class CachedOwnerSkill extends SkillMechanic implements ITargetedEntitySkill {

	public CachedOwnerSkill(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (data.getCaster() instanceof ActiveMob) {
			ActiveMob am = (ActiveMob) data.getCaster();
			am.setOwner(target.getUniqueId());
			if (target.isPlayer() && data.getCaster().getEntity().getBukkitEntity() instanceof Wolf) {
				((Wolf) data.getCaster().getEntity().getBukkitEntity())
						.setOwner((AnimalTamer) target.getBukkitEntity());
				((Wolf) data.getCaster().getEntity().getBukkitEntity()).setTamed(true);
			}
			CachedOwnerHandler.addCachedOwner(am.getUniqueId(), target.getUniqueId());
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}
