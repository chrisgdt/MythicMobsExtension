package com.gmail.berndivader.mythicmobsext.volatilecode.v1_20_R3.pathfindergoals;

import java.util.Optional;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.phys.Vec3;
import org.bukkit.entity.Monster;

import com.gmail.berndivader.mythicmobsext.utils.Utils;

public class PathfinderGoalAttack extends MeleeAttackGoal {
	protected float r;
	boolean is_monster;
	Optional<ActiveMob> am;

	public PathfinderGoalAttack(PathfinderMob e, double d, boolean b, float r) {
		super(e, d, b);
		is_monster = e.getBukkitEntity() instanceof Monster;
		this.r = r;
		am = Utils.mobmanager.getActiveMob(e.getUUID());
	}

	@Override
	protected void checkAndPerformAttack(LivingEntity entityLiving) {

		if (am.isPresent()) {
			ActiveMob active_mob = am.get();
			if (active_mob.getOwner().isPresent()) {
				if (active_mob.getOwner().get() == entityLiving.getUUID())
					return;
			}
		}

		double d3 = this.getAttackReachSqr(entityLiving);
		double d2 = getPerceivedTargetDistanceSquareForMeleeAttack(this.mob, entityLiving);
		if (d2 <= d3 && isTimeToAttack()) {
			resetAttackCooldown();
			if (is_monster) {
				this.mob.swing(InteractionHand.MAIN_HAND);
				this.mob.doHurtTarget(entityLiving);
			}
			ActiveMob am = Utils.mobmanager.getMythicMobInstance(this.mob.getBukkitEntity());
			if (am != null)
				am.signalMob(BukkitAdapter.adapt(entityLiving.getBukkitEntity()), Utils.signal_AIHIT);
		}
	}

	// Overriden until 1.20.4 where it were changed
	protected double getAttackReachSqr(LivingEntity e) {
		return this.mob.getBbWidth() * this.r * this.mob.getBbWidth() * this.r + e.getBbWidth();
	}

	// Copied from Mob class (since 1.20.4)
	public static double getPerceivedTargetDistanceSquareForMeleeAttack(Mob mob, LivingEntity entityliving) {
		return Math.max(mob.distanceToSqr(entityliving.position()), mob.distanceToSqr(entityliving.position()));
	}
}