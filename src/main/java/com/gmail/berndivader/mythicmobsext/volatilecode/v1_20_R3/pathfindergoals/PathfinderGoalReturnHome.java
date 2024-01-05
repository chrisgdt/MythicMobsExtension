package com.gmail.berndivader.mythicmobsext.volatilecode.v1_20_R3.pathfindergoals;

import java.util.EnumSet;
import java.util.Optional;

import io.lumine.mythic.core.mobs.ActiveMob;
import net.minecraft.core.BlockPos;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.event.entity.EntityTeleportEvent;

import com.gmail.berndivader.mythicmobsext.utils.Utils;

public class PathfinderGoalReturnHome extends Goal {
	private final Mob d;
	private final Vec3 v;
	private Optional<ActiveMob> mM;
	private Vec3 aV;
	private final double f;
	private final double mR, tR;
	Level a;
	private final PathNavigation g;
	private int h;
	float b;
	float c;
	private float i;
	private boolean iF, iT;

	public PathfinderGoalReturnHome(Mob entity, double d0, double hx, double hy, double hz, double mR,
									double tR, boolean iT) {
		this.d = entity;
		this.f = d0;
		this.a = entity.level();
		g = entity.getNavigation();
		this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		this.v = new Vec3(hx, hy + (double) d.getEyeHeight(), hz);
		this.mR = mR;
		this.tR = tR;
		this.iF = false;
		this.iT = iT;
		if ((!(entity.getNavigation() instanceof GroundPathNavigation))
				&& (!(entity.getNavigation() instanceof FlyingPathNavigation))) {
			throw new IllegalArgumentException("Unsupported mob type for ReturnHomeGoal");
		}
		this.mM = Utils.mobmanager.getActiveMob(entity.getUUID());
	}

	@Override
	public boolean canUse() {
		this.aV = new Vec3(d.getX(), d.getY(), d.getZ());
		if (this.iT || this.d.getTarget() == null || !this.d.getTarget().isAlive()) {
			double ds = v.distanceToSqr(this.aV);
			if (ds > this.mR) {
				return true;
			} else if (this.iF && ds > 2.0D)
				return true;
		}
		return false;
	}

	@Override
	public boolean canContinueToUse() {
		return (!g.isInProgress()) && v.distanceToSqr(this.aV) > 2.0D;
	}

	@Override
	public void start() {
		h = 0;
		i = d.getPathfindingMalus(BlockPathTypes.WATER);
		d.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
		if (this.mM.isPresent() && !this.iF) {
			ActiveMob am = this.mM.get();
			am.signalMob(null, "GOAL_STARTRETURNHOME");
		}
		this.iF = true;
	}

	@Override
	public void stop() {
		g.getNodeEvaluator();
		d.setPathfindingMalus(BlockPathTypes.WATER, i);
		if (v.distanceToSqr(this.aV) < 10.0D) {
			this.iF = false;
			if (this.mM.isPresent()) {
				ActiveMob am = this.mM.get();
				am.signalMob(null, "GOAL_ENDRETURNHOME");
			}
		}
	}

	@Override
	public void tick() {
		d.getLookControl().setLookAt(v.x, v.y, v.z, 10.0F, d.getMaxHeadYRot());
		if (h-- <= 0) {
			h = 10;
			if (!g.moveTo(v.x, v.y, v.z, f) && (!d.isLeashed()) && (!d.isPassenger())
					&& v.distanceToSqr(this.aV) > this.tR) {
				CraftEntity entity = d.getBukkitEntity();
				Location to = new Location(entity.getWorld(), v.x, v.y, v.z, d.getYRot(), d.getXRot());
				EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
					return;
				to = event.getTo();
				d.moveTo(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
				g.getNodeEvaluator();
			}
		}
	}

	protected boolean a(int i, int j, int k, int l, int i1) {
		BlockPos blockposition = new BlockPos(i + l, k - 1, j + i1);
		BlockState iblockdata = a.getBlockState(blockposition);
		return (iblockdata.getDirectSignal(a, blockposition, Direction.DOWN) == 1) && (a.isEmptyBlock(blockposition.above()))
				&& (a.isEmptyBlock(blockposition.above(2)));
	}
}
