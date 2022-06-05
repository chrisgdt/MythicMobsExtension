package com.gmail.berndivader.mythicmobsext.thiefs;

import java.util.Iterator;
import java.util.UUID;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class DropStolenItemsMechanic extends SkillMechanic implements INoTargetSkill {

	public DropStolenItemsMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		Entity e1 = data.getCaster().getEntity().getBukkitEntity();
		Iterator<Thief> ti = Thiefs.thiefhandler.getThiefs().iterator();
		UUID uuid = e1.getUniqueId();
		while (ti.hasNext()) {
			Thief thief = ti.next();
			if (uuid.equals((thief.getUuid()))) {
				e1.getWorld().dropItem(e1.getLocation(), new ItemStack(thief.getItem()));
				ti.remove();
			}
		}
		return SkillResult.SUCCESS;
	}
}
