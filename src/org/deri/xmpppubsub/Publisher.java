package org.deri.xmpppubsub;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.deri.any23.extractor.ExtractionException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.FormType;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;

/**
 * @author Maciej Dabrowski
 * @author Julia Anaya
 *
 */
public class Publisher extends PubSubClient {

    public Publisher(String userName, String password, String xmppserver) 
            throws XMPPException, InterruptedException {
        super(userName, password, xmppserver);
    }
    
    public Publisher(String userName, String password, String xmppserver, 
            int port, boolean createAccountIfNotExist) throws XMPPException, InterruptedException {
        super(userName, password, xmppserver, port, createAccountIfNotExist);           
    }

    public Publisher(String fileName) throws IOException, XMPPException, InterruptedException {
        super(fileName);
    }
    
    public Publisher(String fileName, boolean createAccountIfNotExist) 
            throws IOException, XMPPException, InterruptedException {
        super(fileName, createAccountIfNotExist);
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
        
            String confFileName = "xmpppubsub.properties"; 
		
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
		 
		    Publisher p = new Publisher(confFileName);
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
		} catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}
}
