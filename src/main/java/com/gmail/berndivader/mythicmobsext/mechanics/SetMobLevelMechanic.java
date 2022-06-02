package com.gmail.berndivader.mythicmobsext.mechanics;

import java.util.Optional;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;

@ExternalAnnotation(name = "setrandomlevel,setmoblevel", author = "BerndiVader")
public class SetMobLevelMechanic extends SkillMechanic implements ITargetedEntitySkill {
	private PlaceholderString a;

	public SetMobLevelMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
		this.a = new PlaceholderStringImpl(mlc.getString(new String[] { "amount", "a" }, "1").toLowerCase());
		r(mlc.getInteger("min", -1), mlc.getInteger("max", -1));
	}

	private void r(int min, int max) {
		if (min > -1 && max > -1 && max >= min) {
			a = new PlaceholderStringImpl(Integer.toString(min) + "to" + Integer.toString(max));
		}
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (Utils.mobmanager.isActiveMob(target)) {
			ActiveMob am = Utils.mobmanager.getMythicMobInstance(target);
			try {
				Optional<String> mount = (Optional<String>) NMSUtils.getField(MythicMob.class, "mount", am.getType());
				if (am.getMount().isPresent() && mount.isPresent())
					am.getMount().get().getEntity().remove();
				am.setLevel(MathUtils.randomRangeInt(a.get(data, target)));
			} catch (NullPointerException ex) {
				Main.logger.warning("Failed to set moblevel with for: " + this.config.getLine());
			}
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}
