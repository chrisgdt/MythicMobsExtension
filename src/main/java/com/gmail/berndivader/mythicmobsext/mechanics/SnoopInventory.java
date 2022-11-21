package com.gmail.berndivader.mythicmobsext.mechanics;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;

import com.gmail.berndivader.mythicmobsext.backbags.InventoryViewer;
import com.gmail.berndivader.mythicmobsext.externals.*;

import java.io.File;

@ExternalAnnotation(name = "snoopinventory,openinventory", author = "BerndiVader")
public class SnoopInventory extends SkillMechanic implements ITargetedEntitySkill {

	boolean view_only, snoop;
	InventoryType type;

	public SnoopInventory(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		snoop = mlc.getLine().toLowerCase().startsWith("snoop");
		view_only = mlc.getBoolean("viewonly", true);
		try {
			type = InventoryType.valueOf(mlc.getString("type", "PLAYER").toUpperCase());
		} catch (Exception ex) {
			type = InventoryType.PLAYER;
		}

	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity abstract_entity) {
		if (snoop) {
			if (data.getCaster().getEntity().isPlayer() && abstract_entity.isPlayer()) {
				Player caster = (Player) data.getCaster().getEntity().getBukkitEntity();
				Player victim = (Player) abstract_entity.getBukkitEntity();
				new InventoryViewer(victim, caster);
			}
		} else {
			if (data.getCaster().getEntity().isPlayer()) {
				Player caster = (Player) data.getCaster().getEntity().getBukkitEntity();
				new InventoryViewer(caster, caster, view_only, type);
			}
		}
		return SkillResult.SUCCESS;
	}

}
