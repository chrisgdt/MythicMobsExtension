package com.gmail.berndivader.mythicmobsext.compatibility.quests;

import java.util.Arrays;
import java.util.List;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.core.skills.SkillString;
import me.pikamug.quests.BukkitQuestsPlugin;
import me.pikamug.quests.player.Quester;
import me.pikamug.quests.quests.Quest;
import org.bukkit.entity.Player;

import com.gmail.berndivader.mythicmobsext.conditions.AbstractCustomCondition;

public class QuestCompleteCondition extends AbstractCustomCondition implements IEntityCondition {
	BukkitQuestsPlugin quests = QuestsSupport.inst().quests();
	List<String> questList;

	public QuestCompleteCondition(String line, MythicLineConfig mlc) {
		super(line, mlc);
		String s1 = mlc.getString("quest", "any").toLowerCase();
		if (!s1.isEmpty() && s1.charAt(0) == '"')
			s1 = SkillString.parseMessageSpecialChars(s1.substring(1, s1.length() - 1));
		questList = Arrays.asList(s1.toLowerCase().split(","));
	}

	@Override
	public boolean check(AbstractEntity entity) {
		if (!entity.isPlayer())
			return false;
		Player p = (Player) entity.getBukkitEntity();
		Quester q = quests.getQuester(p.getUniqueId());
		boolean bl1 = false;
		if (q != null) {
			for (Quest value : q.getCompletedQuests()) {
				String quest = value.getName().toLowerCase();
				if (questList.contains(quest)) {
					return true;
				}
			}
		}
		return bl1;
	}

}
