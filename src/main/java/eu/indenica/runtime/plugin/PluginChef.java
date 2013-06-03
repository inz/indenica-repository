package eu.indenica.runtime.plugin;

import java.io.File;
import java.util.LinkedList;
import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.chef.ChefContext;
import org.jclouds.hostedchef.HostedChefApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import at.ac.tuwien.infosys.util.Configuration;
import at.ac.tuwien.infosys.util.Util;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import eu.indenica.runtime.Constants;
import eu.indenica.runtime.dto.Data;
import eu.indenica.runtime.dto.Filter;

public class PluginChef implements IPlugin {
    private static final Logger LOG = LoggerFactory.getLogger(PluginChef.class);

	public static Util util = Util.getInstance();
	
	public boolean canHandle(Filter f) {
		return (f.value instanceof Element) && 
				((Element)f.value).getNodeName().equals(
						Constants.DATA_INFRASTRUCTURE);
	}

	public Data getData(Filter f) {
		try {

			String client = Configuration.getValue("chef.user.name");
			String pemFile = Configuration.getValue("chef.user.pem");
			if(pemFile == null) {
				throw new Exception("Please provide chef.user.pem in config.properties file.");
			}
			if(client == null) {
				throw new Exception("Please provide chef.user.name in config.properties file.");
			}
			LOG.debug("Using pem file {}", pemFile);
			String credential = Files.toString(new File(pemFile), Charsets.UTF_8);

			ChefContext context = ContextBuilder.newBuilder("hostedchef").
					endpoint("https://api.opscode.com/organizations/indenica-tuv").
					credentials(client, credential).buildView(ChefContext.class);

			Set<String> nodes = context.getApi(HostedChefApi.class).listNodes();

			context.close();
			Data d = new Data();
			d.value = util.xml.toElement(new LinkedList<String>(nodes));

			return d;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean canHandle(Data d) {
		// TODO Auto-generated method stub
		return false;
	}

	public Data storeData(Data d) {
		// TODO Auto-generated method stub
		return d;
	}

}
