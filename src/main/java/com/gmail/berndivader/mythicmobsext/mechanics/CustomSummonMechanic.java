package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.api.mobs.entities.MythicEntity;
import io.lumine.mythic.api.mobs.entities.SpawnReason;
import io.lumine.mythic.api.skills.*;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

import java.io.File;
import java.util.Optional;

@ExternalAnnotation(name = "customsummon", author = "BerndiVader")
public class CustomSummonMechanic extends SkillMechanic implements ITargetedLocationSkill, ITargetedEntitySkill {
	MythicMob mm = null;
	MythicEntity me;
	String tag, amount;
	int noise, yNoise;
	boolean yUpOnly, onSurface, inheritThreatTable, copyThreatTable, useEyeDirection, setowner, invisible,
			leashtocaster;
	float yaw;
	double addx, addy, addz, inFrontBlocks;
	String reason;

	public CustomSummonMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		this.amount = mlc.getString(new String[] { "amount", "a" }, "1");
		if (this.amount.startsWith("-"))
			this.amount = "1";
		String strType = mlc.getString(new String[] { "mobtype", "type", "t", "mob", "m" }, "SKELETON");
		this.invisible = mlc.getBoolean(new String[] { "invisible", "inv" }, false);
		this.tag = SkillString.unparseMessageSpecialChars(mlc.getString(new String[] { "addtag", "tag", "at" }, ""));
		this.noise = mlc.getInteger(new String[] { "noise", "n", "radius", "r" }, 0);
		this.yNoise = mlc.getInteger(new String[] { "ynoise", "yn", "yradius", "yr" }, this.noise);
		this.yUpOnly = mlc.getBoolean(new String[] { "yradiusuponly", "ynoiseuponly", "yruo", "ynuo", "yu" }, false);
		this.onSurface = mlc.getBoolean(new String[] { "onsurface", "os", "s" }, true);
		this.copyThreatTable = mlc.getBoolean(new String[] { "copythreattable", "ctt" }, false);
		this.inheritThreatTable = mlc.getBoolean(new String[] { "inheritthreattable", "itt" }, false);
		this.addx = mlc.getDouble(new String[] { "addx", "ax", "relx", "rx" }, 0);
		this.addy = mlc.getDouble(new String[] { "addy", "ay", "rely", "ry" }, 0);
		this.addz = mlc.getDouble(new String[] { "addz", "az", "relz", "rz" }, 0);
		this.yaw = mlc.getFloat("yaw", -1337);
		this.useEyeDirection = mlc.getBoolean(new String[] { "useeyedirection", "eyedirection", "ued" }, false);
		this.inFrontBlocks = mlc.getDouble(new String[] { "infrontblocks", "infront", "ifb" }, 0D);
		this.setowner = mlc.getBoolean(new String[] { "setowner", "so" }, false);
		this.leashtocaster = mlc.getBoolean(new String[] { "leashtocaster", "leash", "lc" }, false);
		Optional<MythicMob> mob = Utils.mobmanager.getMythicMob(strType);
		if (mob.isEmpty())
			this.me = BukkitEntityType.getMythicEntity(strType);
		else {
			this.mm = mob.get();
		}
		this.reason = mlc.getString(new String[] { "customreason", "custom", "cr" }, "SUMMON").toUpperCase();
	}

	@Override
	public SkillResult castAtLocation(SkillMetadata data, AbstractLocation t) {
		return cast(data, t, null);
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		return cast(data, target.getLocation(), target);
	}

	private SkillResult cast(SkillMetadata data, AbstractLocation tl, AbstractEntity te) {
		AbstractLocation target = tl.clone();
		if (!data.getCaster().getEntity().getWorld().equals(tl.getWorld()))
			return SkillResult.CONDITION_FAILED;
		if (this.useEyeDirection) {
			target = BukkitAdapter.adapt(MathUtils.getLocationInFront(BukkitAdapter.adapt(target), this.inFrontBlocks));
		}
		target.add(this.addx, this.addy, this.addz);
		int amount = MathUtils.randomRangeInt(this.amount);
		if (this.mm != null) {
			for (int i = 1; i <= amount; i++) {
				AbstractLocation l = noise > 0 ? Utils.findSafeSpawnLocation(target, this.noise,
						this.yNoise, this.mm.getMythicEntity().getHeight(), this.yUpOnly) : target;
				if (this.yaw != -1337)
					l.setYaw(Math.abs(this.yaw));
				ActiveMob ams = this.mm.spawn(l, data.getCaster().getLevel());
				if (ams == null || ams.getEntity() == null || ams.getEntity().isDead())
					continue;
				ams.getEntity().getBukkitEntity().setMetadata(Utils.meta_CUSTOMSPAWNREASON,
						new FixedMetadataValue(Main.getPlugin(), this.reason));
				if (this.leashtocaster && ams.getEntity().getBukkitEntity() instanceof Creature) {
					Creature c = (Creature) ams.getEntity().getBukkitEntity();
					c.setLeashHolder(data.getCaster().getEntity().getBukkitEntity());
				}
				if (this.invisible)
					Utils.applyInvisible((LivingEntity) ams.getEntity().getBukkitEntity(), 0);
				Utils.mythicmobs.getMobManager().registerActiveMob(ams);
				if (!this.tag.isEmpty()) {
					ams.getEntity().addScoreboardTag(new PlaceholderStringImpl(this.tag).get(data, te));
				}
				if (this.setowner) {
					ams.setOwner(data.getCaster().getEntity().getUniqueId());
				}
				if (data.getCaster() instanceof ActiveMob) {
					ActiveMob am = (ActiveMob) data.getCaster();
					ams.setParent(am);
					ams.setFaction(am.getFaction());
					if (this.copyThreatTable) {
						try {
							ams.importThreatTable(am.getThreatTable().clone());
							ams.getThreatTable().targetHighestThreat();
						} catch (CloneNotSupportedException e1) {
							e1.printStackTrace();
						}
						continue;
					}
					if (!this.inheritThreatTable || am.getThreatTable() == null)
						continue;
					ams.importThreatTable(am.getThreatTable());
					ams.getThreatTable().targetHighestThreat();
				}
			}
			return SkillResult.SUCCESS;
		}
		if (this.me != null) {
			for (int i = 1; i <= amount; ++i) {
				AbstractLocation l = this.noise > 0
						? Utils.findSafeSpawnLocation(target, this.noise, this.yNoise,
								this.me.getHeight(), this.yUpOnly)
						: target;
				if (this.yaw != -1337)
					l.setYaw(Math.abs(this.yaw));
				this.me.spawn(l, SpawnReason.valueOf(reason));
			}
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

}
