package eu.indenica.runtime.dto;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Filter {

	@XmlMixed
	@XmlAnyElement
	public Object value;

}
