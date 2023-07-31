package com.gmail.berndivader.mythicmobsext.volatilecode.v1_20_R1.pathfindergoals;

import java.util.Optional;

import io.lumine.mythic.core.mobs.ActiveMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.entity.LivingEntity;

import com.gmail.berndivader.mythicmobsext.utils.Utils;

public class PathfinderGoalNotifyHeal extends Goal {
	static final String tag = "mme_last_health";
	long time_stamp;
	String signal;
	LivingEntity entity;
	Optional<ActiveMob> am;
	float old_health, new_health;

	public PathfinderGoalNotifyHeal(Mob entity, String signal) {
		this.entity = (LivingEntity) entity.getBukkitEntity();
		this.am = Optional.ofNullable(Utils.mobmanager.getMythicMobInstance(this.entity));
		this.signal = signal;
		old_health = entity.getHealth();
		time_stamp = System.currentTimeMillis();
	}

	@Override
	public boolean canUse() {
		return old_health != (float) entity.getHealth();
	}

	@Override
	public boolean canContinueToUse() {
		return old_health != (float) entity.getHealth() && System.currentTimeMillis() - time_stamp < 40;
	}

	@Override
	public void stop() {
	}

	@Override
	public void tick() {
		old_health = (float) entity.getHealth();
	}

	@Override
	public void start() {
		time_stamp = System.currentTimeMillis();
	}
}