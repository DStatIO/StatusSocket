package com.statussocket.data.player;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class PlayerDataBuilder
{

	private Client client;
	private PlayerDataPacket pdp;

	public PlayerDataBuilder(Client client)
	{
		this.client = client;
		this.pdp = null;
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
				pdp.equipment[index] = new EquipmentPacket(i, items[i]);
				index++;
			}
		}
	}

	private void loadSkills()
	{
		Skill[] skills = Skill.values();
		pdp.skills = new SkillPacket[skills.length];
		for (int i = 0; i < skills.length; i++)
		{
			if (skills[i] == Skill.OVERALL) continue;
			pdp.skills[i] = new SkillPacket();
			pdp.skills[i].skillName = skills[i].name();
			pdp.skills[i].experience = client.getSkillExperience(skills[i]);
			pdp.skills[i].boostedLevel = client.getBoostedSkillLevel(skills[i]);
			pdp.skills[i].realLevel = client.getRealSkillLevel(skills[i]);
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

	public PlayerDataPacket build()
	{
		if (pdp == null)
		{
			pdp = new PlayerDataPacket();

			pdp.playerName = client.getLocalPlayer().getName();
			pdp.runEnergy = client.getEnergy();
			pdp.specialAttack = client.getVar(VarPlayer.SPECIAL_ATTACK_PERCENT);

			loadInventory();
			loadEquipment();
			loadSkills();
			loadPrayers();
			loadLocation();

		}

		return pdp;
	}
}
