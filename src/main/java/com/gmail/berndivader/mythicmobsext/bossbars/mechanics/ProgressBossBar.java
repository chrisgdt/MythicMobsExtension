package com.gmail.berndivader.mythicmobsext.bossbars.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.boss.BossBar;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.bossbars.BossBars;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;

public class ProgressBossBar extends SkillMechanic implements ITargetedEntitySkill {
	PlaceholderString title, expr;
	boolean set;

	public ProgressBossBar(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		title = mlc.getPlaceholderString("title", "Bar");
		set = mlc.getBoolean("set", false);
		expr = mlc.getPlaceholderString("value", "0.05d");
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity abstract_entity) {
		if (abstract_entity.isPlayer()) {
			double value = 0d;
			String parsed_expr = expr.get(data, abstract_entity);
			try {
				value = Double.parseDouble(parsed_expr);
			} catch (Exception e) {
				Main.logger.info(parsed_expr + " is not valid for double in " + this.line);
				value = 0d;
			}
			int delta = value > 0 ? 1 : -1;
			value = Math.abs(value);
			if (BossBars.contains(abstract_entity.getUniqueId())) {
				BossBar bar = BossBars.getBar(abstract_entity.getUniqueId(), title.get(data, abstract_entity));
				if (bar != null) {
					bar.setProgress(MathUtils.clamp(set ? value : bar.getProgress() + value * delta, 0d, 1d));
				}
			}
			return SkillResult.SUCCESS;
		}
		return SkillResult.CONDITION_FAILED;
	}

}
