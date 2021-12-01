package com.gmail.berndivader.MythicPlayers.Mechanics;

import io.lumine.xikage.mythicmobs.skills.AbstractSkill;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;

public class mmSetTarget extends SkillMechanic implements INoTargetSkill {

	protected String[] filter;
	protected boolean targetself;
	int length;

	public mmSetTarget(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		this.threadSafetyLevel = AbstractSkill.ThreadSafetyLevel.SYNC_ONLY;

		this.filter = mlc.getString(new String[] { "filter", "f" }, "").split(",");
		this.targetself = mlc.getBoolean(new String[] { "selfnotarget", "snt" }, false);
		length = mlc.getInteger("length", 32);
	}

	@Override
	public boolean cast(SkillMetadata data) {
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
		return true;
	}

}
