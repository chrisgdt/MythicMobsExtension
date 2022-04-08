package com.gmail.berndivader.mythicmobsext.bossbars.conditions;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import org.bukkit.boss.BossBar;

import com.gmail.berndivader.mythicmobsext.bossbars.BossBars;
import com.gmail.berndivader.mythicmobsext.conditions.AbstractCustomCondition;
import com.gmail.berndivader.mythicmobsext.utils.RangedDouble;

public class ProgressBossBar extends AbstractCustomCondition implements IEntityCondition {
	RangedDouble range;
	PlaceholderString title;

	public ProgressBossBar(String line, MythicLineConfig mlc) {
		super(line, mlc);
		range = new RangedDouble(mlc.getString(new String[] { "range", "r", "a", "amount" }, ">0"));
		title = mlc.getPlaceholderString("title", "bar");
	}

	@Override
	public boolean check(AbstractEntity abstract_entity) {
		if (abstract_entity.isPlayer() && BossBars.contains(abstract_entity.getUniqueId())) {
			BossBar bar = BossBars.getBar(abstract_entity.getUniqueId(), title.get(abstract_entity));
			if (bar != null)
				return range.equals(bar.getProgress());
		}
		return false;
	}
}
