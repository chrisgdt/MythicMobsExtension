package com.gmail.berndivader.mythicmobsext.mechanics;

import java.lang.reflect.Field;
import java.util.Optional;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "getmobfield", author = "BerndiVader")
public class GetMobField extends SkillMechanic implements ITargetedEntitySkill, INoTargetSkill {
	String s2;
	boolean bl1;
	Field f1;

	public GetMobField(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		s2 = mlc.getString("meta", null);
		bl1 = mlc.getBoolean("stance", false);
		try {
			f1 = ActiveMob.class.getDeclaredField(mlc.getString("field", ""));
			f1.setAccessible(true);
		} catch (NoSuchFieldException | SecurityException e) {
			Main.logger.warning("Field " + e.getLocalizedMessage() + " doesnt exists in ActiveMob.class!");
		}
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
		Object o = null;
		ActiveMob am;
		if (f1 != null && (am = Utils.mobmanager.getMythicMobInstance(target)) != null) {
			try {
				if ((o = f1.get(am)) == null)
					return SkillResult.CONDITION_FAILED;
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			if ((o instanceof Optional) && ((Optional<?>) o).isPresent())
				o = ((Optional<?>) o).get();
			if (!bl1 && s2 != null) {
				target.getBukkitEntity().setMetadata(s2, new FixedMetadataValue(Main.getPlugin(), o.toString()));
			} else {
				am.setStance(o.toString());
			}
		}
		return SkillResult.SUCCESS;
	}

	@Override
	public SkillResult cast(SkillMetadata data) {
		return castAtEntity(data, data.getCaster().getEntity());
	}

}
