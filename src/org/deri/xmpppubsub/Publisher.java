package org.deri.xmpppubsub;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
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

    public LeafNode node;

    /**
     *
     * @param userName
     * @param password
     * @param xmppserver
     * @throws XMPPException
     * @throws InterruptedException
     */
    public Publisher(String userName, String password, String xmppserver)
            throws XMPPException, InterruptedException {
        super(userName, password, xmppserver);
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
    public Publisher(String userName, String password, String xmppserver,
            int port, boolean createAccountIfNotExist) throws XMPPException,
            InterruptedException {
        super(userName, password, xmppserver, port, createAccountIfNotExist);
    }

    /**
     *
     * @param fileName
     * @throws IOException
     * @throws XMPPException
     * @throws InterruptedException
     */
    public Publisher(String fileName) throws IOException, XMPPException, InterruptedException {
        super(fileName);
    }

    /**
     *
     * @param fileName
     * @param createAccountIfNotExist
     * @throws IOException
     * @throws XMPPException
     * @throws InterruptedException
     */
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
        //logger.debug(form.getMaxPayloadSize);
        node = (LeafNode) mgr.createNode(nodename, form);
        logger.debug("node " + nodename  + " created");
        return node;
    }

    @Override
    public LeafNode getNode(String nodename) throws XMPPException {
        node = super.getNode(nodename);
        return node;
    }

    /**
     *
     * @param nodename
     * @return
     * @throws XMPPException
     */
    public LeafNode getOrCreateNode(String nodename) throws XMPPException {
        try {
            node = this.getNode(nodename);
        } catch (Exception e){
            node = this.createNode(nodename);
        }
        return node;
    }


    public void publishQuery(String query) throws XMPPException {
            this.publishQuery(query, connection.getUser());
    }

    /**
     *
     * @param query
     * @param msgId
     * @throws XMPPException
     */
    public void publishQuery(String query, String msgId) throws XMPPException {
        //String itemID = connection.getUser() + System.nanoTime();
        SimplePayload payloadNS = new SimplePayload(
          "query", "http://www.w3.org/TR/sparql11-update/", query);
        PayloadItem<SimplePayload> item = new PayloadItem<SimplePayload>(
          msgId + "," + System.currentTimeMillis(), payloadNS);
        //logger.debug(item.toString());
        try {
            node.send(item);
        } catch(XMPPException e) {
            logger.error("server timeout?");
            logger.error(e.getMessage());
        }
//        logger.debug("item sent");
    }


    /**
     * @param args
     */
    public static void main(String[] args){
        try {
            BasicConfigurator.configure();

            logger.setLevel(Level.DEBUG);
            logger.debug("Entering application.");

            XMPPConnection.DEBUG_ENABLED = true;

            String confFileName = "xmpppubsub.properties";
            Publisher p = new Publisher(confFileName);
            String nodeName = "node1";
            p.getOrCreateNode(nodeName);

            String triples = "<http://example/book1> dc:title 'A new book' ; dc:creator 'A.N. Other' .";

            SPARQLQuery query = new SPARQLQuery();
            query.wrapTriples(triples);
            logger.debug(query.toXML());
            p.publishQuery(query.toXML(), p.getUser());
            logger.debug("query sent");

            //p.disconnect();

            //System.exit(0);

        } catch(XMPPException e) {
            logger.error(e);
        } catch(IOException e) {
            logger.error(e);
        } catch (QueryTypeException e) {
            logger.error(e);
        } catch (InterruptedException e) {
            logger.error(e);
        }

    }
}
