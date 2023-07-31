package com.gmail.berndivader.mythicmobsext.volatilecode.v1_20_R1.pathfindergoals;

import java.util.HashSet;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R1.event.CraftEventFactory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.gmail.berndivader.mythicmobsext.Main;

public class PathfinderGoalBreakBlocks extends Goal {
	protected Mob entity;
	protected boolean isBreaking;
	protected int chance;
	protected HashSet<Material> materials;
	Block[] blocks;

	public PathfinderGoalBreakBlocks(Mob entity, String mL, int chance) {
		this.blocks = new Block[2];
		this.isBreaking = false;
		this.entity = entity;
		this.materials = new HashSet<>();
		this.chance = chance > 100 ? 100 : Math.max(chance, 0);
		if (mL != null) {
			String[] parse = mL.toUpperCase().split(",");
			for (String s : parse) {
				try {
					this.materials.add(Material.valueOf(s));
				} catch (Exception ex) {
					Main.logger.warning("Material " + s + " is not valid for PathfinderGoalBreakBlocks.");
				}
			}
		}
	}

	@Override
	public boolean canUse() {
		return this.entity.isAlive();
	}

	@Override
	public boolean canContinueToUse() {
		return this.entity.getTarget() != null && this.entity.getTarget().isAlive();
	}

	@Override
	public void tick() {
		if (!this.canContinue()) {
			return;
		}
		net.minecraft.world.entity.LivingEntity target = this.entity.getTarget();
		blocks[1] = this.getBreakableTargetBlock(target);
		blocks[0] = blocks[1].getRelative(BlockFace.UP);
		for (int a = 0; a < 2; a++) {
			if (this.materials.isEmpty() || this.materials.contains(blocks[a].getType())) {
				this.attemptBreakBlock(blocks[a]);
			}
		}
	}

	private boolean canContinue() {
		if (Main.random.nextInt(100) <= chance) {
			net.minecraft.world.entity.LivingEntity target = this.entity.getTarget();
			return target != null && target.isAlive() && !this.isBreaking && !this.isReachable(target);
		}
		return false;
	}

	private Block getBreakableTargetBlock(net.minecraft.world.entity.LivingEntity target) {
		Vector direction = ((LivingEntity) this.entity.getBukkitEntity()).getLocation().getDirection();
		double dx = direction.getX();
		double dz = direction.getY();
		int bdx = 0;
		int bdz = 0;
		if (Math.abs(dx) > Math.abs(dz)) {
			bdx = (dx > 0) ? 1 : -1;
		} else {
			bdz = (dx > 0) ? 1 : -1;
		}
		return this.entity.level().getWorld().getBlockAt((int) Math.floor(this.entity.getX() + bdx),
				(int) Math.floor(this.entity.getY()), (int) Math.floor(this.entity.getZ() + bdz));
	}

	private void attemptBreakBlock(Block block) {
		Material type = block.getType();
		if (!this.isBreaking && type != Material.AIR && type.isSolid()) {
			if (Main.random.nextInt(100) <= this.chance) {
				this.isBreaking = true;
				PotionEffect effect = new PotionEffect(PotionEffectType.SLOW, 20, 4, false, false);
				((LivingEntity) this.entity.getBukkitEntity()).addPotionEffect(effect);
				new BukkitRunnable() {
					@Override
					public void run() {
						BlockPos position = new BlockPos(block.getX(), block.getY(), block.getZ());
						if (!CraftEventFactory.callEntityChangeBlockEvent(entity, position,
								entity.level().getBlockState(position).getBlock().defaultBlockState())) {
							// level.triggereffect(int, BlockPos, int) ???
							entity.level().destroyBlockProgress(2001, position,
									net.minecraft.world.level.block.Block.getId(entity.level().getBlockState(position)));
							block.breakNaturally();
							PathfinderGoalBreakBlocks.this.isBreaking = false;
						}
					}
				}.runTaskLater(Main.getPlugin(), 20L);
			}
		}
	}

	private boolean isReachable(net.minecraft.world.entity.LivingEntity target) {
		if (target == null)
			return true;
		return this.entity.hasLineOfSight(target);
	}
}
