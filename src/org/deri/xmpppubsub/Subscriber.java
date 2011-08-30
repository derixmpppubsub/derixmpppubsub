package org.deri.xmpppubsub;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.Subscription;

/**
 * @author Maciej Dabrowski
 * @author Julia Anaya
 *
 */
public class Subscriber {

    XMPPConnection connection;
    PubSubManager mgr;
    static Logger logger = Logger.getLogger(Subscriber.class);

    /**
     * @param userName
     * @param password
     * @param xmppserver
     * @param port
     * @return void 
     *
     */
    public Subscriber(String userName, String password, String xmppserver, int port) throws XMPPException {
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
    public LeafNode getNode(String nodename) throws XMPPException {
		LeafNode node = (LeafNode) mgr.getNode(nodename);
		logger.info("node got");
		return node;
    }

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		try {
		    BasicConfigurator.configure();
		    //logger.setLevel(Level.DEBUG);
		    logger.info("Entering application.");
		    
		    // declare variables
			String username = "testuser3";
			String password = "testuser3pass";
			String xmppserver = "vmuss12.deri.ie";
			int port = 5222;
			
		    // turn on the enhanced debugger
		    XMPPConnection.DEBUG_ENABLED = true;
		 
		    Subscriber p = new Subscriber(username, password, xmppserver, port);
		    
			// Get the node
		    LeafNode node = p.getNode("testNodeWithPayloadU2");
			
			node.addItemEventListener(new ItemEventCoordinator());
		//	node.subscribe("testuser3@vmuss12.deri.ie");
			
		
	//		DiscoverInfo supportedFeatures = mgr.getSupportedFeatures();
//			System.out.println(supportedFeatures.toXML());
						
			List<? extends Subscription> subs = node.getSubscriptions();
			
			
			
			System.out.println(" subs: " + subs.size());
			for(Subscription sub : subs){
				System.out.println("sub-jid: " + sub.getJid() + " id: " + sub.getId());
				//node.getSubscriptionOptions("testuser2@vmuss12.deri.ie", sub.getId());
				//node.unsubscribe(sub.getJid());
			}
			
			List its = node.getItems(5);
			
			Iterator itr = its.iterator();
			
			while (itr.hasNext()){
				Item it = (Item) itr.next();
				System.out.println(it.toXML());
			}
			
			boolean isRunning = true;

			while (isRunning) {
				Thread.sleep(50);
			}
			
		} catch (XMPPException e) {
			e.printStackTrace();
		}
		
//		xmppManager.setStatus(true, "Hello everyone");
//		xmppManager.destroy();
	}
}