package com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

import com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.advancement.FakeAdvancement;
import com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.advancement.FakeDisplay;
import com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.navigation.ControllerFly;
import com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.navigation.ControllerVex;
import com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.navigation.NavigationClimb;
import com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalTravelAround;
import io.lumine.mythic.api.skills.SkillResult;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.TagParser;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.*;

import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.entity.*;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.NMS.NMSUtils;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import com.gmail.berndivader.mythicmobsext.utils.Vec3D;
import com.gmail.berndivader.mythicmobsext.utils.math.MathUtils;
import com.gmail.berndivader.mythicmobsext.volatilecode.Handler;
import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;

public class Core implements Handler, Listener {

	private static Field ai_pathfinderlist_b;
	private static Field ai_pathfinderlist_c;

	private static Set<RelativeArgument> rot_set = new HashSet<>(Arrays
			.asList(RelativeArgument.X_ROT, RelativeArgument.Y_ROT));
	private static Set<RelativeArgument> rot_pos_set = new HashSet<>(
			Arrays.asList(RelativeArgument.X_ROT, RelativeArgument.Y_ROT,
					RelativeArgument.X, RelativeArgument.Y, RelativeArgument.Z));
	private static Set<RelativeArgument> pos_set = new HashSet<>(
			Arrays.asList(RelativeArgument.X, RelativeArgument.Y,
					RelativeArgument.Z));

