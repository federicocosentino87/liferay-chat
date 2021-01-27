package liferay.chat.session;

import java.util.Collection;
import java.util.Collections;

import javax.websocket.Session;

import org.osgi.util.tracker.ServiceTracker;

import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.json.JSONObject;

import liferay.chat.model.UserSession;

public class UserSessionRegistryUtil {

	public static void addUserSession(
		long userId, Session socketSession) {

		getService().addUserSession(userId, socketSession);
	}

	public static UserSession clearUserSession(long userId) {
		return getService().clearUserSession(userId);
	}
	public static void clearSession(Session session) {
		getService().clearSession(session);
	}

	public static Collection<Long> getOnlineUsers(long userId) {
		return getService().getOnlineUsers(userId);
	}

	public static void notifyUser(
		JSONObject messageJSON, long userId) {
		
		getService().notifyUsers(
			messageJSON, Collections.singleton(userId));
	}
	
	public static void notifyUsers(
		JSONObject messageJSON,  Collection<Long> userIds) {
		getService().notifyUsers(messageJSON, userIds);
	}

	public static void updateLastActivityTime(long userId) {
		getService().updateLastActivityTime(userId);
	}

	protected static UserSessionRegistry getService() {
		return _serviceTracker.getService();
	}

	private static ServiceTracker<UserSessionRegistry, UserSessionRegistry>
		_serviceTracker = ServiceTrackerFactory.open(
			UserSessionRegistry.class);
}
