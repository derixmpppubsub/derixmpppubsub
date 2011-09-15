package org.deri.xmpppubsub;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
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
    public Subscriber(String userName, String password, String xmppserver, 
            int port) throws XMPPException {
    	connect(userName, password, xmppserver, port); 
        logger.info("created subscriber for user " + userName);	
    }
    
    /**
     * @param userName
     * @param password
     * @param xmppserver
     * @param port
     * @return void 
     *
     */
    public void connect(String userName, String password, String xmppserver, 
            int port) throws XMPPException {
	    ConnectionConfiguration config = 
	        new ConnectionConfiguration(xmppserver,port);
	    connection = new XMPPConnection(config);
	    connection.connect();
	    connection.login(userName, password);
        logger.info("User " + userName + " logged in to the server " 
                + xmppserver);

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
		logger.info("got node "+ nodename);
		return node;
    }
    
    public boolean isSubscribed(LeafNode node, String jid, String xmppserver) 
            throws XMPPException {
        logger.debug("isSubscribed");
        List<? extends Subscription> jidsubs = subscriptionsByJID(node, jid, 
                xmppserver);
        if (jidsubs.size() > 0) {
            logger.debug("jid " + jid + "is subscribed to node " 
                    + node.getId());
            return true;
        } else {
            logger.debug("jid " + jid + "is not subscribed to node " 
                    + node.getId());
            return false;
        }
    }
    
    
    public List<? extends Subscription> subscriptionsByJID(LeafNode node, String jid, 
            String xmppserver) throws XMPPException {
        logger.debug("subscriptionsByJID");
        ArrayList<Subscription> jidsubs = new ArrayList<Subscription>();
        List<? extends Subscription> subs = node.getSubscriptions();
        logger.debug("number of subscriptions: " + subs.size() + "to node " 
                + node.getId());
        for(Subscription sub : subs){
            logger.debug("Subscription jid " + sub.getJid() 
                    + " id " + sub.getId());
            if (sub.getJid().equals(jid+"@"+xmppserver)) {
                logger.debug("found subscription for jid " + sub.getJid());
                jidsubs.add(sub);
            }
        }
        return (List<? extends Subscription>)jidsubs;
    }

    public void subscribeIfNotSubscribed(LeafNode node, String jid, 
            String xmppserver) throws XMPPException {
        logger.debug("subscribeIfNotSubscribed");
        if (!isSubscribed(node, jid, xmppserver)) {
            node.subscribe(jid+"@"+xmppserver);
            logger.info("jid " + jid+"@"+xmppserver + " subscribed to node ");
       
        }
        // temporal delete extra subscriptions
        else {
            List<? extends Subscription> jidsubs = subscriptionsByJID(node, jid, 
                    xmppserver);
            for(Subscription sub : jidsubs){
                if(jidsubs.size()>1) {
                    node.unsubscribe(sub.getJid(), sub.getId());
                    jidsubs.remove(sub);
                    logger.debug("deleted subscription jid " + sub.getJid() 
                            + " id " + sub.getId()+ " to node " + node.getId());
                }
            }
        }
        
    }
    
    // should not delete subscriptions created by other subscriber-user
//    public void deleteSubscriptions(LeafNode node) throws XMPPException {
//        List<? extends Subscription> subs = node.getSubscriptions();
//        for(Subscription sub : subs){
//            node.unsubscribe(sub.getJid(), sub.getId());
//            logger.info("deleted jid " + sub.getJid() + 
//                    " subscription to node " + node + " with id " + 
//                    sub.getId());
//        }
//    }
    
    public void deleteSubscriptions(LeafNode node, String jid, 
            String xmppserver) throws XMPPException {

        List<? extends Subscription> jidsubs = subscriptionsByJID(node, jid, 
                xmppserver);
        for(Subscription sub : jidsubs){
            node.unsubscribe(sub.getJid(), sub.getId());
            logger.debug("deleted subscription jid " + sub.getJid() 
                    + " id " + sub.getId() + " to node " + node.getId());
        }
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
			
		    // turn on the enhanced debugger
		    XMPPConnection.DEBUG_ENABLED = true;

            Properties prop = new Properties();
            File file = new File("subscriber.properties");
            String filePath = file.getCanonicalPath();
            InputStream is = new FileInputStream(filePath);
            prop.load(is);
            String username = prop.getProperty("username");  
            String password = prop.getProperty("password");
            String xmppserver = prop.getProperty("xmppserver");
            int port = Integer.parseInt(prop.getProperty("port")); 
            logger.debug(xmppserver);
            logger.debug(port);
        
            String usage = "Subscriber node <outputfile>";
            String exampleusage = "testNodeWithPayloadU2";

        //    String nodeName = args[0];
          String  nodeName = "twoSubscribers";
//            String outputfile = args[1];
            
		    Subscriber p = new Subscriber(username, password, xmppserver, port);
		    Subscriber p3 = new Subscriber("testuser4", "testuser4pass", xmppserver, port);
		    
		    
			// Get the node
		    LeafNode node = p.getNode(nodeName);
		    LeafNode node3 = p3.getNode(nodeName);
			
//		    node.subscribe(username + "@vmuss12.deri.ie");
            p.subscribeIfNotSubscribed(node, username, xmppserver);
//		    node3.subscribe("testuser4@vmuss12.deri.ie");
            p3.subscribeIfNotSubscribed(node3, "testuser4", xmppserver);
		    
		    // add item event listener
			node.addItemEventListener(new ItemEventCoordinator());
			node3.addItemEventListener(new ItemEventCoordinator());
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
			
			List its = node.getItems(1);
			
			Iterator itr = its.iterator();
			
			while (itr.hasNext()){
				Item it = (Item) itr.next();
				System.out.println(it.toString());
			}
			
			boolean isRunning = true;

			while (isRunning) {
				Thread.sleep(50);
			}
			
		} catch (XMPPException e) {
			e.printStackTrace();
        
        } catch(IOException e) {
            e.printStackTrace();
            logger.debug(e);
        }
//        } catch (ExtractionException e) {
//            e.printStackTrace();
//            logger.debug(e);
//        }
		
//		xmppManager.setStatus(true, "Hello everyone");
//		xmppManager.destroy();
	}
}