package eu.indenica.runtime.services;

import java.util.Date;

import javax.jws.WebService;

import eu.indenica.runtime.structure.IBatch;
import eu.indenica.runtime.structure.IDataLine;
import eu.indenica.runtime.structure.IFilter;
import eu.indenica.runtime.structure.ISearchResult;

@WebService
public interface IRepository {
	
	public ISearchResult getData(IFilter filter, Date from, Date to);
	
	// TODO should really be a WS-A endpoint
	public void subscribeToData(IFilter filter, String callbackEpr);
	
	public void publishSingleData(IFilter filter, IDataLine data);
	public void publishBatchData(IFilter filter, IBatch data);
	
}
