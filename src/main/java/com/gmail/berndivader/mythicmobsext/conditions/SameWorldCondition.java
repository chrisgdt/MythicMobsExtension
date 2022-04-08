package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationComparisonCondition;

@ExternalAnnotation(name = "sameworld", author = "BerndiVader")
public class SameWorldCondition extends AbstractCustomCondition implements ILocationComparisonCondition {

	public SameWorldCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
	}

	@Override
	public boolean check(AbstractLocation c, AbstractLocation t) {
		return c.getWorld() == null || t.getWorld() == null ? false : c.getWorld() == t.getWorld() ? true : false;
	}

}
