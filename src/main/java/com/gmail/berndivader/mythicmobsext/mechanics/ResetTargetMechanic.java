package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "resettarget,settarget_ext", author = "BerndiVader")
public class ResetTargetMechanic extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
	boolean event, trigger, set;
	TargetReason reason;

	public ResetTargetMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;

		set = line.charAt(0) == 's';
		event = mlc.getBoolean("event", false);
		trigger = mlc.getBoolean("trigger", false);
		try {
			reason = TargetReason.valueOf(mlc.getString("reason", "custom").toUpperCase());
		} catch (Exception ex) {
			ex.printStackTrace();
			reason = TargetReason.CUSTOM;
		}
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		return this.castAtEntity(data, data.getCaster().getEntity());
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isLiving()) {
			if (data.getCaster().getEntity().isCreature()) {
				Creature creature = (Creature) data.getCaster().getEntity().getBukkitEntity();
				creature.setTarget(set ? (LivingEntity) target.getBukkitEntity() : null);
			} else {
				NMSUtils.setGoalTarget(data.getCaster().getEntity().getBukkitEntity(),
						set ? target.getBukkitEntity() : null, reason, event);
			}
			if (trigger && Utils.mobmanager.isActiveMob(data.getCaster().getEntity())) {
				new TriggeredSkill(SkillTriggers.TARGETCHANGE,
						Utils.mobmanager.getMythicMobInstance(data.getCaster().getEntity()), target, true);
			}
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}
