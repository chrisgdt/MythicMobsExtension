package com.gmail.berndivader.mythicmobsext.mechanics;

import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;

@ExternalAnnotation(name = "swap", author = "BerndiVader")
public class SwapMechanic extends SkillMechanic implements ITargetedEntitySkill {
	private boolean keepTargetYaw;
	private boolean keepCasterYaw;

	public SwapMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.keepTargetYaw = mlc.getBoolean(new String[] { "keeptargetyaw", "kty" }, false);
		this.keepCasterYaw = mlc.getBoolean(new String[] { "keepcasteryaw", "kcy" }, false);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		AbstractLocation tl = target.getLocation().clone();
		AbstractLocation cl = data.getCaster().getLocation().clone();
		if (this.keepTargetYaw)
			cl.setYaw(target.getLocation().getYaw());
		if (this.keepCasterYaw)
			tl.setYaw(data.getCaster().getLocation().getYaw());
		target.teleport(cl);
		data.getCaster().getEntity().teleport(tl);
		return SkillResult.SUCCESS;
	}

}
