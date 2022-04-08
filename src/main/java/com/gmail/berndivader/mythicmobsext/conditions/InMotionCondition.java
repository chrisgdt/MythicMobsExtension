package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "inmotion", author = "BerndiVader")
public class InMotionCondition extends AbstractCustomCondition implements IEntityCondition {
	public InMotionCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
	}

	@Override
	public boolean check(AbstractEntity e) {
		if (e.isLiving() && !e.isPlayer()) {
			return Volatile.handler.inMotion((LivingEntity) e.getBukkitEntity());
		} else if (e.isPlayer()) {
			return MathUtils.playerInMotion((Player) e.getBukkitEntity());
		}
		return false;
	}
}
