package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "changegamemode", author = "BerndiVader")
public class ChangeGamemodeMechanic extends SkillMechanic implements ITargetedEntitySkill {

	GameMode mode;

	public ChangeGamemodeMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		try {
			mode = GameMode.valueOf(mlc.getString("mode", "SURVIVAL").toUpperCase());
		} catch (Exception ex) {
			mode = GameMode.SURVIVAL;
			Main.logger.warning("UNKNOWN GAMEMODETYPE. USING SURVIVAL INSTEAD");
		}

	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity entity) {
		if (entity.isPlayer()) {
			((Player) entity.getBukkitEntity()).setGameMode(mode);
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

}
