package eu.indenica.runtime.services.impl;

import java.io.StringWriter;
import java.net.URL;

import javax.jws.WebService;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

@WebService(endpointInterface = "eu.indenica.runtime.services.IRepository")
public class RepositoryImpl extends AbstractNode implements IRepository {
    private final static Logger LOG = LoggerFactory
            .getLogger(RepositoryImpl.class);

    private PluginChain plugins = new PluginChain();
    private static Util util = Util.getInstance();
  	private NotificationEngine notifications = new NotificationEngine(plugins);

    public Data getData(Filter filter) {
        LOG.info("Get data for: " + filter);
        return plugins.getData(filter);
    }

  	public void subscribeToData(Filter filter, EndpointReference epr) {
      LOG.info("Subscribe {} to {}", epr, filter);
  		notifications.subscribeToData(filter, epr);
  	}

    public void publishData(Data data) {
        LOG.info("Publish data: " + data);
    		plugins.storeData(data);
    		notifications.publishData(data);
    }

    public static void main(String[] args) throws Exception {
  	    NotificationReceiverService.start();
        Logger LOG = LoggerFactory.getLogger("main");
        String url = "http://0.0.0.0:45689/repo";
        URL wsdl = new URL(url + "?wsdl");
        {
            LOG.info("Starting repository...");
            RepositoryImpl r = new RepositoryImpl();
            r.deploy(url);
        }

        IRepository client =
                DynamicWSClient.createClientJaxws(IRepository.class, wsdl);

        Marshaller m = JAXBContext.newInstance(Data.class).createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);

        {
            LOG.info("Publish infrastructure data...");
            Data d = new Data();
            d.value =
                    util.xml.toElement("<infrastructure><node>foo</node></infrastructure>");
            client.publishData(d);
        }

        {
            LOG.info("Get infrastructure data...");
            Filter f = new Filter();
            f.value =
                    util.xml.toElement("<infrastructure><node>foo</node></infrastructure>");

            Data d1 = client.getData(f);
            LOG.info("Got data: {} ({})", d1.value, d1.value.getClass()
                    .getCanonicalName());

            StringWriter sw = new StringWriter();
            m.marshal(d1, sw);
            LOG.info("Raw: {}", sw);
        }

        {
            LOG.info("Publish variability data...");
            Data d = new Data();
            d.value =
                    util.xml.toElement("<variability><name>some-name</name><some><stuff></stuff><here /></some></variability>");
            client.publishData(d);
        }

        {
            LOG.info("Get variability data...");
            Filter f = new Filter();
            f.value =
                    util.xml.toElement("<variability><name>some-name</name></variability>");
            Data d = client.getData(f);

            StringWriter sw = new StringWriter();
            m.marshal(d, sw);
            LOG.info("Got data (raw): {}", sw);
        }
        
        {
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

        LOG.info("Exiting.");
        System.exit(0);
    }
	
}
