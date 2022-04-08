package com.gmail.berndivader.MythicPlayers.Mechanics;

import java.util.Optional;

import com.gmail.berndivader.MythicPlayers.ActivePlayer;
import com.gmail.berndivader.MythicPlayers.MythicPlayers;
import com.gmail.berndivader.MythicPlayers.PlayerManager;

import com.gmail.berndivader.mythicmobsext.utils.Utils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;

public class mmNormalPlayer extends SkillMechanic implements ITargetedEntitySkill {
	protected PlayerManager playermanager = MythicPlayers.inst().getPlayerManager();
	protected MobManager mobmanager = Utils.mobmanager;

	public mmNormalPlayer(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (!mobmanager.getActiveMobs().contains(target))
			return SkillResult.CONDITION_FAILED;
		Optional<ActivePlayer> maybePlayer = playermanager.getActivePlayer(target.getUniqueId());
		if (maybePlayer.isPresent()) {
			playermanager.makeNormalPlayer(maybePlayer.get());
		}
		return SkillResult.SUCCESS;
	}

}
