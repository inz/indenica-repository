package eu.indenica.runtime.plugin;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.indenica.runtime.dto.Data;
import eu.indenica.runtime.dto.Filter;

public class PluginChain {
    private static final Logger LOG = LoggerFactory.getLogger(PluginChain.class);

	private List<IPlugin> plugins = new LinkedList<IPlugin>();

	public PluginChain() {
		// TODO search for all plugins in classpath
	    plugins.add(new PluginChef());
		plugins.add(new PluginFilesystem());
	}

	public Data getData(Filter f) {
		for(IPlugin p : plugins) {
			if(p.canHandle(f)) {
			    LOG.debug("Getting data using plugin {}", p);
				return p.getData(f);
			}
		}
		return null;
	}

	public Data storeData(Data d) {
		for(IPlugin p : plugins) {
			if(p.canHandle(d)) {
				return p.storeData(d);
			}
		}
		return null;
	}
	
	public List<IPlugin> getPlugins() {
		return plugins;
	}
}
