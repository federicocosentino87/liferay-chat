/**
 * Copyright 2000-present Liferay, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package liferay.chat.service.listener;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.messaging.MessageListenerException;
import com.liferay.portal.kernel.util.Validator;

import liferay.chat.jedis.pub.sub.JedisPublisher;
import liferay.chat.jedis.pub.sub.JedisPublisherManager;
import liferay.chat.model.ChatMessage;
import liferay.chat.service.endpoint.WebSocketEndpoint;

@Component (
	immediate = true,
	property = {"destination.name=/socket/gateway-publisher"},
	service = MessageListener.class
)
public class ChatSocketMessageListener implements MessageListener {

	@Override
	public void receive(Message message) throws MessageListenerException {
		ChatMessage chatMessage = JSONFactoryUtil.looseDeserialize(
			String.valueOf(message.getPayload()), ChatMessage.class);		
		_log.info(chatMessage.getMsgType());
		
		JedisPublisher jedisPublisher =
			jedisPublisherManager.getJedisPublisher(chatMessage.getMsgType());
		
		if(Validator.isNotNull(jedisPublisher)) {
			jedisPublisher.publish(chatMessage);
		}
		
	}
	
	@Reference 
	JedisPublisherManager jedisPublisherManager;

	private final static Log _log = 
		LogFactoryUtil.getLog(WebSocketEndpoint.class);
}