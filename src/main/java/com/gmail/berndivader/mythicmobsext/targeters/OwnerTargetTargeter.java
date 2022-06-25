package com.gmail.berndivader.mythicmobsext.targeters;

import java.util.HashSet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "ownertarget", author = "BerndiVader")
public class OwnerTargetTargeter extends ISelectorEntity {

	public OwnerTargetTargeter(SkillExecutor manager, MythicLineConfig mlc) {
		super(manager, mlc);
	}

	@Override
	public HashSet<AbstractEntity> getEntities(SkillMetadata data) {
		HashSet<AbstractEntity> targets = new HashSet<>();
		if (Utils.mobmanager.isActiveMob(data.getCaster().getEntity())) {
			ActiveMob am = Utils.mobmanager.getMythicMobInstance(data.getCaster().getEntity());
			if (am.getOwner() != null && am.getOwner().isPresent()) {
				Entity owner = NMSUtils.getEntity(am.getEntity().getBukkitEntity().getWorld(), am.getOwner().get());
				if (owner != null) {
					AbstractEntity pt;
					if (owner instanceof Creature) {
						if ((pt = BukkitAdapter.adapt(((Creature) owner).getTarget())) != null)
							targets.add(pt);
					} else if (owner instanceof Player) {
						if ((pt = BukkitAdapter.adapt(Utils.getTargetedEntity((Player) owner, length))) != null)
							targets.add(pt);
					} else if (owner.getLastDamageCause() != null) {
						targets.add(BukkitAdapter.adapt(owner.getLastDamageCause().getEntity()));
					}
				}
			}
		}
		return this.applyOffsets(targets);
	}
}
