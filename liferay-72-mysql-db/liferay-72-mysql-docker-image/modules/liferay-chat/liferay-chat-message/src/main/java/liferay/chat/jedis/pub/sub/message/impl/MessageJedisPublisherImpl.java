package liferay.chat.jedis.pub.sub.message.impl;

import org.osgi.service.component.annotations.Component;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import liferay.chat.jedis.pub.sub.JedisPublisher;
import liferay.chat.jedis.pub.sub.base.BaseJedisPublisher;
import liferay.chat.model.ChatMessage;

@Component(
	immediate = true,
	service = JedisPublisher.class
)
public class MessageJedisPublisherImpl extends BaseJedisPublisher{
	
	public MessageJedisPublisherImpl() {
		setType(TYPE);
	}

	@Override
	public void publish(ChatMessage chatMessage) {
		_log.info(chatMessage);
		
		JSONSerializer serializer = JSONFactoryUtil.createJSONSerializer();
		
		String jsonMessage = serializer.serialize(chatMessage);
		
		getJedis().publish("/chat", jsonMessage);
	}

	public static final String TYPE = "NEW_MESSAGE";
	
	private final static Log _log = 
		LogFactoryUtil.getLog(MessageJedisPublisherImpl.class);
}
