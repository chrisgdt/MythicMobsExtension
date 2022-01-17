package com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.navigation;

import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;

public class NavigationClimb extends Navigation {
	private BlockPosition i;

	public NavigationClimb(EntityInsentient entityInsentient, World world) {
		super(entityInsentient, world);
		NMSUtils.setField("moveController", EntityInsentient.class, this.a, new MoveController(this.a));
	}

	@Override
	public PathEntity a(BlockPosition blockPosition, int i1) {
		this.i = blockPosition;
		return super.a(blockPosition, i1);
	}

	@Override
	public PathEntity a(Entity entity, int i1) {
		this.i = new BlockPosition(entity.locX(), entity.locY(), entity.locZ());
		return super.a(entity, i1);
	}

	@Override
	public boolean a(Entity entity, double d2) {
		PathEntity pathEntity = this.a(entity, 0);
		if (pathEntity != null) {
			return this.a(pathEntity, d2);
		}
		this.i = new BlockPosition(entity.locX(), entity.locY(), entity.locZ());
		this.d = d2;
		return true;
	}

	@Override
	public void c() {
		if (this.n()) {
			if (this.i != null) {
				if (this.i.a((IPosition) this.a.getPositionVector(), (double) this.a.getWidth())
						|| this.a.locY() > (double) this.i.getY()
								&& new BlockPosition((double) this.i.getX(), this.a.locY(), (double) this.i.getZ())
										.a((IPosition) this.a.getPositionVector(), (double) this.a.getWidth())) {
					this.i = null;
				} else {
					if (!(this.a.getControllerMove() instanceof MoveController)) {
						NMSUtils.setField("moveController", EntityInsentient.class, this.a, new MoveController(this.a));
					}
					((MoveController) this.a.getControllerMove()).aa(this.i.getX(), this.i.getY(), this.i.getZ(),
							this.d);
				}
			}
			return;
		}
		super.c();
	}

	class MoveController extends ControllerMove {

		public MoveController(EntityInsentient arg0) {
			super(arg0);
		}

		public void aa(double d2, double d3, double d4, double d5) {
			this.e = d2;
			this.f = d3;
			this.g = d4;
			this.h = d5;
			this.k = Operation.b; // MOVE_TO
		}
	}
}
