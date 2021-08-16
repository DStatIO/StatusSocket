package com.statussocket;

import com.google.inject.Provides;
import com.statussocket.models.AnimationData;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ActorDeath;
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
	private int lastTickAttacked; // last tick the client player attacked

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
			slc.sendInventoryChangeLog();
		}
	}

	// send hitsplat packet when main Player does damage to another Player
	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		Player player = client.getLocalPlayer();
		Actor actor = event.getActor();
		Hitsplat hitsplat = event.getHitsplat();

		if (player == null || actor == null || hitsplat == null || !hitsplat.isMine() || Objects.equals(actor.getName(), player.getName()))
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
		// delay animation processing, since we will also want to use equipment data for deserved
		// damage, and equipment updates are loaded shortly after the animation updates.
		// without the invokeLater, equipped gear would be 1 tick behind the animation.
		clientThread.invokeLater(() ->
		{
			// Player does an animation targeting another player, or gets targeted by a player.
			Player player = client.getLocalPlayer();
			Actor actor = event.getActor();

			if (player == null || !(actor instanceof Player))
			{
				return;
			}

			// if the event actor is the player, then we're attacking.
			// otherwise, the player is being attacked. so the target attacker is the event actor
			boolean isAttacking = Objects.equals(actor.getName(), player.getName());

			Actor target = actor.getInteracting();
			// make sure that the player is one of the people involved in the interaction
			// (either attacking or being attacked)
			// I forget why exactly use names, but it behaves more consistently than comparing the whole player object.
			if (!(target instanceof Player) ||
				(!isAttacking && !Objects.equals(target.getName(), player.getName()))) // basically: if !isAttacking & !isBeingAttacked
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


			String targetName = isAttacking ? target.getName() : actor.getName();

			// if we are somehow sending more than 1 attack per tick, it has to be invalid so ignore it.
			if (isAttacking && lastTickAttacked == client.getTickCount())
			{
				return;
			}

			// send combat log which will include attack/animation data
			slc.sendCombatLog(targetName, isAttacking);
			if (isAttacking)
			{
				lastTickAttacked = client.getTickCount();
			}
		});
	}

	// detect when any Player dies
	@Subscribe
	public void onActorDeath(ActorDeath event)
	{
		// don't really need player here, but if it's null then something wrong
		Player player = client.getLocalPlayer();
		Actor actor = event.getActor();

		// only check Player deaths
		if (player == null || !(actor instanceof Player))
		{
			return;
		}

		slc.sendDeath(actor.getName());
	}
}
