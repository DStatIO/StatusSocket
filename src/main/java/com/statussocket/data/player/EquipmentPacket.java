package com.statussocket.data.player;

import net.runelite.api.Item;

public class EquipmentPacket
{

	public int index;
	public int id;
	public int amount;
	// This is not always the "direct"/real item name, this is a "common" name so that
	// items with duplicates can be referred to the same way. For example, Armadyl Godsword (or)
	// and Armadyl Godsword will both be called ARMADYL_GODSWORD (see EquipmentData)
	public String name;

	public EquipmentPacket()
	{
	}

	public EquipmentPacket(int index, Item item, String name)
	{
		this.index = index;
		this.id = item.getId();
		this.amount = item.getQuantity();
		this.name = name != null ? name : "N/A";
	}
}
