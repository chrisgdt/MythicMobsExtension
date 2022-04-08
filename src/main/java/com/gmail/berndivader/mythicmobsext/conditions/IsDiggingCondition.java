package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;

@ExternalAnnotation(name = "ismining,mining,isdigging,digging", author = "BerndiVader")
public class IsDiggingCondition extends AbstractCustomCondition implements IEntityCondition {

	public IsDiggingCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
	}

	@Override
	public boolean check(AbstractEntity e) {
		if (e.isPlayer() && e.getBukkitEntity().hasMetadata(Utils.meta_MMEDIGGING)) {
			return e.getBukkitEntity().getMetadata(Utils.meta_MMEDIGGING).get(0).asString()
					.equals("START_DESTROY_BLOCK");
		}
		return false;
	}
}
