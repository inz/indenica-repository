package eu.indenica.runtime.plugin;

import java.net.UnknownHostException;

import net.sf.json.JSON;

import org.w3c.dom.Element;

import at.ac.tuwien.infosys.util.Util;

import com.mongodb.DB;
import com.mongodb.DBAddress;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import de.flapdoodle.embedmongo.MongoDBRuntime;
import eu.indenica.runtime.Constants;
import eu.indenica.runtime.dto.Data;
import eu.indenica.runtime.dto.Filter;

public class PluginMongoDB implements IPlugin {

	public static Util util = new Util();
	private DB db;
	private DBAddress address;
	private String dbName = "indenica";
	private String collectionName = "indenica";
	private DBCollection collection;

	static {
		System.out.println("Starting embedded MongoDB instance");
		MongoDBRuntime.getDefaultInstance();
	}

	public PluginMongoDB(String host) {
		try {
			address = new DBAddress(host, dbName);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	public Data storeData(Data d) {
		System.out.println("Storing data in MongoDB");
		DBCollection coll = getCollection(collectionName);
		JSON json = util.xml.toJSON((Element)d.value);
		coll.save((DBObject) com.mongodb.util.JSON.parse(json.toString(0)));
		return d;
	}

	public Data getData(Filter f) {
		try {
			DBCollection coll = getCollection(collectionName);

			String query = util.xml.getChildElements(((Element)f.value), "query").get(0).getTextContent();
			System.out.println("Finding data in MongoDB: " + query);
			DBObject search = (DBObject)com.mongodb.util.JSON.parse(query);

			Data d = new Data();
			d.value = util.xml.toElement("<mongodb/>");
			DBCursor c = coll.find(search);
			while(c.hasNext()) {
				DBObject o = c.next();
				util.xml.appendChild((Element)d.value, util.xml.toElement(
						com.mongodb.util.JSON.serialize(o)));
			}

			return d;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private DBCollection getCollection(String collection2) {
		if(collection == null) {
			collection =getDB().getCollection(collectionName);
		}
		return collection;
	}

	public boolean canHandle(Data d) {
		return (d.value instanceof Element) && 
				((Element)d.value).getNodeName().equals(
						Constants.DATA_MONGODB);
	}
	public boolean canHandle(Filter f) {
		return (f.value instanceof Element) && 
				((Element)f.value).getNodeName().equals(
						Constants.DATA_MONGODB);
	}

	/* HELPER METHODS */

	private DB getDB() {
		if(db == null) {
			db = Mongo.connect(address);
		}
		return db;
	}
}
