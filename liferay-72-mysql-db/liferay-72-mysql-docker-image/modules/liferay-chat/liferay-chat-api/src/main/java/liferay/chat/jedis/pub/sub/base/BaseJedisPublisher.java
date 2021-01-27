package liferay.chat.jedis.pub.sub.base;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

import liferay.chat.jedis.pub.sub.JedisPublisher;
import redis.clients.jedis.Jedis;

public abstract class BaseJedisPublisher implements JedisPublisher{

	private String type;
	private Jedis jedis;
	
	@Activate
	protected void activate() {
		jedis = new Jedis("redis");
	}
	
	@Deactivate
	protected void deactivate() {
		jedis.close();
	}
	
	@Override
	public String getTypeName() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Jedis getJedis() {
		return jedis;
	}
}
