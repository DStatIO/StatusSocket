package com.statussocket.data.player;

import com.statussocket.models.AnimationData;
import lombok.AccessLevel;
import lombok.Setter;

public class AttackPacket
{

	public String playerName;

	// If player hit another player, targetName = target player's in-game name. Else "".
	public String targetName;

	// If player hit an entity, targetId = NPC's in-game id. Else -1.
	public int targetId;

	// The animation data about the attack the player used. See @AnimationData
	// intent is to send all its properties to the server, not just the enum name.
	public AnimationData animationData;
	//public String attackType;

	// The animation id the player used to launch the attack. - this is now included within animationData
	//public int interactionId;

}
