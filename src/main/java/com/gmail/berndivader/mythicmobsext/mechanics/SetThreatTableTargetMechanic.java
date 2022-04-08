package com.gmail.berndivader.mythicmobsext.mechanics;

import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;

@ExternalAnnotation(name = "setthreattarget", author = "BerndiVader")
public class SetThreatTableTargetMechanic extends SkillMechanic implements ITargetedEntitySkill {
	private double amount;

	public SetThreatTableTargetMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.amount = mlc.getDouble(new String[] { "amount", "a" }, 65536);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (data.getCaster() instanceof ActiveMob) {
			ActiveMob am = (ActiveMob) data.getCaster();
			if (am.getThreatTable().size() > 0) {
				am.getThreatTable().clearTarget();
				am.getThreatTable().getAllThreatTargets().clear();
			}
			if (target != null) {
				am.getThreatTable().threatGain(target, this.amount);
				am.getThreatTable().targetHighestThreat();
				new TriggeredSkill(SkillTriggers.ENTERCOMBAT, am, target);
			}
			return SkillResult.SUCCESS;
		} else {
			return SkillResult.CONDITION_FAILED;
		}
	}

}
