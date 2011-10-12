package org.deri.xmpppubsub;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.ConnectionConfiguration;
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
    public PubSubManager mgr;
//    public LeafNode node;
    protected static Logger logger = Logger.getLogger(PubSubClient.class);

    public PubSubClient(String userName, String password, String xmppserver)
            throws XMPPException, InterruptedException {
        this(userName, password, xmppserver, 5222, true);
    }
    
    public PubSubClient(String userName, String password, String xmppserver, 
            boolean createAccountIfNotExist) throws XMPPException, 
            InterruptedException {
        this(userName, password, xmppserver, 5222, createAccountIfNotExist);
    }
    
    public PubSubClient(String fileName) throws IOException, XMPPException, 
            InterruptedException {
        this(fileName, true);
    }
    
    public PubSubClient(String fileName, boolean createAccountIfNotExist) 
            throws IOException, XMPPException, InterruptedException {
            // the file path was not correct
    //      Configuration config = new PropertiesConfiguration(fileName);
    //      // declare variables
    //      String username = config.getString("username");
    //      String password = config.getString("password");
    //      String xmppserver = config.getString("xmppserver");
    //      int port = config.getInt("port");
            
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
    
    public PubSubClient(String userName, String password, String domain, 
            int port, boolean createAccountIfNotExist) throws XMPPException, 
            InterruptedException {
        this.init(userName, password, domain, port, createAccountIfNotExist); 
    }
    
    public void init(String userName, String password, String domain, 
            int port, boolean createAccountIfNotExist) throws XMPPException, 
            InterruptedException {
        ConnectionConfiguration config = new ConnectionConfiguration(domain
                ,port);
        connection = new XMPPConnection(config);
        connection.connect();

        
        try {
            connection.getAccountManager().createAccount(userName, password);
            logger.info("User " + userName + " created " 
                    + domain);
        } catch(XMPPException e) {
            logger.info("User " + userName + " already created ");
        }        
        try {
            connection.login(userName, password);
            logger.info("User " + connection.getUser() + " login ");
        } catch(IllegalStateException e) {
            logger.info("User " + connection.getUser() + " already login ");
        }
        
        
//        try {
//            connection.login(userName, password);
//            logger.info("User " + connection.getUser() 
//                    + " logged in to the server ");
//        } catch(XMPPException e) {
//            if (createAccountIfNotExist) {
//                connection.getAccountManager().createAccount(userName, password);
//                logger.info("Created account for " + userName );
//                // login fail (not-authorized) just after the account creation
////                Thread.sleep(50);
//                connection.login(userName, password);
//                logger.info("User " + connection.getUser()  
//                        + " logged in to the server ");
//            } else {
//                logger.info("account " + userName + "doesn't exist "
//                        + "and is not going to be created");
//            }
//        }
        //Create a pubsub manager using an existing Connection
        mgr = new PubSubManager(connection);
//        logger.info("PubSub manager created");
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
    public String getUser() {
        return connection.getUser();    
    }

    public LeafNode getNode(String nodename) throws XMPPException {
        LeafNode node = (LeafNode) mgr.getNode(nodename);
//        logger.info("got node " + nodename);
        return node;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            //close();        // close open files
        } finally {
            super.finalize();
        }
    }
}
