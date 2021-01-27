package liferay.chat.jedis.pub.sub;

import liferay.chat.model.ChatMessage;

public interface JedisPublisher {

	public void publish(ChatMessage chatMessage);
	
	public String getTypeName();
}
