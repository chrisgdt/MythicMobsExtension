package com.gmail.berndivader.mythicmobsext.mechanics;

import java.util.Collections;
import java.util.List;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.gmail.berndivader.mythicmobsext.backbags.BackBagHelper;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.items.HoldingItem;

@ExternalAnnotation(name = "dropinventory", author = "BerndiVader")
public class DropInventoryMechanic extends SkillMechanic implements ITargetedEntitySkill {

	private HoldingItem holding;
	private int pd;
	private int p;
	boolean c;

	public DropInventoryMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		String tmp = mlc.getString(new String[] { "item" }, null);
		this.holding = new HoldingItem();
		if (tmp == null) {
			this.holding.setMaterial("ANY");
			this.holding.setWhere("ANY");
			this.holding.setName("ANY");
			this.holding.setLore("ANY");
			this.holding.setEnchantment("ANY");
			this.holding.setBagName(BackBagHelper.str_name);
			this.holding.setSlot("-7331");
			this.holding.setAmount("1");
		} else {
			if (tmp.startsWith("\""))
				tmp = tmp.substring(1, tmp.length() - 1);
			tmp = SkillString.parseMessageSpecialChars(tmp);
			HoldingItem.parse(tmp, holding);
		}
		this.pd = mlc.getInteger(new String[] { "pickupdelay", "pd" }, 20);
		this.p = mlc.getInteger(new String[] { "pieces", "amount", "a", "p" }, 1);
		this.c = mlc.getBoolean(new String[] { "clear", "nodrop", "nd" }, false);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target.isLiving()) {
			HoldingItem holding = this.holding.clone();
			if (holding != null) {
				holding.parseSlot(data, target);
				if (holding.getBagName() != null)
					holding.setBagName(new PlaceholderStringImpl(holding.getBagName()).get(data, target));
				final LivingEntity entity = (LivingEntity) target.getBukkitEntity();
				final Location location = target.getBukkitEntity().getLocation();
				for (int a = 0; a < this.p; a++) {
					List<ItemStack> contents = HoldingItem.getContents(holding, entity);
					Collections.shuffle(contents);
					for (int i1 = 0; i1 < contents.size(); i1++) {
						ItemStack item_stack = contents.get(i1);
						if (holding.stackMatch(item_stack, true)) {
							HoldingItem.spawnItem(item_stack, holding, location, this.pd, this.c);
							break;
						}
					}
				}
			}
		}
		return SkillResult.SUCCESS;
	}

}
