package eu.indenica.runtime.event;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import at.ac.tuwien.infosys.ws.DynamicWSClient;
import at.ac.tuwien.infosys.ws.EndpointReference;

import eu.indenica.runtime.dto.Data;
import eu.indenica.runtime.dto.Filter;
import eu.indenica.runtime.plugin.PluginChain;

public class NotificationEngine {

	private List<EventSubscription> subscriptions = new LinkedList<EventSubscription>();
	private PluginChain pluginChain;

	public NotificationEngine(PluginChain plugins) {
		this.pluginChain = plugins;
	}

	public void publishData(Data data) {
		synchronized (subscriptions) {
			for(EventSubscription s : subscriptions) {
				Data d = pluginChain.getData(s.getFilter());
				if(data != null) {
					// notify
					for(EndpointReference r : s.getListeners()) {
						try {
							INotification n = DynamicWSClient.
									createClientJaxws(INotification.class,
											new URL(r.getAddress()));
							n.notifyStore(d);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void subscribeToData(Filter filter, EndpointReference epr) {
		synchronized (subscriptions) {
			for(EventSubscription s : subscriptions) {
				if(s.getFilter().equals(filter)) {
					s.getListeners().add(epr);
					return;
				}
			}
			EventSubscription sub = new EventSubscription();
			sub.setFilter(filter);
			sub.getListeners().add(epr);
			subscriptions.add(sub);
		}
	}

}
