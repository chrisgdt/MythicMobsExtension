package com.gmail.berndivader.mythicmobsext.mechanics;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

import java.io.File;

@ExternalAnnotation(name = "forceshader", author = "BerndiVader")
public class ForceShaderMechanic extends SkillMechanic implements ITargetedEntitySkill {

	EntityType entityType;

	public ForceShaderMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		entityType = EntityType.valueOf(mlc.getString("type", "CREEPER"));
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isPlayer()) {
			Player p = (Player) target.getBukkitEntity();
			Location l = p.getLocation();
			double dx = l.getX(), dz = l.getZ();
			LivingEntity entity = (LivingEntity) p.getWorld().spawnEntity(new Location(p.getWorld(), dx, 0, dz),
					entityType);
			entity.setAI(false);
			entity.setInvulnerable(false);
			Volatile.handler.forceSpectate(p, entity, true);
			return SkillResult.SUCCESS;
		}
		return SkillResult.ERROR;
	}
}
