/**
 * 
 */
package eu.indenica.runtime.api;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.ac.tuwien.infosys.ws.AbstractNode;
import at.ac.tuwien.infosys.ws.DynamicWSClient;
import eu.indenica.runtime.services.IRepository;

/**
 * @author Christian Inzinger
 * 
 */
public abstract class ApiPlugin extends AbstractNode {
    private static final Logger LOG = LoggerFactory.getLogger(ApiPlugin.class);

    protected IRepository repository;

    /**
     * 
     */
    public ApiPlugin(URL repositoryWsdl) {
        LOG.info("Connecting API plugin to {}", repositoryWsdl);
        repository =
                DynamicWSClient.createClientJaxws(IRepository.class,
                        repositoryWsdl);
    }
}
