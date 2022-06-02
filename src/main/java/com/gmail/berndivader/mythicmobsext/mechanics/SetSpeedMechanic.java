package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

@ExternalAnnotation(name = "setspeed,randomspeed", author = "BerndiVader")
public class SetSpeedMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
	private String s1;
	boolean bl1;

	public SetSpeedMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		s1 = mlc.getString(new String[] { "amount", "a", "range", "r" }, "0.2D").toLowerCase();
		bl1 = mlc.getBoolean("debug", false);
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		return d(data.getCaster().getEntity());
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		return d(target);
	}

	private SkillResult d(AbstractEntity target) {
		if (target.isLiving()) {
			((LivingEntity) target.getBukkitEntity()).getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)
					.setBaseValue(MathUtils.randomRangeDouble(s1));
			LivingEntity l = (LivingEntity) target.getBukkitEntity();
			if (bl1) {
				System.out.println("randomspeed debug");
				System.out.println("Value: " + l.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue());
				System.out.println("Base: " + l.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue());
			}
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}
