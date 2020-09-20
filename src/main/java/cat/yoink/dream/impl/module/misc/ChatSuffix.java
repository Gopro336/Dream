package cat.yoink.dream.impl.module.misc;

import cat.yoink.dream.api.module.Category;
import cat.yoink.dream.api.module.Module;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

public class ChatSuffix extends Module
{
	public ChatSuffix(String name, String description, Category category)
	{
		super(name, description, category);
	}

	@SubscribeEvent
	public void onChat(ClientChatEvent event)
	{
		for (String s : Arrays.asList("/", ".", "-", ",", ":", ";", "'", "\"", "+", "\\"))
		{
			if (event.getMessage().startsWith(s)) return;
		}

		event.setMessage(event.getMessage() + " \uFF5C \u1D05\u0280\u1D07\u1D00\u1D0D");
	}
}
