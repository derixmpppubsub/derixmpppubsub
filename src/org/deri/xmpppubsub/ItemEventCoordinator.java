package org.deri.xmpppubsub;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.xml.sax.SAXParseException;

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
			String itemId = item.getId();
//            System.out.println("item id: " + itemId);
			
			String beginmillis = "" ;
			Matcher m = Pattern.compile("[0-9]{13}").matcher(itemId) ;
		    if( m.find() ) {
		        beginmillis = m.group(0) ;
		    } 
//		    System.out.println(beginmillis);
			
//			String beginmillis = item.getId.substring(text.length() - 13);
//			beginmillis = itemId.replace(target, "");
			float endmillis = System.currentTimeMillis();
//            System.out.println(endmillis);
			
			Float delay = endmillis - Float.parseFloat(beginmillis);
//			logger.info(delay);
			System.out.println("Millis delay: " + delay);
			

		    FileWriter writer;
            try {
                writer = new FileWriter("results.csv", true);
                writer.append(itemId);
                writer.append(',');
                writer.append(delay.toString());
                writer.append('\n');
                writer.flush();
                writer.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

			
			//TODO: put data into the RDF store
//			try {
//			    System.out.println("item content: " + item.toXML());
//			} catch (Exception e) {
//			    logger.info("error printing item content");
//	            e.printStackTrace();
//			}
		}
		
	}
}