package liferay.chat.service.destination.configurator;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.Destination;
import com.liferay.portal.kernel.messaging.DestinationConfiguration;
import com.liferay.portal.kernel.messaging.DestinationFactory;
import com.liferay.portal.kernel.util.HashMapDictionary;

@Component (
	immediate = true,
	service = ChatDestinationConfigurator.class
)
public class ChatDestinationConfigurator {

	@Activate
	protected void activate(BundleContext bundleContext) {

		_bundleContext = bundleContext;

		// Create a DestinationConfiguration for parallel destinations.

		DestinationConfiguration destinationConfiguration =
			new DestinationConfiguration(
				DestinationConfiguration.DESTINATION_TYPE_PARALLEL,
					"/socket/gateway-publisher");

		// Create the destination

		Destination chatGatewyDestination = _destinationFactory.createDestination(
			destinationConfiguration);

		// Add the destination to the OSGi service registry

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		properties.put("destination.name", chatGatewyDestination.getName());

		ServiceRegistration<Destination> serviceRegistration =
			_bundleContext.registerService(
				Destination.class, chatGatewyDestination, properties);

		// Track references to the destination service registrations 

		_serviceRegistrations.put(
			chatGatewyDestination.getName(), serviceRegistration);
	}

	@Deactivate
	protected void deactivate() {

		// Unregister and destroy destinations this component unregistered

		for (ServiceRegistration<Destination> serviceRegistration : 
			_serviceRegistrations.values()) {

			Destination chatGatewyDestination = _bundleContext.getService(
				serviceRegistration.getReference());

			serviceRegistration.unregister();

			chatGatewyDestination.destroy();

		}

		_serviceRegistrations.clear();

	}

	private BundleContext _bundleContext;

	@Reference
	private DestinationFactory _destinationFactory;

	private final Map<String, ServiceRegistration<Destination>>
		_serviceRegistrations = new HashMap<>();
	
	private final static Log _log = 
		LogFactoryUtil.getLog(ChatDestinationConfigurator.class);
}
