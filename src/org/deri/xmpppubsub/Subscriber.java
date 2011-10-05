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
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Subscription;

/**
 * @author Maciej Dabrowski
 * @author Julia Anaya
 *
 */
public class Subscriber extends PubSubClient {

//    public ArrayList<LeafNode> nodesSubscribedTo;
    public ArrayList<String> nodesSubscribedTo;
    
    public Subscriber(String userName, String password, String xmppserver) 
            throws XMPPException, InterruptedException {
        super(userName, password, xmppserver);
        initNodesSubscribedTo();
    }
    
    public Subscriber(String userName, String password, String xmppserver, 
            int port, boolean createAccountIfNotExist) throws XMPPException, InterruptedException {
        super(userName, password, xmppserver, port, createAccountIfNotExist); 
        initNodesSubscribedTo();          
    }

    public Subscriber(String fileName) throws IOException, XMPPException, InterruptedException {
        super(fileName);
        initNodesSubscribedTo();
    }
    
    public Subscriber(String fileName, boolean createAccountIfNotExist) 
            throws IOException, XMPPException, InterruptedException {
        super(fileName, createAccountIfNotExist);
        initNodesSubscribedTo();
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
//        nodesSubscribedTo = new ArrayList<LeafNode>();

        nodesSubscribedTo = new ArrayList<String>();
        try {
            List<Affiliation> affs = mgr.getAffiliations();
            for(Affiliation aff : affs ) {
                String nodeName = aff.getNodeId();
//                LeafNode node = this.getNode(nodeName);
                logger.debug("jid " + this.getUser() + "is affiliated to node "
                        + nodeName);
//                nodesSubscribedTo.add(node);
                nodesSubscribedTo.add(nodeName);
            }
        } catch (XMPPException e) {
            logger.info("no affiliations");
        }
    }

    public boolean isSubscribedTo(LeafNode node) {
        boolean subscribed;
//        subscribed = nodesSubscribedTo.contains(node);
        subscribed = nodesSubscribedTo.contains(node.getId());
        return subscribed;
    }
    
