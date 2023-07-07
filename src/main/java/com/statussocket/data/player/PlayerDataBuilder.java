package com.statussocket.data.player;

import com.statussocket.data.PacketTypes;
import com.statussocket.models.AnimationData;
import com.statussocket.models.EquipmentData;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.http.api.item.ItemEquipmentStats;
import net.runelite.http.api.item.ItemStats;

public class PlayerDataBuilder
{
	private static Skill[] COMBAT_SKILLS = { Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.RANGED, Skill.MAGIC, Skill.HITPOINTS, Skill.PRAYER };
	private static String UNKNOWN_ANIMATION_STR = "N/A";

	private Client client;
	private ItemManager itemManager;

	private String targetName;
	private boolean isAttacking;
	private String packetType;
	
	private PlayerDataPacket pdp;

	// used for inventory update logs (no target or attacks)
	public PlayerDataBuilder(Client client, ItemManager itemManager)
	{
		this.client = client;
		this.itemManager = itemManager;
		this.pdp = null;
		this.packetType = PacketTypes.inventory.name();
	}

	// used for combat logs (attacker or defender)
	public PlayerDataBuilder(Client client, ItemManager itemManager, String targetName, boolean isAttacking)
	{
		this.client = client;
		this.itemManager = itemManager;
		this.targetName = targetName;
		this.isAttacking = isAttacking;
		this.pdp = null;
		this.packetType = isAttacking ? PacketTypes.attacking.name() : PacketTypes.defending.name();
	}

	private void loadInventory()
	{
		ItemContainer ic = client.getItemContainer(InventoryID.INVENTORY);
		if (ic == null)
		{
			pdp.inventory = new InventoryPacket[0];
			return;
		}

		Item[] items = ic.getItems();
		if (items == null || items.length == 0)
		{
			pdp.inventory = new InventoryPacket[0];
			return;
		}

		int validItems = 0;
		for (int i = 0; i < items.length; i++)
		{
			if (items[i] != null && items[i].getId() > 0)
			{
				validItems++;
			}
		}

		pdp.inventory = new InventoryPacket[validItems];
		int index = 0;

		for (int i = 0; i < items.length; i++)
		{
			if (items[i] != null && items[i].getId() > 0)
			{
				pdp.inventory[index] = new InventoryPacket(i, items[i]);
				index++;
			}
		}
	}

	private void loadEquipment()
	{
		ItemContainer ic = client.getItemContainer(InventoryID.EQUIPMENT);
		if (ic == null)
		{
			pdp.equipment = new EquipmentPacket[0];
			return;
		}

		Item[] items = ic.getItems();
		if (items == null || items.length == 0)
		{
			pdp.equipment = new EquipmentPacket[0];
			return;
		}

		int validItems = 0;
		for (int i = 0; i < items.length; i++)
		{
			if (items[i] != null && items[i].getId() > 0)
			{
				validItems++;
			}
		}

		pdp.equipment = new EquipmentPacket[validItems];
		int index = 0;

		for (int i = 0; i < items.length; i++)
		{
			if (items[i] != null && items[i].getId() > 0)
			{
				// First, attempt to see if the item has a match in EquipmentData, to use a "common" name for some items
				// with duplicates. If it doesn't have a match, just use the actual item's name.
				EquipmentData itemData = EquipmentData.fromId(items[i].getId());
				String itemName = itemData != null ? itemData.name() : itemManager.getItemComposition(items[i].getId()).getName();

				pdp.equipment[index] = new EquipmentPacket(i, items[i], itemName);
				index++;
			}
		}
	}

	// this has to be called AFTER loadEquipment, so we can access pdp.equipment
	private void loadEquipmentStats()
	{
		if (pdp.equipment.length < 1)
		{
			pdp.equipmentStats = new EquipmentStatsPacket();
			return;
		}

		ItemEquipmentStats[] itemEquipmentStats = new ItemEquipmentStats[pdp.equipment.length];
		for (int i = 0; i < pdp.equipment.length; i++)
		{
			// first, directly lookup the equipment's itemId for stats. This could fail if it is a weird "copy" of an item,
			// such as an LMS version or a charged version.
			int itemId = pdp.equipment[i].id;
			ItemStats itemStats = itemManager.getItemStats(itemId, false);

			// if that failed, then use EquipmentData to potentially get the "real" item's match.
			if (itemStats == null)
			{
				EquipmentData itemData = EquipmentData.fromId(itemId);
				if (itemData != null)
				{
					itemId = itemData.getItemId();
					itemStats = itemManager.getItemStats(itemId, false);
				}
			}

			if (itemStats != null)
			{
				itemEquipmentStats[i] = itemStats.getEquipment();
			}
			else
			{
				itemEquipmentStats[i] = null; // NULLs will have to be skipped in EquipmentStatsPacket in case stats aren't found.
			}

		}

		pdp.equipmentStats = new EquipmentStatsPacket(itemEquipmentStats);
	}

