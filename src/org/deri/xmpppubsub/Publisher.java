package org.deri.xmpppubsub;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import com.tecnick.htmlutils.htmlentities.HTMLEntities;
//import org.apache.commons.configuration.Configuration;
//import org.apache.commons.configuration.ConfigurationException;
//import org.apache.commons.configuration.PropertiesConfiguration;
//import com.javacodegeeks.xmpp.XmppManager;
import org.deri.xmpppubsub.SPARQLQuery;

/**
 * @author Maciej Dabrowski
 * @author Julia Anaya
 *
 */
public class Publisher {
    XMPPConnection connection;
    PubSubManager mgr;
    static Logger logger = Logger.getLogger(Publisher.class);

    /**
     * @param userName
     * @param password
     * @param xmppserver
     * @param port
     * @return void 
     *
     */
    public Publisher(String userName, String password, String xmppserver, int port) throws XMPPException {
    	connect(userName, password, xmppserver, port); 	
    }
    
    /**
     * @param userName
     * @param password
     * @param xmppserver
     * @param port
     * @return void 
     *
     */
    public void connect(String userName, String password, String xmppserver, int port) throws XMPPException {
	    ConnectionConfiguration config = new ConnectionConfiguration(xmppserver,port);
	    connection = new XMPPConnection(config);
	    connection.connect();
	    connection.login(userName, password);
	    logger.info("logued in");

		//Create a pubsub manager using an existing Connection
		mgr = new PubSubManager(connection);
		logger.info("PubSub manager created");
    }

    /**
     * @return void 
     *
     */
    public void disconnect() {
    	connection.disconnect();
		logger.info("disconected");
    }


    /**
     * @return void 
     *
     */
    public LeafNode createNode(String nodename) throws XMPPException {
		ConfigureForm form = new ConfigureForm(FormType.submit);
		form.setAccessModel(AccessModel.open);
		form.setDeliverPayloads(true);
		form.setNotifyRetract(true);
		form.setPersistentItems(true);
		form.setPublishModel(PublishModel.open);
		LeafNode leaf = (LeafNode) mgr.createNode(nodename, form);
		logger.info("node" + nodename  + "created");
    	return leaf;
    }


    /**
     * @return void 
     *
     */
    public LeafNode getNode(String nodename) throws XMPPException {
		LeafNode node = (LeafNode) mgr.getNode(nodename);
		logger.info("node got");
		return node;
    }

    /**
     * @return void 
     *
     */
    public LeafNode getOrCreateNode(String nodename) throws XMPPException {
    	LeafNode node;
    	try {
    		node = (LeafNode) mgr.getNode(nodename);
    	} catch (Exception e){
    		node = createNode(nodename);
    	}
		return node;
    }


    /**
     * @return void 
     *
     */
    public void sendPayload(LeafNode node, SPARQLQuery query) {
    	String itemid = "test" + System.currentTimeMillis();
	    SimplePayload payloadNS = new SimplePayload("query", "http://www.w3.org/TR/sparql11-update/", query.toXML());
	    PayloadItem<SimplePayload> item = new PayloadItem<SimplePayload>(itemid, payloadNS);
    	try {
    		node.send(item);
    	} catch (XMPPException e) {
    		logger.debug("exception sending payload");
		    e.printStackTrace();
		    logger.debug(e);
    	}
    }
    
	/**
	 * @param args
	 * TODO fix exception, methods, variables... to follow the java style
	 * TODO when to disconnect
	 * TODO manage arguments
	 * 
	 * TODO separate xmpp login in other class?, inherit publisher and subscriber from a common class that has the methods connect and getNode?
	 * TODO separate create node in other executable or new function getOrCreateNode?
	 * 
	 * TODO why the xml formatting error in the sparql query, even scaping xml entities?
	 * TODO extend method to get triples from sparql endpoint instead of only file
	 */
	public static void main(String[] args){

        try {
    	    // Set up a simple configuration that logs on the console.
    	    BasicConfigurator.configure();
    	    //logger.setLevel(Level.DEBUG);
    	    logger.info("Entering application.");
    
            // turn on the enhanced debugger
            XMPPConnection.DEBUG_ENABLED = true;
        
            // Using a properties file
            
//	    	Configuration config = new PropertiesConfiguration("./xmpppubsub.properties");
//		    // declare variables
//			String username = config.getString("username");
//			String password = config.getString("password");
//			String xmppserver = config.getString("xmppserver");
//			int port = config.getInt("port");
	        
	        Properties prop = new Properties();
	        File file = new File("config/xmpppubsub.properties");
	        String filePath = file.getCanonicalPath();
	        logger.debug(filePath);
	        InputStream is = new FileInputStream(filePath);
	        prop.load(is);
	        String username = prop.getProperty("username");  
            String password = prop.getProperty("password");
            String xmppserver = prop.getProperty("xmppserver");
            int port = Integer.parseInt(prop.getProperty("port")); 
            logger.debug(xmppserver);
            logger.debug(port);
		
			String usage = "Publisher method triples";
			String exampleusage = "insert \"<http://example/book1> dc:title 'A new book' ; dc:creator 'A.N. Other' .\" testNodeWithPayloadU2";
	    	String method = args[0];
	    	String triplesSource = args[1];
	    	String nodeName = args[2];
            logger.debug(triplesSource);
            logger.debug(nodeName);
            logger.debug(args.length);

//            String username = "testuser3";
//            String password = "testuser3pass";
//            String xmppserver = "vmuss12.deri.ie";
//            int port = 5222;
//            String nodeName = "testNodeWithPayloadU2";
//            String method = "insert";
		 
		    Publisher p = new Publisher(username, password, xmppserver, port);
		    
			// Get the node
		    LeafNode node = p.getOrCreateNode(nodeName);
		    
		    
		    //String triples = get_triples(fileName);
	    	//String triples = "<http://example/book1> dc:title 'A new book' ; dc:creator 'A.N. Other' .";
	    	SPARQLQuery query = new SPARQLQuery(method, triplesSource);
	    	
	    	p.sendPayload(node, query);
		    
			logger.info("query sent");
			
			//p.disconnect();
	
		    //System.exit(0);
//	    } catch(ConfigurationException e) {
//		    e.printStackTrace();
//		    logger.debug(e);
//	    	
	    } catch(XMPPException e) {
		    e.printStackTrace();
		    logger.debug(e);
	    	
//	    } catch(IOException  e) {
	    	
	    } catch(Exception e) {
		    e.printStackTrace();
		    logger.debug(e);
	    	
	    }
		
	}
}
