package com.gmail.berndivader.mythicmobsext.volatilecode.v1_20_R3.pathfindergoals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.TriggeredSkill;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.events.EntityCollideEvent;
import com.gmail.berndivader.mythicmobsext.utils.Utils;

public class PathfinderGoalNotifyOnCollide extends Goal {
	int c;
	Mob e;
	Optional<ActiveMob> am;
	Level w;
	HashMap<UUID, Integer> cooldown;

	public PathfinderGoalNotifyOnCollide(Mob e2, int i1) {
		this.c = i1;
		this.e = e2;
		this.am = Optional.ofNullable(Utils.mobmanager.getMythicMobInstance(e.getBukkitEntity()));
		this.w = e2.level();
		this.cooldown = new HashMap<>();
	}

	@Override
	public boolean canUse() {
		return this.e.collides;
	}

	@Override
	public boolean canContinueToUse() {
		Iterator<Map.Entry<UUID, Integer>> it = this.cooldown.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<UUID, Integer> pair = it.next();
			int i1 = pair.getValue();
			if (i1-- < 1) {
				it.remove();
				continue;
			}
			pair.setValue(i1);
		}
		return true;
	}

	@Override
	public void tick() {
		for (Entity ee : this.w.getEntities(this.e, this.e.getBoundingBox())) {
			if (this.cooldown.containsKey(ee.getUUID()))
				continue;
			this.cooldown.put(ee.getUUID(), this.c);
			Main.pluginmanager
					.callEvent(new EntityCollideEvent(am.get(), this.e.getBukkitEntity(), ee.getBukkitEntity()));
			this.e.getBukkitEntity().setMetadata(Utils.meta_LASTCOLLIDETYPE,
					new FixedMetadataValue(Main.getPlugin(), ee.getBukkitEntity().getType().toString()));
			if (am.isPresent())
				new TriggeredSkill(SkillTriggers.BLOCK, this.am.get(), BukkitAdapter.adapt(ee.getBukkitEntity()), true);
		}
	}
}