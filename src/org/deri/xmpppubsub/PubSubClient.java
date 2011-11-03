package org.deri.xmpppubsub;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;

/**
 * @author Julia Anaya
 *
 */
public class PubSubClient {

    protected XMPPConnection connection;
    protected PubSubManager pubSubMgr;

	//    public LeafNode node;
    protected static Logger logger = Logger.getLogger(PubSubClient.class);

    /**
     *
     * @param userName
     * @param password
     * @param xmppserver
     * @throws XMPPException
     * @throws InterruptedException
     */
    public PubSubClient(String userName, String password, String xmppserver)
            throws XMPPException, InterruptedException {
        this(userName, password, xmppserver, 5222, true);
    }

    /**
     *
     * @param userName
     * @param password
     * @param xmppserver
     * @param createAccountIfNotExist
     * @throws XMPPException
     * @throws InterruptedException
     */
    public PubSubClient(String userName, String password, String xmppserver,
            boolean createAccountIfNotExist) throws XMPPException,
            InterruptedException {
        this(userName, password, xmppserver, 5222, createAccountIfNotExist);
    }

    /**
     *
     * @param fileName
     * @throws IOException
     * @throws XMPPException
     * @throws InterruptedException
     */
    public PubSubClient(String fileName) throws IOException, XMPPException,
            InterruptedException {
        this(fileName, true);
    }

    /**
     *
     * @param fileName
     * @param createAccountIfNotExist
     * @throws IOException
     * @throws XMPPException
     * @throws InterruptedException
     */
    public PubSubClient(String fileName, boolean createAccountIfNotExist)
            throws IOException, XMPPException, InterruptedException {
            // the file path was not correct
            Properties prop = new Properties();
            File file = new File(fileName);
            String filePath = file.getCanonicalPath();
            logger.debug(filePath);
            InputStream is = new FileInputStream(filePath);
            prop.load(is);
            String userName = prop.getProperty("username");
            String password = prop.getProperty("password");
            String xmppserver = prop.getProperty("xmppserver");
            int port = Integer.parseInt(prop.getProperty("port"));
            is.close();
            this.init(userName, password, xmppserver, port, createAccountIfNotExist);
    }

    /**
     *
     * @param userName
     * @param password
     * @param domain
     * @param port
     * @param createAccountIfNotExist
     * @throws XMPPException
     * @throws InterruptedException
     */
    public PubSubClient(String userName, String password, String domain,
            int port, boolean createAccountIfNotExist) throws XMPPException,
            InterruptedException {
        this.init(userName, password, domain, port, createAccountIfNotExist);
    }

    /**
     *
     * @param userName
     * @param password
     * @param domain
     * @param port
     * @param createAccountIfNotExist
     * @throws XMPPException
     * @throws InterruptedException
     */
    public void init(String userName, String password, String domain,
            int port, boolean createAccountIfNotExist) throws XMPPException,
            InterruptedException {
        SmackConfiguration.setPacketReplyTimeout(60000);
        
        ConnectionConfiguration config = new ConnectionConfiguration(domain
                ,port);
        connection = new XMPPConnection(config);
        connection.connect();


        try {
            connection.getAccountManager().createAccount(userName, password);
            logger.info("User " + userName + " created "
                    + domain);
        } catch(XMPPException e) {
            //logger.info("User " + userName + " already created ");
        }
        try {
            connection.login(userName, password);
            logger.info("User " + connection.getUser() + " login ");
        } catch(IllegalStateException e) {
            logger.info("User " + connection.getUser() + " already login ");
        }

        pubSubMgr = new PubSubManager(connection);
//        logger.info("PubSub manager created");
    }

    /**
     * @return void
     *
     */
    public void disconnect() {
        try {
            connection.disconnect();
            logger.info("disconected");
        } catch(java.lang.IllegalStateException e) {
            logger.error(e);
        }
    }

    /**
     * get full ID of the user that is logged in
     * @return user or null (when not logged in)
     */
    public String getUser() {
        return connection.getUser();
    }

    /**
     *
     * @param nodename
     * @return
     * @throws XMPPException
     */
    public LeafNode getNode(String nodename) throws XMPPException {
        LeafNode node = (LeafNode) pubSubMgr.getNode(nodename);
//        logger.info("got node " + nodename);
        return node;
    }
    
    /**
    * getter for the PubSubManager
    *
    * @return PubSubManager
    */
    public PubSubManager getPubSubMgr() {
		return pubSubMgr;
	}

    /**
    * setter for the PubSubManager
    * 
    * @param PubSubManager instance
    * @throws XMPPException
    */
	public void setPubSubMgr(PubSubManager pubSubMgr) {
		this.pubSubMgr = pubSubMgr;
	}
}
