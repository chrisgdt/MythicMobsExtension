package com.gmail.berndivader.mythicmobsext.conditions;

import java.util.Map;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.core.mobs.ActiveMob;

@ExternalAnnotation(name = "threattable", author = "BerndiVader")
public class ThreatTable extends AbstractCustomCondition implements IEntityComparisonCondition {
	RangedDouble range;

	public ThreatTable(String line, MythicLineConfig mlc) {
		super(line, mlc);
		range = new RangedDouble(mlc.getString(new String[] { "range", "r", "a", "amount" }, ">0"));
	}

	@Override
	public boolean check(AbstractEntity source, AbstractEntity target) {
		if (Utils.mobmanager.isActiveMob(source)) {
			ActiveMob am = Utils.mobmanager.getMythicMobInstance(source);
			if (am.hasThreatTable()) {
				Map<AbstractEntity, Double> threattable_map = Utils.getActiveMobThreatTable(am);
				return threattable_map.containsKey(target) && range.equals(threattable_map.get(target));
			}
		}
		return false;
	}
}
