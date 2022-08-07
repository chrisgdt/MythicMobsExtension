package com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals;

import java.util.EnumSet;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class PathfinderGoalVexA extends Goal {
	Mob entity;
	BlockPos c;
	EntityDataAccessor<Byte> a;

	public PathfinderGoalVexA(Mob monster) {
		this.entity = monster;
		setFlags(EnumSet.of(Goal.Flag.MOVE));
		this.a = (EntityDataAccessor<Byte>) NMSUtils.getField("a", Mob.class, entity);
//    	System.err.println(a!=null);
	}

	@Override
	public boolean canUse() {
		boolean bl1 = entity.getTarget() != null && !entity.getMoveControl().hasWanted() && Main.random.nextInt(7) == 0 && entity.distanceToSqr(entity.getTarget()) > 4.0;
//        System.err.println("a:"+bl1);
		return bl1;
	}

	@Override
	public boolean canContinueToUse() {
		boolean bl1 = entity.getMoveControl().hasWanted() && this.c(1) && entity.getTarget() != null
				&& entity.getTarget().isAlive();
//        System.err.println("b:"+bl1);
		return bl1;
	}

	private boolean c(int i2) {
		byte b0 = entity.getEntityData().get(a);
		boolean bl1 = (b0 & i2) != 0;
//        System.err.println("c:"+bl1);
		return bl1;
	}

	private void a(int i2, boolean flag) {
		byte b0 = entity.getEntityData().get(this.a);
		int j = flag ? b0 | i2 : b0 & ~i2;
		entity.swing(InteractionHand.MAIN_HAND);
//        entity.getEntityData().set(a,Byte.valueOf((byte)(j&255)));
//        System.err.println("aa:"+b0+":"+j);
	}

	@Override
	public void start() {
		LivingEntity entityliving = entity.getTarget();
		if (entityliving != null) {
			Vec3 vec3d = entityliving.getViewVector(1.0f);
			entity.getMoveControl().setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0);
			a(1, true);
			entity.playSound(SoundEvents.VEX_CHARGE, 1.0f, 1.0f);
//        System.err.println("cc");
		}
	}

	@Override
	public void stop() {
		this.a(1, false);
	}

	@Override
	public void tick() {
//        System.err.println("ee");
		LivingEntity entityliving = entity.getTarget();
		if (entityliving != null) {
			if (entity.getBoundingBox().intersects(entityliving.getBoundingBox())) {
				entity.doHurtTarget(entityliving);
				a(1, false);
			} else {
				double d0 = entity.distanceToSqr(entityliving);
				if (d0 < 9.0) {
					Vec3 vec3d = entityliving.getViewVector(1.0f);
					entity.getMoveControl().setWantedPosition(vec3d.x, vec3d.y, vec3d.z, 1.0);
				}
			}
		}
	}
}