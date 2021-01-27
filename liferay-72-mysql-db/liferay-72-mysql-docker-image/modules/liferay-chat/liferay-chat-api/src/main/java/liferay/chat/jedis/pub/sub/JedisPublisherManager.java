package liferay.chat.jedis.pub.sub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.liferay.osgi.util.ServiceTrackerFactory;

@Component(
	immediate = true,
	service = JedisPublisherManager.class
)
public class JedisPublisherManager {
	
	public JedisPublisher getJedisPublisher(String type) {
		return _jedisPublisherMap.get(type);
	}
	
	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;
		_jedisPublisherMap = new ConcurrentHashMap<>();
		_serviceTracker = ServiceTrackerFactory.open(
			bundleContext, JedisPublisher.class,
			new JedisPublisherServiceTrackerCustomizer());
	}
	
	@Deactivate
	public void deactivate() {
		_serviceTracker.close();
	}
	
	private BundleContext _bundleContext;
	private ServiceTracker<
		JedisPublisher,JedisPublisher> _serviceTracker;
	private Map<String, JedisPublisher> _jedisPublisherMap;
	

	public class JedisPublisherServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<
			JedisPublisher, JedisPublisher> {
		
		@Override
		public JedisPublisher addingService(
			ServiceReference<JedisPublisher> reference) {
			
			JedisPublisher jedisPublisher =
				_bundleContext.getService(reference);
			
			String typeName = jedisPublisher.getTypeName();
			
			_jedisPublisherMap.put(typeName, jedisPublisher);
			
			return jedisPublisher;
		}
		
		@Override
		public void modifiedService(
			ServiceReference<JedisPublisher> reference, 
			JedisPublisher service) {
			
			removedService(reference, service);
			
			addingService(reference);
		}
		
		@Override
		public void removedService(
			ServiceReference<JedisPublisher> reference,
			JedisPublisher service) {
			
			_bundleContext.ungetService(reference);
			
			_jedisPublisherMap.remove(service.getTypeName());
			
		}
		
	}
}
