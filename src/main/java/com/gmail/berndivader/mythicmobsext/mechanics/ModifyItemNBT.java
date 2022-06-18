package com.gmail.berndivader.mythicmobsext.mechanics;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.variables.VariableMechanic;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.CompoundTagBuilder;
import org.bukkit.Material;
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
	    Map<String, Object> tags = new HashMap<>();
	    tags.put(NBTkey, NBTvalue.get(skill, abstract_entity));
	    //iS = MythicItem.addItemNBT(iS, "Base", tags);
		iS = addItemNBT(iS, tags);
		return iS;
	}

	public static ItemStack addItemNBT(ItemStack itemStack, Map<String, Object> pairs) {
		if (itemStack != null && itemStack.getType() != Material.AIR) {
			CompoundTag compoundTag = MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().getNBTData(itemStack);
			CompoundTagBuilder builder;
			Iterator<?> var6;
			Map.Entry<?,?> entry;
			Object val;
			builder = compoundTag.createBuilder();
			var6 = pairs.entrySet().iterator();

			while(var6.hasNext()) {
				entry = (Map.Entry<?,?>) var6.next();
				val = entry.getValue();
				if (val instanceof Integer) {
					builder.putInt((String)entry.getKey(), (Integer)val);
				} else if (val instanceof Double) {
					builder.putDouble((String)entry.getKey(), (Double)val);
				} else if (val instanceof Float) {
					builder.putFloat((String)entry.getKey(), (Float)val);
				} else if (val instanceof Boolean) {
					builder.putBoolean((String)entry.getKey(), (Boolean)val);
				} else if (val instanceof Byte) {
					builder.putByte((String)entry.getKey(), (Byte)val);
				} else {
					builder.putString((String)entry.getKey(), val.toString());
				}
			}
			compoundTag = builder.build();
			return MythicBukkit.inst().getVolatileCodeHandler().getItemHandler().setNBTData(itemStack, compoundTag);
		} else {
			return null;
		}
	}

}
