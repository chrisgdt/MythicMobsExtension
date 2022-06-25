package com.gmail.berndivader.mythicmobsext.targeters;

import java.util.HashSet;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "triggerdirection,targetdirection,ownerdirection", author = "BerndiVader")
public class TriggerDirectionTargeter extends ISelectorLocation {
	private char c;

	public TriggerDirectionTargeter(SkillExecutor manager, MythicLineConfig mlc) {
		super(manager, mlc);
		this.length = mlc.getInteger(new String[] { "length", "l" }, 10);
		c(mlc.getLine().toLowerCase().charAt(1));
	}

	@Override
	public HashSet<AbstractLocation> getLocations(SkillMetadata data) {
		HashSet<AbstractLocation> targets = new HashSet<>();
		AbstractLocation l = null;
		switch (c) {
		case 'a':
			if (data.getCaster().getEntity().getTarget() != null
					&& data.getCaster().getEntity().getTarget().isLiving()) {
				l = data.getCaster().getEntity().getTarget().getEyeLocation();
			}
		case 'r':
			if (data.getTrigger().isLiving()) {
				l = data.getTrigger().getEyeLocation();
			}
		case 'w':
			ActiveMob am = Utils.mobmanager.getMythicMobInstance(data.getCaster().getEntity());
			if (am != null && am.getOwner() != null && am.getOwner().isPresent()) {
				Entity o = NMSUtils.getEntity(am.getEntity().getBukkitEntity().getWorld(), am.getOwner().get());
				if (o instanceof LivingEntity)
					l = BukkitAdapter.adapt(((LivingEntity) o).getEyeLocation());
			}
		}
		if (l != null) {
			targets.add(l);
		}
		return applyOffsets(targets);
	}

	private void c(char c) {
		this.c = c;
	}

}
