package com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R3.pathfindergoals;

import java.util.Optional;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
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
	protected void checkAndPerformAttack(LivingEntity entityLiving, double d2) {

		if (am.isPresent()) {
			ActiveMob active_mob = am.get();
			if (active_mob.getOwner().isPresent()) {
				if (active_mob.getOwner().get() == entityLiving.getUUID())
					return;
			}
		}

		double d3 = this.getAttackReachSqr(entityLiving);
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

	@Override
	protected double getAttackReachSqr(LivingEntity e) {
		return this.mob.getBbWidth() * this.r * this.mob.getBbWidth() * this.r + e.getBbWidth();
	}
}