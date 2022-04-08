package com.gmail.berndivader.mythicmobsext.targeters;

import java.util.HashSet;

import com.gmail.berndivader.mythicmobsext.externals.*;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.SkillExecutor;

@ExternalAnnotation(name = "eyedirection", author = "BerndiVader")
public class EyeDirectionTargeter extends ISelectorLocation {

	public EyeDirectionTargeter(SkillExecutor manager, MythicLineConfig mlc) {
		super(manager, mlc);
	}

	@Override
	public HashSet<AbstractLocation> getLocations(SkillMetadata data) {
		HashSet<AbstractLocation> targets = new HashSet<>();
		if (data.getCaster().getEntity().isLiving()) {
			targets.add(data.getCaster().getEntity().getEyeLocation());
		}
		return applyOffsets(targets);
	}

}
