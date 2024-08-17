package com.gmail.berndivader.mythicmobsext.volatilecode.v1_21_R1;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.*;

public interface IPacketReceivingHandler {
	Packet<?> handle(ServerboundSwingPacket packet);

	Packet<?> handle(ServerboundResourcePackPacket packet);

	Packet<?> handle(ServerboundMovePlayerPacket.Pos packet);

	Packet<?> handle(ServerboundMovePlayerPacket packet);

	Packet<?> handle(ServerboundPlayerInputPacket packet);

	Packet<?> handle(ServerboundPlayerActionPacket packet);
}
