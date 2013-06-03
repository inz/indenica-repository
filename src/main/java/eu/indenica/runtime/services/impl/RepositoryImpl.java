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
import eu.indenica.runtime.plugin.PluginChain;
import eu.indenica.runtime.services.IRepository;

@WebService(endpointInterface = "eu.indenica.runtime.services.IRepository")
public class RepositoryImpl extends AbstractNode implements IRepository {
    private final static Logger LOG = LoggerFactory
            .getLogger(RepositoryImpl.class);

    private PluginChain plugins = new PluginChain();
    private static Util util = Util.getInstance();

    public Data getData(Filter filter) {
        LOG.info("Get data for: " + filter);
        return plugins.getData(filter);
    }

    public void subscribeToData(Filter filter, EndpointReference epr) {
        // TODO Auto-generated method stub

    }

    public void publishData(Data data) {
        LOG.info("Publish data: " + data);
        plugins.storeData(data);
    }

    public static void main(String[] args) throws Exception {
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

        LOG.info("Exiting.");
        System.exit(0);
    }

}
