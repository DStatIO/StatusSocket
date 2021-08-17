package com.statussocket.data.death;

import lombok.AccessLevel;
import lombok.Setter;
import net.runelite.api.Client;

public class DeathBuilder
{

	private Client client;
	private DeathPacket packet;

	@Setter(AccessLevel.PUBLIC)
	private String targetName;

	public DeathBuilder(Client client)
	{
		this.client = client;
		this.packet = null;
	}

	public DeathPacket build()
	{
		if (packet == null)
		{
			packet = new DeathPacket();

			packet.playerName = client.getLocalPlayer().getName();
			packet.targetName = targetName;
			packet.tick = client.getTickCount();
		}

		return packet;
	}
}
