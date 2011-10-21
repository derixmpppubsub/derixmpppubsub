package org.deri.xmpppubsub;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.Affiliation;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Subscription;

/**
 * @author Maciej Dabrowski
 * @author Julia Anaya
 *
 */
public class Subscriber extends PubSubClient {
    public HashMap<String, LeafNode> nodeSubscriptions;

    /**
     *
     * @param userName
     * @param password
     * @param xmppserver
     * @throws XMPPException
     * @throws InterruptedException
     */
    public Subscriber(String userName, String password, String xmppserver)
            throws XMPPException, InterruptedException {
        super(userName, password, xmppserver);
        initNodesSubscribedTo();
//        logger.debug("no initialization");
    }

    /**
     *
     * @param userName
     * @param password
     * @param xmppserver
     * @param port
     * @param createAccountIfNotExist
     * @throws XMPPException
     * @throws InterruptedException
     */
    public Subscriber(String userName, String password, String xmppserver,
            int port, boolean createAccountIfNotExist) throws XMPPException, InterruptedException {
        super(userName, password, xmppserver, port, createAccountIfNotExist);
        initNodesSubscribedTo();
    }

    /**
     *
     * @param fileName
     * @throws IOException
     * @throws XMPPException
     * @throws InterruptedException
     */
    public Subscriber(String fileName) throws IOException, XMPPException, InterruptedException {
        super(fileName);
        initNodesSubscribedTo();
    }

    /**
     *
     * @param fileName
     * @param createAccountIfNotExist
     * @throws IOException
     * @throws XMPPException
     * @throws InterruptedException
     */
    public Subscriber(String fileName, boolean createAccountIfNotExist)
            throws IOException, XMPPException, InterruptedException {
        super(fileName, createAccountIfNotExist);
        initNodesSubscribedTo();
    }

    /**
     *
     * @throws XMPPException
     */
    public void initNodesSubscribedTo() throws XMPPException {
        nodeSubscriptions = new HashMap<String, LeafNode>();
        try {
            List<Affiliation> affs = mgr.getAffiliations();
            for(Affiliation aff : affs ) {
                String nodeName = aff.getNodeId();
//                logger.debug(this.getUser() + "is affiliated to node "
//                        + nodeName);
                LeafNode node = this.getNode(nodeName);
                nodeSubscriptions.put(nodeName, node);
//                nodesSubscribedTo.add(nodeName);
            }
        } catch (XMPPException e) {
            logger.debug("no affiliations");
        }
    }

    /**
     *
     * @param nodeName
     * @return
     */
    public boolean isSubscribedTo(String nodeName) {
        boolean subscribed;
//        subscribed = nodesSubscribedTo.contains(node);
        subscribed = nodeSubscriptions.containsKey(nodeName);
        return subscribed;
    }

    /**
     *
     * @param nodeName
     * @throws XMPPException
     */
    public void subscribeTo(String nodeName) throws XMPPException {
        LeafNode node = this.getNode(nodeName);
        node.subscribe(this.getUser());
//            nodes.add(node);
//            nodesSubscribedTo.add(node.getId());
        nodeSubscriptions.put(nodeName, node);
        logger.debug(this.getUser() + " subscribed to node " + node.getId());
    }

    public void getOrCreateSubscription(String nodeName) throws XMPPException {
        Subscription s = new Subscription(this.getUser(), nodeName);
        if (mgr.getSubscriptions().contains(s)) {
            //never enter here but subscribe twice
            logger.debug("subsription to node " + s.getNode() + " by " + s.getJid()
                    +  " with id " + s.getId() + " already exists");
        } else {
            this.getNode(nodeName).subscribe(this.getUser());
            logger.debug(this.getUser() + " subscribed to node " + nodeName);
        }

    }

    /**
     *
     * @param nodeName
     * @throws XMPPException
     */
    public void subscribeIfNotSubscribedTo(String nodeName) throws XMPPException {
        if (!isSubscribedTo(nodeName)) {
            subscribeTo(nodeName);
        } else {
//            logger.debug(this.getUser() + " is already subscribed to " + nodeName);
        }
    }

    public void addListenerToNode(String subSeq,
            String nodeName, String fileName, String endpoint)
            throws XMPPException, IOException {
        nodeSubscriptions.get(nodeName).addItemEventListener(new ItemEventCoordinator(subSeq));
//        LeafNode node = this.getNode(nodeName);
//        node.addItemEventListener(new ItemEventCoordinator(subSeq, fileName, endpoint));
    }
//    /**
//     *
//     * @param subSeq
//     * @param nodeName
//     * @param fileName
//     * @param endpoint
//     * @throws XMPPException
//     * @throws IOException
//     */
//    public void addListenerToNode(String subSeq,
//            String nodeName, String fileName, String endpoint)
//            throws XMPPException, IOException {
//        nodeSubscriptions.get(nodeName).addItemEventListener(new ItemEventCoordinator(subSeq, fileName, endpoint));
////        LeafNode node = this.getNode(nodeName);
////        node.addItemEventListener(new ItemEventCoordinator(subSeq, fileName, endpoint));
//    }