    public void subscribeIfNotSubscribedTo(LeafNode node) throws XMPPException {
        if (!isSubscribedTo(node)) {
            logger.info("not subscribed");
            logger.info("trying to subscribe " + this.getUser());
            node.subscribe(this.getUser());
//            nodesSubscribedTo.add(node);
            nodesSubscribedTo.add(node.getId());
            logger.info(this.getUser() + " subscribed to node " + node.getId());
        } else {
            logger.info(this.getUser() + " is already subscribed to " + node.getId());
        }
    }
    
    
//    Deprecated
//    public boolean isSubscribed(LeafNode node) 
//            throws XMPPException {
//        logger.debug("isSubscribed");
//        List<? extends Subscription> jidsubs = subscriptionsByJID(node);
//        if (jidsubs.size() > 0) {
//            logger.debug("jid " + userName + "is subscribed to node " 
//                    + node.getId());
//            return true;
//        } else {
//            logger.info("jid " + domain + "is not subscribed to node " 
//                    + node.getId());
//            return false;
//        }
//      }

//  Deprecated
//    public List<? extends Subscription> subscriptionsByJID(LeafNode node) throws XMPPException {
//        logger.info("subscriptionsByJID");
//        ArrayList<Subscription> jidsubs = new ArrayList<Subscription>();
//        List<? extends Subscription> subs = node.getSubscriptions();
//        logger.info("number of subscriptions: " + subs.size() + "to node " 
//                + node.getId());
//        for(Subscription sub : subs){
//            logger.info("Subscription jid " + sub.getUser() 
//                    + " id " + sub.getId());
//            if (sub.getUser().equals(this.getUser())) {
//                logger.info("found subscription for jid " + sub.getUser());
//                jidsubs.add(sub);
//            }
//        }
//        return (List<? extends Subscription>)jidsubs;
//    }

//  Deprecated
//    public void subscribeIfNotSubscribed(LeafNode node) throws XMPPException {
//        logger.info("subscribeIfNotSubscribed");
//        if (!isSubscribed(node)) {
//            node.subscribe(this.getUser());
//            logger.info("jid " + this.getUser() + " subscribed to node ");
//       
//        }
//        // temporal delete extra subscriptions
////        else {
////            List<? extends Subscription> jidsubs = subscriptionsByJID(node, jid, 
////                    xmppserver);
////            for(Subscription sub : jidsubs){
////                if(jidsubs.size()>1) {
////                    node.unsubscribe(sub.getUser(), sub.getId());
////                    jidsubs.remove(sub);
////                    logger.info("deleted subscription jid " + sub.getUser() 
////                            + " id " + sub.getId()+ " to node " + node.getId());
////                }
////            }
////        }
//        
//    }
    
    
    // should not delete subscriptions created by other subscriber-user
    // though node.getSubscriptions seems to return only the subscriptions 
    // created by the subscriber that calls it
//    public void deleteSubscriptions(LeafNode node) throws XMPPException {
//        List<? extends Subscription> subs = node.getSubscriptions();
//        for(Subscription sub : subs){
//            node.unsubscribe(sub.getUser(), sub.getId());
//            logger.info("deleted jid " + sub.getUser() + 
//                    " subscription to node " + node + " with id " + 
//                    sub.getId());
//        }
//    }
    
//    public void deleteSubscriptions(LeafNode node) throws XMPPException {
//        List<? extends Subscription> jidsubs = this.subscriptionsByJID(node);
//        for(Subscription sub : jidsubs){
//            node.unsubscribe(sub.getUser(), sub.getId());
//            logger.info("deleted subscription jid " + sub.getUser() 
//                    + " id " + sub.getId() + " to node " + node.getId());
//        }
//    }
    
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

        
            String usage = "Subscriber node <outputfile>";
            String exampleusage = "testNodeWithPayloadU2";

//        //    String nodeName = args[0];
//          String  nodeName = "twoSubscribers";
//            String outputfile = args[1];
            String  nodeName = "node1";
//            
//            Subscriber p = new Subscriber("subscriber.properties");
            Subscriber p = new Subscriber("sub1", "sub1pass", "vmuss12.deri.ie");
//            Subscriber p3 = new Subscriber("testuser4", "testuser4pass", "vmuss12.deri.ie");
            
            
            // Get the node
            LeafNode node = p.getNode(nodeName);
//            LeafNode node3 = p3.getNode(nodeName);
            

            node.addItemEventListener(new ItemEventCoordinator("sub1", "results.csv"));
            
//            node.subscribe(username + "@vmuss12.deri.ie");
            p.subscribeIfNotSubscribedTo(node);
//            node3.subscribe("testuser4@vmuss12.deri.ie");
//            p3.subscribeIfNotSubscribedTo(node3);
            
            
        
    //        DiscoverInfo supportedFeatures = mgr.getSupportedFeatures();
//            System.out.println(supportedFeatures.toXML());
                        
//            List<? extends Subscription> subs = node.getSubscriptions();
//            
//            
//            
//            System.out.println(" subs: " + subs.size());
//            for(Subscription sub : subs){
//                System.out.println("sub-jid: " + sub.getUser() + " id: " + sub.getId());
//                //node.getSubscriptionOptions("testuser2@vmuss12.deri.ie", sub.getId());
//                //node.unsubscribe(sub.getUser());
//            }
//            
//            List its = node.getItems(1);
//            
//            Iterator itr = its.iterator();
//            
//            while (itr.hasNext()){
//                Item it = (Item) itr.next();
//                System.out.println(it.toString());
//            }
//            
//            boolean isRunning = true;
//
//            while (isRunning) {
//                Thread.sleep(50);
//            }
            
        } catch (XMPPException e) {
            e.printStackTrace();
        
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
//        xmppManager.setStatus(true, "Hello everyone");
//        xmppManager.destroy();
    }
}
