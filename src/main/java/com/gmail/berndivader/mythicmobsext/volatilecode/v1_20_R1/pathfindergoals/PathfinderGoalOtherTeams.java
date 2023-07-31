package com.gmail.berndivader.mythicmobsext.volatilecode.v1_20_R1.pathfindergoals;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.function.Predicate;

public class PathfinderGoalOtherTeams<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
	PlayerTeam team1, team2;

	public PathfinderGoalOtherTeams(PathfinderMob entitycreature, Class<T> oclass, boolean flag) {
		this(entitycreature, oclass, flag, false);
	}

	public PathfinderGoalOtherTeams(PathfinderMob entitycreature, Class<T> oclass, boolean flag, boolean flag1) {
		this(entitycreature, oclass, 10, flag, flag1, null);
	}

	public PathfinderGoalOtherTeams(PathfinderMob entitycreature, Class<T> oclass, int i2, boolean flag, boolean flag1,
									Predicate<LivingEntity> predicate) {
		super(entitycreature, oclass, i2, flag, flag1, predicate);
	}

	@Override
	public boolean canUse() {
		//
		return false;
	}

	@Override
	public boolean canContinueToUse() {
		LivingEntity entityliving = this.mob.getTarget();
		if (entityliving == null)
			entityliving = this.targetMob;
		if (entityliving == null)
			return false;
		if (!entityliving.isAlive())
			return false;
		teams(entityliving);
		if (this.team1 != null && this.team1 == this.team2) {
			this.stop();
			return false;
		}
		double d0 = this.getFollowDistance();
		if (this.mob.distanceToSqr(entityliving) > d0 * d0) {
			return false;
		}
		if (this.mustSee) {
			int d = (int) NMSUtils.getField("d", TargetGoal.class, this);
			if (this.mob.getSensing().hasLineOfSight(entityliving)) {
				NMSUtils.setField("d", TargetGoal.class, this, 0);
			} else {
				NMSUtils.setField("d", TargetGoal.class, this, d++);
				if (d++ > this.unseenMemoryTicks)
					return false;
			}
		}
		if (entityliving instanceof Player && ((Player) entityliving).getAbilities().invulnerable) {
			return false;
		}
		this.mob.setTarget(entityliving, EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true);
		return true;
	}

	private void teams(LivingEntity entityliving) {
		this.team1 = this.mob.level().getScoreboard().getPlayerTeam(this.mob.getUUID().toString());
		this.team2 = this.mob.level().getScoreboard().getPlayerTeam(entityliving.getScoreboardName());
	}

}