    public void addListenerToAllNodes(String subSeq)
            throws XMPPException, IOException {
        for(LeafNode node : nodeSubscriptions.values()) {
            // to see the thread stack when the mem error is due to the stack
            try {
                node.addItemEventListener(new ItemEventCoordinator(subSeq));
            } catch(OutOfMemoryError e) {
                logger.error(e.getMessage());
                Thread.getAllStackTraces();
                Thread.dumpStack();
//                 Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//                     public void uncaughtException( final Thread t, final Throwable e ) {
//                     }
//                 });
            }
        }
    }

//    /**
//     *
//     * @param subSeq
//     * @param fileName
//     * @param endpoint
//     * @throws XMPPException
//     * @throws IOException
//     */
//    public void addListenerToAllNodes(String subSeq, String fileName, String endpoint)
//            throws XMPPException, IOException {
//        for(LeafNode node : nodeSubscriptions.values()) {
//            node.addItemEventListener(new ItemEventCoordinator(subSeq, fileName, endpoint));
//        }
//    }


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
//            logger.debug("jid " + domain + "is not subscribed to node "
//                    + node.getId());
//            return false;
//        }
//      }

//  Deprecated
//    public List<? extends Subscription> subscriptionsByJID(LeafNode node) throws XMPPException {
//        logger.debug("subscriptionsByJID");
//        ArrayList<Subscription> jidsubs = new ArrayList<Subscription>();
//        List<? extends Subscription> subs = node.getSubscriptions();
//        logger.debug("number of subscriptions: " + subs.size() + "to node "
//                + node.getId());
//        for(Subscription sub : subs){
//            logger.debug("Subscription jid " + sub.getUser()
//                    + " id " + sub.getId());
//            if (sub.getUser().equals(this.getUser())) {
//                logger.debug("found subscription for jid " + sub.getUser());
//                jidsubs.add(sub);
//            }
//        }
//        return (List<? extends Subscription>)jidsubs;
//    }

//  Deprecated
//    public void subscribeIfNotSubscribed(LeafNode node) throws XMPPException {
//        logger.debug("subscribeIfNotSubscribed");
//        if (!isSubscribed(node)) {
//            node.subscribe(this.getUser());
//            logger.debug("jid " + this.getUser() + " subscribed to node ");
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
////                    logger.debug("deleted subscription jid " + sub.getUser()
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
//            logger.debug("deleted jid " + sub.getUser() +
//                    " subscription to node " + node + " with id " +
//                    sub.getId());
//        }
//    }

//    public void deleteSubscriptions(LeafNode node) throws XMPPException {
//        List<? extends Subscription> jidsubs = this.subscriptionsByJID(node);
//        for(Subscription sub : jidsubs){
//            node.unsubscribe(sub.getUser(), sub.getId());
//            logger.debug("deleted subscription jid " + sub.getUser()
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
            logger.setLevel(Level.DEBUG);
            logger.debug("Entering application.");

//            XMPPConnection.DEBUG_ENABLED = true;

//            String fileName = "results.csv";
//            String endpoint = "http://localhost:8000/update/";
//            String  nodeName = "node1";
//            Subscriber p = new Subscriber("subscriber.properties");
            Subscriber s = new Subscriber("sub1", "pass", args[0]);
//            s.getOrCreateSubscription("node1");
            List<Affiliation> affs = s.mgr.getAffiliations();
            for(Affiliation aff : affs ) {
                String nodeName = aff.getNodeId();
//                Affiliation.Type = aff.getType();
                logger.debug(s.getUser() + "is affiliated to node "
                        + nodeName);
            }
            List<Subscription> subs = s.mgr.getSubscriptions();
            for(Subscription sub: subs) {
                String jid = sub.getJid();
                String id = sub.getId();
                String nodeNam = sub.getNode();
//                Subscription.State = sub.getState();
                logger.debug(jid + "has subscription" + id + "to node" + nodeNam);
            }
            //5950 [main] DEBUG org.deri.xmpppubsub.PubSubClient  - sub1@127.0.0.1/Smack subscribed to node node1
            //5957 [main] DEBUG org.deri.xmpppubsub.PubSubClient  - sub1@127.0.0.1/Smackis affiliated to node node1
            //5967 [main] DEBUG org.deri.xmpppubsub.PubSubClient  - sub1@127.0.0.1/Smackhas subscription4DhqDi3jBxqNWLbjw2xLm7CO6r9CXq0hs2zRRA3Pto nodenode1
            //5967 [main] DEBUG org.deri.xmpppubsub.PubSubClient  - sub1@127.0.0.1/Smackhas subscriptionhJY0J9v87F7ki6R3T8NVo0ebTU59FqVoIM0m3N9Xto nodenode1


//            p.addListenerToNode("sub1of1", nodeName, fileName, endpoint);
//            p.subscribeIfNotSubscribedTo(nodeName);


//        } catch (IOException e) {
//            logger.error(e);
        } catch (XMPPException e) {
            logger.error(e);
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }
}
