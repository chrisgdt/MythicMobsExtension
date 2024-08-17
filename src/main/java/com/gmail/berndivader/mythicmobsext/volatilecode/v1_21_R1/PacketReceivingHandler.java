package com.gmail.berndivader.mythicmobsext.volatilecode.v1_21_R1;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.gmail.berndivader.mythicmobsext.Main;
import com.gmail.berndivader.mythicmobsext.conditions.GetLastDamageIndicatorCondition;
import com.gmail.berndivader.mythicmobsext.utils.Utils;
import com.gmail.berndivader.mythicmobsext.volatilecode.Volatile;

public class PacketReceivingHandler implements IPacketReceivingHandler {

	Player player;
	ServerPlayer entity_player;

	public PacketReceivingHandler(Player player) {
		this.player = player;
		entity_player = ((CraftPlayer) this.player).getHandle();
	}

	@Override
	public Packet<?> handle(ServerboundSwingPacket packet) {
		float f1 = Volatile.handler.getIndicatorPercentage(player);
		player.setMetadata(GetLastDamageIndicatorCondition.meta_LASTDAMAGEINDICATOR,
				new FixedMetadataValue(Main.getPlugin(), f1));
		return packet;
	}

	@Override
	public Packet<?> handle(ServerboundResourcePackPacket packet) {
		player.setMetadata(Utils.meta_RESOURCEPACKSTATUS,
				new FixedMetadataValue(Main.getPlugin(), packet.action().name()));
		return packet;
	}

	@Override
	public Packet<?> handle(ServerboundMovePlayerPacket.Pos packet) {
		// TODO Auto-generated method stub
		return packet;
	}

	@Override
	public Packet<?> handle(ServerboundMovePlayerPacket packet) {
		com.gmail.berndivader.mythicmobsext.utils.Vec3D v3 = new com.gmail.berndivader.mythicmobsext.utils.Vec3D(
				entity_player.getX(), entity_player.getY(), entity_player.getZ());
		double dx = packet.getX(entity_player.getX()),
			   dy = packet.getY(entity_player.getY()),
			   dz = packet.getZ(entity_player.getZ());
		v3 = (v3.getX() != dx || v3.getY() != dy || v3.getZ() != dz)
				? v3.length(new com.gmail.berndivader.mythicmobsext.utils.Vec3D(dx, dy, dz))
				: new com.gmail.berndivader.mythicmobsext.utils.Vec3D(0, 0, 0);
		Utils.players.put(player.getUniqueId(), v3);
		return packet;
	}

	@Override
	public Packet<?> handle(ServerboundPlayerInputPacket packet) {
		// TODO Auto-generated method stub
		return packet;
	}

	@Override
	public Packet<?> handle(ServerboundPlayerActionPacket packet) {
		player.setMetadata(Utils.meta_MMEDIGGING, new FixedMetadataValue(Main.getPlugin(), packet.getAction().name()));
		return packet;
	}

}
