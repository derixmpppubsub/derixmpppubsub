package org.deri.xmpppubsub;
import java.io.IOException;

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

//import com.javacodegeeks.xmpp.XmppManager;

/**
 * @author Maciej Dabrowski
 * @author Julia Anaya
 *
 */
public class Publisher {
    XMPPConnection connection;
    PubSubManager mgr;

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
	    System.out.println("logued in");

		//Create a pubsub manager using an existing Connection
		mgr = new PubSubManager(connection);
		System.out.println("manager created");
    }

    /**
     * @return void 
     *
     */
    public void disconnect() {
    	connection.disconnect();
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
		System.out.println("node created");
    	return leaf;
    }


    /**
     * @return void 
     *
     */
    public LeafNode getNode(String nodename) throws XMPPException {
		LeafNode node = (LeafNode) mgr.getNode(nodename);
		System.out.println("got node");
		return node;
    }


    /**
     * @return void 
     *
     */
    public void send_payload() {
    	
    }
    
	/**
	 * @param args
	 */
	public static void main(String[] args) throws XMPPException, IOException {

	    // declare variables
		String username = "testuser2";
		String password = "testuser2pass";
		String xmppserver = "vmuss12.deri.ie";
		int port = 5222;
	    
	    // turn on the enhanced debugger
	    XMPPConnection.DEBUG_ENABLED = true;
	 
	    Publisher p = new Publisher(username, password, xmppserver, port);
	    
		// Get the node
	    LeafNode node = p.getNode("testNodeWithPayloadU2");
			
		// Create the node
	    //LeafNode node = p.createNode("testNodeWithPayloadU2");
		
		
		node.send(new PayloadItem("test" + System.currentTimeMillis(), 
				new SimplePayload("book2", "pubsub:test:book", "<title>book4</title>")));
		node.send(new PayloadItem("test" + System.currentTimeMillis(), 
				new SimplePayload("book4", "pubsub:test:book", "<title>book6</title>")));
		System.out.println("book send");
		
		//p.disconnect();

	    //System.exit(0);
		
	}
}
