package com.gmail.berndivader.mythicmobsext.healthbar;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;

import java.io.File;

public class RemoveSpeechBubbleMechanic extends SkillMechanic implements INoTargetSkill {
	private String id;

	public RemoveSpeechBubbleMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
		this.id = mlc.getString("id", "bubble");
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		if (HealthbarHandler.speechbubbles
				.containsKey(data.getCaster().getEntity().getUniqueId().toString() + this.id)) {
			HealthbarHandler.speechbubbles.get(data.getCaster().getEntity().getUniqueId().toString() + this.id)
					.remove();
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}