	private void loadSkills()
	{
		pdp.skills = new SkillPacket[COMBAT_SKILLS.length];
		for (int i = 0; i < COMBAT_SKILLS.length; i++)
		{
			pdp.skills[i] = new SkillPacket();
			pdp.skills[i].skillName = COMBAT_SKILLS[i].name();
			pdp.skills[i].experience = client.getSkillExperience(COMBAT_SKILLS[i]);
			pdp.skills[i].boostedLevel = client.getBoostedSkillLevel(COMBAT_SKILLS[i]);
			pdp.skills[i].realLevel = client.getRealSkillLevel(COMBAT_SKILLS[i]);
		}
	}

	private void loadPrayers()
	{
		int count = 0;
		for (Prayer prayer : Prayer.values())
		{
			if (client.isPrayerActive(prayer))
			{
				count++;
			}
		}

		pdp.prayers = new String[count];
		int index = 0;
		for (Prayer prayer : Prayer.values())
		{
			if (client.isPrayerActive(prayer))
			{
				pdp.prayers[index] = prayer.name();
				index++;
			}
		}
	}

	private void loadLocation()
	{
		Player p = client.getLocalPlayer();

		LocalPoint ll = p.getLocalLocation();
		pdp.localPoint = new LocalPointPacket();
		pdp.localPoint.x = ll.getX();
		pdp.localPoint.y = ll.getY();
		pdp.localPoint.sceneX = ll.getSceneX();
		pdp.localPoint.sceneY = ll.getSceneY();

		WorldPoint wl = p.getWorldLocation();
		pdp.worldPoint = new WorldPointPacket();
		pdp.worldPoint.x = wl.getX();
		pdp.worldPoint.y = wl.getY();
		pdp.worldPoint.plane = wl.getPlane();
		pdp.worldPoint.regionID = wl.getRegionID();
		pdp.worldPoint.regionX = wl.getRegionX();
		pdp.worldPoint.regionY = wl.getRegionY();

		pdp.camera = new CameraPacket();
		pdp.camera.yaw = client.getCameraYaw();
		pdp.camera.pitch = client.getCameraPitch();

		pdp.camera.x = client.getCameraX();
		pdp.camera.y = client.getCameraY();
		pdp.camera.z = client.getCameraZ();

		pdp.camera.x2 = client.getCameraX2();
		pdp.camera.y2 = client.getCameraY2();
		pdp.camera.z2 = client.getCameraZ2();

	}

	private void loadAttack()
	{
		pdp.attack = new AttackPacket();

		pdp.attack.targetName = targetName;
		pdp.attack.isAttacking = this.isAttacking;

		pdp.attack.animationId = client.getLocalPlayer().getAnimation();
		AnimationData animationData = AnimationData.fromId(client.getLocalPlayer().getAnimation());
		if (animationData == null)
		{
			pdp.attack.animationName = UNKNOWN_ANIMATION_STR;
			return;
		}
		pdp.attack.animationName = animationData.name();
		pdp.attack.animationIsSpecial = animationData.isSpecial();
		pdp.attack.animationAttackStyle = animationData.getAttackStyle().name();
		pdp.attack.animationBaseSpellDmg = animationData.getBaseSpellDamage();
	}

	public PlayerDataPacket build()
	{
		if (pdp == null)
		{
			pdp = new PlayerDataPacket();

			pdp.tick = client.getTickCount();
			pdp.packetType = packetType != null ? packetType : PacketTypes.unknown.name();
			pdp.playerName = client.getLocalPlayer().getName();
			pdp.runEnergy = client.getEnergy();
			pdp.specialAttack = client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT);

			loadInventory();
			loadEquipment();
			loadEquipmentStats();
			loadSkills();
			loadPrayers();
			loadLocation();
			loadAttack();
		}

		return pdp;
	}
}
