package liferay.chat.jedis.pub.sub.user.search.impl;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import liferay.chat.jedis.pub.sub.JedisSubscriber;
import liferay.chat.jedis.pub.sub.user.search.client.UserSearchJedisSubscriberClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Component(
	immediate=true,
	property = {},
	service = JedisSubscriber.class
)
public class UserSearchJedisSubscriberImpl implements JedisSubscriber{
	
	private JedisPubSub jedisPubSub;
	private Jedis jedis;

	@Activate
	protected void activate() {
		jedis = new Jedis("redis");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				jedisPubSub = new UserSearchJedisSubscriberClient();
				jedis.subscribe(jedisPubSub, CHAT_CHANNELL);
			}
		}).start();
		
	}
	
	@Deactivate
	protected void deactivate() {
		if (jedisPubSub != null) {
			jedisPubSub.unsubscribe();
		}
		
		if (jedis != null) {
			jedis.close();
		}
	}
	
	private final static String CHAT_CHANNELL = "/user/seach";
	
	private final static Log _log = 
		LogFactoryUtil.getLog(UserSearchJedisSubscriberImpl.class);

}
