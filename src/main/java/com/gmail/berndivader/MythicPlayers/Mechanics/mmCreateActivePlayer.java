package com.gmail.berndivader.MythicPlayers.Mechanics;

import com.gmail.berndivader.mythicmobsext.utils.Utils;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.MythicPlayers.MythicPlayers;
import com.gmail.berndivader.MythicPlayers.PlayerManager;

import java.io.File;
import java.util.Optional;

public class mmCreateActivePlayer extends SkillMechanic implements ITargetedEntitySkill {
	protected MobManager mobmanager = Utils.mobmanager;
	protected PlayerManager playermanager = MythicPlayers.inst().getPlayerManager();
	private String mobtype;

	public mmCreateActivePlayer(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.mobtype = mlc.getString(new String[] { "mobtype", "type", "mob", "t", "m" }, "Player");
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (!target.isPlayer()) {
			return SkillResult.ERROR;
		}
		Optional<MythicMob> omm = mobmanager.getMythicMob(this.mobtype);
		if (omm.isEmpty()) {
			return SkillResult.ERROR;
		}
		MythicMob mm = omm.get();
		if (playermanager.isActivePlayer(target.getUniqueId()))
			return SkillResult.ERROR;
		playermanager.createActivePlayer((LivingEntity) target.getBukkitEntity(), mm);
		return SkillResult.SUCCESS;
	}

}
