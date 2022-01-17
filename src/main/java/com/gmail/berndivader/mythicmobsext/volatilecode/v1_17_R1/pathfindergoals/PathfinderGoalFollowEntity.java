package com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.pathfindergoals;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;

import java.util.EnumSet;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathType;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.event.entity.EntityTeleportEvent;

public class PathfinderGoalFollowEntity extends PathfinderGoal {
	private final EntityInsentient d;
	private final EntityLiving d1;
	private EntityLiving e;
	World a;
	private final double f;
	private final NavigationAbstract g;
	private int h;
	float b;
	float c;
	private float i;

	public PathfinderGoalFollowEntity(EntityInsentient entity, EntityLiving entity1, double d0, float f, float f1) {
		this.d = entity;
		this.a = entity.getWorld();
		this.d1 = entity1;
		this.f = d0;
		g = entity.getNavigation();
		c = f;
		b = f1;
		a(EnumSet.of(PathfinderGoal.Type.a, PathfinderGoal.Type.b));
		if ((!(entity.getNavigation() instanceof Navigation))
				&& (!(entity.getNavigation() instanceof NavigationFlying))) {
			throw new IllegalArgumentException("Unsupported mob type for FollowEntityGoal");
		}
	}

	public boolean a() {
		this.e = this.d1;
		return (this.e != null && this.e.isAlive())
				&& ((!(this.e instanceof EntityHuman)) || !this.e.isSpectator()) && (!(d.f(this.e) < c * c)); // d.f : distanceToSqr
	}

	public boolean b() {
		return (!g.n()) && (d.f(e) > b * b); // d.f : // distanceToSqr
	}

	public void c() {
		h = 0;
		i = d.a(PathType.i); // WATER
		d.a(PathType.i, 0.0F);
	}

	public void d() {
		e = null;
		g.q();
		d.a(PathType.i, i);
	}

	public void e() {
		d.getControllerLook().a(e, 10.0F, d.fa()); // Mob#getMaxHeadYRot()
		if (h-- <= 0) {
			h = 10;
			if ((!g.a(e, f)) && (!d.isLeashed()) && (!d.isPassenger()) && (d.f(e) >= 144.0D)) { // d.f : // distanceToSqr
				int i = MathHelper.floor(e.locX()) - 2;
				int j = MathHelper.floor(e.locZ()) - 2;
				int k = MathHelper.floor(e.getBoundingBox().e); // maxY
				for (int l = 0; l <= 4; l++) {
					for (int i1 = 0; i1 <= 4; i1++) {
						if (((l < 1) || (i1 < 1) || (l > 3) || (i1 > 3)) && (a(i, j, k, l, i1))) {
							CraftEntity entity = d.getBukkitEntity();
							Location to = new Location(entity.getWorld(), i + l + 0.5F, k, j + i1 + 0.5F, d.getYRot(),
									d.getXRot());
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
