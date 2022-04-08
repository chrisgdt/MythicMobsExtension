package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

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
