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

public class PubSubClient {

    protected XMPPConnection connection;
    public PubSubManager mgr;
    public String userName;
    public String password;
    public String domain;
    public int port;
    public LeafNode node;
    protected static Logger logger = Logger.getLogger(PubSubClient.class);

    public PubSubClient(String userName, String password, String xmppserver)
            throws XMPPException {
        this(userName, password, xmppserver, 5222, true);
    }
    
    public PubSubClient(String userName, String password, String xmppserver, 
            boolean createAccountIfNotExist) throws XMPPException {
        this(userName, password, xmppserver, 5222, createAccountIfNotExist);
    }
    
    public PubSubClient(String userName, String password, String xmppserver, 
            int port, boolean createAccountIfNotExist) throws XMPPException {
        this.userName = userName;
        this.password = password;
        this.domain = xmppserver;
        this.port = port;
        initPubSub(createAccountIfNotExist);   
    }

    public PubSubClient(String fileName) throws IOException, XMPPException {
        this(fileName, true);
    }
    
    public PubSubClient(String fileName, boolean createAccountIfNotExist) 
            throws IOException, XMPPException {
        confFromFile(fileName);
        initPubSub(createAccountIfNotExist);
    }

    public void confFromFile(String fileName) throws IOException {
            
    //      Configuration config = new PropertiesConfiguration("./xmpppubsub.properties");
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
            userName = prop.getProperty("username");  
            password = prop.getProperty("password");
            domain = prop.getProperty("xmppserver");
            port = Integer.parseInt(prop.getProperty("port")); 
        }

    public void initPubSub(boolean createAccountIfNotExist) throws XMPPException {
    	ConnectionConfiguration config = new ConnectionConfiguration(domain,port);
        connection = new XMPPConnection(config);
        connection.connect();

        try {
            connection.login(userName, password);
            logger.info("User " + userName + " logged in to the server " 
                    + domain);
        } catch(XMPPException e) {
            if (createAccountIfNotExist) {
                connection.getAccountManager().createAccount(userName, password);
                logger.info("authenticated? " + connection.isAuthenticated());
                logger.info("Created account for " + userName );
                connection.login(userName, password);
                logger.info("User " + userName + " logged in to the server " 
                        + domain);
            } else {
                System.out.println("account " + userName + "doesn't exist "
                        + "and is not going to be created");
            }
        }
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
    public String getUser() {
       	return connection.getUser();	
    }
    
    public String getJid() {
        String jid = userName + "@" + domain;
        return jid;
    }

    public LeafNode getNode(String nodename) throws XMPPException {
        node = (LeafNode) mgr.getNode(nodename);
        logger.info("node" + nodename  + "got");
        return node;
    }

}