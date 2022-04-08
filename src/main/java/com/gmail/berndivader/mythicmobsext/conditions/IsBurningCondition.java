package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;

@ExternalAnnotation(name = "isburning", author = "BerndiVader")
public class IsBurningCondition extends AbstractCustomCondition implements IEntityCondition {
	private RangedDouble rd;

	public IsBurningCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		this.rd = new RangedDouble(mlc.getString(new String[] { "range", "r" }, ">-1"));
	}

	@Override
	public boolean check(AbstractEntity e) {
		return rd.equals(e.getBukkitEntity().getFireTicks());
	}

}
