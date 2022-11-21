package com.gmail.berndivader.mythicmobsext.conditions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;
import com.gmail.berndivader.mythicmobsext.targeters.CustomTargeters;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.adapters.BukkitEntity;
import io.lumine.mythic.core.config.MythicLineConfigImpl;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTargeter;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderStringImpl;
import io.lumine.mythic.core.skills.targeters.*;

@ExternalAnnotation(name = "checktargeter,targetcondition", author = "Seyarada")
public class CheckTargeter extends AbstractCustomCondition implements IEntityCondition {
	
	private PlaceholderString stargeter;
	private SkillCondition condition;
	private Boolean forFalse;
	
	public CheckTargeter(String line, MythicLineConfig mlc) {
		super(line, mlc);

		String s = mlc.getString(new String[] { "targeter", "t"}, "@self");
		forFalse = mlc.getBoolean(new String[] { "false", "f"}, false);
		this.stargeter = new PlaceholderStringImpl(s);
		
		String conditionString = mlc.getString(new String[] { "condition", "cond", "c"});
		condition = Utils.mythicmobs.getSkillManager().getCondition(conditionString);
		
	}

	@Override
	public boolean check(AbstractEntity caster) {
		SkillMetadata data = new SkillMetadataImpl(SkillTriggers.API, new GenericCaster(caster), caster);

		String targeter = this.stargeter.get(data);
		if (caster.getClass().equals(BukkitEntity.class) || caster.getClass().equals(BukkitPlayer.class)) {
			targeter = this.stargeter.get(data, caster);
		}
		
		Collection<?> entries = getDestination(targeter, data);
		if(entries.size()==0) return false;
		for(Object i:getDestination(targeter, data)) {
			
			
			if (i instanceof BukkitEntity) {
				data.setEntityTarget((AbstractEntity) i);
				
			}
			else if (i instanceof AbstractLocation) {
				data.setLocationTarget((AbstractLocation) i);
			}
			
			// Returns false if any condition isn't meet
			boolean a = condition.evaluateTargets(data);
			if(forFalse) a=!a;
			if(!a) return a;
		}
		return true;
	}
	
	protected Collection<?> getDestination(String target, SkillMetadata skilldata) {
		SkillMetadata data = new SkillMetadataImpl(SkillTriggers.API, skilldata.getCaster(), skilldata.getTrigger(),
				skilldata.getOrigin(), null, null, 1.0f);
		Optional<SkillTargeter> maybeTargeter;
		maybeTargeter = Optional.of(Utils.parseSkillTargeter(target));
		if (maybeTargeter.isPresent()) {
			SkillTargeter targeter = maybeTargeter.get();
			if (targeter instanceof CustomTargeter) {
				String s1 = target.substring(1);
				MythicLineConfig mlc = new MythicLineConfigImpl(s1);
				String s2 = s1.contains("{") ? s1.substring(0, s1.indexOf("{")) : s1;
				if ((targeter = CustomTargeters.getCustomTargeter(s2, mlc, targeter.getManager())) == null)
					//targeter = new TriggerTargeter(getPlugin().getSkillManager(), mlc);
					targeter = new TriggerTargeter(targeter.getManager(), mlc);
			}
			if (targeter instanceof IEntitySelector) {
				data.setEntityTargets(((IEntitySelector) targeter).getEntities(data));
				((IEntitySelector) targeter).filter(data, false);
				return data.getEntityTargets();
			}
			if (targeter instanceof ILocationSelector) {
				data.setLocationTargets(((ILocationSelector) targeter).getLocations(data));
				((ILocationSelector) targeter).filter(data);
			} else if (targeter instanceof OriginTargeter) {
				data.setLocationTargets(((OriginTargeter) targeter).getLocations(skilldata));
			} else if (targeter instanceof TargetLocationTargeter) {
				HashSet<AbstractLocation> lTargets = new HashSet<AbstractLocation>();
				lTargets.add(data.getTrigger().getLocation());
				data.setLocationTargets(lTargets);
			}
			if (targeter instanceof ConsoleTargeter) {
				data.setEntityTargets(null);
				data.setLocationTargets(null);
			}
			return data.getLocationTargets();
		}
		return null;
	}

}
