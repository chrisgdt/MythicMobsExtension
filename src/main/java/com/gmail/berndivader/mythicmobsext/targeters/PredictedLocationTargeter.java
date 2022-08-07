package com.gmail.berndivader.mythicmobsext.targeters;

import java.util.HashSet;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Vec3D;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "targetpredict,triggerpredict,selfpredict,ownerpredict", author = "BerndiVader")
public class PredictedLocationTargeter extends ISelectorLocation {
	String selector;
	float delta;

	public PredictedLocationTargeter(SkillExecutor manager, MythicLineConfig mlc) {
		super(manager, mlc);
		selector = mlc.getLine().toLowerCase().split("predict")[0];
		delta = mlc.getFloat("delta", 5f);
	}

	@Override
	public HashSet<AbstractLocation> getLocations(SkillMetadata data) {
		Entity ee = null;
		HashSet<AbstractLocation> targets = new HashSet<>();
		switch (selector) {
		case "target":
			ee = data.getEntityTargets().size() > 0 ? data.getEntityTargets().iterator().next().getBukkitEntity()
					: data.getCaster().getEntity().getTarget() != null
							? data.getCaster().getEntity().getTarget().getBukkitEntity()
							: null;
			break;
		case "trigger":
			if (data.getTrigger() != null)
				ee = data.getTrigger().getBukkitEntity();
			break;
		case "owner":
			ActiveMob am = (ActiveMob) data.getCaster();
			if (am != null && am.getOwner().isPresent()) {
				ee = NMSUtils.getEntity(data.getCaster().getEntity().getBukkitEntity().getWorld(), am.getOwner().get());
			}
			break;
		default:
			ee = data.getCaster().getEntity().getBukkitEntity();
			break;
		}
		if (ee instanceof LivingEntity && data.getCaster().getEntity().isLiving()) {
			Vec3D target_position = Volatile.handler.getPredictedMotion(
					(LivingEntity) data.getCaster().getEntity().getBukkitEntity(), (LivingEntity) ee, delta);
			targets.add(BukkitAdapter.adapt(ee.getLocation().clone().add(target_position.getX(), target_position.getY(),
					target_position.getZ())));
		}
		return applyOffsets(targets);
	}
}
