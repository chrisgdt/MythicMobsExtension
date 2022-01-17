package com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R1.pathfindergoals;

import java.util.EnumSet;
import java.util.Optional;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;

import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTeleportEvent;

import com.gmail.berndivader.mythicmobsext.utils.Utils;

import io.lumine.xikage.mythicmobs.mobs.ActiveMob;

public class PathfinderGoalReturnHome extends PathfinderGoal {
	private final EntityInsentient d;
	private final Vec3D v;
	private Optional<ActiveMob> mM;
	private Vec3D aV;
	private final double f;
	private final double mR, tR;
	World a;
	private final NavigationAbstract g;
	private int h;
	float b;
	float c;
	private float i;
	private boolean iF, iT;

	public PathfinderGoalReturnHome(EntityInsentient entity, double d0, double hx, double hy, double hz, double mR,
			double tR, boolean iT) {
		this.d = entity;
		this.f = d0;
		this.a = entity.getWorld();
		g = entity.getNavigation();
		this.a(EnumSet.of(PathfinderGoal.Type.a, PathfinderGoal.Type.b)); // MOVE LOOK
		this.v = new Vec3D(hx, hy + (double) d.getHeadHeight(), hz);
		this.mR = mR;
		this.tR = tR;
		this.iF = false;
		this.iT = iT;
		if ((!(entity.getNavigation() instanceof Navigation))
				&& (!(entity.getNavigation() instanceof NavigationFlying))) {
			throw new IllegalArgumentException("Unsupported mob type for ReturnHomeGoal");
		}
		this.mM = Utils.mobmanager.getActiveMob(entity.getUniqueID());
	}

	public boolean a() {
		this.aV = new Vec3D(d.locX(), d.locY(), d.locZ());
		if (this.iT || this.d.getGoalTarget() == null || !this.d.getGoalTarget().isAlive()) {
			double ds = v.distanceSquared(this.aV);
			if (ds > this.mR) {
				return true;
			} else if (this.iF && ds > 2.0D)
				return true;
		}
		return false;
	}

	public boolean b() {
		return (!g.n()) && v.distanceSquared(this.aV) > 2.0D;
	}

	public void c() {
		h = 0;
		i = d.a(PathType.i); // WATER
		d.a(PathType.i, 0.0F);
		if (this.mM.isPresent() && !this.iF) {
			ActiveMob am = this.mM.get();
			am.signalMob(null, "GOAL_STARTRETURNHOME");
		}
		this.iF = true;
	}

	public void d() {
		g.q();
		d.a(PathType.i, i); // WATER
		if (v.distanceSquared(this.aV) < 10.0D) {
			this.iF = false;
			if (this.mM.isPresent()) {
				ActiveMob am = this.mM.get();
				am.signalMob(null, "GOAL_ENDRETURNHOME");
			}
		}
	}

	public void e() {
		try {
			d.getControllerLook().a(v.getX(), v.getY(), v.getZ(), 10.0F, (int) d.getClass().getMethod("U").invoke(d)); // Mob#getMaxHeadYRot()
		} catch (ReflectiveOperationException ignored) {}
		if (h-- <= 0) {
			h = 10;
			if (!g.a(v.getX(), v.getY(), v.getZ(), f) && (!d.isLeashed()) && (!d.isPassenger())
					&& v.distanceSquared(this.aV) > this.tR) {
				Entity entity = d.getBukkitEntity();
				Location to = new Location(entity.getWorld(), v.getX(), v.getY(), v.getZ(), d.getYRot(), d.getXRot());
				EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
				d.getWorld().getCraftServer().getPluginManager().callEvent(event);
				if (event.isCancelled())
					return;
				to = event.getTo();
				d.setPositionRotation(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
				g.q();
				return;
			}
		}
	}

	protected boolean a(int i, int j, int k, int l, int i1) {
		BlockPosition blockposition = new BlockPosition(i + l, k - 1, j + i1);
		IBlockData iblockdata = a.getType(blockposition);
		return (iblockdata.c(a, blockposition, EnumDirection.a) == 1) && (a.isEmpty(blockposition.up())) // DOWN
				&& (a.isEmpty(blockposition.up(2)));
	}
}
