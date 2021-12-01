package com.gmail.berndivader.mythicmobsext.mechanics;

import java.util.Optional;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import io.lumine.xikage.mythicmobs.skills.AbstractSkill;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;

@ExternalAnnotation(name = "setrandomlevel,setmoblevel", author = "BerndiVader")
public class SetMobLevelMechanic extends SkillMechanic implements ITargetedEntitySkill {
	private PlaceholderString a;

	public SetMobLevelMechanic(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		this.threadSafetyLevel = AbstractSkill.ThreadSafetyLevel.SYNC_ONLY;
		this.a = new PlaceholderString(mlc.getString(new String[] { "amount", "a" }, "1").toLowerCase());
		r(mlc.getInteger("min", -1), mlc.getInteger("max", -1));
	}

	private void r(int min, int max) {
		if (min > -1 && max > -1 && max >= min) {
			a = new PlaceholderString(Integer.toString(min) + "to" + Integer.toString(max));
		}
	}

	@Override
	public boolean castAtEntity(SkillMetadata data, AbstractEntity target) {
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
			return true;
		}
		return false;
	}
}
