package com.statussocket.data.death;

import com.google.gson.annotations.Expose;
import com.statussocket.data.PacketTypes;

public class DeathPacket
{
	public String packetType = PacketTypes.death.name();

	public String playerName; // Name of the client player.
	public String targetName; // Name of the player who died.
	public int tick;

}
