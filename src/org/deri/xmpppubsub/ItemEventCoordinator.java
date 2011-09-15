package org.deri.xmpppubsub;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;

/**
 * @author maciejdabrowski
 * @author Julia Anaya
 *
 */
public class ItemEventCoordinator implements ItemEventListener {
    static Logger logger = Logger.getLogger(ItemEventCoordinator.class);
	
    @Override
	public void handlePublishedItems(ItemPublishEvent items){
		System.out.println("Item count: " + items.getItems().size());
//        System.out.println(items);		
		List<ItemPublishEvent> its = items.getItems();
		Iterator itr = its.iterator();
		while (itr.hasNext()){
			Item item = (Item) itr.next();
			System.out.println("item received: " + item.toXML());
			//TODO: put data into the RDF store
		}
		
	}
}