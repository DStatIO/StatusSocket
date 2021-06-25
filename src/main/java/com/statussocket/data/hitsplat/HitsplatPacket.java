package com.statussocket.data.hitsplat;

public class HitsplatPacket
{

	public String playerName;

	public int damage;

	// If player hits an entity, targetName = target entity's in-game name. Else "".
	public String targetName;

	// If player hits an entity, targetId = NPC's in-game id. Else -1 (hits player).
	public int targetId;

}
