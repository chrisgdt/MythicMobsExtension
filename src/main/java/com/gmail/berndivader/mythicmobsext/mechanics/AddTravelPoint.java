package com.gmail.berndivader.mythicmobsext.mechanics;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Vec3D;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.ITargetedLocationSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;

@ExternalAnnotation(name = "addtravelpoint", author = "BerndiVader")
public class AddTravelPoint extends SkillMechanic implements ITargetedEntitySkill, ITargetedLocationSkill {
	boolean remove_points;

	public AddTravelPoint(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;

		this.remove_points = mlc.getBoolean("removeagain", true);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity abstract_entity) {
		return castAtLocation(data, abstract_entity.getLocation());
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation abstract_location) {
		Volatile.handler.addTravelPoint(data.getCaster().getEntity().getBukkitEntity(),
				new Vec3D(abstract_location.getX(), abstract_location.getY(), abstract_location.getZ()),
				this.remove_points);
		return SkillResult.SUCCESS;
	}
}
