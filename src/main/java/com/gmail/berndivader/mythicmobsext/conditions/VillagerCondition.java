package com.gmail.berndivader.mythicmobsext.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

import com.gmail.berndivader.mythicmobsext.externals.ExternalAnnotation;

@ExternalAnnotation(name = "villager", author = "Seyarada")
public class VillagerCondition extends AbstractCustomCondition implements IEntityCondition {
	private String profession;
	private int level;
	private String type;
	
	public VillagerCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		profession  = mlc.getString(new String[] { "profession", "p"}, null);
		level  = mlc.getInteger(new String[] { "level", "l"}, -1);
		type  = mlc.getString(new String[] { "type", "t"}, null);
		
	}

	@Override
	public boolean check(AbstractEntity e) {
		if (e.getBukkitEntity().getType().equals(EntityType.valueOf("VILLAGER"))) {
			Villager v = (Villager) e.getBukkitEntity();
			if (profession!=null) return v.getProfession().equals(Villager.Profession.valueOf(profession));
			else if (level>0) return v.getVillagerLevel()==level;
			else if (type!=null) return v.getVillagerType().equals(Villager.Type.valueOf(type));
		}
		return false;
	}

}
