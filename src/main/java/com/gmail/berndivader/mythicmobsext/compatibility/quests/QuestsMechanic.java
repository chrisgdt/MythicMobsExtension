package com.gmail.berndivader.mythicmobsext.compatibility.quests;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.SkillString;
import org.bukkit.entity.Player;

import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;

public class QuestsMechanic extends SkillMechanic implements ITargetedEntitySkill {
	Quests quests = QuestsSupport.inst().quests();
	String s1, s2;
	int i1;

	public QuestsMechanic(SkillExecutor manager, String skill, MythicLineConfig mlc) {
		super(manager, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		s1 = mlc.getString("quest", "").toLowerCase();
		i1 = mlc.getInteger("stage", 0);
		if (!s1.isEmpty() && s1.charAt(0) == '"')
			s1 = SkillString.parseMessageSpecialChars(s1.substring(1, s1.length() - 1));
		s2 = skill.toUpperCase().split("QUEST\\{")[0];
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity e) {
		if (e.isPlayer()) {
			Player p = (Player) e.getBukkitEntity();
			Quester q = quests.getQuester(p.getUniqueId());
			if(q!=null) {
				Quest quest = QuestsSupport.getQuestFromCurrent(q, s1);
				switch (s2) {
				case "COMPLETEQUEST":
					if(quest!=null) {
						quest.completeQuest(q);
					}
					break;
				case "TAKEQUEST":
					if(quest==null) {
						quest=quests.getQuest(s1);
						if(quest!=null) {
							q.takeQuest(quest, true);
						}
					}
					break;
				case "FAILQUEST":
					if(quest!=null) {
						quest.failQuest(q);
					}
					break;
				case "NEXTSTAGEQUEST":
					if(quest!=null) {
						quest.nextStage(q, false);
						quest.updateCompass(q, q.getCurrentStage(quest));
					}
					break;
				case "SETSTAGEQUEST":
					if(quest!=null) {
						quest.setStage(q, i1);
						quest.updateCompass(q, q.getCurrentStage(quest));
					}
					break;
				}
			}
		}
		return SkillResult.SUCCESS;
	}

}
