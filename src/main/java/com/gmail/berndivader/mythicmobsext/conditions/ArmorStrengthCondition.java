package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;

@ExternalAnnotation(name = "armorstrength", author = "BerndiVader")
public class ArmorStrengthCondition extends AbstractCustomCondition implements IEntityCondition {
	RangedDouble range;

	public ArmorStrengthCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		range = new RangedDouble(mlc.getString(new String[] { "range", "r", "a", "amount" }, ">0"));
	}

	@Override
	public boolean check(AbstractEntity entity) {
		if (entity.isLiving()) {
			return range.equals(NMSUtils.getArmorStrength((LivingEntity) entity.getBukkitEntity()));
		}
		return false;
	}
}
