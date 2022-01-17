package com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

import com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.advancement.FakeAdvancement;
import com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.navigation.ControllerFly;
import com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.navigation.ControllerVex;
import com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.navigation.NavigationClimb;
import com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.pathfindergoals.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.game.PacketPlayOutPosition.RelativeArgument;

import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.stats.ServerStatisticManager;
import net.minecraft.world.EnumHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.RelativeArgument;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.AxisAlignedBB;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.*;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftMagicNumbers;
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

	private static final Set<RelativeArgument> rot_set = new HashSet<>(
			Arrays.asList(RelativeArgument.X_ROT, RelativeArgument.Y_ROT));
	private static final Set<RelativeArgument> rot_pos_set = new HashSet<>(
			Arrays.asList(RelativeArgument.X_ROT, RelativeArgument.Y_ROT, RelativeArgument.X, RelativeArgument.Y, RelativeArgument.Z));
	private static final Set<RelativeArgument> pos_set = new HashSet<>(
			Arrays.asList(RelativeArgument.X, RelativeArgument.Y, RelativeArgument.Z));

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
				for (int i1 = 0; i1 < players.size(); i1++) {
					CraftPlayer cp = (CraftPlayer) players.get(i1);
					for (int i2 = 0; i2 < packets.length; i2++) {
						cp.getHandle().connection.send(packets[i2]);
					}

				}
			}
		}.runTaskAsynchronously(Main.getPlugin());
	}

	private void sendPlayerPacketsSync(List<Player> players, Packet<?>[] packets) {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (int i1 = 0; i1 < players.size(); i1++) {
					CraftPlayer cp = (CraftPlayer) players.get(i1);
					for (int i2 = 0; i2 < packets.length; i2++) {
						cp.getHandle().connection.send(packets[i2]);
					}

				}
			}
		}.runTask(Main.getPlugin());
	}

	@Override
	public List<Entity> getNearbyEntities(Entity bukkit_entity, int range) {
		return this.getNearbyEntities(bukkit_entity.getWorld(), bukkit_entity.getBoundingBox().expand(range, range, range), null);
	}

	@Override
	public List<Entity> getNearbyEntities(Entity bukkit_entity, int range, Predicate<Entity> filter) {
		return this.getNearbyEntities(bukkit_entity.getWorld(), bukkit_entity.getBoundingBox().expand(range, range, range), filter);
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
		ArrayList<org.bukkit.entity.Entity> bukkitEntityList = new ArrayList<>(entityList.size());
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
		Abilities abilities = (Abilities) Utils.cloneObject(me.getAbilities());
		if (f1 != 0) {
			player.setMetadata(Utils.meta_WALKSPEED, new FixedMetadataValue(Main.getPlugin(), abilities.walkingSpeed));
		} else if (player.hasMetadata(Utils.meta_WALKSPEED)) {
			f1 = player.getMetadata(Utils.meta_WALKSPEED).get(0).asFloat();
		}
		abilities.walkingSpeed = f1;
		me.connection.send(new ClientboundPlayerAbilitiesPacket(abilities));
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
		ServerPlayer ServerPlayer = ((CraftPlayer) player).getHandle();
		ServerPlayer.connection.send(new ClientboundSetCameraPacket(((CraftEntity) entity).getHandle()));
		if (bl1) {
			dupePlayer(ServerPlayer, player.getLocation());
			entity.remove();
		}
	}

	void dupePlayer(ServerPlayer serverPlayer, Location location) {
		ServerLevel worldServer = ((CraftWorld) location.getWorld()).getHandle();
		MinecraftServer minecraftServer = serverPlayer.getCommandSenderWorld().getServer();
		PlayerList playerList = minecraftServer.getPlayerList();

		serverPlayer.stopRiding();
		playerList.players.remove(serverPlayer);
		Map<String, ServerPlayer> ppn = (Map<String, ServerPlayer>) NMSUtils.getField("playersByName",
				playerList.getClass().getSuperclass(), playerList);
		ppn.remove(serverPlayer.getName().getContents().toLowerCase(Locale.ROOT));
		NMSUtils.setFinalField("playersByName", playerList.getClass().getSuperclass(), playerList, ppn);
		serverPlayer.getCommandSenderWorld().removePlayer(serverPlayer);
		ServerPlayer serverPlayer1 = serverPlayer;
		serverPlayer.wonGame = false;
		serverPlayer1.connection = serverPlayer.connection;
		serverPlayer1.restoreFrom(serverPlayer, true);
		serverPlayer1.setId(serverPlayer.getId());
		serverPlayer1.setMainArm(serverPlayer.getMainArm());
		for (String s : serverPlayer.getTags()) {
			serverPlayer1.addTag(s);
		}
		LevelData worlddata = worldServer.getLevelData();
		location.setWorld(minecraftServer.getLevel(serverPlayer.getRespawnDimension()).getWorld());
		serverPlayer1.forceSetPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(),
				location.getPitch());
		serverPlayer1.connection.send(new ClientboundRespawnPacket(
				serverPlayer1.level.dimensionType(), serverPlayer1.level.dimension(),
				BiomeManager.obfuscateSeed(serverPlayer1.getLevel().getSeed()),
				serverPlayer1.playerInteractManager.getGameMode(), serverPlayer1.playerInteractManager.c(),
				serverPlayer1.getWorldServer().isDebugWorld(), serverPlayer1.getWorldServer().isFlatWorld(), true));
		serverPlayer1.connection.send(new PacketPlayOutViewDistance(worldServer.spigotConfig.viewDistance));
		serverPlayer1.spawnIn(worldServer);
		serverPlayer1.dead = false;
		serverPlayer1.connection.teleport(new Location(worldServer.getWorld(), serverPlayer1.locX(),
				serverPlayer1.locY(), serverPlayer1.locZ(), serverPlayer1.yaw, serverPlayer1.pitch));
		serverPlayer1.setSneaking(false);
		BlockPosition blockPosition1 = worldServer.getSpawn();
		serverPlayer1.connection.send(new PacketPlayOutSpawnPosition(blockPosition1, 0));
		serverPlayer1.connection.send(
				new PacketPlayOutServerDifficulty(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
		serverPlayer1.connection.send(
				new PacketPlayOutExperience(serverPlayer1.exp, serverPlayer1.expTotal, serverPlayer1.expLevel));
		playerList.a(serverPlayer1, worldServer);
		playerList.d(serverPlayer1);
		if (!serverPlayer.connection.isDisconnected()) {
			worldServer.addPlayerRespawn(serverPlayer1);
			// worldServer.addEntity(ServerPlayer1);
			playerList.players.add(serverPlayer1);
			ppn = (Map<String, ServerPlayer>) NMSUtils.getField("playersByName", playerList.getClass().getSuperclass(),
					playerList);
			ppn.put(serverPlayer1.getName(), serverPlayer1);
			NMSUtils.setFinalField("playersByName", playerList.getClass().getSuperclass(), playerList, ppn);
			Map<UUID, ServerPlayer> j = (Map<UUID, ServerPlayer>) NMSUtils.getField("j",
					playerList.getClass().getSuperclass(), playerList);
			j.put(serverPlayer1.getUniqueID(), serverPlayer1);
			NMSUtils.setFinalField("j", playerList.getClass().getSuperclass(), playerList, j);
		}
		serverPlayer1.setHealth(serverPlayer1.getHealth());
		serverPlayer.updateAbilities();
		for (Object o1 : serverPlayer.getEffects()) {
			MobEffect mobEffect = (MobEffect) o1;
			serverPlayer.connection.send(new PacketPlayOutEntityEffect(serverPlayer.getId(), mobEffect));
		}
		serverPlayer.triggerDimensionAdvancements(worldServer);
		if (serverPlayer.connection.isDisconnected()) {
			AdvancementDataPlayer advancementdataplayer;
			if (!serverPlayer.getBukkitEntity().isPersistent()) {
				return;
			}
			playerList.playerFileData.save(serverPlayer);
			ServerStatisticManager serverstatisticmanager = serverPlayer.getStatisticManager();
			if (serverstatisticmanager != null) {
				serverstatisticmanager.save();
			}
			if ((advancementdataplayer = serverPlayer.getAdvancementData()) != null) {
				advancementdataplayer.b();
			}
		}
		playerList.updateClient(serverPlayer);
	}

	public void forceEntitySitting(Entity entity) {
	}

	@Override
	public void playEndScreenForPlayer(Player player, float f) {
		ServerPlayer me = ((CraftPlayer) player).getHandle();
		me.connection.send(new PacketPlayOutGameStateChange(new PacketPlayOutGameStateChange.a(4), f));
	}

	@Override
	public void fakeEntityDeath(Entity entity, long d) {
		net.minecraft.world.entity.LivingEntity me = ((CraftLivingEntity) entity).getHandle();
		me.world.broadcastEntityEffect(me, (byte) 3);
		PacketPlayOutEntityDestroy pd = new PacketPlayOutEntityDestroy(me.getId());
		PacketPlayOutSpawnnet.minecraft.world.entity.LivingEntity ps = new PacketPlayOutSpawnnet.minecraft.world.entity.LivingEntity(me);
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
		me.connection.send(new PacketPlayOutCloseWindow(0));
	}

	@Override
	public void forceSetPositionRotation(Entity entity, double x, double y, double z, float yaw, float pitch, boolean f,
			boolean g) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
		me.setLocation(x, y, z, yaw, pitch);
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
		me.connection.send(new PacketPlayOutPosition(x, y, z, yaw, pitch, set, 0));
	}

	@Override
	public void rotateEntityPacket(Entity entity, float y, float p) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
		byte ya = (byte) ((int) (y * 256.0F / 360.0F));
		byte pa = (byte) ((int) (p * 256.0F / 360.0F));
		PacketPlayOutEntity.PacketPlayOutEntityLook el = new PacketPlayOutEntity.PacketPlayOutEntityLook(me.getId(), ya, pa, me.isOnGround());
		PacketPlayOutEntityHeadRotation hr = new PacketPlayOutEntityHeadRotation(me, ya);
		sendPlayerPacketsAsync(Utils.getPlayersInRange(entity.getLocation(), Utils.renderLength),
				new Packet[] { el, hr });
	}

	@Override
	public void playerConnectionLookAt(Entity entity, float yaw, float pitch) {
		ServerPlayer me = ((CraftPlayer) entity).getHandle();
		me.connection.send(new PacketPlayOutPosition(0, 0, 0, yaw, pitch, pos_set, 0));
	}

	@Override
	public void playerConnectionSpin(Entity entity, float s) {
		ServerPlayer me = ((CraftPlayer) entity).getHandle();
		me.connection.send(new PacketPlayOutPosition(0, 0, 0, s, 0, rot_pos_set, 0));
	}

	@Override
	public void changeHitBox(Entity entity, double a0, double a1, double a2) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
		me.getBoundingBox().a(a0, a1, a2);
	}

	@Override
	public void setItemMotion(Item i, Location ol, Location nl) {
		EntityItem ei = (EntityItem) ((CraftItem) i).getHandle();
		ei.setPosition(ol.getX(), ol.getY(), ol.getZ());
	}

	@Override
	public void sendArmorstandEquipPacket(ArmorStand entity) {
		
		PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(entity.getEntityId(),
				Lists.newArrayList(Pair.of(EnumItemSlot.CHEST, new ItemStack(Blocks.DIAMOND_BLOCK, 1))));
		sendPlayerPacketsAsync(Utils.getPlayersInRange(entity.getLocation(), Utils.renderLength),
				new Packet[] { packet });
	}

	@Override
	public void teleportEntityPacket(Entity entity) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
		PacketPlayOutEntityTeleport tp = new PacketPlayOutEntityTeleport(me);
		sendPlayerPacketsAsync(Utils.getPlayersInRange(entity.getLocation(), Utils.renderLength), new Packet[] { tp });
	}

	@Override
	public void moveEntityPacket(Entity entity, Location cl, double x, double y, double z) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) entity).getHandle();
		double x1 = cl.getX() - me.locX();
		double y1 = cl.getY() - me.locY();
		double z1 = cl.getZ() - me.locZ();
		PacketPlayOutEntityVelocity vp = new PacketPlayOutEntityVelocity(me.getId(),
				new net.minecraft.world.phys.Vec3D(x1, y1, z1));
		sendPlayerPacketsAsync(Utils.getPlayersInRange(entity.getLocation(), Utils.renderLength), new Packet[] { vp });
	}

	@Override
	public boolean inMotion(LivingEntity entity) {
		Mob e = (Mob) ((CraftLivingEntity) entity).getHandle();
		if (e.lastX != e.locX() || e.lastY != e.locY() || e.lastZ != e.locZ())
			return true;
		return false;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void aiTargetSelector(LivingEntity entity, String uGoal, LivingEntity target) {
		World w = entity.getWorld();
		Mob e = (Mob) ((CraftLivingEntity) entity).getHandle();
		net.minecraft.world.entity.LivingEntity tE = null;
		if (target != null) {
			tE = (net.minecraft.world.entity.LivingEntity) ((CraftLivingEntity) entity).getHandle();
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
			PathfinderGoalSelector goals = (PathfinderGoalSelector) goalsField.get((Object) e);
			switch (goal) {
			case "otherteams":
				goals.a(i, new PathfinderGoalOtherTeams((EntityCreature) e, EntityHuman.class, true));
				break;
			default:
				List<String> gList = new ArrayList<String>();
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
			tE = (net.minecraft.world.entity.LivingEntity) ((CraftLivingEntity) target).getHandle();
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
		PathfinderGoalSelector goals = e.goalSelector;
		Optional<PathfinderGoal> pathfindergoal = Optional.empty();
		if (!goal.equals("removegoal")) {
			switch (goal) {
			case "rangedmelee": {
				if (e instanceof EntityCreature) {
					float range = 2.0f;
					if (data != null) {
						range = Float.parseFloat(data);
					}
					pathfindergoal = Optional
							.ofNullable(new PathfinderGoalMeleeRangeAttack((EntityCreature) e, 1.0, true, range));
				}
				break;
			}
			case "attack": {
				if (e instanceof EntityCreature) {
					double s = 1.0d;
					float r = 2.0f;
					if (data != null)
						s = Double.parseDouble(data);
					if (data1 != null)
						r = Float.parseFloat(data1);
					pathfindergoal = Optional.ofNullable(new PathfinderGoalAttack((EntityCreature) e, s, true, r));
				}
				break;
			}
			case "runfromsun": {
				if (e instanceof EntityCreature) {
					double s = 1.0d;
					if (data != null)
						s = Double.parseDouble(data);
					pathfindergoal = Optional.ofNullable(new PathfinderGoalFleeSun((EntityCreature) e, s));
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
					pathfindergoal = Optional.ofNullable(new PathFinderGoalShoot((Mob) e, d1, i1, i2, f1));
				}
				break;
			}
			case "followentity": {
				UUID uuid = null;
				if (e instanceof EntityCreature) {
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
							tE = (net.minecraft.world.entity.LivingEntity) ((CraftLivingEntity) (LivingEntity) ee).getHandle();
						}
					}
					if (tE != null && tE.isAlive()) {
						pathfindergoal = Optional.ofNullable(
								new PathfinderGoalFollowEntity(
										e, tE, speed, zR, aR));
					}
				}
				break;
			}
			case "breakblocks": {
				if (e instanceof EntityCreature) {
					int chance = 50;
					if (data1 != null && MathUtils.isNumeric(data1))
						chance = Integer.parseInt(data1);
					pathfindergoal = Optional.ofNullable(new PathfinderGoalBreakBlocks(e, data, chance));
				}
				break;
			}
			case "jumpoffvehicle": {
				if (e instanceof EntityCreature) {
					pathfindergoal = Optional.ofNullable(new PathfinderGoalJumpOffFromVehicle(e));
				}
				break;
			}
			case "notifycollide": {
				if (e instanceof Mob) {
					int c = data != null && MathUtils.isNumeric(data) ? Integer.parseInt(data) : 5;
					pathfindergoal = Optional.ofNullable(new PathfinderGoalNotifyOnCollide(e, c));
				}
				break;
			}
			case "notifyheal": {
				if (e instanceof net.minecraft.world.entity.LivingEntity) {
					pathfindergoal = Optional.ofNullable(new PathfinderGoalNotifyHeal(e, "mme_heal"));
				}
				break;
			}
			case "notifygrow":
			case "grownotify": {
				if (e instanceof EntityAgeable) {
					pathfindergoal = Optional.ofNullable(new PathfinderGoalEntityGrowNotify(e, data));
				} else {
					Main.logger.warning("No ageable entity");
				}
				break;
			}
			case "returnhome": {
				if (e instanceof EntityCreature) {
					double speed = 1.0d;
					double x = e.locX();
					double y = e.locY();
					double z = e.locZ();
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
					pathfindergoal = Optional.ofNullable(new PathfinderGoalReturnHome(e, speed, x, y, z, mR, tR, iT));
					break;
				}
			}
			case "travelaround": {
				if (e instanceof EntityCreature) {
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
					pathfindergoal = Optional.ofNullable(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.pathfindergoals.PathfinderGoalTravelAround(e, speed, mR, tR, iT));
					break;
				}
			}
			case "doorsopen": {
				if (e instanceof Mob) {
					boolean bl1 = data != null ? Boolean.parseBoolean(data) : false;
					pathfindergoal = Optional.ofNullable(new PathfinderGoalDoorOpen(e, bl1));
				}
				break;
			}
			case "doorsbreak": {
				if (e instanceof Mob) {
					boolean bl1 = data != null ? Boolean.parseBoolean(data) : false;
					pathfindergoal = Optional.ofNullable(new PathfinderGoalDoorBreak(e, bl1));
				}
				break;
			}
			case "avoidtarget":
			case "avoidentity":
				if (data == null || data.isEmpty())
					return;
				float distance = 16f;
				double speed = 1.2d;
				EntityTypes<?> type = EntityTypes.a(data).get();
				if (type != null) {
					if (data1 != null) {
						String[] arr1 = data1.split(",");
						if (arr1.length > 0)
							distance = Float.parseFloat(arr1[0]);
						if (arr1.length > 1)
							speed = Double.parseDouble(arr1[1]);
					}
					pathfindergoal = Optional
							.ofNullable(new PathfinderGoalAvoidTarget<>((EntityCreature) e, null, distance, 1d, speed));
				}
				break;
			case "vexa": {
				if (e instanceof Mob) {
					pathfindergoal = Optional.ofNullable(new PathfinderGoalVexA(e));
				}
			}
			case "vexd": {
				if (e instanceof Mob) {
					pathfindergoal = Optional.ofNullable(new PathfinderGoalVexD(e));
				}
			}
			}
			if (pathfindergoal.isPresent()) {
				if (i > -1) {
					e.goalSelector.a(i, pathfindergoal.get());
				} else {
					e.goalSelector.a(pathfindergoal.get());
				}
			} else {
				List<String> gList = new ArrayList<String>();
				gList.add(uGoal);
				Utils.mythicmobs.getVolatileCodeHandler().getAIHandler().addPathfinderGoals(entity, gList);
			}
		} else {
			if (i > -1) {
				try {
					((Map) ai_pathfinderlist_c.get(goals)).clear();
					LinkedHashSet<Object> list = (LinkedHashSet) ai_pathfinderlist_b.get((Object) goals);
					Iterator<Object> iter = list.iterator();
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
					((Map) ai_pathfinderlist_c.get(goals)).clear();
					((LinkedHashSet) ai_pathfinderlist_b.get(goals)).clear();
				} catch (IllegalArgumentException | IllegalAccessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	@Override
	public void addTravelPoint(Entity bukkit_entity, Vec3D vector, boolean remove) {
		Mob entity = (Mob) ((CraftLivingEntity) bukkit_entity).getHandle();
		PathfinderGoalSelector goals = entity.goalSelector;
		try {
			((Map) ai_pathfinderlist_c.get((Object) goals)).clear();
			LinkedHashSet<Object> list = (LinkedHashSet) ai_pathfinderlist_b.get((Object) goals);
			Iterator<Object> iter = list.iterator();
			while (iter.hasNext()) {
				Object object = iter.next();
				PathfinderGoal goal = (PathfinderGoal) NMSUtils.getPathfinderGoalFromPathFinderSelectorItem(object);
				if (goal instanceof com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.pathfindergoals.PathfinderGoalTravelAround) {
					((com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.pathfindergoals.PathfinderGoalTravelAround) goal).addTravelPoint(vector, remove);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void clearTravelPoints(Entity bukkit_entity) {
		Mob entity = (Mob) ((CraftLivingEntity) bukkit_entity).getHandle();
		PathfinderGoalSelector goals = entity.goalSelector;
		try {
			((Map) ai_pathfinderlist_c.get((Object) goals)).clear();
			LinkedHashSet<Object> list = (LinkedHashSet) ai_pathfinderlist_b.get((Object) goals);
			Iterator<Object> iter = list.iterator();
			while (iter.hasNext()) {
				Object object = iter.next();
				PathfinderGoal goal = (PathfinderGoal) NMSUtils.getPathfinderGoalFromPathFinderSelectorItem(object);
				if (goal instanceof com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.pathfindergoals.PathfinderGoalTravelAround) {
					((PathfinderGoalTravelAround) goal).clearTravelPoints();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean getNBTValueOf(Entity e1, String s1, boolean b1) {
		return getNBTValue(e1, s1);
	}

	@Override
	public boolean addNBTTag(Entity e1, String s) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) e1).getHandle();
		NBTTagCompound nbt1 = null, nbt2 = null, nbt3 = null;
		if ((nbt1 = TFa(me)) != null) {
			nbt3 = nbt1.clone();
			try {
				nbt2 = MojangsonParser.parse(s);
			} catch (CommandSyntaxException ex) {
				System.err.println(ex.getLocalizedMessage());
				return false;
			}
			UUID u = me.getUniqueID();
			nbt1.a(nbt2);
			me.a_(u);
			if (nbt3.equals(nbt1)) {
				return false;
			}
			me.load(nbt1);
			return true;
		}
		return false;
	}

	private boolean getNBTValue(Entity e1, String s) {
		net.minecraft.world.entity.Entity me = ((CraftEntity) e1).getHandle();
		NBTTagCompound nbt1 = null, nbt2 = null;
		boolean bl1 = false;
		if ((nbt1 = TFa(me)) != null) {
			try {
				nbt2 = MojangsonParser.parse(s);
			} catch (CommandSyntaxException ex) {
				System.err.println(ex.getLocalizedMessage());
				return false;
			}
			NBTBase nb1 = null, nb2 = null;
			for (String s2 : nbt1.getKeys()) {
				if (nbt2.hasKey(s2)) {
					nb1 = nbt1.get(s2);
					nb2 = nbt2.get(s2);
					break;
				}
			}
			if (nb1 != null && nb2 != null)
				bl1 = nbtA(nb2, nb1, bl1 = true);
		}
		return bl1;
	}

	private boolean nbtA(NBTBase nb1, NBTBase nb2, boolean bl1) {
		if (!bl1)
			return bl1;
		switch (nb1.getTypeId()) {
		case CraftMagicNumbers.NBT.TAG_LIST:
			NBTTagList nbl1 = (NBTTagList) nb1;
			NBTTagList nbl2 = (NBTTagList) nb2;
			for (int i1 = 0; i1 < nbl1.size(); i1++) {
				NBTBase nb3 = nbl1.b(i1);
				NBTBase nb4 = nbl2.b(i1);
				if (nb3.toString().toLowerCase().contains("id:\"ignore\""))
					nb3 = nb4;
				if (!(bl1 = nbtA(nb3, nb4, bl1)))
					break;
			}
			break;
		case CraftMagicNumbers.NBT.TAG_COMPOUND:
			NBTTagCompound nbt1 = (NBTTagCompound) nb1;
			NBTTagCompound nbt2 = (NBTTagCompound) nb2;
			if (nbt1.isEmpty() && !nbt2.isEmpty())
				bl1 = false;
			for (String s : nbt1.getKeys()) {
				if (!bl1)
					break;
				if (nbt2.hasKey(s)) {
					NBTBase nb3 = nbt1.get(s);
					bl1 = nbtA(nb3, nbt2.get(s), bl1 = true);
				} else {
					NBTBase nb3 = nbt1.get(s);
					bl1 = nbtA(nb3, nbt2.get(s), bl1 = false);
				}
			}
			break;
		default:
			bl1 = Utils.parseNBToutcome(nb1.toString(), nb2.toString(), nb1.getTypeId());
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
		NBTTagCompound nbt1 = null, nbt2 = null;
		try {
			nbt2 = MojangsonParser.parse(c);
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

	private NBTTagCompound TFa(net.minecraft.world.entity.Entity e) {
		NBTTagCompound nbt = null;
		if (e.valid) {
			try {
				ItemStack is;
				nbt = e.save(new NBTTagCompound());
				if (e instanceof EntityHuman && !(is = ((EntityHuman) e).inventory.getItemInHand()).isEmpty()) {
					nbt.set("SelectedItem", is.save(new NBTTagCompound()));
				}
			} catch (Throwable t) {
				Main.logger.warning("Error while getting NBT for entity " + e.getBukkitEntity().getType().toString()
						+ " " + e.getCustomName());
			}
		}
		return nbt;
	}

	private boolean GPa(NBTBase b1, NBTBase b2, boolean lc) {
		if (b1 == b2 || b1 == null)
			return true;
		if (b2 == null || !b1.getClass().equals(b2.getClass()))
			return false;
		if (b1 instanceof NBTTagCompound) {
			NBTTagCompound nbt1 = (NBTTagCompound) b1;
			NBTTagCompound nbt2 = (NBTTagCompound) b2;
			for (String s : nbt1.getKeys()) {
				NBTBase b3 = nbt1.get(s);
				if (GPa(b3, nbt2.get(s), false)) {
					continue;
				}
				return false;
			}
			return true;
		}
		if (b1 instanceof NBTTagList) {
			NBTTagList nbtl1 = (NBTTagList) b1;
			NBTTagList nbtl2 = (NBTTagList) b2;
			if (nbtl1.isEmpty())
				return nbtl2.isEmpty();
			for (int j = 0; j < nbtl1.size(); j++) {
				NBTBase b4 = nbtl1.b(j);
				boolean bl2 = false;
				for (int k = 0; k < nbtl2.size(); k++) {
					if (!GPa(b4, nbtl2.b(k), false))
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
		return me.isSleeping() || me.isDeeplySleeping();
	}

	@Override
	public boolean playerIsRunning(Player p) {
		ServerPlayer me = ((CraftPlayer) p).getHandle();
		return me.isSprinting();
	}

	@Override
	public boolean playerIsCrouching(Player p) {
		ServerPlayer me = ((CraftPlayer) p).getHandle();
		return me.isSneaking();
	}

	@Override
	public boolean playerIsJumping(Player p) {
		ServerPlayer me = ((CraftPlayer) p).getHandle();
		return !me.isOnGround() && MathUtils.round(me.getMot().getY(), 5) != -0.00784;
	}

	@Override
	public void setDeath(Player p, boolean b) {
		ServerPlayer me = ((CraftPlayer) p).getHandle();
		me.dead = b;
	}

	@Override
	public float getIndicatorPercentage(Player p) {
		EntityHuman eh = ((CraftHumanEntity) p).getHandle();
		return eh.getAttackCooldown(0.0f);
	}

	@Override
	public float getItemCoolDown(Player p, int i1) {
		EntityHuman eh = ((CraftHumanEntity) p).getHandle();
		return eh.getCooldownTracker()
				.a(i1 == -1 ? eh.inventory.getItemInHand().getItem() : eh.inventory.getItem(i1).getItem(), 0.0f);
	}

	@Override
	public boolean setItemCooldown(Player p, int j1, int i1) {
		EntityHuman eh = ((CraftHumanEntity) p).getHandle();
		net.minecraft.world.item.Item i = i1 == -1 ? eh.inventory.getItemInHand().getItem()
				: eh.inventory.getItem(i1).getItem();
		if (eh.getCooldownTracker().cooldowns.containsKey(i)) {
			eh.getCooldownTracker().cooldowns.remove(i);
		}
		;
		eh.getCooldownTracker().setCooldown(i, j1);
		return true;
	}

	@Override
	public void moveto(LivingEntity entity) {
		// empty
	}

	@Override
	public void setWorldborder(Player p, int density, boolean play) {
		ServerPlayer ep = ((CraftPlayer) p).getHandle();
		WorldBorder border = ep.world.getWorldBorder();
		if (play) {
			border = new WorldBorder();
			border.world = ep.world.getWorldBorder().world;
			if (density == 0) {
				border.setCenter(0, 0);
				border.setSize(Integer.MAX_VALUE);
				border.setWarningDistance(Integer.MAX_VALUE);
			} else {
				border.setCenter(99999, 99999);
				border.setSize(1);
				border.setWarningDistance(1);
			}
		}
		// TODO packet play out world border
		ep.connection.send(new PacketPlayOutWorldBorder(border, EnumWorldBorderAction.INITIALIZE));
		border = null;
	}

	@Override
	public void setMNc(LivingEntity e1, String s1) {
		Mob ei = (Mob) ((CraftLivingEntity) e1).getHandle();
		switch (s1) {
		case "FLY":
			NMSUtils.setField("navigation", Mob.class, ei, new NavigationFlying(ei, ei.world));
			NMSUtils.setField("moveController", Mob.class, ei, new ControllerFly(ei));
			break;
		case "VEX":
			NMSUtils.setField("navigation", Mob.class, ei, new Navigation(ei, ei.world));
			NMSUtils.setField("moveController", Mob.class, ei, new ControllerVex(ei));
			break;
		case "WALK":
			NMSUtils.setField("navigation", Mob.class, ei, new Navigation(ei, ei.world));
			NMSUtils.setField("moveController", Mob.class, ei, new ControllerMove(ei));
			break;
		case "CLIMB":
			NMSUtils.setField("navigation", Mob.class, ei, new NavigationClimb(ei, ei.world));
			NMSUtils.setField("moveController", Mob.class, ei, new ControllerMove(ei));
			break;
		}
	}

	@Override
	public void forceBowDraw(LivingEntity e1, LivingEntity target, boolean bl1) {
		if (bl1)
			System.err.println("try to draw bow");
		Mob ei = (Mob) ((CraftLivingEntity) e1).getHandle();
		if (ei.isHandRaised()) {
			if (bl1)
				System.err.println("hand not raised!");
			ei.clearActiveItem();
		} else {
			if (bl1)
				System.err.println("hand is raised draws bow");
			ei.c(EnumHand.MAIN_HAND);
		}
	}

	@Override
	public void changeResPack(Player p, String url, String hash) {
		ServerPlayer player = ((CraftPlayer) p).getHandle();
		player.connection.send(new PacketPlayOutResourcePackSend(url, hash));
	}

	@Override
	public void forceSpectate(Player player, Entity entity) {
		this.forceSpectate(player, entity, false);
	}

	@Override
	public void playAnimationPacket(LivingEntity e, Integer[] ints) {
		net.minecraft.world.entity.LivingEntity living = (net.minecraft.world.entity.LivingEntity) ((CraftLivingEntity) e).getHandle();
		PacketPlayOutAnimation[] packets = new PacketPlayOutAnimation[ints.length];
		for (int j = 0; j < ints.length; j++) {
			packets[j] = new PacketPlayOutAnimation(living, ints[j]);
		}
		sendPlayerPacketsAsync(Utils.getPlayersInRange(e.getLocation(), Utils.renderLength), packets);
	}

	@Override
	public void playAnimationPacket(LivingEntity e, int id) {
		sendPlayerPacketsAsync(Utils.getPlayersInRange(e.getLocation(), Utils.renderLength),
				new PacketPlayOutAnimation[] {
						new PacketPlayOutAnimation((net.minecraft.world.entity.LivingEntity) ((CraftLivingEntity) e).getHandle(), id) });
	}

	@Override
	public boolean velocityChanged(Entity bukkit_entity) {
		net.minecraft.world.entity.Entity entity = ((CraftEntity) bukkit_entity).getHandle();
		return entity.world.b(entity.getBoundingBox().grow(0.001, 0.001, 0.001));
	}

	@Override
	public Vec3D getPredictedMotion(LivingEntity bukkit_source, LivingEntity bukkit_target, float delta) {
		net.minecraft.world.entity.LivingEntity target = ((CraftLivingEntity) bukkit_target).getHandle();
		net.minecraft.world.entity.LivingEntity source = ((CraftLivingEntity) bukkit_source).getHandle();

		double delta_x = target.locX() + (target.locX() - target.lastX) * delta - source.locX();
		double delta_y = target.locY() + (target.locY() - target.lastY) * delta + target.getHeadHeight() - 0.15f
				- source.locY() - source.getHeadHeight();
		double delta_z = target.locZ() + (target.locZ() - target.lastZ) * delta - source.locZ();

		return new Vec3D(delta_x, delta_y, delta_z);
	}

	@Override
	public void sendPlayerAdvancement(Player player, Material material, String title, String description, String task) {
		new FakeAdvancement(new com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.advancement.FakeDisplay(material, title, description, com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1.advancement.FakeDisplay.AdvancementFrame.valueOf(task), null))
				.displayToast(player);
	}

	@Override
	public boolean isReachable1(LivingEntity bukkit_entity, LivingEntity bukkit_target) {
		net.minecraft.world.entity.LivingEntity target = ((CraftLivingEntity) bukkit_target).getHandle();
		Mob entity = (Mob) ((CraftLivingEntity) bukkit_entity).getHandle();
		if (target == null)
			return true;
		PathEntity pe = entity.getNavigation().a(target, 16);
		if (pe == null) {
			return entity.isOnGround() != true;
		} else {
			PathPoint pp = pe.d();
			return pp != null;
		}
	}

	@Override
	public void addTravelPoint(Entity bukkit_entity, Vec3D vector) {
		this.addTravelPoint(bukkit_entity, vector, true);
	}

}
