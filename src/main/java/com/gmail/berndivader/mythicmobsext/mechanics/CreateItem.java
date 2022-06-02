package com.gmail.berndivader.mythicmobsext.mechanics;

import java.util.Optional;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.items.ItemManager;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitItemStack;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.items.HoldingItem;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "giveitem", author = "BerndiVader")
public class CreateItem extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {

	static ItemManager itemmanager = Utils.mythicmobs.getItemManager();
	public final static String str_viewonly = "view_only";

	PlaceholderString bag_name;
	PlaceholderString item_name;
	String click_skill;
	HoldingItem holding;
	boolean override;
	Optional<Boolean> view_only = Optional.empty();
	PlaceholderInt amount;

	public CreateItem(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		holding = new HoldingItem();
		this.holding.setWhere(mlc.getString("to", "inventory"));
		this.holding.setSlot(mlc.getString("slot", "-1"));
		this.holding.setBagName(mlc.getString("bagname"));

		this.item_name = PlaceholderString.of(mlc.getString("item"));
		this.amount = PlaceholderInt.of(mlc.getString("amount", "1"));
		this.override = mlc.getBoolean("override", true);
		this.click_skill = mlc.getString("clickskill");
		if (mlc.getLine().contains("viewonly")) {
			this.view_only = Optional.ofNullable(mlc.getBoolean("viewonly"));
		}
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		castAtEntity(data, data.getCaster().getEntity());
		return SkillResult.SUCCESS;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity abstract_entity) {
		HoldingItem holding = this.holding.clone();
		if (holding != null) {
			if (item_name == null || !abstract_entity.isLiving())
				return SkillResult.CONDITION_FAILED;
			holding.parseSlot(data, abstract_entity);
			if (bag_name != null)
				holding.setBagName(this.bag_name.get(data, abstract_entity));
			Optional<MythicItem> o_item_stack = itemmanager.getItem(this.item_name.get(data, abstract_entity));
			if (o_item_stack.isPresent()) {
				ItemStack item_stack = BukkitAdapter.adapt(o_item_stack.get().generateItemStack(amount.get(data, abstract_entity)));
				item_stack = NMSUtils.makeReal(item_stack);
				if (this.click_skill != null)
					NMSUtils.setMeta(item_stack, Utils.meta_CLICKEDSKILL, this.click_skill);
				if (this.view_only.isPresent())
					NMSUtils.setMetaBoolean(item_stack, str_viewonly, view_only.get());
				HoldingItem.giveItem((LivingEntity) abstract_entity.getBukkitEntity(), holding, item_stack, override);
				return SkillResult.SUCCESS;
			}
		}
		return SkillResult.ERROR;
	}

}
