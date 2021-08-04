package com.statussocket;

import com.google.inject.Provides;
import com.statussocket.models.AnimationData;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.OkHttpClient;
import javax.inject.Inject;

@Slf4j
@PluginDescriptor(
	name = "Status Socket",
	description = "Actively logs the player status to a remote server.",
	tags = {"status", "socket"},
	enabledByDefault = false
)
public class StatusSocketPlugin extends Plugin
{

	@Inject
	@Getter(AccessLevel.PUBLIC)
	private Client client;

	@Inject
	@Getter(AccessLevel.PUBLIC)
	private EventBus eventBus;

	@Inject
	private StatusSocketConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	private OkHttpClient okClient = new OkHttpClient();

	private StatusSocketClient slc;

	@Provides
	StatusSocketConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(StatusSocketConfig.class);
	}

	@Override
	protected void startUp()
	{
		slc = new StatusSocketClient(client, itemManager, config, okClient);
	}


	@Override
	protected void shutDown()
	{

	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		// Update the status when the player changes their inventory.
		ItemContainer container = event.getItemContainer();
		if (container == client.getItemContainer(InventoryID.INVENTORY) ||
			container == client.getItemContainer(InventoryID.EQUIPMENT))
		{
			slc.sendLog();
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		// Player does damage to another entity (either NPC or player).
		Player player = client.getLocalPlayer();
		Actor actor = event.getActor();
		Hitsplat hitsplat = event.getHitsplat();

		if (player == null || actor == null || hitsplat == null || !hitsplat.isMine() || player == actor)
		{
			return;
		}

		if (actor instanceof Player)
		{
			Player target = (Player) actor;
			slc.sendHitsplat(hitsplat.getAmount(), target.getName());
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		// Player does an animation targeting another player, or gets targeted by a player.
		Player player = client.getLocalPlayer();
		Actor actor = event.getActor();

		if (player == null || !(actor instanceof Player))
		{
			return;
		}

		Actor target = actor.getInteracting();
		if (!(target instanceof Player))
		{
			return;
		}

		int animationId = actor.getAnimation();
		if (animationId == -1)
		{
			return;
		}

		AnimationData animationData = AnimationData.fromId(animationId);
		if (animationData == null) // disregard non-combat or unknown animations
		{
			return;
		}

		// the target name could be = to the player name, this would mean the main player is being attacked
		String targetName = target.getName();
		// delay animation processing, since we will also want to use equipment data for deserved
		// damage, and equipment updates are loaded shortly after the animation updates.
		// without the invokeLater, equipped gear would be 1 tick behind the animation.
		clientThread.invokeLater(() ->
		{
			// send full log including attack/animation data
			slc.sendLog(targetName);
		});
	}
}
