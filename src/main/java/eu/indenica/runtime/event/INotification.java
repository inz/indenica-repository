package eu.indenica.runtime.event;

import javax.jws.WebService;

import eu.indenica.runtime.dto.Data;

@WebService
public interface INotification {

	void notifyStore(Data newData);

}
