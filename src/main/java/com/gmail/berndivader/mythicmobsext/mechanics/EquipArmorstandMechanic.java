package com.gmail.berndivader.mythicmobsext.mechanics;

import java.util.Optional;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "asequip", author = "BerndiVader")
public class EquipArmorstandMechanic extends SkillMechanic implements INoTargetSkill {
	Optional<MythicItem> mythicItem;
	PlaceholderString item;
	Material material;
	String[] parse;
	int slot;
	int pos;
	
	public EquipArmorstandMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		parse = mlc.getString(new String[] { "item", "i" }).split(":");
		item = PlaceholderString.of(parse[0]);
		pos = Integer.parseInt(parse[1]);

	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		if (data.getCaster().getEntity().getBukkitEntity() instanceof ArmorStand) {
			AbstractEntity target = data.getCaster().getEntity();
			
			try {
				this.material = Material.valueOf(item.get(data, target));
			} catch (Exception e) {this.material = Material.DIRT;}
			
			if (parse.length == 2) this.slot = pos;
			mythicItem = Utils.mythicmobs.getItemManager().getItem(item.get(data, target));
			
			ArmorStand as = (ArmorStand) data.getCaster().getEntity().getBukkitEntity();
			ItemStack is = mythicItem.isPresent() ? BukkitAdapter.adapt(mythicItem.get().generateItemStack(1))
					: new ItemStack(this.material, 1);
			
			switch (slot) {
				case 0: {
					as.getEquipment().setItemInHand(is);
					break;
					}
				case 1: {
					as.getEquipment().setBoots(is);
					break;
					}
				case 2: {
					as.getEquipment().setLeggings(is);
					break;
					}
				case 3: {
					as.getEquipment().setChestplate(is);
					break;
					}
				case 4: {
					as.getEquipment().setHelmet(is);
					break;
					}
				case 5: {
					as.getEquipment().setItemInOffHand(is);
					break;
				}
			}
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}
}
