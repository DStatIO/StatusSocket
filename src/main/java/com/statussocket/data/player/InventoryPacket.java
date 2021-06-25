package com.statussocket.data.player;

import net.runelite.api.Item;

public class InventoryPacket
{

	public int index;
	public int id;
	public int amount;

	public InventoryPacket()
	{
	}

	public InventoryPacket(int index, Item item)
	{
		this.index = index;
		this.id = item.getId();
		this.amount = item.getQuantity();
	}
}
