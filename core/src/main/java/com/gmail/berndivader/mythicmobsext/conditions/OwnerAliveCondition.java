package com.gmail.berndivader.mythicmobsext.conditions;

import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.skills.conditions.IEntityCondition;

@ExternalAnnotation(name = "owneralive", author = "BerndiVader")
public class OwnerAliveCondition extends AbstractCustomCondition implements IEntityCondition {

	public OwnerAliveCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
	}

	@Override
	public boolean check(AbstractEntity e) {
		if (Utils.mobmanager.isActiveMob(e)) {
			ActiveMob am = Utils.mobmanager.getMythicMobInstance(e);
			if (am.getOwner().isPresent()) {
				Entity o = NMSUtils.getEntity(e.getBukkitEntity().getWorld(), am.getOwner().get());
				if (o != null && !o.isDead())
					return true;
			}
		}
		return false;
	}
}
