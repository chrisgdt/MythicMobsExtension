package com.gmail.berndivader.mythicmobsext.backbags.mechanics;

import com.gmail.berndivader.mythicmobsext.backbags.BackBagHelper;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;

public class RemoveBackBag extends SkillMechanic implements INoTargetSkill {
	PlaceholderString bag_name;
	boolean all;

	public RemoveBackBag(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		bag_name = mlc.getPlaceholderString(new String[] { "title", "name" }, BackBagHelper.str_name);
		all = mlc.getBoolean("all", false);
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		if (all) {
			BackBagHelper.removeAll(data.getCaster().getEntity().getUniqueId());
		} else {
			BackBagHelper.remove(data.getCaster().getEntity().getUniqueId(), bag_name.get(data));
		}
		return SkillResult.SUCCESS;
	}
}
