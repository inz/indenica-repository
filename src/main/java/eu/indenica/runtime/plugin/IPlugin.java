package eu.indenica.runtime.plugin;

import eu.indenica.runtime.dto.Data;
import eu.indenica.runtime.dto.Filter;

public interface IPlugin {

	boolean canHandle(Filter f);

	boolean canHandle(Data d);

	Data getData(Filter f);

	Data storeData(Data d);

}
