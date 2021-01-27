package liferay.chat.service.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class SubscriberActivator implements BundleActivator{

	@Override
	public void start(BundleContext context) throws Exception {
		//JedisSubscriberRegistryUtil.subscribeAll();
		
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
