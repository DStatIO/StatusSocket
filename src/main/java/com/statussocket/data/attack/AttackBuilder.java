package com.statussocket.data.attack;

import lombok.AccessLevel;
import lombok.Setter;
import net.runelite.api.Client;

public class AttackBuilder
{

	private Client client;
	private AttackPacket packet;

	@Setter(AccessLevel.PUBLIC)
	private String targetName = "";

	@Setter(AccessLevel.PUBLIC)
	private int targetId = -1;

	@Setter(AccessLevel.PUBLIC)
	private AttackType attackType;

	@Setter(AccessLevel.PUBLIC)
	private int interactionId;

	public AttackBuilder(Client client)
	{
		this.client = client;
		this.packet = null;
	}

	public AttackPacket build()
	{
		if (packet == null)
		{
			packet = new AttackPacket();

			packet.playerName = client.getLocalPlayer().getName();

			packet.targetName = targetName;
			packet.targetId = targetId;

			packet.attackType = attackType.name();
			packet.interactionId = interactionId;
		}

		return packet;
	}
}
