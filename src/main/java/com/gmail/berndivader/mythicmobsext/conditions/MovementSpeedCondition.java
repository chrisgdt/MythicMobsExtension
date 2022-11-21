package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;

@ExternalAnnotation(name = "movespeed", author = "BerndiVader")
public class MovementSpeedCondition extends AbstractCustomCondition implements IEntityCondition {
	private RangedDouble r1;

	public MovementSpeedCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		r1 = new RangedDouble(mlc.getString("range", ">0"));
	}

	@Override
	public boolean check(AbstractEntity e) {
		if (e.isLiving()) {
			LivingEntity le1 = (LivingEntity) e.getBukkitEntity();
			return r1.equals(le1.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue());
		}
		return false;
	}

}
