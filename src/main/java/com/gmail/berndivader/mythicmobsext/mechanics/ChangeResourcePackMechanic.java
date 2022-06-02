package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

@ExternalAnnotation(name = "changeresourcepack", author = "BerndiVader")
public class ChangeResourcePackMechanic extends SkillMechanic implements ITargetedEntitySkill {

	String url, hash;

	public ChangeResourcePackMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		url = mlc.getString("url", "");
		hash = mlc.getString("hash", "mme");
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity entity) {
		if (entity.isPlayer()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Volatile.handler.changeResPack((Player) entity.getBukkitEntity(), url, hash);
				}
			}.runTaskAsynchronously(Main.getPlugin());
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

}
