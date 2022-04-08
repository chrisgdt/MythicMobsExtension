package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;

@ExternalAnnotation(name = "getindicator,damageindicator,indicator", author = "BerndiVader")
public class GetDamageIndicatorCondition extends AbstractCustomCondition implements IEntityCondition {
	private RangedDouble rd;

	public GetDamageIndicatorCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		rd(mlc.getString("value", "<1.1"));
	}

	@Override
	public boolean check(AbstractEntity e) {
		if (e.isPlayer()) {
			return this.rd.equals((double) Volatile.handler.getIndicatorPercentage((Player) e.getBukkitEntity()));
		}
		return true;
	}

	private void rd(String s1) {
		this.rd = new RangedDouble(s1);
	}

}
