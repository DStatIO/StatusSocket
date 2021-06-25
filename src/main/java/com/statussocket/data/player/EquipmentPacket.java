package com.statussocket.data.player;

import net.runelite.api.Item;

public class EquipmentPacket
{

	public int index;
	public int id;
	public int amount;

	public EquipmentPacket()
	{
	}

	public EquipmentPacket(int index, Item item)
	{
		this.index = index;
		this.id = item.getId();
		this.amount = item.getQuantity();
	}
}
