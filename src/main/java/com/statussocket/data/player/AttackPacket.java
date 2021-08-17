package com.statussocket.data.player;

import com.statussocket.models.AnimationData;
import lombok.AccessLevel;
import lombok.Setter;

public class AttackPacket
{
	// If player hit another player, targetName = target player's in-game name.
	public String targetName;

	public boolean isAttacking;

	// The animation data about the attack the player used. See @AnimationData
	public String animationName;
	public int animationId;
	public boolean animationIsSpecial;
	public String animationAttackStyle;
	public int animationBaseSpellDmg;
}
