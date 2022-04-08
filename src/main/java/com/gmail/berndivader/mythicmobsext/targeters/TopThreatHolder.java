package com.gmail.berndivader.mythicmobsext.targeters;

import java.util.HashSet;

import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;

@ExternalAnnotation(name = "tth,topthreatholder", author = "BerndiVader")
public class TopThreatHolder extends ISelectorEntity {

	public TopThreatHolder(SkillExecutor manager, MythicLineConfig mlc) {
		super(manager, mlc);
	}

	@Override
	public HashSet<AbstractEntity> getEntities(SkillMetadata data) {
		HashSet<AbstractEntity> targets = new HashSet<>();
		ActiveMob am = (ActiveMob) data.getCaster();
		if (am != null && am.hasThreatTable())
			targets.add(am.getThreatTable().getTopThreatHolder());
		return this.applyOffsets(targets);
	}
}
