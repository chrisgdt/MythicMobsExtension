package com.gmail.berndivader.mythicmobsext.mechanics;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.ThreadSafetyLevel;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import org.bukkit.entity.Entity;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

@ExternalAnnotation(name = "getentitymeta,getplayermeta", author = "BerndiVader")
public class GetEntityMetadataMap extends SkillMechanic implements ITargetedEntitySkill {
	boolean use_players;

	enum FilterEnum {
		NONE, ENTITY, PLUGIN, TAG;
	}

	FilterEnum filter;

	enum UsageEnum {
		CONSOLE, STANCE;
	}

	UsageEnum usage;
	String data;

	public GetEntityMetadataMap(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
		super(manager, file, skill, mlc);
		this.line = skill;
		this.threadSafetyLevel = ThreadSafetyLevel.SYNC_ONLY;

		use_players = mlc.getLine().toLowerCase().startsWith("getplayermeta");
		data = mlc.getString("data", "ANY");

		if ((filter = Utils.enum_lookup(FilterEnum.class, mlc.getString("filter", "NONE").toUpperCase())) == null)
			filter = FilterEnum.NONE;
		if ((usage = Utils.enum_lookup(UsageEnum.class, mlc.getString("usage", "CONSOLE").toUpperCase())) == null)
			usage = UsageEnum.CONSOLE;
	}

	@Override
	public SkillResult castAtEntity(SkillMetadata data, AbstractEntity abstract_entity) {
		Entity target = abstract_entity.getBukkitEntity();
		Map<String, Map<Plugin, MetadataValue>> map = this.filter(
				use_players ? NMSUtils.getPlayerMetadataMap(target) : NMSUtils.getEntityMetadataMap(target.getServer()),
				target.getUniqueId());
		Iterator<Entry<String, Map<Plugin, MetadataValue>>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Map<Plugin, MetadataValue>> entry = iter.next();
			Map<Plugin, MetadataValue> values = entry.getValue();
			Iterator<Entry<Plugin, MetadataValue>> values_iter = values.entrySet().iterator();
			while (values_iter.hasNext()) {
				Entry<Plugin, MetadataValue> values_entry = values_iter.next();
				String outcome = entry.getKey() + "->" + values_entry.getKey().getName() + "->"
						+ values_entry.getValue().asString();
				switch (usage) {
				case CONSOLE:
					System.out.println(outcome);
					break;
				case STANCE:
					if (Utils.mobmanager.isActiveMob(data.getCaster().getEntity()))
						Utils.mythicmobs.getAPIHelper().getMythicMobInstance(target).setStance(outcome);
					break;
				default:
					break;
				}
			}
		}
		return SkillResult.SUCCESS;
	}

	Map<String, Map<Plugin, MetadataValue>> filter(Map<String, Map<Plugin, MetadataValue>> map, UUID entity_uuid) {
		Iterator<Entry<String, Map<Plugin, MetadataValue>>> map_iter = map.entrySet().iterator();
		while (map_iter.hasNext()) {
			Entry<String, Map<Plugin, MetadataValue>> map_entry = map_iter.next();
			if (filter == FilterEnum.NONE) {
			} else if (filter == FilterEnum.ENTITY) {
				if (!map_entry.getKey().split(":")[0].toLowerCase().equals(entity_uuid.toString()))
					map_iter.remove();
			} else if (filter == FilterEnum.TAG) {
				if (!map_entry.getKey().split(":")[1].toLowerCase().equals(data))
					map_iter.remove();
			} else if (filter == FilterEnum.PLUGIN) {
				Iterator<Plugin> entry_iter = map_entry.getValue().keySet().iterator();
				while (entry_iter.hasNext()) {
					String plugin_name = entry_iter.next().getName().toLowerCase();
					if (!plugin_name.equals(data) && !data.equals("ANY"))
						entry_iter.remove();
				}
			}
		}
		return map;
	}

}
