package publisher;
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

	private String username = "testuser2";
	private String password = "testuser2pass";
	private String xmppserver = "vmuss12.deri.ie";
	private int port = 5222;
 
    XMPPConnection connection;

    /**
     * @param userName
     * @param password
     * @return void 
     *
     */
    public void login(String userName, String password) throws XMPPException     {
	    ConnectionConfiguration config = new ConnectionConfiguration(this.xmppserver,this.port);
	    connection = new XMPPConnection(config);
	    connection.connect();
	    connection.login(this.username, this.password);
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
    public void pubsub_manager() {
    	
    }


    /**
     * @return void 
     *
     */
    public void create_node() {
    	
    }


    /**
     * @return void 
     *
     */
    public void get_node() {
    	
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
	    Publisher p = new Publisher();
	    
	    // turn on the enhanced debugger
	    XMPPConnection.DEBUG_ENABLED = true;
	    

	    // Enter your login information here
	    p.login("julia", "julia");
	    System.out.println("logued in");

		//Create a pubsub manager using an existing Connection
		PubSubManager mgr = new PubSubManager(p.connection);
		System.out.println("manager created");
			
		// Create the node
		//ConfigureForm form = new ConfigureForm(FormType.submit);
		//form.setAccessModel(AccessModel.open);
		//form.setDeliverPayloads(true);
		//form.setNotifyRetract(true);
		//form.setPersistentItems(true);
		//form.setPublishModel(PublishModel.open);
		//LeafNode leaf = (LeafNode) mgr.createNode("testNodeWithPayloadU2", form);
		//System.out.println("node created");
		
		// Get the node
		LeafNode node = (LeafNode) mgr.getNode("testNodeWithPayloadU2");
		System.out.println("got node");
		
		node.send(new PayloadItem("test" + System.currentTimeMillis(), 
				new SimplePayload("book2", "pubsub:test:book", "<title>book4</title>")));
		node.send(new PayloadItem("test" + System.currentTimeMillis(), 
				new SimplePayload("book4", "pubsub:test:book", "<title>book6</title>")));
		System.out.println("book send");
		
		p.disconnect();

	    //System.exit(0);
		
	}
}
