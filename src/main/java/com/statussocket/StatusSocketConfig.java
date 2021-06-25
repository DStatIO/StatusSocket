package com.statussocket;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("statussocket")
public interface StatusSocketConfig extends Config
{

	@ConfigSection(
		name = "Server",
		description = "Config the server you wish to connect to.",
		position = 1
	)
	String title = "serverTitle";

	@ConfigItem(
		name = "Endpoint",
		description = "The endpoint of the server's API.",
		position = 2,
		keyName = "endpoint",
		section = "serverTitle"
	)
	default String endpoint()
	{
		return "http://localhost/";
	}

}
