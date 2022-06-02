package com.gmail.berndivader.mythicmobsext.mechanics;

import java.util.Iterator;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "cure,removepotion", author = "BerndiVader")
public class RemovePotionEffectMechanic extends SkillMechanic implements ITargetedEntitySkill {
	private String[] type;

	public RemovePotionEffectMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.type = mlc.getString(new String[] { "potion", "p", "type", "t" }, "ALL").toUpperCase().split(",");
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		LivingEntity le = (LivingEntity) target.getBukkitEntity();
		if (this.type[0].equals("ALL")) {
			if (target.hasPotionEffect()) {
				for (Iterator<PotionEffect> i = le.getActivePotionEffects().iterator(); i.hasNext();) {
					le.removePotionEffect(i.next().getType());
				}
			}
		} else {
			try {
				for (String s1 : this.type) {
					if (target.hasPotionEffect(s1)) {
						le.removePotionEffect(PotionEffectType.getByName(s1));
					}
				}
			} catch (Exception ex) {
				return SkillResult.CONDITION_FAILED;
			}
		}
		return SkillResult.SUCCESS;
	}

}
