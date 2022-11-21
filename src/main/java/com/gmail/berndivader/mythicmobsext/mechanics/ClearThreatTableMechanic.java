package com.gmail.berndivader.mythicmobsext.mechanics;

import com.gmail.berndivader.mythicmobsext.externals.*;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;

import java.io.File;

@ExternalAnnotation(name = "clearthreattarget,dropcombat", author = "BerndiVader")
public class ClearThreatTableMechanic extends SkillMechanic implements INoTargetSkill {

	public ClearThreatTableMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		if (data.getCaster() instanceof ActiveMob) {
			ActiveMob am = (ActiveMob) data.getCaster();
			if (am.hasThreatTable()) {
				am.getThreatTable().getAllThreatTargets().clear();
				am.getThreatTable().dropCombat();
				return SkillResult.SUCCESS;
			}
		}
		return SkillResult.CONDITION_FAILED;
	}

}
