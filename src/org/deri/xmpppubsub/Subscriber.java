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
import org.jivesoftware.smackx.pubsub.Affiliation;
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
    public String userName;
    public String password;
    public String domain;
    public int port;
    public ArrayList<LeafNode> nodesSubscribedTo;
    static Logger logger = Logger.getLogger(Subscriber.class);


    /**
     * @param userName
     * @param password
     * @param xmppserver
     * @param port
     * @return void 
     *
     */

    public Subscriber(String userName, String password, String xmppserver) throws XMPPException {
        this(userName, password, xmppserver, 5222);
    }
    
    public Subscriber(String userName, String password, String xmppserver, int port) throws XMPPException {
        this.userName = userName;
        this.password = password;
        this.domain = xmppserver;
        this.port = port;
        this.initPubSub();   
        this.initNodesSubscribedTo();
    }

    
    /**
     * @param userName
     * @param password
     * @param xmppserver
     * @param port
     * @return void 
     *
     */
    public void initPubSub() throws XMPPException {
	    ConnectionConfiguration config = 
	        new ConnectionConfiguration(domain,port);
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
    

    public LeafNode getNode(String nodename) throws XMPPException {
        LeafNode node = (LeafNode) mgr.getNode(nodename);
        logger.info("node" + nodename  + "got");
        return node;
    }

    
    public String getJid() {
        String jid = userName + "@" + domain;
        return jid;
    }

//  public ArrayList<LeafNode> getNodesSubscribedTo() throws XMPPException {
//      List<Affiliation> affs = mgr.getAffiliations();
//      for(Affiliation aff : affs ) {
//          String nodeName = aff.getNodeId();
//          LeafNode node = this.getNode(nodeName);
//          nodesSubscribedTo.add(node);
//      }
//      return nodesSubscribedTo;
//  }
    
    public void initNodesSubscribedTo() throws XMPPException {
        nodesSubscribedTo = new ArrayList<LeafNode>();
        List<Affiliation> affs = mgr.getAffiliations();
        for(Affiliation aff : affs ) {
            String nodeName = aff.getNodeId();
            LeafNode node = this.getNode(nodeName);
            logger.debug("jid " + this.getJid() + "is affiliated to node "
                    + nodeName);
            nodesSubscribedTo.add(node);
        }
    }

    public boolean isSubscribedTo(LeafNode node) {
        boolean subscribed;
        subscribed = nodesSubscribedTo.contains(node);
        logger.debug(this.getJid() + " is subscribed to " + node.getId());
        return subscribed;
    }
    
    public void subscribeIfNotSubscribedTo(LeafNode node) throws XMPPException {
        if (!isSubscribedTo(node)) {
            node.subscribe(this.getJid());
            nodesSubscribedTo.add(node);
            logger.info("jid " + this.getJid() + " subscribed to node " 
                    + node.getId());
        }
    }
    
//    Deprecated
    public boolean isSubscribed(LeafNode node) 
            throws XMPPException {
        logger.debug("isSubscribed");
        List<? extends Subscription> jidsubs = subscriptionsByJID(node);
        if (jidsubs.size() > 0) {
            logger.debug("jid " + userName + "is subscribed to node " 
                    + node.getId());
            return true;
        } else {
            logger.debug("jid " + domain + "is not subscribed to node " 
                    + node.getId());
            return false;
        }
      }

//  Deprecated
    public List<? extends Subscription> subscriptionsByJID(LeafNode node) throws XMPPException {
        logger.debug("subscriptionsByJID");
        ArrayList<Subscription> jidsubs = new ArrayList<Subscription>();
        List<? extends Subscription> subs = node.getSubscriptions();
        logger.debug("number of subscriptions: " + subs.size() + "to node " 
                + node.getId());
        for(Subscription sub : subs){
            logger.debug("Subscription jid " + sub.getJid() 
                    + " id " + sub.getId());
            if (sub.getJid().equals(this.getJid())) {
                logger.debug("found subscription for jid " + sub.getJid());
                jidsubs.add(sub);
            }
        }
        return (List<? extends Subscription>)jidsubs;
    }

//  Deprecated
    public void subscribeIfNotSubscribed(LeafNode node) throws XMPPException {
        logger.debug("subscribeIfNotSubscribed");
        if (!isSubscribed(node)) {
            node.subscribe(this.getJid());
            logger.info("jid " + this.getJid() + " subscribed to node ");
       
        }
        // temporal delete extra subscriptions
//        else {
//            List<? extends Subscription> jidsubs = subscriptionsByJID(node, jid, 
//                    xmppserver);
//            for(Subscription sub : jidsubs){
//                if(jidsubs.size()>1) {
//                    node.unsubscribe(sub.getJid(), sub.getId());
//                    jidsubs.remove(sub);
//                    logger.debug("deleted subscription jid " + sub.getJid() 
//                            + " id " + sub.getId()+ " to node " + node.getId());
//                }
//            }
//        }
        
    }
    
    // should not delete subscriptions created by other subscriber-user
    // though node.getSubscriptions seems to return only the subscriptions 
    // created by the subscriber that calls it
//    public void deleteSubscriptions(LeafNode node) throws XMPPException {
//        List<? extends Subscription> subs = node.getSubscriptions();
//        for(Subscription sub : subs){
//            node.unsubscribe(sub.getJid(), sub.getId());
//            logger.info("deleted jid " + sub.getJid() + 
//                    " subscription to node " + node + " with id " + 
//                    sub.getId());
//        }
//    }
    
    public void deleteSubscriptions(LeafNode node) throws XMPPException {

        List<? extends Subscription> jidsubs = this.subscriptionsByJID(node);
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
			

		    node.addItemEventListener(new ItemEventCoordinator());
		    
//		    node.subscribe(username + "@vmuss12.deri.ie");
            p.subscribeIfNotSubscribedTo(node);
//		    node3.subscribe("testuser4@vmuss12.deri.ie");
            p3.subscribeIfNotSubscribedTo(node3);
		    
			
		
	//		DiscoverInfo supportedFeatures = mgr.getSupportedFeatures();
//			System.out.println(supportedFeatures.toXML());
						
//			List<? extends Subscription> subs = node.getSubscriptions();
//			
//			
//			
//			System.out.println(" subs: " + subs.size());
//			for(Subscription sub : subs){
//				System.out.println("sub-jid: " + sub.getJid() + " id: " + sub.getId());
//				//node.getSubscriptionOptions("testuser2@vmuss12.deri.ie", sub.getId());
//				//node.unsubscribe(sub.getJid());
//			}
//			
//			List its = node.getItems(1);
//			
//			Iterator itr = its.iterator();
//			
//			while (itr.hasNext()){
//				Item it = (Item) itr.next();
//				System.out.println(it.toString());
//			}
//			
//			boolean isRunning = true;
//
//			while (isRunning) {
//				Thread.sleep(50);
//			}
			
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