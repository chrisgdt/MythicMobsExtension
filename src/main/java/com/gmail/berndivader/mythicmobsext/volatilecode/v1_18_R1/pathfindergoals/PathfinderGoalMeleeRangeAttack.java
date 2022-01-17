package com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R1.pathfindergoals;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;

public class PathfinderGoalMeleeRangeAttack extends PathfinderGoalMeleeAttack {
	protected float range;

	public PathfinderGoalMeleeRangeAttack(EntityCreature entityCreature, double d, boolean b, float range) {
		super(entityCreature, d, b);
		this.range = range;
	}

	@Override
	protected double a(EntityLiving entity) {
		return (double) (this.a.getWidth() * this.range * this.a.getWidth() * this.range + entity.getWidth());
	}
}
