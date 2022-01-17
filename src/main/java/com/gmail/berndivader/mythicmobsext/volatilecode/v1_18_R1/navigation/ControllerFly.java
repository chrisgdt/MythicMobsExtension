package com.gmail.berndivader.mythicmobsext.volatilecode.v1_18_R1.navigation;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfinderAbstract;

public class ControllerFly extends ControllerMove {
	public ControllerFly(EntityInsentient entityInsentient) {
		super(entityInsentient);
	}

	@Override
	public void a() {
		EntityInsentient mob = this.d;
		if (this.k == Operation.c) { // STRAFE
			PathfinderAbstract pathfinderAbstract;
			float f2 = (float) mob.getAttributeInstance(GenericAttributes.d).getValue(); // MOVEMENT_SPEED
			float f3 = (float) this.e * f2;
			float f4 = this.i;
			float f5 = this.j;
			float f6 = MathHelper.c(f4 * f4 + f5 * f5);
			if (f6 < 1.0f)
				f6 = 1.0f;
			f6 = f3 / f6;
			float f7 = MathHelper.sin(mob.getYRot() * 0.017453292f);
			float f8 = MathHelper.cos(mob.getYRot() * 0.017453292f);
			float f9 = (f4 *= f6) * f8 - (f5 *= f6) * f7;
			float f10 = f5 * f8 + f4 * f7;
			NavigationAbstract navigationAbstract = mob.getNavigation();
			if (navigationAbstract != null && (pathfinderAbstract = navigationAbstract.q()) != null
					&& pathfinderAbstract.a(mob.getWorld(), MathHelper.floor(mob.locX() + (double) f9),
							MathHelper.floor(mob.locY()),
							MathHelper.floor(mob.locZ() + (double) f10)) != PathType.c) { // WALKABLE
				this.f = 1.0f;
				this.g = 0.0f;
				f3 = f2;
			}
			mob.n(f3);
			mob.v(this.i);
			mob.u(this.j);
			this.k = Operation.a; // WAIT
		} else if (this.k == ControllerMove.Operation.b) { // MOVE_TO
			this.k = ControllerMove.Operation.a; // WAIT
			mob.setNoGravity(true);
			double d2 = this.e - mob.locX();
			double d3 = this.f - mob.locY();
			double d4 = this.g - mob.locZ();
			double d5 = d2 * d2 + d3 * d3 + d4 * d4;
			if (d5 < 2.500000277905201E-7) {
				mob.u(0.0f);
				mob.v(0.0f);
				return;
			}
			float f2 = (float) (MathHelper.d(d4, d2) * 57.2957763671875) - 90.0f;
			mob.setYRot(this.a(mob.getYRot(), f2, 10.0f));
			float f3 = (float) (this.e * mob.getAttributeInstance(GenericAttributes.d).getValue());
			mob.n(f3);
			double d6 = MathHelper.c(d2 * d2 + d4 * d4); // sqrt
			float f4 = (float) (-MathHelper.d(d3, d6) * 57.2957763671875);
			mob.setXRot(this.a(mob.getXRot(), f4, 10.0f));
			mob.u(d3 > 0.0 ? f3 : -f3);
		} else {
			mob.setNoGravity(false);
			mob.u(0.0f);
			mob.v(0.0f);
		}
	}
}
