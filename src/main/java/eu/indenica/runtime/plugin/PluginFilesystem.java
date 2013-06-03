/**
 * 
 */
package eu.indenica.runtime.plugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import at.ac.tuwien.infosys.util.Configuration;
import at.ac.tuwien.infosys.util.Util;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import eu.indenica.runtime.Constants;
import eu.indenica.runtime.dto.Data;
import eu.indenica.runtime.dto.Filter;

/**
 * @author Christian Inzinger
 * 
 */
public class PluginFilesystem implements IPlugin {
    private static final Logger LOG = LoggerFactory
            .getLogger(PluginFilesystem.class);
    private static final String DEFAULT_BASEPATH = "./data";
    public static Util util = Util.getInstance();
    private static Set<String> SUPPORTED_ASSETS = Sets.newHashSet(
            Constants.DATA_VARIABILITY, Constants.DATA_VIEW_MODEL,
            Constants.DATA_REQUIREMENTS);

    private String basePath;

    /**
     * 
     */
    public PluginFilesystem() {
        basePath = Configuration.getValue("filesystem.basepath");
        if(basePath == null) {
            basePath = DEFAULT_BASEPATH;
        }
        LOG.info("Filesystem storage base path set to: {}", basePath);
    }

    public boolean canHandle(Filter f) {
        // System.out.println(f.value);
        return (f.value instanceof Element)
                && SUPPORTED_ASSETS.contains(((Element) f.value).getNodeName());
    }

    public Data getData(Filter f) {
        try {
            Unmarshaller u =
                    JAXBContext.newInstance(Data.class).createUnmarshaller();
            String fileName = findFilenameFor((Element) f.value);
            LOG.debug("Unmarshalling data from {}", fileName);
            Object unmarshalled = u.unmarshal(new File(fileName));

            if(!(unmarshalled instanceof Data)) {
                LOG.warn("Don't know what to do with {}", unmarshalled);
                return null;
            }

            return (Data) unmarshalled;
        } catch(JAXBException e) {
            LOG.error("Error unmarshalling file", e);
            throw new RuntimeException(e);
        }
    }

    private String findFilenameFor(Element rootElement) {
        String assetType = rootElement.getNodeName();
        LOG.debug("Found asset type: {}", assetType);

        Map<String, Node> filterElements = Maps.newHashMap();

        for(Node n = rootElement.getFirstChild(); n != null; n =
                n.getNextSibling()) {
            LOG.debug("Found filter element for {}", n.getNodeName());
            filterElements.put(n.getNodeName(), n);
        }

        // For starters, get asset by name
        if(filterElements.containsKey("name")) {
            LOG.debug("Returning file by name");
            return Joiner.on(File.separator).join(basePath, assetType,
                    filterElements.get("name").getTextContent());
        }

        LOG.info("No file found for filter {}", rootElement);
        return null;
    }

    public boolean canHandle(Data d) {
        return (d.value instanceof Element)
                && SUPPORTED_ASSETS.contains(((Element) d.value).getNodeName());
    }

    public Data storeData(Data d) {
        try {
            File file = new File(findFilenameFor((Element) d.value));
            file.getParentFile().mkdirs();
            BufferedWriter writer =
                    new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            Marshaller m =
                    JAXBContext.newInstance(Data.class).createMarshaller();
            // m.setProperty(Marshaller.JAXB_FRAGMENT, true);
            LOG.info("Storing data into {}: {}", file, d);
            m.marshal(d, writer);
            return d;
        } catch(JAXBException e) {
            LOG.error("Could not create marshaller", e);
        } catch(IOException e) {
            LOG.error("Could not write file", e);
        }

        return null;
    }
}
