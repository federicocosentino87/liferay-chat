package liferay.chat.jedis.pub.sub.user.search.client;

import java.util.LinkedHashMap;
import java.util.List;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import liferay.chat.model.UserPojo;
import liferay.chat.session.UserSessionRegistryUtil;
import redis.clients.jedis.JedisPubSub;

public class UserSearchJedisSubscriberClient extends JedisPubSub {
	
	@Override
	public void onMessage(String channel, String message) {
		System.out.println(
				"Channel " + channel + " has sent a message : " + message);
		JSONObject messageJSON =JSONFactoryUtil.createJSONObject();
		try {
			messageJSON = JSONFactoryUtil.createJSONObject(message);
		
		
		User user = UserLocalServiceUtil.getUser(messageJSON.getLong("userId"));
		
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		
		OrderByComparator<User> comparator = 
			OrderByComparatorFactoryUtil.create("User", "firstName", true);
		
		List<User> users = UserLocalServiceUtil.search(
			user.getCompanyId(), messageJSON.getString("data"), 
			WorkflowConstants.STATUS_ANY, map, QueryUtil.ALL_POS, 
			QueryUtil.ALL_POS, comparator);
		
		JSONArray jsonUsers = JSONFactoryUtil.createJSONArray();
		
		for (User u:users) {
			jsonUsers.put(
				new UserPojo(
					u.getUserId(), u.getFullName(),
					UserConstants.getPortraitURL(
						PortalUtil.getPathImage(), u.isMale(), 
						u.getPortraitId(), u.getUserUuid()))
					.toJSON());
		}
		
		messageJSON = JSONFactoryUtil.createJSONObject();
		
		messageJSON.put("users", jsonUsers);
		messageJSON.put("msgType", "USER_SEARCH");
		
		UserSessionRegistryUtil.notifyUser(messageJSON, user.getUserId());
		} catch (PortalException e) {
			_log.error(e.getMessage());
		}
		
		
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
	
	private final static Log _log = 
		LogFactoryUtil.getLog(UserSearchJedisSubscriberClient.class);
}
