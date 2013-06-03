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

		Filter f1 = new Filter();
		f1.value = util.xml.toElement("<infrastructure><query>node=foo</query></infrastructure>");
		client.subscribeToData(f1, NotificationReceiverService.DEFAULT_ENDPOINT);
		Filter f2 = new Filter();
		f2.value = util.xml.toElement("<mongodb><query>{node:{foo:'abc'}}</query></mongodb>");
		client.subscribeToData(f2, NotificationReceiverService.DEFAULT_ENDPOINT);

		Data d = new Data();
		d.value = util.xml.toElement("<infrastructure><node>foo</node></infrastructure>");
		client.publishData(d);
		Data d1 = new Data();
		d1.value = util.xml.toElement("<mongodb><node><foo>abc</foo><bar>123</bar></node></mongodb>");
		client.publishData(d1);
		Data r1 = client.getData(f1);
		System.out.println(r1.value);
		Data r2 = client.getData(f2);
		System.out.println(r2.value);

	}

}
