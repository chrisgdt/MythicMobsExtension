package com.gmail.berndivader.mythicmobsext.targeters;

import java.util.HashSet;
import java.util.UUID;
import java.util.regex.Pattern;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.externals.*;

@ExternalAnnotation(name = "find", author = "BerndiVader")
public class NameTargeter extends ISelectorEntity {
	PlaceholderString place_holder;
	Pattern is_uuid = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");

	public NameTargeter(SkillExecutor manager, MythicLineConfig mlc) {
		super(manager, mlc);

		place_holder = mlc.getPlaceholderString("name", "<target.uuid>");
	}

	@Override
	public HashSet<AbstractEntity> getEntities(SkillMetadata data) {
		HashSet<AbstractEntity> targets = new HashSet<>();
		String name = place_holder.get(data);
		if (name != null) {
			if (is_uuid.matcher(name).matches()) {
				Entity target = NMSUtils.getEntity(data.getCaster().getEntity().getBukkitEntity().getWorld(),
						UUID.fromString(name));
				if (target != null)
					targets.add(BukkitAdapter.adapt(target));
			} else {
				Entity target = Bukkit.getPlayer(name);
				if (target != null)
					targets.add(BukkitAdapter.adapt(target));
			}
		}
		return this.applyOffsets(targets);
	}
}
