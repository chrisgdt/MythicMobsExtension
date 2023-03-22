package com.gmail.berndivader.mythicmobsext.volatilecode.v1_19_R3;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.gmail.berndivader.mythicmobsext.Main;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class PacketReader extends PacketReceivingHandler {

	Channel channel;

	private static String str_decoder = "BlablaPacketInjector";
	public static HashMap<UUID, PacketReader> readers;

	static {
		readers = new HashMap<>();
	}

	public PacketReader(Player player) {
		super(player);
	}

	public void inject() {
		//channel = entity_player.connection.getConnection().channel; // BEFORE 1.19.3
		// 1.19.3+ : Mojang removed getConnection and "connection" field is private. Paper modified the access to public
		// but not on spigot
		Class<? extends ServerGamePacketListenerImpl> connectionClass = entity_player.connection.getClass();
		try {
			Field connectionField = connectionClass.getField("h"); // see e.g. https://nms.screamingsandals.org/1.19.4/net/minecraft/server/network/ServerGamePacketListenerImpl.html
			connectionField.setAccessible(true);
			this.channel = ((Connection) connectionField.get(entity_player.connection)).channel;
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			Main.logger.warning("Error with an obfuscated name for 'connection' field !");
		}
		channel.pipeline().addAfter("decoder", str_decoder, new MessageToMessageDecoder<Packet<?>>() {

			@Override
			protected void decode(ChannelHandlerContext context, Packet<?> packet, List<Object> packets) {
				if (packet instanceof ServerboundSwingPacket) {
					packet = handle((ServerboundSwingPacket) packet);
				} else if (packet instanceof ServerboundMovePlayerPacket.Pos
						|| packet instanceof ServerboundMovePlayerPacket.PosRot
						|| packet instanceof ServerboundMovePlayerPacket.Rot) {
					packet = handle((ServerboundMovePlayerPacket) packet);
				} else if (packet instanceof ServerboundPlayerActionPacket) {
					packet = handle((ServerboundPlayerActionPacket) packet);
				} else if (packet instanceof ServerboundResourcePackPacket){
					packet = handle((ServerboundResourcePackPacket) packet);
				}
				packets.add(packet);
			}
		});
	}

	public void uninject() {
		if (channel.pipeline().get(str_decoder) != null) {
			channel.pipeline().remove(str_decoder);
		}
	}

}
