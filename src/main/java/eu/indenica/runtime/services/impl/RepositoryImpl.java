package eu.indenica.runtime.services.impl;

import java.net.URL;

import javax.jws.WebService;

import at.ac.tuwien.infosys.util.Util;
import at.ac.tuwien.infosys.ws.AbstractNode;
import at.ac.tuwien.infosys.ws.DynamicWSClient;
import at.ac.tuwien.infosys.ws.EndpointReference;
import eu.indenica.runtime.dto.Data;
import eu.indenica.runtime.dto.Filter;
import eu.indenica.runtime.event.NotificationEngine;
import eu.indenica.runtime.event.NotificationReceiverService;
import eu.indenica.runtime.plugin.PluginChain;
import eu.indenica.runtime.services.IRepository;

@WebService(endpointInterface="eu.indenica.runtime.services.IRepository")
public class RepositoryImpl extends AbstractNode implements IRepository {

	private PluginChain plugins = new PluginChain();
	private static Util util = new Util();
	private NotificationEngine notifications = new NotificationEngine(plugins);

	public Data getData(Filter filter) {
		System.out.println("get: " + filter);
		return plugins.getData(filter);
	}

	public void subscribeToData(Filter filter, EndpointReference epr) {
		notifications.subscribeToData(filter, epr);
	}

	public void publishData(Data data) {
		System.out.println("publish: " + data);
		plugins.storeData(data);
		notifications.publishData(data);
	}

	public static void main(String[] args) throws Exception {
		NotificationReceiverService.start();
		String url = "http://0.0.0.0:45689/repo";
		new RepositoryImpl().deploy(url);
		URL wsdl = new URL(url + "?wsdl");
		IRepository client = DynamicWSClient.createClientJaxws(IRepository.class, wsdl);

		Filter f = new Filter();
		f.value = util.xml.toElement("<infrastructure><query>node=foo</query></infrastructure>");
		client.subscribeToData(f, NotificationReceiverService.DEFAULT_ENDPOINT);

		Data d = new Data();
		d.value = util.xml.toElement("<infrastructure><node>foo</node></infrastructure>");
		client.publishData(d);
		Data d1 = client.getData(f);
		System.out.println(d1.value);

	}

}
