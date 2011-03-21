package eu.indenica.runtime.services.impl;

import java.util.Date;

import eu.indenica.runtime.services.IRepository;
import eu.indenica.runtime.structure.IBatch;
import eu.indenica.runtime.structure.IDataLine;
import eu.indenica.runtime.structure.IFilter;
import eu.indenica.runtime.structure.ISearchResult;

public class HashtableRepository implements IRepository {

	@Override
	public ISearchResult getData(IFilter filter, Date from, Date to) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribeToData(IFilter filter, String callbackEpr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publishSingleData(IFilter filter, IDataLine data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publishBatchData(IFilter filter, IBatch data) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
