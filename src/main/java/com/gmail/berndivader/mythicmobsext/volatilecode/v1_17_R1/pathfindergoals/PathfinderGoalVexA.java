package com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.pathfindergoals;

import java.util.EnumSet;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalVexA extends PathfinderGoal {
	EntityInsentient entity;
	BlockPosition c;
	DataWatcherObject<Byte> a;

	public PathfinderGoalVexA(EntityInsentient monster) {
		this.entity = monster;
		a(EnumSet.of(PathfinderGoal.Type.a)); // MOVE
		this.a = (DataWatcherObject<Byte>) NMSUtils.getField("a", EntityInsentient.class, entity);
//    	System.err.println(a!=null);
	}

	@Override
	public boolean a() {
		boolean bl1 = entity.getGoalTarget() != null && !entity.getControllerMove().b() && Main.random.nextInt(7) == 0
				? entity.f(entity.getGoalTarget()) > 4.0 // getViewVector
				: false;
//        System.err.println("a:"+bl1);
		return bl1;
	}

	@Override
	public boolean b() {
		boolean bl1 = entity.getControllerMove().b() && this.c(1) && entity.getGoalTarget() != null
				&& entity.getGoalTarget().isAlive();
//        System.err.println("b:"+bl1);
		return bl1;
	}

	private boolean c(int i2) {
		byte b0 = entity.getDataWatcher().get(a).byteValue();
		boolean bl1 = (b0 & i2) != 0;
//        System.err.println("c:"+bl1);
		return bl1;
	}

	private void a(int i2, boolean flag) {
		byte b0 = entity.getDataWatcher().get(this.a).byteValue();
		int j = flag ? b0 | i2 : b0 & ~i2;
		entity.swingHand(EnumHand.a);
//        entity.getDataWatcher().set(a,Byte.valueOf((byte)(j&255)));
//        System.err.println("aa:"+b0+":"+j);
	}

	@Override
	public void c() {
		EntityLiving entityliving = entity.getGoalTarget();
		Vec3D vec3d = entityliving.e(1.0f); // getViewVector
		entity.getControllerMove().a(vec3d.getX(), vec3d.getY(), vec3d.getZ(), 1.0);
		a(1, true);
		entity.playSound(SoundEffects.tB, 1.0f, 1.0f);
//        System.err.println("cc");
	}

	@Override
	public void d() {
		this.a(1, false);
	}

	@Override
	public void e() {
//        System.err.println("ee");
		EntityLiving entityliving = entity.getGoalTarget();
		if (entity.getBoundingBox().c(entityliving.getBoundingBox())) {
			entity.attackEntity(entityliving);
			a(1, false);
		} else {
			double d0 = entity.f(entityliving); // distanceToSqr
			if (d0 < 9.0) {
				Vec3D vec3d = entityliving.e(1.0f); // getViewVector
				entity.getControllerMove().a(vec3d.getX(), vec3d.getY(), vec3d.getZ(), 1.0);
			}
		}
	}
}