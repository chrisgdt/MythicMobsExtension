package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Vec2D;
import com.gmail.berndivader.mythicmobsext.utils.Vec3D;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "jumpex,jump_ex", author = "BerndiVader")
public class JumpCasterMechanic extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {

	float speed, force, G;
	boolean debug, default_g;

	public JumpCasterMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.speed = MathUtils.clamp(mlc.getFloat("speed", 1.3f), 1.01f, 3.0f);
		this.G = mlc.getFloat("gravity", 0.02155f);

		this.debug = mlc.getBoolean("debug", false);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity t) {
		if (data.getCaster().getEntity().isLiving() && t.isLiving()) {
			LivingEntity caster = (LivingEntity) data.getCaster().getEntity().getBukkitEntity();
			LivingEntity target = (LivingEntity) t.getBukkitEntity();
			Vec3D target_position = Volatile.handler.getPredictedMotion(caster, target, 1.0f);
			return calc(caster, target_position);
		}
		return SkillResult.CONDITION_FAILED;
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation abstract_location) {
		if (data.getCaster().getEntity().isLiving()) {
			LivingEntity caster = (LivingEntity) data.getCaster().getEntity().getBukkitEntity();
			Vec3D target_position = new Vec3D(abstract_location.getX(), abstract_location.getY(),
					abstract_location.getZ());
			return calc(caster, target_position);
		}
		return SkillResult.CONDITION_FAILED;
	}

	SkillResult calc(LivingEntity caster, Vec3D target_position) {
		Vec2D direction2d = MathUtils.calculateDirectionVec2D(target_position, (float) speed / 3, G);
		Vector direction = MathUtils.getDirection((float) direction2d.getX(), (float) direction2d.getY());
		if (Float.isFinite((float) direction.getX()) && Float.isFinite((float) direction.getZ())) {
			caster.setVelocity(direction.multiply(speed));
		} else {
			if (debug)
				Main.logger.warning("Target cannot be reached with the given speed and gravity");
			return SkillResult.ERROR;
		}
		return SkillResult.SUCCESS;
	}

}
