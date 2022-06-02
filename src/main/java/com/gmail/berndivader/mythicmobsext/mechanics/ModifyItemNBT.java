package com.gmail.berndivader.mythicmobsext.mechanics;

import java.util.HashMap;
import java.util.Map;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.variables.VariableMechanic;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;

@ExternalAnnotation(name = "modifyitemnbt,setitemnbt", author = "Seyarada")
public class ModifyItemNBT extends VariableMechanic implements ITargetedEntitySkill {

	private final String where;
	private final String NBTkey;
	private final PlaceholderString NBTvalue;
	private SkillMetadata skill;
	private AbstractEntity abstract_entity;
	
	public ModifyItemNBT(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.where = mlc.getString(new String[] { "where", "w" }, "HAND");
		this.NBTkey = mlc.getString(new String[] { "key", "k" }, "Hello");
		this.NBTvalue = PlaceholderString.of(mlc.getString(new String[] { "value", "v" }, "World"));
		
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		skill = data; abstract_entity = target;
		if (!target.isLiving())
			return SkillResult.CONDITION_FAILED;
		LivingEntity entity = (LivingEntity) target.getBukkitEntity();
		EntityEquipment equipment = entity.getEquipment();
		
		switch (where) {
			case "HAND":
				equipment.setItemInMainHand(setItemNBT(equipment.getItemInMainHand().clone()));
				break;
			case "OFFHAND":
				equipment.setItemInOffHand(setItemNBT(equipment.getItemInOffHand().clone()));
				break;
			case "HELMET":
				equipment.setHelmet(setItemNBT(equipment.getHelmet().clone()));
				break;
					case "CHESTPLATE":
				equipment.setChestplate(setItemNBT(equipment.getChestplate().clone()));
				break;
			case "LEGGINGS":
				equipment.setLeggings(setItemNBT(equipment.getLeggings().clone()));
				break;
			case "BOOTS":
				equipment.setBoots(setItemNBT(equipment.getBoots().clone()));
				break;
		}
		return SkillResult.SUCCESS;
	}
	
	
	public ItemStack setItemNBT(ItemStack iS) {
	    Map<String, Object> tags = new HashMap<String, Object>();
	    tags.put(NBTkey, NBTvalue.get(skill, abstract_entity));
	    iS = MythicItem.addItemNBT(iS, "Base", tags);
		return iS;
	}
}
