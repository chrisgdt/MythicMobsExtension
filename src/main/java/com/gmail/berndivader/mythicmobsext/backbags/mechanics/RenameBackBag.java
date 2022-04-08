package com.gmail.berndivader.mythicmobsext.backbags.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.backbags.BackBagHelper;
import com.gmail.berndivader.mythicmobsext.backbags.BackBagInventory;

public class RenameBackBag extends SkillMechanic implements INoTargetSkill {
	PlaceholderString bag_name, new_bag_name;

	public RenameBackBag(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		bag_name = mlc.getPlaceholderString(new String[] { "title", "name" }, BackBagHelper.str_name);
		new_bag_name = mlc.getPlaceholderString(new String[] { "new", "newname" }, BackBagHelper.str_name);
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		Entity entity = data.getCaster().getEntity().getBukkitEntity();
		if (BackBagHelper.hasBackBag(entity.getUniqueId())) {
			BackBagInventory bag_inventory = BackBagHelper.getBagInventory(entity.getUniqueId(), bag_name.get(data));
			if (bag_inventory != null)
				bag_inventory.setName(new_bag_name.get(data));
		}
		return SkillResult.SUCCESS;
	}
}
