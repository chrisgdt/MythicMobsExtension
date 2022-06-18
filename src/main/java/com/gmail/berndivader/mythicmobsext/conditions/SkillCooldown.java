package com.gmail.berndivader.mythicmobsext.conditions;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "skillcooldown,cooldown", author = "BerndiVader")
public class SkillCooldown extends AbstractCustomCondition implements IEntityCondition {
	String name, id;
	HashMap<UUID, Long> cd;
	PlaceholderDouble f1;
	Scoreboard sb;
	Objective o1;

	public SkillCooldown(String line, MythicLineConfig mlc) {
		super(line, mlc);
		name = mlc.getString("skill");
		id = mlc.getString("id", "");
		Skill sk = null;
		if (Utils.mythicmobs.getSkillManager().getSkill(name).isPresent()) {
			sk = Utils.mythicmobs.getSkillManager().getSkill(name).get();
		}
		if (sk != null) {
			try {
				Field f = sk.getClass().getSuperclass().getDeclaredField("cooldowns");
				f.setAccessible(true);
				cd = (HashMap<UUID, Long>) f.get(sk);
				f = sk.getClass().getSuperclass().getDeclaredField("cooldown");
				f.setAccessible(true);
				f1 = (PlaceholderDouble) f.get(sk);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			sb = Bukkit.getScoreboardManager().getMainScoreboard();
			o1 = sb.getObjective(name + id);
			if (o1 == null)
				o1 = sb.registerNewObjective(name + id, "dummy", name + id);
		}
	}

	@Override
	public boolean check(AbstractEntity entity) {
		if (f1.get(entity) > 0f && cd != null) {
			Entity e = entity.getBukkitEntity();
			Long last = cd.get(e.getUniqueId());
			if (last != null) {
				long time = System.currentTimeMillis();
				if (last >= time) {
					tag(e, (last - time) / 1000);
				}
				return last > 0;
				//if ((l1 -= AbstractSkill.cooldownTimer) >= 0) {
				//	tag(e, l1 / 20);
				//}
				//return l2 >= AbstractSkill.cooldownTimer;
			} else {
				return false;
			}
		}
		return false;
	}

	void tag(Entity e, long cd) {
		String s1 = e instanceof Player ? e.getName() : e.getUniqueId().toString();
		Score sc = o1.getScore(s1);
		sc.setScore((int) cd);
		e.setMetadata(name + id, new FixedMetadataValue(Main.getPlugin(), (int) cd));
	}

}
