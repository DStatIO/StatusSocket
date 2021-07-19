package com.statussocket;

import com.google.inject.Provides;
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
			slc.sendLog(null, -2);
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
			slc.sendHitsplat(hitsplat.getAmount(), target.getName(), -1);
		}
		else if (actor instanceof NPC)
		{
			NPC target = (NPC) actor;
			slc.sendHitsplat(hitsplat.getAmount(), target.getName(), target.getId());
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		// Player does an animation targetting another entity.
		Player player = client.getLocalPlayer();
		Actor actor = event.getActor();

		if (player == null || actor == null || player != actor)
		{
			return;
		}

		Actor target = player.getInteracting();
		if (target == null)
		{
			return;
		}

		int animationId = player.getAnimation();
		if (animationId == -1)
		{
			return;
		}

		// delay animation processing/sending, since we will also want to use equipment data for deserved
		// damage, and equipment updates are loaded shortly after the animation updates.
		// without the invokeLater, equipped gear would be 1 tick behind the animation.
		clientThread.invokeLater(() ->
		{
			int targetId = (target instanceof NPC) ? ((NPC) target).getId() : -1;

			// send full log including attack/animation data
			slc.sendLog(target.getName(), targetId);
		});
	}
}
