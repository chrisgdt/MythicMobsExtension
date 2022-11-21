package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

import java.io.File;

@ExternalAnnotation(name = "setitemcooldown", author = "BerndiVader")
public class SetItemCooldownMechanic extends SkillMechanic implements ITargetedEntitySkill {
	int j1;
	int i1;

	public SetItemCooldownMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		j(mlc.getInteger(new String[] { "ticks", "t" }, 0));
		i(mlc.getInteger(new String[] { "slot", "s" }, -1));
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isPlayer()) {
			Player p = (Player) target.getBukkitEntity();
			return Volatile.handler.setItemCooldown(p, j1, i1);
		}
		return SkillResult.CONDITION_FAILED;
	}

	void j(int j) {
		this.j1 = j;
	}

	void i(int j) {
		this.i1 = j;
	}
}
