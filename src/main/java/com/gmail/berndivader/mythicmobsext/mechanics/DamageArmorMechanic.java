package com.gmail.berndivader.mythicmobsext.mechanics;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowman;
import org.bukkit.inventory.ItemStack;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "damagearmor", author = "BerndiVader")
public class DamageArmorMechanic extends SkillMechanic implements ITargetedEntitySkill {
	protected HashSet<String> armortype;
	protected int rndMin, rndMax;
	protected String signal;

	public DamageArmorMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.armortype = new HashSet<>();
		this.armortype.addAll(
				Arrays.asList(mlc.getString(new String[] { "armor", "a", "armour" }, "all").toLowerCase().split(",")));
		String[] maybeRnd = mlc.getString(new String[] { "damage", "dmg", "d" }, "1").split("to");
		if (maybeRnd.length > 1) {
			this.rndMin = Integer.parseInt(maybeRnd[0]);
			this.rndMax = Integer.parseInt(maybeRnd[1]);
		} else {
			this.rndMin = Integer.parseInt(maybeRnd[0]);
			this.rndMax = Integer.parseInt(maybeRnd[0]);
		}
		this.signal = mlc.getString(new String[] { "signal", "s" }, null);
	}

	@SuppressWarnings("deprecation")
	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		if (target == null || !target.isLiving() || target.isDead()) {
			return SkillResult.CONDITION_FAILED;
		}
		if (target.getBukkitEntity().getType().equals(EntityType.SNOW_GOLEM)) {
			NMSUtils.setSnowmanPumpkin((Snowman) target.getBukkitEntity(), false);
		}
		LivingEntity e = (LivingEntity) BukkitAdapter.adapt(target);
		ItemStack armor;
		short dur;
		boolean broken = false;
		int damageValue = this.rndMin + (int) (Math.random() * ((this.rndMax - this.rndMin) + 1));

		if(e.getEquipment()==null) return SkillResult.CONDITION_FAILED;

		if (this.armortype.contains("offhand") || this.armortype.contains("all")) {
			armor = e.getEquipment().getItemInOffHand();
			dur = (short) (armor.getDurability() + damageValue);
			armor.setDurability(dur);
			if (armor.getDurability() > armor.getType().getMaxDurability()) {
				e.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));
				broken = true;
			}
		}
		if (this.armortype.contains("hand") || this.armortype.contains("all")) {
			armor = e.getEquipment().getItemInMainHand();
			dur = (short) (armor.getDurability() + damageValue);
			armor.setDurability(dur);
			if (armor.getDurability() > armor.getType().getMaxDurability()) {
				e.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
				broken = true;
			} else {
				e.getEquipment().setItemInMainHand(new ItemStack(armor));
			}
		}
		if (this.armortype.contains("helmet") || this.armortype.contains("all")) {
			armor = e.getEquipment().getHelmet();
			dur = (short) (armor.getDurability() + damageValue);
			armor.setDurability(dur);
			if (armor.getDurability() > armor.getType().getMaxDurability()) {
				e.getEquipment().setHelmet(new ItemStack(Material.AIR));
				broken = true;
			} else {
				e.getEquipment().setHelmet(new ItemStack(armor));
			}
		}
		if (this.armortype.contains("chest") || this.armortype.contains("all")) {
			armor = e.getEquipment().getChestplate();
			dur = (short) (armor.getDurability() + damageValue);
			armor.setDurability(dur);
			if (armor.getDurability() > armor.getType().getMaxDurability()) {
				e.getEquipment().setChestplate(new ItemStack(Material.AIR));
				broken = true;
			} else {
				e.getEquipment().setChestplate(new ItemStack(armor));
			}
		}
		if (this.armortype.contains("leggings") || this.armortype.contains("all")) {
			armor = e.getEquipment().getLeggings();
			dur = (short) (armor.getDurability() + damageValue);
			armor.setDurability(dur);
			if (armor.getDurability() > armor.getType().getMaxDurability()) {
				e.getEquipment().setLeggings(new ItemStack(Material.AIR));
				broken = true;
			} else {
				e.getEquipment().setLeggings(new ItemStack(armor));
			}
		}
		if (this.armortype.contains("boots") || this.armortype.contains("all")) {
			armor = e.getEquipment().getBoots();
			dur = (short) (armor.getDurability() + damageValue);
			armor.setDurability(dur);
			if (armor.getDurability() > armor.getType().getMaxDurability()) {
				e.getEquipment().setBoots(new ItemStack(Material.AIR));
				broken = true;
			} else {
				e.getEquipment().setBoots(new ItemStack(armor));
			}
		}
		ActiveMob am = null;
		if (data.getCaster() instanceof ActiveMob)
			am = (ActiveMob) data.getCaster();
		if (am != null && broken && this.signal != null)
			am.signalMob(BukkitAdapter.adapt(e), this.signal);
		return SkillResult.SUCCESS;
	}
}
