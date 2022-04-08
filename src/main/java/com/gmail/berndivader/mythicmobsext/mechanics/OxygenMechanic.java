package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "oxygen", author = "BerndiVader")
public class OxygenMechanic extends SkillMechanic implements ITargetedEntitySkill {
	private int amount;

	public OxygenMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.amount = mlc.getInteger(new String[] { "amount", "a" }, 1);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (!target.isLiving())
			return SkillResult.CONDITION_FAILED;
		LivingEntity le = (LivingEntity) target.getBukkitEntity();
		le.setRemainingAir(Integer.min(le.getRemainingAir() + amount, le.getMaximumAir()));
		return SkillResult.SUCCESS;
	}

}