	static {
		try {
			ai_pathfinderlist_b = GoalSelector.class.getDeclaredField("d"); // availableGoals
			ai_pathfinderlist_c = GoalSelector.class.getDeclaredField("c"); // lockedFlags
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
		ai_pathfinderlist_b.setAccessible(true);
		ai_pathfinderlist_c.setAccessible(true);
	}

	public Core() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getPlugin());
	}

	@EventHandler
	public static void join(PlayerJoinEvent e) {
		PacketReader packet_reader = new PacketReader(e.getPlayer());
		packet_reader.inject();
		PacketReader.readers.put(e.getPlayer().getUniqueId(), packet_reader);
	}

	@EventHandler
	public static void quit(PlayerQuitEvent e) {
		UUID uuid = e.getPlayer().getUniqueId();
		PacketReader packet_reader = PacketReader.readers.get(uuid);
		if (packet_reader != null) {
			packet_reader.uninject();
			PacketReader.readers.remove(uuid);
		}
	}

	@Override
	public Parrot spawnCustomParrot(Location l1, boolean b1) {
		return null;
	}

	@Override
	public LivingEntity spawnCustomZombie(Location location, boolean sunBurn) {
		return null;
	}

	private void sendPlayerPacketsAsync(List<Player> players, Packet<?>[] packets) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : players) {
					CraftPlayer cp = (CraftPlayer) player;
					for (Packet<?> packet : packets) {
						cp.getHandle().connection.send(packet);
					}
				}
			}
		}.runTaskAsynchronously(Main.getPlugin());
	}

	private void sendPlayerPacketsSync(List<Player> players, Packet<?>[] packets) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : players) {
					CraftPlayer cp = (CraftPlayer) player;
					for (Packet<?> packet : packets) {
						cp.getHandle().connection.send(packet);
					}

				}
			}
		}.runTask(Main.getPlugin());
	}

	@Override
	public List<Entity> getNearbyEntities(Entity bukkit_entity, int range) {
		net.minecraft.world.entity.Entity entity = ((CraftEntity) bukkit_entity).getHandle();
		return this.getNearbyEntities(entity.getLevel(), entity.getBoundingBox().inflate(range, range, range), null);
	}

	@Override
	public List<Entity> getNearbyEntities(Entity bukkit_entity, int range, Predicate<Entity> filter) {
		net.minecraft.world.entity.Entity entity = ((CraftEntity) bukkit_entity).getHandle();
		return this.getNearbyEntities(entity.getLevel(), entity.getBoundingBox().inflate(range, range, range), filter);
	}

	@Override
	public List<Entity> getNearbyEntities(World bukkit_world, BoundingBox bukkit_aabb, Predicate<Entity> filter) {
		return this.getNearbyEntities(
				((CraftWorld) bukkit_world).getHandle(), new AABB(bukkit_aabb.getMinX(), bukkit_aabb.getMinY(),
						bukkit_aabb.getMinZ(), bukkit_aabb.getMaxX(), bukkit_aabb.getMaxY(), bukkit_aabb.getMaxZ()),
				filter);
	}

	@Override
	public List<Entity> getNearbyEntities(World bukkit_world, Location bukkit_location, double x, double y, double z,
			Predicate<Entity> filter) {
		BoundingBox bukkit_aabb = BoundingBox.of(bukkit_location, x, y, z);
		return this.getNearbyEntities(
				((CraftWorld) bukkit_world).getHandle(), new AABB(bukkit_aabb.getMinX(), bukkit_aabb.getMinY(),
						bukkit_aabb.getMinZ(), bukkit_aabb.getMaxX(), bukkit_aabb.getMaxY(), bukkit_aabb.getMaxZ()),
				filter);
	}

	public List<Entity> getNearbyEntities(net.minecraft.world.level.Level world, AABB bb,
										  Predicate<Entity> filter) {
		List<net.minecraft.world.entity.Entity> entityList = world.getEntities(null, bb);
		ArrayList<org.bukkit.entity.Entity> bukkitEntityList = new ArrayList<org.bukkit.entity.Entity>(
				entityList.size());
		for (net.minecraft.world.entity.Entity entity : entityList) {
			CraftEntity bukkitEntity = entity.getBukkitEntity();
			if (filter != null && !filter.test(bukkitEntity))
				continue;
			bukkitEntityList.add(bukkitEntity);
		}
		return bukkitEntityList;
	}

	@Override
	public void setFieldOfViewPacketSend(Player player, float f1) {
		ServerPlayer me = ((CraftPlayer) player).getHandle();
		Abilities arg1 = (Abilities) Utils.cloneObject(me.getAbilities());
		if (f1 != 0) {
			player.setMetadata(Utils.meta_WALKSPEED, new FixedMetadataValue(Main.getPlugin(), arg1.walkingSpeed));
		} else if (player.hasMetadata(Utils.meta_WALKSPEED)) {
			f1 = player.getMetadata(Utils.meta_WALKSPEED).get(0).asFloat();
		}
		arg1.walkingSpeed = f1;
		me.connection.send(new ClientboundPlayerAbilitiesPacket(arg1));
	}

	@Override
	public void playBlockBreak(int eid, Location location, int stage) {
		BlockPos blockPosition = new BlockPos(location.getBlockX(), location.getBlockY(),
				location.getBlockZ());
		List<Player> players = Utils.getPlayersInRange(location, Utils.renderLength);
		ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(eid, blockPosition, stage);
		sendPlayerPacketsAsync(players, new Packet[] { packet });
	}

	@Override
	public void forceSpectate(Player player, Entity e, boolean bl1) {
		LivingEntity entity = (LivingEntity) e;
		ServerPlayer entityPlayer = ((CraftPlayer) player).getHandle();
		entityPlayer.connection.send(new ClientboundSetCameraPacket(((CraftEntity) entity).getHandle()));
		if (bl1) {
			dupePlayer(entityPlayer, player.getLocation());
			entity.remove();
		}
	}

	void dupePlayer(ServerPlayer entityplayer, Location location) {
		ServerLevel worldServer = ((CraftWorld) location.getWorld()).getHandle();
		MinecraftServer minecraftServer = entityplayer.level.getServer();
		PlayerList playerList = minecraftServer.getPlayerList();

		entityplayer.stopRiding();
		playerList.players.remove(entityplayer);

		// TODO
		Map<UUID, ServerPlayer> ppn;
		try {
			Field playersByUUID = playerList.getClass().getDeclaredField("k");
			playersByUUID.setAccessible(true);
			ppn = (Map<UUID, ServerPlayer>) playersByUUID.get(playerList);

			Field players = playerList.getClass().getDeclaredField("j");
			players.setAccessible(true);
			((List<ServerPlayer>) players.get(playerList)).remove(entityplayer);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return;
		}

		ppn.remove(entityplayer.getUUID());
		//NMSUtils.setFinalField("k", playerList.getClass(), playerList, ppn);

		entityplayer.getLevel().removePlayerImmediately(entityplayer, net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
		ServerPlayer entityplayer1 = entityplayer;
		entityplayer.wonGame = false;
		entityplayer1.connection = entityplayer.connection;
		entityplayer1.restoreFrom(entityplayer, true);
		entityplayer1.setId(entityplayer.getId());
		entityplayer1.setMainArm(entityplayer.getMainArm());
		for (String s : entityplayer.getTags()) {
			entityplayer1.addTag(s);
		}
		LevelData worlddata = worldServer.getLevelData();
		location.setWorld(minecraftServer.getLevel(entityplayer.getRespawnDimension()).getWorld());
		entityplayer1.forceSetPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
				location.getPitch());
		/*
		entityplayer1.playerConnection.sendPacket(new PacketPlayOutRespawn(
				entityplayer1.world.getDimensionManager(), entityplayer1.world.getDimensionKey(),
				BiomeManager.a(entityplayer1.getWorldServer().getSeed()),
				entityplayer1.playerInteractManager.getGameMode(), entityplayer1.playerInteractManager.c(),
				entityplayer1.getWorldServer().isDebugWorld(), entityplayer1.getWorldServer().isFlatWorld(), true));
		 */
		entityplayer1.connection.send(new ClientboundRespawnPacket(
				entityplayer1.level.dimensionTypeId(), entityplayer1.level.dimension(),
				BiomeManager.obfuscateSeed(entityplayer1.getLevel().getSeed()),
				entityplayer1.gameMode.getGameModeForPlayer(), entityplayer1.gameMode.getPreviousGameModeForPlayer(),
				entityplayer1.getLevel().isDebug(), entityplayer1.getLevel().isFlat(), true, entityplayer1.getLastDeathLocation()));
		entityplayer1.connection.send(new ClientboundSetChunkCacheRadiusPacket(worldServer.spigotConfig.viewDistance));
		entityplayer1.spawnIn(worldServer);

		// TODO :
		//entityplayer1.dead = false;
		NMSUtils.setField(entityplayer1, "be", false);

		entityplayer1.connection.teleport(new Location(worldServer.getWorld(), entityplayer1.getX(),
				entityplayer1.getY(), entityplayer1.getZ(), entityplayer1.getYRot(), entityplayer1.getXRot()));
		entityplayer1.setShiftKeyDown(false);
		BlockPos blockPosition1 = worldServer.getSharedSpawnPos();
		entityplayer1.connection.send(new ClientboundSetDefaultSpawnPositionPacket(blockPosition1, 0));
		entityplayer1.connection.send(
				new ClientboundChangeDifficultyPacket(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
		entityplayer1.connection.send(
				new ClientboundSetExperiencePacket(entityplayer1.experienceProgress, entityplayer1.totalExperience, entityplayer1.experienceLevel));
		playerList.sendLevelInfo(entityplayer1, worldServer);
		playerList.sendPlayerPermissionLevel(entityplayer1);
		if (!entityplayer.connection.isDisconnected()) {
			worldServer.addRespawnedPlayer(entityplayer1);
			// worldServer.addEntity(entityplayer1);
			playerList.players.add(entityplayer1);
			/*
			ppn = (Map<String, EntityPlayer>) NMSUtils.getField("playersByName", playerList.getClass().getSuperclass(),
					playerList);
			ppn.put(entityplayer1.getName(), entityplayer1);
			NMSUtils.setFinalField("playersByName", playerList.getClass().getSuperclass(), playerList, ppn);
			Map<UUID, EntityPlayer> j = (Map<UUID, EntityPlayer>) NMSUtils.getField("j",
					playerList.getClass().getSuperclass(), playerList);
			j.put(entityplayer1.getUniqueID(), entityplayer1);
			NMSUtils.setFinalField("j", playerList.getClass().getSuperclass(), playerList, j);
			 */
			// TODO
			try {
				Field playersByUUID = playerList.getClass().getDeclaredField("k");
				playersByUUID.setAccessible(true);
				ppn = (Map<UUID, ServerPlayer>) playersByUUID.get(playerList);

				Field players = playerList.getClass().getDeclaredField("j");
				players.setAccessible(true);
				((List<ServerPlayer>) players.get(playerList)).add(entityplayer);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			ppn.put(entityplayer1.getUUID(), entityplayer1);
			//NMSUtils.setFinalField("k", playerList.getClass(), playerList, ppn);
			//NMSUtils.setFinalField("j", playerList.getClass().getSuperclass(), playerList, j);
		}
		entityplayer1.setHealth(entityplayer1.getHealth());
		entityplayer.onUpdateAbilities();
		for (MobEffectInstance mobEffect : entityplayer.getActiveEffects()) {
			entityplayer.connection.send(new ClientboundUpdateMobEffectPacket(entityplayer.getId(), mobEffect));
		}
		entityplayer.triggerDimensionChangeTriggers(worldServer);
		if (entityplayer.connection.isDisconnected()) {
			PlayerAdvancements advancementdataplayer;
			if (!entityplayer.getBukkitEntity().isPersistent()) {
				return;
			}
			playerList.playerIo.save(entityplayer);
			ServerStatsCounter serverstatisticmanager = entityplayer.getStats();
			if (serverstatisticmanager != null) {
				serverstatisticmanager.save();
			}
			if ((advancementdataplayer = entityplayer.getAdvancements()) != null) {
				advancementdataplayer.save();
			}
		}
		playerList.sendAllPlayerInfo(entityplayer);
	}

	public void forceEntitySitting(Entity entity) {
	}

	@Override
	public void playEndScreenForPlayer(Player player, float f) {
		ServerPlayer me = ((CraftPlayer) player).getHandle();
		me.connection.send(new ClientboundGameEventPacket(new ClientboundGameEventPacket.Type(4), f));
	}

	@Override
	public void fakeEntityDeath(Entity entity, long d) {
		net.minecraft.world.entity.LivingEntity me = ((CraftLivingEntity) entity).getHandle();
		me.getLevel().broadcastEntityEvent(me, (byte) 3);
		ClientboundRemoveEntitiesPacket pd = new ClientboundRemoveEntitiesPacket(me.getId());
		ClientboundAddEntityPacket ps = new ClientboundAddEntityPacket(me);
		new BukkitRunnable() {
			@Override
			public void run() {
				sendPlayerPacketsAsync(Utils.getPlayersInRange(entity.getLocation(), Utils.renderLength),
						new Packet[] { pd, ps });
			}
		}.runTaskLaterAsynchronously(Main.getPlugin(), d);
	}

	@Override
	public void forceCancelEndScreenPlayer(Player player) {
		ServerPlayer me = ((CraftPlayer) player).getHandle();
		me.connection.send(new ClientboundContainerClosePacket(0));
	}

	@Override
	public void forceSetPositionRotation(Entity entity, double x, double y, double z, float yaw, float pitch, boolean f,
			boolean g) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
		me.absMoveTo(x, y, z, yaw, pitch);
		if (entity instanceof Player) {
			playerConnectionTeleport(entity, x, y, z, yaw, pitch, f, g);
		}
//        me.world.entityJoinedWorld(me, false);
	}

	private void playerConnectionTeleport(Entity entity, double x, double y, double z, float yaw, float pitch,
			boolean f, boolean g) {
		ServerPlayer me = ((CraftPlayer) entity).getHandle();
		Set<RelativeArgument> set = new HashSet<>();
		if (f) {
			set = rot_set;
			yaw = 0.0F;
			pitch = 0.0F;
		}
		if (g) {
			set.add(RelativeArgument.Y);
			y = 0.0D;
		}
		me.connection.send(new ClientboundPlayerPositionPacket(x, y, z, yaw, pitch, set, 0, false));
	}

	@Override
	public void rotateEntityPacket(Entity entity, float y, float p) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
		byte ya = (byte) ((int) (y * 256.0F / 360.0F));
		byte pa = (byte) ((int) (p * 256.0F / 360.0F));
		ClientboundMoveEntityPacket.Rot el = new ClientboundMoveEntityPacket.Rot(me.getId(), ya, pa, me.isOnGround());
		ClientboundRotateHeadPacket hr = new ClientboundRotateHeadPacket(me, ya);
		sendPlayerPacketsAsync(Utils.getPlayersInRange(entity.getLocation(), Utils.renderLength),
				new Packet[] { el, hr });
	}

	@Override
	public void playerConnectionLookAt(Player entity, float yaw, float pitch) {
		ServerPlayer me = ((CraftPlayer) entity).getHandle();
		me.connection.send(new ClientboundPlayerPositionPacket(0, 0, 0, yaw, pitch, pos_set, 0, false));
	}

	@Override
	public void playerConnectionSpin(Entity entity, float s) {
		if (entity instanceof CraftPlayer) {
			ServerPlayer me = ((CraftPlayer) entity).getHandle();
			me.connection.send(new ClientboundPlayerPositionPacket(0, 0, 0, s, 0, rot_pos_set, 0, false));
		}
	}

	@Override
	public void changeHitBox(Entity entity, double a0, double a1, double a2) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
		me.getBoundingBox().inflate(a0, a1, a2);
	}

	@Override
	public void setItemMotion(Item i, Location ol, Location nl) {
		ItemEntity ei = (ItemEntity) ((CraftItem) i).getHandle();
		ei.setPosRaw(ol.getX(), ol.getY(), ol.getZ());
	}

	@Override
	public void sendArmorstandEquipPacket(ArmorStand entity) {
		ClientboundSetEquipmentPacket packet = new ClientboundSetEquipmentPacket(entity.getEntityId(),
				Lists.newArrayList(Pair.of(EquipmentSlot.CHEST, new ItemStack(Blocks.DIAMOND_BLOCK, 1))));
		sendPlayerPacketsAsync(Utils.getPlayersInRange(entity.getLocation(), Utils.renderLength),
				new Packet[] { packet });
	}

	@Override
	public void teleportEntityPacket(Entity entity) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
		ClientboundTeleportEntityPacket tp = new ClientboundTeleportEntityPacket(me);
		sendPlayerPacketsAsync(Utils.getPlayersInRange(entity.getLocation(), Utils.renderLength), new Packet[] { tp });
	}

	@Override
	public void moveEntityPacket(Entity entity, Location cl, double x, double y, double z) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
		double x1 = cl.getX() - me.getX();
		double y1 = cl.getY() - me.getY();
		double z1 = cl.getZ() - me.getZ();
		ClientboundSetEntityMotionPacket vp = new ClientboundSetEntityMotionPacket(me.getId(),
				new net.minecraft.world.phys.Vec3(x1, y1, z1));
		sendPlayerPacketsAsync(Utils.getPlayersInRange(entity.getLocation(), Utils.renderLength), new Packet[] { vp });
	}

	@Override
	public boolean inMotion(LivingEntity entity) {
		net.minecraft.world.entity.LivingEntity e = ((CraftLivingEntity) entity).getHandle();
		if (e.xOld != e.getX() || e.yOld != e.getY() || e.zOld != e.getZ())
			return true;
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void aiTargetSelector(LivingEntity entity, String uGoal, LivingEntity target) {
		World w = entity.getWorld();
		net.minecraft.world.entity.LivingEntity e = ((CraftLivingEntity) entity).getHandle();
		net.minecraft.world.entity.LivingEntity tE = null;
		if (target != null) {
			tE = ((CraftLivingEntity) entity).getHandle();
		}
		Field goalsField;
		int i = 0;
		String goal = uGoal;
		String data = null;
		String data1 = null;
		String[] parse = uGoal.split(" ");
		if (parse[0].matches("[0-9]*")) {
			i = Integer.parseInt(parse[0]);
			String[] cpy = new String[parse.length - 1];
			System.arraycopy(parse, 0, cpy, 0, 0);
			System.arraycopy(parse, 1, cpy, 0, parse.length - 1);
			parse = cpy;
		}
		if (parse.length > 0) {
			goal = parse[0];
			if (parse.length > 1) {
				data = parse[1];
			}
			if (parse.length > 2) {
				data1 = parse[2];
			}
		}
		try {
			goalsField = Mob.class.getDeclaredField("targetSelector");
			goalsField.setAccessible(true);
			GoalSelector goals = (GoalSelector) goalsField.get((Object) e);
			switch (goal) {
			case "otherteams":
				goals.addGoal(i, new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalOtherTeams(
								(PathfinderMob) e, net.minecraft.world.entity.player.Player.class, true));
				break;
			default:
				List<String> gList = new ArrayList<>();
				gList.add(uGoal);
				Utils.mythicmobs.getVolatileCodeHandler().getAIHandler().addPathfinderGoals(entity, gList);
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void aiPathfinderGoal(LivingEntity entity, String uGoal, LivingEntity target) {
		World w = entity.getWorld();
		Mob e = (Mob) ((CraftLivingEntity) entity).getHandle();
		net.minecraft.world.entity.LivingEntity tE = null;
		if (target != null)
			tE = ((CraftLivingEntity) target).getHandle();
		int i = -1;
		String goal = uGoal;
		String data = null;
		String data1 = null;
		String[] parse = uGoal.split(" ");
		if (parse[0].matches("[0-9]*")) {
			i = Integer.parseInt(parse[0]);
			String[] cpy = new String[parse.length - 1];
			System.arraycopy(parse, 0, cpy, 0, 0);
			System.arraycopy(parse, 1, cpy, 0, parse.length - 1);
			parse = cpy;
		}
		if (parse.length > 0) {
			goal = parse[0];
			if (parse.length > 1) {
				data = parse[1];
			}
			if (parse.length > 2) {
				data1 = parse[2];
			}
		}
		GoalSelector goals = e.goalSelector;
		Optional<Goal> pathfindergoal = Optional.empty();
		if (!goal.equals("removegoal")) {
			switch (goal) {
			case "rangedmelee": {
				if (e instanceof PathfinderMob) {
					float range = 2.0f;
					if (data != null) {
						range = Float.parseFloat(data);
					}
					pathfindergoal = Optional
							.of(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalMeleeRangeAttack((PathfinderMob) e, 1.0, true, range));
				}
				break;
			}
			case "attack": {
				if (e instanceof PathfinderMob) {
					double s = 1.0d;
					float r = 2.0f;
					if (data != null)
						s = Double.parseDouble(data);
					if (data1 != null)
						r = Float.parseFloat(data1);
					pathfindergoal = Optional.of(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalAttack((PathfinderMob) e, s, true, r));
				}
				break;
			}
			case "runfromsun": {
				if (e instanceof PathfinderMob) {
					double s = 1.0d;
					if (data != null)
						s = Double.parseDouble(data);
					pathfindergoal = Optional.of(new FleeSunGoal((PathfinderMob) e, s));
				}
				break;
			}
			case "shootattack": {
				if (e instanceof Mob) {
					double d1 = 1.0d;
					int i1 = 20, i2 = 60;
					float f1 = 15.0f;
					if (data != null) {
						String[] p = data.split(",");
						for (int a = 0; a < p.length; a++) {
							switch (a) {
							case 0:
								d1 = Double.parseDouble(p[a]);
								break;
							case 1:
								i1 = Integer.parseInt(p[a]);
								break;
							case 2:
								i2 = Integer.parseInt(p[a]);
								break;
							case 3:
								f1 = Float.parseFloat(p[a]);
								break;
							}
						}
					}
					pathfindergoal = Optional.of(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathFinderGoalShoot((Mob) e, d1, i1, i2, f1));
				}
				break;
			}
			case "followentity": {
				UUID uuid = null;
				if (e instanceof PathfinderMob) {
					double speed = 1.0d;
					float aR = 2.0F;
					float zR = 10.0F;
					String[] p = data.split(",");
					for (int a = 0; a < p.length; a++) {
						switch (a) {
						case 0:
							speed = Double.parseDouble(p[a]);
							break;
						case 1:
							aR = Float.parseFloat(p[a]);
							break;
						case 2:
							zR = Float.parseFloat(p[a]);
							break;
						}
					}
					if (data1 != null && (uuid = Utils.isUUID(data1)) != null) {
						Entity ee = NMSUtils.getEntity(w, uuid);
						if (ee instanceof LivingEntity) {
							tE = ((CraftLivingEntity) ee).getHandle();
						}
					}
					if (tE != null && tE.isAlive()) {
						pathfindergoal = Optional.of(
								new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalFollowEntity(
										e, tE, speed, zR, aR));
					}
				}
				break;
			}
			case "breakblocks": {
				if (e instanceof PathfinderMob) {
					int chance = 50;
					if (data1 != null && MathUtils.isNumeric(data1))
						chance = Integer.parseInt(data1);
					pathfindergoal = Optional.of(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalBreakBlocks(e, data, chance));
				}
				break;
			}
			case "jumpoffvehicle": {
				if (e instanceof PathfinderMob) {
					pathfindergoal = Optional.of(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalJumpOffFromVehicle(e));
				}
				break;
			}
			case "notifycollide": {
				if (e instanceof Mob) {
					int c = data != null && MathUtils.isNumeric(data) ? Integer.parseInt(data) : 5;
					pathfindergoal = Optional.ofNullable(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalNotifyOnCollide(e, c));
				}
				break;
			}
			case "notifyheal": {
				if (e instanceof net.minecraft.world.entity.LivingEntity) {
					pathfindergoal = Optional.ofNullable(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalNotifyHeal(e, "mme_heal"));
				}
				break;
			}
			case "notifygrow":
			case "grownotify": {
				if (e instanceof AgeableMob) {
					pathfindergoal = Optional.ofNullable(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalEntityGrowNotify(e, data));
				} else {
					Main.logger.warning("No ageable entity");
				}
				break;
			}
			case "returnhome": {
				if (e instanceof PathfinderMob) {
					double speed = 1.0d;
					double x = e.getX();
					double y = e.getY();
					double z = e.getZ();
					double mR = 10.0D;
					double tR = 512.0D;
					boolean iT = false;
					if (data != null) {
						speed = Double.parseDouble(data);
					}
					if (data1 != null) {
						String[] p = data1.split(",");
						for (int a = 0; a < p.length; a++) {
							if (MathUtils.isNumeric(p[a])) {
								switch (a) {
								case 0:
									x = Double.parseDouble(p[a]);
									break;
								case 1:
									y = Double.parseDouble(p[a]);
									break;
								case 2:
									z = Double.parseDouble(p[a]);
									break;
								case 3:
									mR = Double.parseDouble(p[a]);
									break;
								case 4:
									tR = Double.parseDouble(p[a]);
									break;
								}
							} else if (a == 5) {
								iT = Boolean.parseBoolean(p[a].toUpperCase());
							}
						}
					}
					pathfindergoal = Optional.ofNullable(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalReturnHome(e, speed, x, y, z, mR, tR, iT));
					break;
				}
			}
			case "travelaround": {
				if (e instanceof PathfinderMob) {
					double speed = 1.0d;
					double mR = 50.0D;
					double tR = 1100.0D;
					boolean iT = false;
					if (data != null)
						speed = Double.parseDouble(data);
					if (data1 != null) {
						String[] p = data1.split(",");
						for (int a = 0; a < p.length; a++) {
							if (MathUtils.isNumeric(p[a])) {
								switch (a) {
								case 0:
									mR = Double.parseDouble(p[a]);
									break;
								case 1:
									tR = Double.parseDouble(p[a]);
									break;
								}
							} else if (a == 2) {
								iT = Boolean.parseBoolean(p[a].toUpperCase());
							}
						}
					}
					pathfindergoal = Optional.ofNullable(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalTravelAround(e, speed, mR, tR, iT));
					break;
				}
			}
			case "doorsopen": {
				if (e instanceof Mob) {
					boolean bl1 = data != null ? Boolean.parseBoolean(data) : false;
					pathfindergoal = Optional.ofNullable(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalDoorOpen(e, bl1));
				}
				break;
			}
			case "doorsbreak": {
				if (e instanceof Mob) {
					boolean bl1 = data != null ? Boolean.parseBoolean(data) : false;
					pathfindergoal = Optional.ofNullable(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalDoorBreak(e, bl1));
				}
				break;
			}
			case "avoidtarget":
			case "avoidentity":
				if (data == null || data.isEmpty())
					return;
				float distance = 16f;
				double speed = 1.2d;
				Optional<EntityType<?>> type = EntityType.byString(data);
				if (type.isPresent()) {
					if (data1 != null) {
						String[] arr1 = data1.split(",");
						if (arr1.length > 0)
							distance = Float.parseFloat(arr1[0]);
						if (arr1.length > 1)
							speed = Double.parseDouble(arr1[1]);
					}
					pathfindergoal = Optional.of(new AvoidEntityGoal<>((PathfinderMob) e, null, distance, 1d, speed));
				}
				break;
			case "vexa": {
				pathfindergoal = Optional.of(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalVexA(e));
			}
			case "vexd": {
				pathfindergoal = Optional.of(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalVexD(e));
			}
			}
			if (pathfindergoal.isPresent()) {
				if (i > -1) {
					e.goalSelector.addGoal(i, pathfindergoal.get());
				} else {
					e.goalSelector.removeGoal(pathfindergoal.get());
				}
			} else {
				List<String> gList = new ArrayList<>();
				gList.add(uGoal);
				Utils.mythicmobs.getVolatileCodeHandler().getAIHandler().addPathfinderGoals(entity, gList);
			}
		} else {
			if (i > -1) {
				try {
					((Map<?,?>) ai_pathfinderlist_c.get(goals)).clear();
					Set<?> list = (Set<?>) ai_pathfinderlist_b.get(goals);
					Iterator<?> iter = list.iterator();
					while (iter.hasNext()) {
						Object object = iter.next();
						int priority = NMSUtils.getPathfinderGoalSelectorItemPriority(object);
						if (priority > -1 && priority == i)
							iter.remove();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				try {
					((Map<?,?>) ai_pathfinderlist_c.get(goals)).clear();
					((Set<?>) ai_pathfinderlist_b.get(goals)).clear();
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void addTravelPoint(Entity bukkit_entity, Vec3D vector, boolean remove) {
		Mob entity = ((CraftMob) bukkit_entity).getHandle();
		GoalSelector goals = entity.goalSelector;
		try {
			((Map) ai_pathfinderlist_c.get(goals)).clear();
			LinkedHashSet<Object> list = (LinkedHashSet) ai_pathfinderlist_b.get((Object) goals);
			Iterator<Object> iter = list.iterator();
			while (iter.hasNext()) {
				Object object = iter.next();
				Goal goal = (Goal) NMSUtils.getPathfinderGoalFromPathFinderSelectorItem(object);
				if (goal instanceof com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalTravelAround) {
					((com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R1.pathfindergoals.PathfinderGoalTravelAround) goal).addTravelPoint(vector, remove);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void clearTravelPoints(Entity bukkit_entity) {
		Mob entity = ((CraftMob) bukkit_entity).getHandle();
		GoalSelector goals = entity.goalSelector;
		Set<WrappedGoal> avaibleGoals = goals.getAvailableGoals();
		for (WrappedGoal avaibleGoal : avaibleGoals) {
			Goal goal = avaibleGoal.getGoal();
		}
		goals.removeAllGoals();
		try {
			((Map) ai_pathfinderlist_c.get(goals)).clear();
			LinkedHashSet<Object> list = (LinkedHashSet) ai_pathfinderlist_b.get((Object) goals);
			for (Object object : list) {
				Goal goal = (Goal) NMSUtils.getPathfinderGoalFromPathFinderSelectorItem(object);
				if (goal instanceof PathfinderGoalTravelAround) {
					((PathfinderGoalTravelAround) goal).clearTravelPoints();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean getNBTValueOf(Entity e1, String s1, boolean def) {
		return getNBTValue(e1, s1, def);
	}

	@Override
	public boolean addNBTTag(Entity e1, String s) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) e1).getHandle();
		CompoundTag nbt1 = null, nbt2 = null, nbt3 = null;
		if ((nbt1 = TFa(me)) != null) {
			nbt3 = nbt1.copy();
			try {
				nbt2 = TagParser.parseTag(s);
			} catch (CommandSyntaxException ex) {
				System.err.println(ex.getLocalizedMessage());
				return false;
			}
			UUID u = me.getUUID();
			nbt1.merge(nbt2);
			me.setUUID(u);
			if (nbt3.equals(nbt1)) {
				return false;
			}
			me.load(nbt1);
			return true;
		}
		return false;
	}

	private boolean getNBTValue(Entity e1, String s, boolean def) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) e1).getHandle();
		CompoundTag nbt1 = null, nbt2 = null;
		boolean bl1 = false;
		if ((nbt1 = TFa(me)) != null) {
			try {
				nbt2 = TagParser.parseTag(s);
			} catch (CommandSyntaxException ex) {
				System.err.println(ex.getLocalizedMessage());
				return def;
			}
			Tag nb1 = null, nb2 = null;
			for (String s2 : nbt1.getAllKeys()) {
				if (nbt2.contains(s2)) {
					nb1 = nbt1.get(s2);
					nb2 = nbt2.get(s2);
					break;
				}
			}
			if (nb1 != null && nb2 != null) {
				bl1 = nbtA(nb2, nb1, bl1 = true);
			}
		}
		return bl1;
	}

	private boolean nbtA(Tag nb1, Tag nb2, boolean bl1) {
		if (!bl1)
			return bl1;
		switch (nb1.getId()) {
		case CraftMagicNumbers.NBT.TAG_LIST:
			ListTag nbl1 = (ListTag) nb1;
			ListTag nbl2 = (ListTag) nb2;
			for (int i1 = 0; i1 < nbl1.size(); i1++) {
				Tag nb3 = nbl1.get(i1);
				Tag nb4 = nbl2.get(i1);
				if (nb3.toString().toLowerCase().contains("id:\"ignore\""))
					nb3 = nb4;
				if (!(bl1 = nbtA(nb3, nb4, bl1)))
					break;
			}
			break;
		case CraftMagicNumbers.NBT.TAG_COMPOUND:
			CompoundTag nbt1 = (CompoundTag) nb1;
			CompoundTag nbt2 = (CompoundTag) nb2;
			if (nbt1.isEmpty() && !nbt2.isEmpty())
				bl1 = false;
			for (String s : nbt1.getAllKeys()) {
				if (!bl1)
					break;
				if (nbt2.contains(s)) {
					Tag nb3 = nbt1.get(s);
					bl1 = nbtA(nb3, nbt2.get(s), bl1 = true);
				} else {
					Tag nb3 = nbt1.get(s);
					bl1 = nbtA(nb3, nbt2.get(s), bl1 = false);
				}
			}
			break;
		default:
			bl1 = Utils.parseNBToutcome(nb1.toString(), nb2.toString(), nb1.getId());
			break;
		}
		return bl1;
	}

	@Override
	public boolean testForCondition(Entity e, String ns, char m) {
		return testFor(e, ns, m);
	}

	private boolean testFor(Entity e, String c, char m) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) e).getHandle();
		CompoundTag nbt1 = null, nbt2 = null;
		try {
			nbt2 = TagParser.parseTag(c);
		} catch (CommandSyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		nbt1 = TFa(me);
		if (nbt2 != null && !GPa(nbt2, nbt1, true)) {
			return false;
		}
		return true;
	}

	private CompoundTag TFa(net.minecraft.world.entity.Entity e) {
		CompoundTag nbt = null;
		if (e.valid) {
			try {
				ItemStack is;
				nbt = e.saveWithoutId(new CompoundTag());
				if (e instanceof net.minecraft.world.entity.player.Player
						&& !(is = ((net.minecraft.world.entity.player.Player) e).getInventory().player.getMainHandItem()).isEmpty()) {
					nbt.put("SelectedItem", is.save(new CompoundTag()));
				}
			} catch (Throwable t) {
				Main.logger.warning("Error while getting NBT for entity " + e.getBukkitEntity().getType()
						+ " " + e.getCustomName() + " : " + t.getMessage() + " caused by " + t.getCause().getMessage());
			}
		}
		return nbt;
	}

	private boolean GPa(Tag b1, Tag b2, boolean lc) {
		if (b1 == b2 || b1 == null)
			return true;
		if (b2 == null || !b1.getClass().equals(b2.getClass()))
			return false;
		if (b1 instanceof CompoundTag) {
			CompoundTag nbt1 = (CompoundTag) b1;
			CompoundTag nbt2 = (CompoundTag) b2;
			for (String s : nbt1.getAllKeys()) {
				Tag b3 = nbt1.get(s);
				if (GPa(b3, nbt2.get(s), false)) {
					continue;
				}
				return false;
			}
			return true;
		}
		if (b1 instanceof ListTag) {
			ListTag nbtl1 = (ListTag) b1;
			ListTag nbtl2 = (ListTag) b2;
			if (nbtl1.isEmpty())
				return nbtl2.isEmpty();
			for (Tag b4 : nbtl1) {
				boolean bl2 = false;
				for (Tag tag : nbtl2) {
					if (!GPa(b4, tag, false))
						continue;
					bl2 = true;
					break;
				}
				if (bl2)
					continue;
				return false;
			}
			return true;
		}
		return b1.equals(b2);
	}

	@Override
	public boolean playerIsSleeping(Player p) {
		ServerPlayer me = ((CraftPlayer) p).getHandle();
		return me.isSleeping() || me.isSleepingLongEnough();
	}

	@Override
	public boolean playerIsRunning(Player p) {
		ServerPlayer me = ((CraftPlayer) p).getHandle();
		return me.isSprinting();
	}

	@Override
	public boolean playerIsCrouching(Player p) {
		ServerPlayer me = ((CraftPlayer) p).getHandle();
		return me.isShiftKeyDown();
	}

	@Override
	public boolean playerIsJumping(Player p) {
		ServerPlayer me = ((CraftPlayer) p).getHandle();
		return !me.isOnGround() && MathUtils.round(me.getDeltaMovement().y , 5) != -0.00784;
	}

	@Override
	public void setDeath(Player p, boolean b) {
		ServerPlayer me = ((CraftPlayer) p).getHandle();
		// TODO :
		//me.dead = b;
		NMSUtils.setField(me, "be", b);
	}

	@Override
	public float getIndicatorPercentage(Player p) {
		net.minecraft.world.entity.player.Player eh = ((CraftHumanEntity) p).getHandle();
		return eh.getAttackStrengthScale(0.0f);
	}

	@Override
	public float getItemCoolDown(Player p, int i1) {
		net.minecraft.world.entity.player.Player eh = ((CraftPlayer) p).getHandle();
		return eh.getCooldowns()
				.getCooldownPercent(i1 == -1 ? eh.getMainHandItem().getItem() : eh.getInventory().getItem(i1).getItem(), 0.0f);
	}

	@Override
	public SkillResult setItemCooldown(org.bukkit.entity.Player p, int j1, int i1) {
		ServerPlayer eh = ((CraftPlayer) p).getHandle();
		net.minecraft.world.item.Item i = i1 == -1 ? eh.getItemInHand(InteractionHand.MAIN_HAND).getItem()
				: eh.getInventory().getItem(i1).getItem();
		if (eh.getCooldowns().cooldowns.containsKey(i)) {
			eh.getCooldowns().cooldowns.remove(i);
		}
		eh.getCooldowns().addCooldown(i, j1);
		return SkillResult.SUCCESS;
	}

	@Override
	public void moveto(LivingEntity entity) {
		// empty
	}

	@Override
	public void setWorldborder(Player p, int density, boolean play) {
		ServerPlayer ep = ((CraftPlayer) p).getHandle();
		WorldBorder border = ep.level.getWorldBorder();
		if (play) {
			border = new WorldBorder();
			border.world = ep.level.getWorldBorder().world;
			if (density == 0) {
				border.setCenter(0, 0);
				border.setSize(Integer.MAX_VALUE);
				border.setWarningBlocks(Integer.MAX_VALUE);
			} else {
				border.setCenter(99999, 99999);
				border.setSize(1);
				border.setWarningBlocks(1);
			}
		}
		// TODO packet play out world border
		//ep.connection.send(new PacketPlayOutWorldBorder(border, EnumWorldBorderAction.INITIALIZE));
		ep.connection.send(new ClientboundSetBorderCenterPacket(border));
		ep.connection.send(new ClientboundSetBorderLerpSizePacket(border));
		ep.connection.send(new ClientboundSetBorderSizePacket(border));
		ep.connection.send(new ClientboundSetBorderWarningDelayPacket(border));
		ep.connection.send(new ClientboundSetBorderWarningDistancePacket(border));
		border = null;
	}

	@Override
	public void setMNc(LivingEntity e1, String s1) {
		Mob ei = (Mob) ((CraftLivingEntity) e1).getHandle();
		switch (s1) {
		case "FLY":
			NMSUtils.setField("navigation", Mob.class, ei, new FlyingPathNavigation(ei, ei.level));
			NMSUtils.setField("moveController", Mob.class, ei, new ControllerFly(ei));
			break;
		case "VEX":
			NMSUtils.setField("navigation", Mob.class, ei, new GroundPathNavigation(ei, ei.level));
			NMSUtils.setField("moveController", Mob.class, ei, new ControllerVex(ei));
			break;
		case "WALK":
			NMSUtils.setField("navigation", Mob.class, ei, new GroundPathNavigation(ei, ei.level));
			NMSUtils.setField("moveController", Mob.class, ei, new MoveControl(ei));
			break;
		case "CLIMB":
			NMSUtils.setField("navigation", Mob.class, ei, new NavigationClimb(ei, ei.level));
			NMSUtils.setField("moveController", Mob.class, ei, new MoveControl(ei));
			break;
		}
	}

	@Override
	public void forceBowDraw(LivingEntity e1, LivingEntity target, boolean bl1) {
		if (bl1)
			System.err.println("try to draw bow");
		net.minecraft.world.entity.LivingEntity ei = ((CraftLivingEntity) e1).getHandle();
		if (ei.isUsingItem()) {
			if (bl1)
				System.err.println("hand not raised!");
			ei.stopUsingItem();
		} else {
			if (bl1)
				System.err.println("hand is raised draws bow");
			ei.swing(InteractionHand.MAIN_HAND);
		}
	}

	@Override
	public void changeResPack(Player p, String url, String hash) {
		ServerPlayer player = ((CraftPlayer) p).getHandle();
		player.connection.send(new ClientboundResourcePackPacket(url, hash, false, null));
	}

	@Override
	public void forceSpectate(Player player, Entity entity) {
		this.forceSpectate(player, entity, false);
	}

	@Override
	public void playAnimationPacket(LivingEntity e, int[] ints) {
		net.minecraft.world.entity.LivingEntity living = ((CraftLivingEntity) e).getHandle();
		ClientboundAnimatePacket[] packets = new ClientboundAnimatePacket[ints.length];
		for (int j = 0; j < ints.length; j++) {
			packets[j] = new ClientboundAnimatePacket(living, ints[j]);
		}
		sendPlayerPacketsAsync(Utils.getPlayersInRange(e.getLocation(), Utils.renderLength), packets);
	}

	@Override
	public void playAnimationPacket(LivingEntity e, int id) {
		playAnimationPacket(e, new int[]{ id });
	}

	@Override
	public boolean velocityChanged(Entity bukkit_entity) {
		net.minecraft.world.entity.Entity entity = ((CraftEntity) bukkit_entity).getHandle();
		return entity.getLevel().noCollision(entity.getBoundingBox().inflate(0.001, 0.001, 0.001));
	}

	@Override
	public Vec3D getPredictedMotion(LivingEntity bukkit_source, LivingEntity bukkit_target, float delta) {
		net.minecraft.world.entity.LivingEntity target = ((CraftLivingEntity) bukkit_target).getHandle();
		net.minecraft.world.entity.LivingEntity source = ((CraftLivingEntity) bukkit_source).getHandle();

		double delta_x = target.getX() + (target.getX() - target.xOld) * delta - source.getX();
		double delta_y = target.getY() + (target.getY() - target.yOld) * delta + target.getEyeHeight() - 0.15f
				- source.getY() - source.getEyeHeight();
		double delta_z = target.getZ() + (target.getZ() - target.zOld) * delta - source.getZ();

		return new Vec3D(delta_x, delta_y, delta_z);
	}

	@Override
	public void sendPlayerAdvancement(Player player, Material material, String title, String description, String task) {
		new FakeAdvancement(new FakeDisplay(material, title, description, FakeDisplay.AdvancementFrame.valueOf(task), null))
				.displayToast(player);
	}

	@Override
	public boolean isReachable1(LivingEntity bukkit_entity, LivingEntity bukkit_target) {
		net.minecraft.world.entity.LivingEntity target = ((CraftLivingEntity) bukkit_target).getHandle();
		Mob entity = (Mob) ((CraftLivingEntity) bukkit_entity).getHandle();
		if (target == null)
			return true;
		Path pe = entity.getNavigation().createPath(target, 16);
		if (pe == null) {
			return !entity.isOnGround();
		} else {
			Node pp = pe.getEndNode();
			return pp != null;
		}
	}

	@Override
	public void addTravelPoint(Entity bukkit_entity, Vec3D vector) {
		this.addTravelPoint(bukkit_entity, vector, true);
	}

}
