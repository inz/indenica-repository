package eu.indenica.runtime.plugin;

import java.util.LinkedList;
import java.util.List;

import eu.indenica.runtime.dto.Data;
import eu.indenica.runtime.dto.Filter;

public class PluginChain {

	private List<IPlugin> plugins = new LinkedList<IPlugin>();

	public PluginChain() {
		// TODO search for all plugins in classpath
		plugins.add(new PluginChef());
	}

	public Data getData(Filter f) {
		for(IPlugin p : plugins) {
			if(p.canHandle(f)) {
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
