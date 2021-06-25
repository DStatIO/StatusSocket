package com.statussocket;

import com.google.inject.Provides;
import com.statussocket.data.attack.AttackType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.OkHttpClient;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

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

	private OkHttpClient okClient = new OkHttpClient();

	private StatusSocketClient slc;

	private final List<Integer> MELEE_ATTACKS = Arrays.asList(376, 381, 386, 390, 393, 393, 395, 400,
		401, 406, 407, 414, 419, 422, 423, 428, 429, 440, 1058, 1060, 1062, 1378, 1658, 1665, 1667,
		2066, 2067, 2078, 2661, 3297, 3298, 3852, 5865, 7004, 7045, 7054, 7514, 7515, 7516, 7638,
		7639, 7640, 7641, 7642, 7643, 7644, 7645, 8056, 8145);

	private final List<Integer> RANGE_ATTACKS = Arrays.asList(426, 929, 1074, 4230, 5061, 6600, 7218,
		7521, 7552, 7555, 7617, 8194, 8195, 8292);

	private final List<Integer> MAGE_ATTACKS = Arrays.asList(710, 711, 1161, 1162, 1167, 7855, 1978,
		1979, 8532);

	@Provides
	StatusSocketConfig provideConfig(final ConfigManager configManager)
	{
		return configManager.getConfig(StatusSocketConfig.class);
	}

	@Override
	protected void startUp()
	{
		slc = new StatusSocketClient(client, config, okClient);
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
	public void onGameTick(GameTick event)
	{
		// Update the status every game tick.
		slc.sendLog();
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

		int targetId = (target instanceof NPC) ? ((NPC) target).getId() : -1;

		if (MELEE_ATTACKS.contains(animationId))
		{
			slc.sendAttack(target.getName(), targetId, AttackType.MELEE, animationId);
		}

		if (RANGE_ATTACKS.contains(event.getActor().getAnimation()))
		{
			slc.sendAttack(target.getName(), targetId, AttackType.RANGED, animationId);
		}

		if (MAGE_ATTACKS.contains(event.getActor().getAnimation()))
		{
			slc.sendAttack(target.getName(), targetId, AttackType.MAGIC, animationId);
		}
	}
}
