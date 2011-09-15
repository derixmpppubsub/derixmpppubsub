package org.deri.xmpppubsub;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.deri.any23.extractor.ExtractionException;
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

/**
 * @author Maciej Dabrowski
 * @author Julia Anaya
 *
 */
public class Publisher {
    
	private XMPPConnection connection;
    public PubSubManager mgr;
    public String userName;
    public String password;
    public String domain;
    public int port;
    public LeafNode node;
    
    static Logger logger = Logger.getLogger(Publisher.class);
    
    /**
     * @param userName
     * @param password
     * @param xmppserver
     * @param port
     * @return void 
     *
     */

    public Publisher(String userName, String password, String xmppserver) throws XMPPException {
        this(userName, password, xmppserver, 5222);
    }
    
    public Publisher(String userName, String password, String xmppserver, int port) throws XMPPException {
    	this.userName = userName;
        this.password = password;
        this.domain = xmppserver;
        this.port = port;
        initPubSub(); 	
    }

    /**
     * Constructor with the use of the default port (for simplicity)
     * 
     * @param userName
     * @param password
     * @param xmppServer
     * @throws XMPPException
     */
//    public Publisher(String userName, String password, String xmppServer) throws XMPPException {
//        Publisher(userName, password, xmppServer, defaultXmppPort);
//    }
    
    /**
     * @param userName
     * @param password
     * @param xmppserver
     * @param port
     * @return void 
     *
     */
    public void initPubSub() throws XMPPException {
    	ConnectionConfiguration config = new ConnectionConfiguration(domain,port);
	    connection = new XMPPConnection(config);
	    connection.connect();
	    connection.login(userName, password);
	    logger.info("User " + userName + " logged in to the server " 
	            + domain);
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
     * get full ID of the user that is logged in
     * @return user or null (when not logged in)
     */
    public String getUser(){
       	return connection.getUser();	
    }

    /**
     * The method created a node with a given name
     * @param nodename - name of the node to be created
     * @return LeafNode that was created
     * @throws XMPPException
     */
    public LeafNode createNode(String nodename) throws XMPPException {
		ConfigureForm form = new ConfigureForm(FormType.submit);
		form.setAccessModel(AccessModel.open);
		form.setDeliverPayloads(true);
		form.setNotifyRetract(true);
		form.setPersistentItems(true);
		form.setPublishModel(PublishModel.open);
		node = (LeafNode) mgr.createNode(nodename, form);
		logger.info("node " + nodename  + " created");
    	return node;
    }

    public LeafNode getNode(String nodename) throws XMPPException {
		node = (LeafNode) mgr.getNode(nodename);
		logger.info("node" + nodename  + "got");
		return node;
    }

    /**
     * @return void 
     *
     */
    public LeafNode getOrCreateNode(String nodename) throws XMPPException {
    	try {
    		node = this.getNode(nodename);
    	} catch (Exception e){
    		node = this.createNode(nodename);
    	}
		return node;
    }


    /**
     * @return void 
     *
     */
    public void publishQuery(String query) throws XMPPException {
    	String itemID = connection.getUser() + System.currentTimeMillis();
	    SimplePayload payloadNS = new SimplePayload("query", "http://www.w3.org/TR/sparql11-update/", query);
	    PayloadItem<SimplePayload> item = new PayloadItem<SimplePayload>(itemID, payloadNS);
	    node.send(item);
    }
    
    
	/**
	 * @param args
	 */
	public static void main(String[] args){

		//TODO when to disconnect
		//TODO separate xmpp login in other class?, inherit publisher and subscriber from a common class that has the methods connect and getNode?
		//TODO extend method to get triples from sparql endpoint instead of only file? [this is not needed for the component, possible for the evaluation]
		
		
		
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
	        File file = new File("xmpppubsub.properties");
	        String filePath = file.getCanonicalPath();
	        logger.debug(filePath);
	        InputStream is = new FileInputStream(filePath);
	        prop.load(is);
	        String username = prop.getProperty("username");  
            String password = prop.getProperty("password");
            String xmppserver = prop.getProperty("xmppserver");
            int port = Integer.parseInt(prop.getProperty("port")); 
		
			String usage = "Publisher method triples";
			//String triples = "<http://example/book1> dc:title 'A new book' ; dc:creator 'A.N. Other' .";
			String triples = "test dupa dupa2";
//            String exampleusage = "insert example.ttl testNodeWithPayloadU2";

	    	String method = "insert";
	    	
	    	//String triplesSource = args[1];
	    	String nodeName = "twoSubscribers";
            logger.debug(method);
            //logger.debug(triplesSource);
            logger.debug(nodeName);
            logger.debug(args.length);
		 
		    Publisher p = new Publisher(username, password, xmppserver, port);
	//	    p.mgr.deleteNode("twoSubscribers");		    
			// Get the node
		    p.getOrCreateNode(nodeName);
		    
		    
		    //String triples = get_triples(fileName);
	    	//String triples = "<http://example/book1> dc:title 'A new book' ; dc:creator 'A.N. Other' .";
	    	//SPARQLQuery query = new SPARQLQuery(SPARQLQueryType.valueOf(method.toUpperCase()), triplesSource);
		    SPARQLQuery query = new SPARQLQuery(triples);
	    	//logger.debug(query.toXML());
	    	//p.publish(node, query.toXML());
		    logger.debug(query.toXMLDecodingEntitiesCDATA());
		    p.publishQuery(query.toXMLDecodingEntitiesCDATA());
		    
			logger.info("query sent");
			
			//p.disconnect();
	
		    //System.exit(0);
			
	    } catch(XMPPException e) {
		    e.printStackTrace();
		    logger.debug(e);
	    	
	    } catch(IOException e) {
		    e.printStackTrace();
		    logger.debug(e);
	    	
	    } catch (ExtractionException e) {
            e.printStackTrace();
            logger.debug(e);
        } catch (QueryTypeException e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
		
	}
}
