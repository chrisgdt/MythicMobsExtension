package com.gmail.berndivader.MythicPlayers.Mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.utils.Utils;

import java.io.File;

public class mmSetTarget extends SkillMechanic implements INoTargetSkill {

	protected String[] filter;
	protected boolean targetself;
	int length;

	public mmSetTarget(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.filter = mlc.getString(new String[] { "filter", "f" }, "").split(",");
		this.targetself = mlc.getBoolean(new String[] { "selfnotarget", "snt" }, false);
		length = mlc.getInteger("length", 32);
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		LivingEntity le;
		if (data.getCaster().getEntity().isPlayer() && (data.getCaster() instanceof ActiveMob)) {
			ActiveMob am = (ActiveMob) data.getCaster();
			if (am.getThreatTable().size() > 0) {
				am.getThreatTable().clearTarget();
				am.getThreatTable().getAllThreatTargets().clear();
			}
			le = Utils.getTargetedEntity((Player) BukkitAdapter.adapt(data.getCaster().getEntity()), length);
			if (le != null) {
				am.getThreatTable().threatGain(BukkitAdapter.adapt(le), 99999999);
				am.getThreatTable().targetHighestThreat();
			} else if (this.targetself) {
				am.getThreatTable().threatGain(am.getEntity(), 99999999);
				am.getThreatTable().targetHighestThreat();
			}
		}
		return SkillResult.SUCCESS;
	}

}
