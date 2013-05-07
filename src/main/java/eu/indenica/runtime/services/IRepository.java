package eu.indenica.runtime.services;

import javax.jws.WebService;

import at.ac.tuwien.infosys.ws.EndpointReference;
import eu.indenica.runtime.dto.Data;
import eu.indenica.runtime.dto.Filter;

@WebService
public interface IRepository {

	public Data getData(Filter filter);

	public void subscribeToData(Filter filter, EndpointReference epr);

	public void publishData(Data data);

}
