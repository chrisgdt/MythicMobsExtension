package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.ILocationCondition;

@ExternalAnnotation(name = "minecraftversion", author = "BerndiVader")
public class MinecraftVersionCondition extends AbstractCustomCondition implements ILocationCondition {

	boolean match;

	public MinecraftVersionCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);

		match = (new RangedDouble(mlc.getString("version", ">10"))).equals(Utils.serverV);
	}

	@Override
	public boolean check(AbstractLocation var1) {
		return match;
	}
}
