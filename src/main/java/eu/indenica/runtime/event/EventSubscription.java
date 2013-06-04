package eu.indenica.runtime.event;

import java.util.HashSet;
import java.util.Set;

import at.ac.tuwien.infosys.ws.EndpointReference;
import eu.indenica.runtime.dto.Filter;

public class EventSubscription {

	private final Set<EndpointReference> listeners = new HashSet<EndpointReference>();
	private Filter filter;

	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	public Set<EndpointReference> getListeners() {
		return listeners;
	}
}
