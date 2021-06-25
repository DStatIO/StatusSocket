package com.statussocket;

import com.google.gson.Gson;
import com.statussocket.data.attack.AttackBuilder;
import com.statussocket.data.attack.AttackType;
import com.statussocket.data.hitsplat.HitsplatBuilder;
import com.statussocket.data.player.PlayerDataBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import okhttp3.*;

import java.io.IOException;

import static com.statussocket.StatusSocketEndpoints.LOG_ENDPOINT;

@Slf4j
@AllArgsConstructor
public class StatusSocketClient
{

	private Client client;
	private StatusSocketConfig config;
	private OkHttpClient okClient;

	public void sendAttack(String targetName, int targetId, AttackType style, int interactionId)
	{
		AttackBuilder builder = new AttackBuilder(client);
		builder.setTargetName(targetName != null ? targetName : "");
		builder.setTargetId(targetId);
		builder.setAttackType(style);
		builder.setInteractionId(interactionId);
		post(builder.build());
	}

	public void sendHitsplat(int damage, String targetName, int targetId)
	{
		HitsplatBuilder builder = new HitsplatBuilder(client);
		builder.setDamage(damage);
		builder.setTargetName(targetName != null ? targetName : "");
		builder.setTargetId(targetId);
		post(builder.build());
	}

	public void sendLog()
	{
		PlayerDataBuilder builder = new PlayerDataBuilder(client);
		post(builder.build());
	}

	private void post(Object obj)
	{
		Gson gson = new Gson();
		String json = gson.toJson(obj);

		HttpUrl url = HttpUrl.parse(config.endpoint() + LOG_ENDPOINT);
		MediaType mt = MediaType.parse("application/json; charset=utf-8");
		RequestBody body = RequestBody.create(mt, json);

		Request request = new Request.Builder().url(url).post(body).build();
		okClient.newCall(request).enqueue(new Callback()
		{
			@Override
			public void onFailure(Call call, IOException e)
			{
				log.warn("Failure");
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException
			{
				log.info("Code: {} - Response: {}", response.code(), response.body().string());
				response.close();
			}
		});
	}
}
