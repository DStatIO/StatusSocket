package com.statussocket.data.player;

import net.runelite.http.api.item.ItemEquipmentStats;

// packet which includes total equipment stats for all worn gear,
// as well as
public class EquipmentStatsPacket
{
	public int aStab; // attack stab bonus
	public int aSlash; // attack slash bonus (...)
	public int aCrush;
	public int aMagic;
	public int aRange;
	public int dStab; // defence stab bonus
	public int dSlash; // defence slash bonus (...)
	public int dCrush;
	public int dMagic;
	public int dRange;
	public int str; // melee str
	public int rStr; // range str
	public int mDmg; // mage dmg%

	public EquipmentStatsPacket()
	{
	}

	// ints default to 0 so we don't need to initialize, just add stats.
	public EquipmentStatsPacket(ItemEquipmentStats[] equipmentStats)
	{
		for (ItemEquipmentStats equipmentStat : equipmentStats)
		{
			if (equipmentStat == null) { continue; } // skip nulls if stats weren't found for some items.

			this.aStab += equipmentStat.getAstab();		// 0 // these indexes may be useful
			this.aSlash += equipmentStat.getAslash();	// 1
			this.aCrush += equipmentStat.getAcrush();	// 2
			this.aMagic += equipmentStat.getAmagic();	// 3
			this.aRange += equipmentStat.getArange();	// 4
			this.dStab += equipmentStat.getDstab();		// 5
			this.dSlash += equipmentStat.getDslash();	// 6
			this.dCrush += equipmentStat.getDcrush();	// 7
			this.dMagic += equipmentStat.getDmagic();	// 8
			this.dRange += equipmentStat.getDrange();	// 9
			this.str += equipmentStat.getStr();			// 10
			this.rStr += equipmentStat.getRstr();		// 11
			this.mDmg += equipmentStat.getMdmg();		// 12
		}
	}
}
