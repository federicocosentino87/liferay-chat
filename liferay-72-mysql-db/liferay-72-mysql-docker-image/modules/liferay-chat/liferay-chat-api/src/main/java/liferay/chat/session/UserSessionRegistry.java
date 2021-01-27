package liferay.chat.session;

import java.util.Collection;

import javax.websocket.Session;

import com.liferay.portal.kernel.json.JSONObject;

import liferay.chat.model.UserSession;
import liferay.chat.model.UserSession.UserStatus;

public interface UserSessionRegistry {

	public void addUserSession(long userId, Session socketSession);

	public UserSession clearUserSession(long userId);

	public void clearSession(Session session);

	public Collection<Long> getOnlineUsers(long userId);

	public UserSession getUserSession(long userId);

	public UserStatus getUserStatus(long userId);

	public boolean isOnline(long userId);

	public void notifyUsers(JSONObject messageJSON,  Collection<Long> userIds);

	public void updateLastActivityTime(long userId);

}
