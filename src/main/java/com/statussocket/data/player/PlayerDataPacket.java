package com.statussocket.data.player;

public class PlayerDataPacket
{

	public String playerName;
	public int runEnergy;
	public int specialAttack;

	public EquipmentPacket[] equipment;
	public InventoryPacket[] inventory;

	public SkillPacket[] skills;
	public String[] prayers;

	public LocalPointPacket localPoint;
	public WorldPointPacket worldPoint;
	public CameraPacket camera;

}
