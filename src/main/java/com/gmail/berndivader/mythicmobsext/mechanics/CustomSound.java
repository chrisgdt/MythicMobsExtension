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

import java.io.File;

@ExternalAnnotation(name = "customsound", author = "BerndiVader")
public class CustomSound extends SkillMechanic implements ITargetedEntitySkill {
	String type;
	float volume, pitch;

	public CustomSound(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;

		type = mlc.getString("type", "block.chest.open");
		volume = mlc.getFloat("volume", 1f);
		pitch = mlc.getFloat("pitch", 1f);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity var2) {
		if (var2.isPlayer()) {
			playSoundAtPlayer(this.type, this.volume, this.pitch, (Player) var2.getBukkitEntity());
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

	private static boolean playSoundAtPlayer(String name, float volume, float pitch, Player player) {
		if (player.isOnline()) {
			player.playSound(player.getLocation(), name, volume, pitch);
			return true;
		}
		return false;
	}

}
