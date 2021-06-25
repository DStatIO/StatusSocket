package com.statussocket.data.attack;

import lombok.AccessLevel;
import lombok.Setter;

public class AttackPacket
{

	public String playerName;

	// If player hit another player, targetName = target player's in-game name. Else "".
	public String targetName;

	// If player hit an entity, targetId = NPC's in-game id. Else -1.
	public int targetId;

	// The type of attack the player used. See @AttackType
	public String attackType;

	// The animation id the player used to launch the attack.
	public int interactionId;

}
