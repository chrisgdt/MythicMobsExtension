package com.gmail.berndivader.mythicmobsext.conditions;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.spawning.spawners.MythicSpawner;

@ExternalAnnotation(name = "samespawner", author = "BerndiVader")
public class SameMythicSpawnerCondition extends AbstractCustomCondition implements IEntityComparisonCondition {

	public SameMythicSpawnerCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
	}

	@Override
	public boolean check(AbstractEntity caster, AbstractEntity target) {
		ActiveMob cm = Utils.mobmanager.getMythicMobInstance(caster),
				tm = Utils.mobmanager.getMythicMobInstance(target);
		if (cm != null && tm != null) {
			MythicSpawner cs = cm.getSpawner(), ts = tm.getSpawner();
			if (cs != null && ts != null) {
				return cs.getName().equals(ts.getName());
			}
		}
		return false;
	}

}
