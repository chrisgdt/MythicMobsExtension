package com.gmail.berndivader.mythicmobsext.mechanics;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "sunresist", author = "BerndiVader")
public class ResistSunMechanic extends SkillMechanic implements INoTargetSkill {
	public ResistSunMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		data.getCaster().getEntity().getBukkitEntity().setMetadata(Utils.meta_NOSUNBURN,
				new FixedMetadataValue(Main.getPlugin(), true));
		return SkillResult.SUCCESS;
	}

}
