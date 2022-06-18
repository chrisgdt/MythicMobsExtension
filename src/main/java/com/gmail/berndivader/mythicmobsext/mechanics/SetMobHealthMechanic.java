package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.config.ConfigExecutor;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

@ExternalAnnotation(name = "setmobhealth", author = "BerndiVader")
public class SetMobHealthMechanic extends SkillMechanic implements INoTargetSkill, ITargetedEntitySkill {
	private PlaceholderString r;
	private char m;
	private boolean b, b1;

	public SetMobHealthMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.r = new PlaceholderStringImpl(
				mlc.getString(new String[] { "amount", "a", "health", "h" }, "20").toLowerCase());
		this.b = mlc.getBoolean(new String[] { "ignoremodifier", "im" }, true);
		this.b1 = mlc.getBoolean(new String[] { "setcurrenthealth", "sch" }, true);
		this.m = mlc.getString(new String[] { "mode", "m", "set", "s" }, "S").toUpperCase().charAt(0);
	}

	SkillResult c(SkillMetadata data, AbstractEntity t) {
		if (t.isValid() && t.isLiving()) {
			ActiveMob am = Utils.mobmanager.getMythicMobInstance(t);
			double h = 20, mod = 0;
			h = MathUtils.randomRangeDouble(this.r.get(data, t));
			if (!b && am != null) {
				mod = ConfigExecutor.defaultLevelModifierHealth.startsWith("+")
						? Double.valueOf(ConfigExecutor.defaultLevelModifierHealth.substring(1))
						: (ConfigExecutor.defaultLevelModifierHealth.startsWith("*")
						? h * Double.valueOf(ConfigExecutor.defaultLevelModifierHealth.substring(1))
						: h * Double.valueOf(ConfigExecutor.defaultLevelModifierHealth));
			}

			if (am != null && am.getLevel() > 1 && mod > 0.0)
				h += mod * (am.getLevel() - 1);
			LivingEntity e = (LivingEntity) t.getBukkitEntity();
			switch (m) {
				case 'A':
					h += e.getMaxHealth();
					break;
				case 'M':
					h = e.getMaxHealth() * h;
					break;
				case 'R':
					h = e.getMaxHealth() - h < 1 ? 1 : e.getMaxHealth() - h;
					break;
			}
			h = Math.ceil(h);
			e.setMaxHealth(h);
			if (b1)
				e.setHealth(e.getMaxHealth());
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		return c(data, data.getCaster().getEntity());
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity t) {
		return c(data, t);
	}

}
