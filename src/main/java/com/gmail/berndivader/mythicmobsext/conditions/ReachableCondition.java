package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "reachable", author = "BerndiVader")
public class ReachableCondition extends AbstractCustomCondition implements IEntityComparisonCondition {

	public ReachableCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
	}

	@Override
	public boolean check(AbstractEntity e, AbstractEntity t) {
		if (e.isLiving() && t.isLiving())
			return Volatile.handler.isReachable1((LivingEntity) e.getBukkitEntity(),
					(LivingEntity) t.getBukkitEntity());
		return false;
	}
}
