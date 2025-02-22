package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.mobs.ActiveMob;

import java.util.Optional;

@ExternalAnnotation(name = "ismythicmob", author = "BerndiVader")
public class IsTargetPresentCondition extends AbstractCustomCondition implements IEntityCondition {
	String types[];

	public IsTargetPresentCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		types = mlc.getString("types", "ANY").split(",");
	}

	@Override
	public boolean check(AbstractEntity entity) {
		Optional<ActiveMob> optional_am = Utils.mobmanager.getActiveMob(entity.getUniqueId());
		if (optional_am.isPresent()) {
			if (types[0].equals("ANY"))
				return true;
			String internal_name = optional_am.get().getType().getInternalName();
			int size = types.length;
			for (int i1 = 0; i1 < size; i1++) {
				if (internal_name.equals(types[i1]))
					return true;
			}
		}
		return false;
	}

}
