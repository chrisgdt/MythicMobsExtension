package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityComparisonCondition;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "isvehicle", author = "BerndiVader")
public class IsVehicleCondition extends AbstractCustomCondition implements IEntityComparisonCondition {

	public IsVehicleCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
	}

	@Override
	public boolean check(AbstractEntity caster, AbstractEntity target) {
		Entity e1 = caster.getBukkitEntity();
		if (e1.getVehicle() != null)
			return e1.getVehicle().equals(target.getBukkitEntity());
		return false;
	}
}
