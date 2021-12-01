package com.gmail.berndivader.mythicmobsext.backbags.mechanics;

import java.util.ArrayList;
import java.util.List;

import io.lumine.xikage.mythicmobs.skills.AbstractSkill;
import org.bukkit.inventory.ItemStack;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.backbags.BackBagHelper;
import com.gmail.berndivader.mythicmobsext.backbags.BackBagInventory;

import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.INoTargetSkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.placeholders.parsers.PlaceholderString;

public class CreateBackBag extends SkillMechanic implements INoTargetSkill {
	int size;
	ItemStack[] default_items = null;
	PlaceholderString bag_name;
	boolean temporary, override, flood;
	List<Integer> excluded_slots;

	public CreateBackBag(String skill, MythicLineConfig mlc) {
		super(skill, mlc);
		this.threadSafetyLevel = AbstractSkill.ThreadSafetyLevel.SYNC_ONLY;

		size = mlc.getInteger("size", 9);
		default_items = BackBagHelper.createDefaultItemStack(mlc.getString("items", null));
		if ((flood = mlc.getBoolean("flood", false)) && default_items != null) {
			List<ItemStack> flood = new ArrayList<>();
			for (int i1 = 0; i1 < size; i1++) {
				flood.add(default_items[0].clone());
			}
			default_items = flood.toArray(new ItemStack[size]);
		}
		bag_name = mlc.getPlaceholderString(new String[] { "title", "name" }, BackBagHelper.str_name);
		temporary = mlc.getBoolean("temporary", false);
		override = mlc.getBoolean("override", true);
		String[] temp = mlc.getString("excludedslots", "").split(",");
		excluded_slots = new ArrayList<>();
		for (int i1 = 0; i1 < temp.length; i1++) {
			try {
				if (!temp[i1].isEmpty()) {
					int slot = Integer.parseInt(temp[i1]);
					excluded_slots.add(slot);
				}
			} catch (Exception ex) {
				Main.logger.warning("Ignoring " + temp[i1] + " in skill " + skill + " its not a valid slot number.");
			}
		}
	}

	@Override
	public boolean cast(SkillMetadata data) {
		new BackBagInventory(data.getCaster().getEntity().getUniqueId(), bag_name.get(data), size, default_items,
				temporary, override);
		return true;
	}

}
