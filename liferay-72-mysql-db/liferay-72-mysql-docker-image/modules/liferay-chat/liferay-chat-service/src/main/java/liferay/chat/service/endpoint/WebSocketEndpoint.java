package liferay.chat.service.endpoint;

import java.io.IOException;
import java.lang.reflect.Field;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.MessageBus;
import com.liferay.portal.kernel.messaging.MessageBusUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.servlet.PortalSessionContext;
import com.liferay.portal.kernel.util.ReflectionUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import liferay.chat.jedis.pub.sub.JedisPublisherManager;
import liferay.chat.model.ChatMessage;
import liferay.chat.session.UserSessionRegistryUtil;
import redis.clients.jedis.Jedis;

@Component(
	immediate = true,
	property = {
		"org.osgi.http.websocket.endpoint.path=/o/chat"
	},
	service = Endpoint.class
)
public class WebSocketEndpoint extends Endpoint {
	
	@Override
	public void onOpen(Session session, EndpointConfig config) {
		
		String socketSessionId = session.getId();
		_log.info(socketSessionId);
		
		long userId = _getUserId(session);
		
		JSONDeserializer<ChatMessage> deserializer = 
			JSONFactoryUtil.createJSONDeserializer();

		if (Validator.isNull(userId)) {
			try {
				session.close(
					new CloseReason(
						CloseReason.CloseCodes.VIOLATED_POLICY,
						"AUTH ERROR"));

				_log.warn(
					"Error during open websocket session " +
					"due of no chat user role");
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
			return;
		}
		
		
		UserSessionRegistryUtil.addUserSession(userId, session);
		
		session.addMessageHandler(
			new MessageHandler.Whole<String>() {

				@Override
				public void onMessage(String text) {
					
					MessageBusUtil.sendMessage("/socket/gateway-publisher", text);
					
//					
//					_log.info(text);
//					new Thread(new Runnable() {
//						public void run(){
//							
//							Message message = JSONFactoryUtil.looseDeserialize(text, Message.class);		
//							_log.info(message.getMsgType());
//							
//							jedisPublisherManager.getJedisPublisher(message.getMsgType()).publish(text);
//						} 
//					} ).start();
					
					
				}

			});
	}
	
	@Override
	public void onClose(Session session, CloseReason closeReason) {
		UserSessionRegistryUtil.clearSession(session);
	}
	
	private long _getUserId(Session session) {

		User user = null;
				
		try {
			HttpSession httpSession = _getHttpSession(session);

			user = (User)httpSession.getAttribute(WebKeys.USER);

		}
		catch (Exception e) {
			_log.error(e, e);
		}
		
		if (Validator.isNotNull(user)) {
			return user.getUserId();
		}

		return 0L;
	}
	
	private HttpSession _getHttpSession(Session session) throws Exception {
		String httpSessionId = null;
		if (_httpSessionIdField == null) {

			_httpSessionIdField = ReflectionUtil
				.getDeclaredField(session.getClass(), "httpSessionId");
		}

		httpSessionId = (String)_httpSessionIdField.get(session);
		
		
		return PortalSessionContext.get(httpSessionId);
	}
	
	private Field _httpSessionIdField;
	
	@Reference 
	JedisPublisherManager jedisPublisherManager;
	
	private final static Log _log = 
		LogFactoryUtil.getLog(WebSocketEndpoint.class);

}
