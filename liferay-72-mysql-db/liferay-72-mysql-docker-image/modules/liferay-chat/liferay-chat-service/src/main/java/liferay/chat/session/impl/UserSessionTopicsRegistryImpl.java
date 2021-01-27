package liferay.chat.session.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.websocket.Session;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;

import liferay.chat.model.UserSession;
import liferay.chat.model.UserSession.UserStatus;
import liferay.chat.service.endpoint.ServerToClientMessageType;
import liferay.chat.session.UserSessionRegistry;

@Component(
	immediate = true,
	service = UserSessionRegistry.class
)
public class UserSessionTopicsRegistryImpl implements UserSessionRegistry {

	@Activate
	protected void activate(Map<String, Object> properties) {

		_userSessionMap = new UserSessionMap();
	}

	@Deactivate
	protected void deactivate(Map<String, Object> properties) {

		_userSessionMap.clear();
	}

	/**
	 * Manage multiple same user session (two browser tabs, desktop vs mobile)
	 * @param userId
	 * @param socketSession
	 */
	@Override
	public void addUserSession(
		long userId, Session socketSession) {

		if (!isOnline(userId)) {
			_log.debug("User (" + userId + ") made login and his session" +
					  " will be created");

			try {
				User user = _userLocalService.getUser(userId);

				String portraitUrl = UserConstants.getPortraitURL(
					_portal.getPathImage(), user.isMale(),
					user.getPortraitId(), user.getUserUuid());

				_userSessionMap.put(
					userId, new UserSession(
						userId, user.getFullName(), portraitUrl));
			}
			catch (PortalException e) {
				_log.error(e, e);
			}

			//_notifyNewUserOthers(userId);
		}

		_userSessionMap.get(userId)
			.addSocketSession(socketSession);

		_log.debug("Added user to session topic registry: " + userId);

//		sendTopics(userId);
	}

	@Override
	public UserSession clearUserSession(long userId) {

		_notifyRemovedUserOthers(userId);

		return _userSessionMap.remove(userId);
	}

	@Override
	public void clearSession(Session session) {

		for (long userId : _userSessionMap.keySet()) {

			UserSession userSession = _userSessionMap.get(userId);

			if (userSession.getSessions().contains(session)) {

				try {
					session.close();

					// CHECK IF IT IS THE LAST ONE
					if (userSession.getSessions().size() == 1) {
						clearUserSession(userId);
					}
					else {
						userSession.getSessions().remove(session);
					}
				}
				catch (IOException e) {
					_log.error(e, e);
				}

				break;
			}
		}

	}

	@Override
	public UserSession getUserSession(
		long userId) {

		return _userSessionMap.get(userId);
	}

	@Override
	public UserStatus getUserStatus(
		long userId) {

		if (isOnline(userId)) {
			return _userSessionMap.get(userId)
				.getStatus();
		}
		return UserStatus.OFFLINE;
	}

	@Override
	public Collection<Long> getOnlineUsers(
		long userId) {

		List<Long> userList = new LinkedList<>(_userSessionMap.keySet());

		userList.remove(userId);

		return userList;
	}

	/**
	 * Online or Inactive
	 * @param userId
	 * @return
	 */
	@Override
	public boolean isOnline(long userId) {
		return _userSessionMap.containsKey(userId);
	}


	@Override
	public void notifyUsers(JSONObject messageJSON,  Collection<Long> userIds) {
		_userSessionMap.sendToClient(messageJSON, userIds);;
		
	}

	
	private void _notifyNewUserOthers(long userId) {
		_notifyUserOthers(ServerToClientMessageType.ACTIVE_USER, userId);
	}

	private void _notifyRemovedUserOthers(long userId) {
		_notifyUserOthers(ServerToClientMessageType.INACTIVE_USER, userId);
	}

	private void _notifyUserOthers(
		ServerToClientMessageType messageType, long userId) {

		if (_userSessionMap.containsKey(userId)) {

			JSONObject newUserJSON = JSONFactoryUtil.createJSONObject();

			newUserJSON.put(
				ServerToClientMessageType.MSG_TYPE, messageType.name());

			newUserJSON.put(
				messageType.getJsonField(),
				_userSessionMap.get(userId)
					.toJSON());

			Set<Long> userIds = _userSessionMap.keySet()
				.stream()
				.filter(uId -> uId != userId)
				.collect(Collectors.toSet());

			_userSessionMap.sendToClient(newUserJSON, userIds);
		}
	}

	public void updateLastActivityTime(long userId) {

		UserSession userSession = _userSessionMap.get(userId);

		if (userSession != null) {
			userSession.updateLastActivityTime();
		}
	}

	@Reference
	private Portal _portal;

	@Reference
	private UserLocalService _userLocalService;

	private UserSessionMap _userSessionMap;

	private static final Log _log = LogFactoryUtil.getLog(
		UserSessionTopicsRegistryImpl.class);

}
