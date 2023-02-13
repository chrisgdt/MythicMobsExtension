package com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R2.pathfindergoals;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;

import io.lumine.mythic.core.mobs.ActiveMob;
import net.minecraft.core.BlockPos;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.event.entity.EntityTeleportEvent;

import com.gmail.berndivader.mythicmobsext.utils.Utils;

public class PathfinderGoalTravelAround extends Goal {

	ArrayList<SimpleEntry<Vec3, Boolean>> travelpoints;

	private final Mob mob;
	private Vec3 v;
	private Optional<ActiveMob> mM;
	private Vec3 aV;
	private final double f;
	private final double mR, tR;
	Level a;
	private final PathNavigation g;
	private int h, travel_index;
	float b;
	float c;
	private float i;
	private boolean iF, iT;

	public PathfinderGoalTravelAround(Mob entity, double d0, double mR, double tR, boolean iT) {
		this.mob = entity;
		this.f = d0;
		this.a = entity.level;
		g = entity.getNavigation();
		this.travelpoints = new ArrayList<>();
		this.travel_index = 0;
		this.v = nextCheckPoint();
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		this.mR = mR;
		this.tR = tR;
		this.iF = false;
		this.iT = iT;
		if ((!(entity.getNavigation() instanceof GroundPathNavigation))
				&& (!(entity.getNavigation() instanceof FlyingPathNavigation))) {
			throw new IllegalArgumentException("Unsupported mob type for TravelAroundGoal");
		}
		this.mM = Utils.mobmanager.getActiveMob(entity.getUUID());
	}

	@Override
	public boolean canUse() {
		this.aV = new Vec3(mob.getX(), mob.getY(), mob.getZ());
		if (this.v != null) {
			if (this.iT || this.mob.getTarget() == null || !this.mob.getTarget().isAlive()) {
				double ds = v.distanceToSqr(this.aV);
				if (ds > this.mR) {
					return true;
				} else if (this.iF && ds > 2.0D) {
					return true;
				} else {
					this.v = nextCheckPoint();
					if (this.iF) {
						if (this.mM.isPresent()) {
							ActiveMob am = this.mM.get();
							am.signalMob(null, v == null ? Utils.signal_GOAL_TRAVELEND : Utils.signal_GOAL_TRAVELPOINT);
						}
						this.iF = false;
					}
				}
			}
		} else {
			this.v = nextCheckPoint();
			if (this.iF) {
				if (this.mM.isPresent()) {
					ActiveMob am = this.mM.get();
					am.signalMob(null, v == null ? Utils.signal_GOAL_TRAVELEND : Utils.signal_GOAL_TRAVELPOINT);
				}
				this.iF = false;
			}
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
		i = mob.getPathfindingMalus(BlockPathTypes.WATER);
		mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
		if (!this.iF) {
			if (this.mM.isPresent()) {
				ActiveMob am = this.mM.get();
				am.signalMob(null, "GOAL_TRAVELSTART");
			}
		}
		this.iF = true;
	}

	@Override
	public void stop() {
		g.getNodeEvaluator();
		mob.setPathfindingMalus(BlockPathTypes.WATER, i);
		if (v.distanceToSqr(this.aV) < 10.0D) {
			this.iF = false;
			this.v = null;
		}
	}

	@Override
	public void tick() {
		mob.getLookControl().setLookAt(v.x, v.y, v.z, 10.0F, mob.getMaxHeadYRot());
		if (h-- <= 0) {
			h = 10;
			if (!g.moveTo(v.x, v.y, v.z, f) && (!mob.isLeashed()) && (!mob.isPassenger())
					&& v.distanceToSqr(this.aV) > this.tR) {
				CraftEntity entity = mob.getBukkitEntity();
				Location to = new Location(entity.getWorld(), v.x, v.y, v.z, mob.getYRot(), mob.getXRot());
				EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
					return;
				to = event.getTo();
				mob.moveTo(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
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

	protected Vec3 nextCheckPoint() {
		int size = this.travelpoints.size();
		Vec3 vector = null;
		if (size > 0) {
			if (travel_index >= size)
				travel_index = 0;
			SimpleEntry<Vec3, Boolean> entry = travelpoints.get(travel_index);
			vector = entry.getKey();
			if (entry.getValue())
				travelpoints.remove(travel_index);
			travel_index++;
		}
		return vector;
	}

	public void addTravelPoint(com.gmail.berndivader.mythicmobsext.utils.Vec3D vector, boolean remove) {
		travelpoints.add(new SimpleEntry<>(new Vec3(vector.getX(), vector.getY(), vector.getZ()), remove));
	}

	public void clearTravelPoints() {
		travelpoints = new ArrayList<>();
	}

}
