package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "playanimationmme", author = "BerndiVader")
public class PlayAnimationMechanic extends SkillMechanic implements ITargetedEntitySkill {
	int[] ids;

	public PlayAnimationMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		String[] parse = mlc.getString(new String[] { "id", "ids" }, "0").split(",");
		ids = new int[parse.length];
		for (int i1 = 0; i1 < parse.length; i1++) {
			try {
				int j1 = Integer.parseInt(parse[i1]);
				if (j1 < 0 || j1 > 5) {
					Main.logger.warning("Integer for animation at " + skill + " out of range.");
					j1 = 0;
				}
				ids[i1] = j1;
			} catch (Exception ex) {
				Main.logger.warning("Invalid Integer for animation at " + skill + " set to default 0");
				ids[i1] = 0;
			}
		}
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isLiving()) {
			LivingEntity e = (LivingEntity) target.getBukkitEntity();
			Volatile.handler.playAnimationPacket(e, this.ids);
		}
		return SkillResult.SUCCESS;
	}

}
