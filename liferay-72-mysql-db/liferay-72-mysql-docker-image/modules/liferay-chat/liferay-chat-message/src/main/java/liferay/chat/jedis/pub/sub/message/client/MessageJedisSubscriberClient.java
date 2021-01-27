package liferay.chat.jedis.pub.sub.message.client;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;

import liferay.chat.session.UserSessionRegistryUtil;
import redis.clients.jedis.JedisPubSub;

public class MessageJedisSubscriberClient extends JedisPubSub {
	
	@Override
	public void onMessage(String channel, String message) {
		System.out.println(
				"Channel " + channel + " has sent a message : " + message);
		
		List<User> users = 
			UserLocalServiceUtil.getUsers(QueryUtil.ALL_POS, QueryUtil.ALL_POS);
		
		Collection<Long> userIds = 
			users.stream().map(User::getUserId).collect(Collectors.toList());
		
		JSONObject messageJSON =JSONFactoryUtil.createJSONObject();
		try {
			messageJSON = JSONFactoryUtil.createJSONObject(message);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UserSessionRegistryUtil.notifyUsers(messageJSON, userIds);
	}

	@Override
	public void onSubscribe(String channel, int subscribedChannels) {
		System.out.println("Client is Subscribed to channel : " + channel);
		System.out.println("Client is Subscribed to " + subscribedChannels
				+ " no. of channels");
	}

	@Override
	public void onUnsubscribe(String channel, int subscribedChannels) {
		System.out.println("Client is Unsubscribed from channel : " + channel);
		System.out.println("Client is Subscribed to " + subscribedChannels
				+ " no. of channels");
	}
}
