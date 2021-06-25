package com.statussocket.data.hitsplat;

import lombok.AccessLevel;
import lombok.Setter;
import net.runelite.api.Client;

public class HitsplatBuilder
{

	private Client client;
	private HitsplatPacket packet;

	@Setter(AccessLevel.PUBLIC)
	private int damage;

	@Setter(AccessLevel.PUBLIC)
	private String targetName;

	@Setter(AccessLevel.PUBLIC)
	private int targetId;

	public HitsplatBuilder(Client client)
	{
		this.client = client;
		this.packet = null;
	}

	public HitsplatPacket build()
	{
		if (packet == null)
		{
			packet = new HitsplatPacket();

			packet.playerName = client.getLocalPlayer().getName();
			packet.damage = damage;

			packet.targetName = targetName;
			packet.targetId = targetId;
		}

		return packet;
	}
}
