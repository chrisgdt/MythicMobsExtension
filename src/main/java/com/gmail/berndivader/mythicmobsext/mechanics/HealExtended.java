package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.RandomDouble;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

@ExternalAnnotation(name = "heal", author = "BerndiVader")
public class HealExtended extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
	double healByDistance;
	boolean incHealByDistance;
	boolean power;
	boolean percentage;
	boolean caster;
	boolean current;
	boolean loss;
	RegainReason reason;
	PlaceholderString amountPlaceholder;
	
	public HealExtended(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		
		amountPlaceholder= PlaceholderString.of(mlc.getString("amount","0"));
		power=mlc.getBoolean("power",false);
		percentage=mlc.getBoolean("percent",false);
		caster=mlc.getBoolean("caster",false);
		current=mlc.getBoolean("current",false);
		if ((loss=mlc.getBoolean("loss",false))) current=false;
		
		healByDistance=mlc.getDouble("dec",0)*-1;
		incHealByDistance=(healByDistance=mlc.getDouble("inc",healByDistance))<0;
		healByDistance=Math.abs(healByDistance);
		
		String reasonString=mlc.getString("reason","custom").toUpperCase();
		reason=RegainReason.CUSTOM;
		for(RegainReason value:RegainReason.values()) {
			if(value.toString().equals(reasonString)) {
				reason=value;
				break;
			}
		}
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity abstract_target) {
		if(!(abstract_target.getBukkitEntity() instanceof LivingEntity)) return SkillResult.CONDITION_FAILED;
		
		LivingEntity t=(LivingEntity)abstract_target.getBukkitEntity();
		LivingEntity c=(LivingEntity)data.getCaster().getEntity().getBukkitEntity();
		
		double amount=new RandomDouble(amountPlaceholder.get(data,abstract_target)).rollDouble();
		AbstractEntity abstract_caster=data.getCaster().getEntity();
		
		if(percentage) {
			amount=current?caster?c.getHealth()*amount:t.getHealth()*amount
					:loss?caster?(c.getMaxHealth()-c.getHealth())*amount:(t.getMaxHealth()-t.getHealth())*amount
					:caster?c.getMaxHealth()*amount:t.getMaxHealth()*amount;
		}
		
		if(power) amount=amount*data.getPower();
		
		if(healByDistance>0) {
			int distance=(int)Math.sqrt(MathUtils.distance3D(abstract_caster.getBukkitEntity().getLocation().toVector(),abstract_target.getBukkitEntity().getLocation().toVector()));
			amount=incHealByDistance?amount-(amount*(distance*healByDistance)):amount+(amount*(distance*healByDistance));
		}
		
		EntityRegainHealthEvent event=new EntityRegainHealthEvent(t,amount,reason);
		Bukkit.getPluginManager().callEvent(event);
		if(!event.isCancelled()) {
			t.setHealth(MathUtils.clamp(t.getHealth()+event.getAmount(),0d,t.getMaxHealth()));
		}
		return SkillResult.SUCCESS;
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		return castAtEntity(data,data.getCaster().getEntity());
	}
}
