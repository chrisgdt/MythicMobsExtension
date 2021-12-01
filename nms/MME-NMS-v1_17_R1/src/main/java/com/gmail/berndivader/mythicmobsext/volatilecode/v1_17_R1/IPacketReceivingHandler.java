package com.gmail.berndivader.mythicmobsext.volatilecode.v1_17_R1;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;

public interface IPacketReceivingHandler {
	Packet<?> handle(PacketPlayInArmAnimation packet);

	Packet<?> handle(PacketPlayInResourcePackStatus packet);

	Packet<?> handle(PacketPlayInFlying.PacketPlayInPosition packet);

	Packet<?> handle(PacketPlayInFlying packet);

	Packet<?> handle(PacketPlayInSteerVehicle packet);

	Packet<?> handle(PacketPlayInBlockDig packet);
}
