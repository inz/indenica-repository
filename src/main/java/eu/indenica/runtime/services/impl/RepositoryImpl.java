package eu.indenica.runtime.services.impl;

import java.net.URL;

import javax.jws.WebService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.infosys.util.Util;
import at.ac.tuwien.infosys.ws.AbstractNode;
import at.ac.tuwien.infosys.ws.DynamicWSClient;
import at.ac.tuwien.infosys.ws.EndpointReference;
import eu.indenica.runtime.dto.Data;
import eu.indenica.runtime.dto.Filter;
import eu.indenica.runtime.plugin.PluginChain;
import eu.indenica.runtime.services.IRepository;

@WebService(endpointInterface="eu.indenica.runtime.services.IRepository")
public class RepositoryImpl extends AbstractNode implements IRepository {

	private PluginChain plugins = new PluginChain();
	private static Util util = new Util();

	public Data getData(Filter filter) {
		System.out.println("get: " + filter);
		return plugins.getData(filter);
	}

	public void subscribeToData(Filter filter, EndpointReference epr) {
		// TODO Auto-generated method stub
		
	}

	public void publishData(Data data) {
		System.out.println("publish: " + data);
		plugins.storeData(data);
	}

	public static void main(String[] args) throws Exception {
		RepositoryImpl r = new RepositoryImpl();
		String url = "http://0.0.0.0:45689/repo";
		URL wsdl = new URL(url + "?wsdl");
		r.deploy(url);
		IRepository client = DynamicWSClient.createClientJaxws(IRepository.class, wsdl);
		Filter f = new Filter();
		f.value = util.xml.toElement("<infrastructure><node>foo</node></infrastructure>");
		Data d = new Data();
		f.value = util.xml.toElement("<infrastructure><node>foo</node></infrastructure>");
		client.publishData(d);
		Data d1 = client.getData(f);
		System.out.println(d1.value);
	}

}
