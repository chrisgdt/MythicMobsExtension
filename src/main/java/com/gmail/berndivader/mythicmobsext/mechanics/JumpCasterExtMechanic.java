package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

@ExternalAnnotation(name = "jumpto", author = "BerndiVader")
public class JumpCasterExtMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {

	double height, gravity, speed;
	boolean use_speed, use_gravity;

	public JumpCasterExtMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		height = mlc.getDouble("height", 2d);
		use_gravity = (gravity = mlc.getDouble("gravity", -1337)) != -1337;
		use_speed = (speed = mlc.getDouble("speed", -1337)) != -1337;

	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity t) {
		return castAtLocation(data, t.getLocation());
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation l) {
		Entity entity = data.getCaster().getEntity().getBukkitEntity();
		Location destination = BukkitAdapter.adapt(l);

		if (!use_gravity)
			gravity = Utils.getGravity(entity.getType());
		entity.setVelocity(
				MathUtils.calculateVelocity(entity.getLocation().toVector(), destination.toVector(), gravity, height));

		return SkillResult.SUCCESS;
	}

}
