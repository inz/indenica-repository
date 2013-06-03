package eu.indenica.runtime.event;

import java.net.URL;

import javax.jws.WebService;

import at.ac.tuwien.infosys.ws.AbstractNode;
import at.ac.tuwien.infosys.ws.EndpointReference;
import eu.indenica.runtime.dto.Data;

@WebService(endpointInterface="eu.indenica.runtime.event.INotification")
public class NotificationReceiverService extends AbstractNode implements INotification {

	public static EndpointReference DEFAULT_ENDPOINT;
	static {
		try {
			DEFAULT_ENDPOINT = new EndpointReference(new URL("http://localhost:8943/events?wsdl"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void notifyStore(Data newData) {
		System.out.println("New data stored: " + newData);
	}

	public static void start() throws Exception {
		start(DEFAULT_ENDPOINT);
	}

	public static void start(EndpointReference epr) throws Exception {
		NotificationReceiverService s = new NotificationReceiverService();
		System.out.println("Starting notification service: " + epr.getAddress());
		s.deploy(epr.getAddress());
	}

	public static void main(String[] args) throws Exception {
		start();
	}
}
