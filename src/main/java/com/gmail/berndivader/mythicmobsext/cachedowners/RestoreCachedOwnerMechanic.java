package com.gmail.berndivader.mythicmobsext.cachedowners;

import java.io.File;
import java.util.UUID;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;

public class RestoreCachedOwnerMechanic extends SkillMechanic implements INoTargetSkill {

	public RestoreCachedOwnerMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		if (data.getCaster() instanceof ActiveMob) {
			ActiveMob am = (ActiveMob) data.getCaster();
			UUID owner_uuid = CachedOwnerHandler.getMobOwner(am.getUniqueId());
			if (owner_uuid != null) {
				AbstractEntity target = BukkitAdapter.adapt(NMSUtils.getEntity(owner_uuid));
				am.setOwner(owner_uuid);
				if (target != null && target.isPlayer()
						&& data.getCaster().getEntity().getBukkitEntity() instanceof Wolf) {
					((Wolf) data.getCaster().getEntity().getBukkitEntity())
							.setOwner((AnimalTamer) target.getBukkitEntity());
					((Wolf) data.getCaster().getEntity().getBukkitEntity()).setTamed(true);
				}
				return SkillResult.SUCCESS;
			}
		}
		return SkillResult.CONDITION_FAILED;
	}
}
