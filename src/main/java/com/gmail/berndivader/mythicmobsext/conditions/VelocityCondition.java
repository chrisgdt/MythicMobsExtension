package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;

@ExternalAnnotation(name = "velocity,speed", author = "BerndiVader")
public class VelocityCondition extends AbstractCustomCondition implements IEntityCondition {

	RangedDouble range;

	public VelocityCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);

		range = new RangedDouble(mlc.getString("speed", ">1"));
	}

	@Override
	public boolean check(AbstractEntity e) {
		return range.equals(e.getBukkitEntity().getVelocity().length());
	}
}
