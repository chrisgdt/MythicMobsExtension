package com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R3.pathfindergoals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class PathfinderGoalMeleeRangeAttack extends MeleeAttackGoal {
	protected float range;

	public PathfinderGoalMeleeRangeAttack(PathfinderMob entityCreature, double d, boolean b, float range) {
		super(entityCreature, d, b);
		this.range = range;
	}

	@Override
	protected double getAttackReachSqr(LivingEntity entity) {
		return this.mob.getBbWidth() * this.range * this.mob.getBbWidth() * this.range + entity.getBbWidth();
	}
}
