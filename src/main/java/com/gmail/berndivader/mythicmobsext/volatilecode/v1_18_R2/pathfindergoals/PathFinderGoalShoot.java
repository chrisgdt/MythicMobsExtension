package com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R2.pathfindergoals;

import java.util.EnumSet;

import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class PathFinderGoalShoot extends Goal {
	private final Mob mob;
	private final double b;
	private int c, h1;
	private final float d, i1;
	private int d1 = -1;
	private int f;
	private boolean g;
	private boolean h;
	private int i = -1;

	public PathFinderGoalShoot(Mob t, double d2, int n, int n1, float f2) {
		this.mob = t;
		this.b = d2;
		this.c = n;
		this.h1 = n1;
		this.d = f2 * f2;
		this.i1 = f2;
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	public void b(int n) {
		this.c = n;
	}

	@Override
	public boolean canUse() {
		LivingEntity target = mob.getTarget();
		if (target == null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean canContinueToUse() {
		return (this.canUse() || !this.mob.getNavigation().isDone());
	}

	@Override
	public void start() {
		super.start();
		if (this.mob instanceof RangedAttackMob)
			this.mob.setAggressive(false);
	}

	@Override
	public void stop() {
		super.stop();
		if (this.mob instanceof RangedAttackMob)
			this.mob.setAggressive(false);
		this.f = 0;
		this.d1 = -1;
//        this.a.dp();
	}

	@Override
	public void tick() {
		boolean bl;
		LivingEntity entityLiving = this.mob.getTarget();
		if (entityLiving == null) {
			return;
		}
		double d2 = this.mob.distanceToSqr(entityLiving.getX(), entityLiving.getBoundingBox().maxY, entityLiving.getZ());
		boolean bl2 = this.mob.getSensing().hasLineOfSight(entityLiving);
		bl = this.f > 0;
		if (bl2 != bl) {
			this.f = 0;
		}
		this.f = bl2 ? ++this.f : this.f--;
		if (d2 > (double) this.d || this.f < 20) {
			this.mob.getNavigation().moveTo(entityLiving, this.b);
			this.i = -1;
		} else {
			this.mob.getNavigation().stop();
			++this.i;
		}
		if (this.i >= 20) {
			if ((double) this.mob.getRandom().nextFloat() < 0.3) {
				this.g = !this.g;
			}
			if ((double) this.mob.getRandom().nextFloat() < 0.3) {
				this.h = !this.h;
			}
			this.i = 0;
		}
		if (this.i > -1) {
			if (d2 > (double) (this.d * 0.75f)) {
				this.h = false;
			} else if (d2 < (double) (this.d * 0.25f)) {
				this.h = true;
			}
			this.mob.getMoveControl().strafe(this.h ? -0.5f : 0.5f, this.g ? 0.5f : -0.5f);
			this.mob.lookAt(entityLiving, 30.0f, 30.0f);
		} else {
			this.mob.getLookControl().setLookAt(entityLiving, 30.0f, 30.0f);
		}

		if (--this.d1 == 0) {
			float f2;
			if (!bl2) {
				return;
			}
			float f3 = f2 = Mth.sqrt((float) d2) / this.i1;
			f3 = Mth.clamp(f3, 0.1f, 1.0f);
			if (this.mob instanceof RangedAttackMob) {
				((RangedAttackMob) this.mob).performRangedAttack(entityLiving, f3);
			} else {
				ActiveMob am = Utils.mobmanager.getMythicMobInstance(this.mob.getBukkitEntity());
				if (am != null)
					am.signalMob(BukkitAdapter.adapt(entityLiving.getBukkitEntity()), Utils.signal_AISHOOT);
			}
			this.d1 = Mth.floor(f2 * (float) (this.h1 - this.c) + (float) this.c);
		} else if (this.d1 < 0) {
			float f4 = Mth.sqrt((float) d2) / this.i1;
			this.d1 = Mth.floor(f4 * (float) (this.h1 - this.c) + (float) this.c);
		}
	}
}
