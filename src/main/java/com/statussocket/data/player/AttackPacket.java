package com.statussocket.data.player;

import com.statussocket.models.AnimationData;
import lombok.AccessLevel;
import lombok.Setter;

public class AttackPacket
{

	public String playerName;

	// If player hit another player, targetName = target player's in-game name. Else "".
	public String targetName;

	// The animation data about the attack the player used. See @AnimationData
	public String animationName;
	public int animationId;
	public boolean animationIsSpecial;
	public String animationAttackStyle;
	public int animationBaseSpellDmg;
}
