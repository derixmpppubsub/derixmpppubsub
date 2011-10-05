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
    String subUser;
    String fileName;
    FileWriter writer;
    
    public ItemEventCoordinator(String subUser, String fileName) throws IOException {
        this.subUser = subUser;
        this.fileName = fileName;
        writer = new FileWriter(fileName, true);
    }
    
    @Override
    public void handlePublishedItems(ItemPublishEvent items) {
        
        //  display offline message’s timestamp
//        DelayInformation inf = null;
//        try {
//            inf = (DelayInformation)packet.getExtension("x","jabber:x:delay");
//        } catch (Exception e) {
//            log.error(e);
//        }
//        // get offline message timestamp
//        if(inf!=null){
//            Date date = inf.getStamp();

        long end = System.currentTimeMillis();
//        long end = System.nanoTime();
        System.out.println("en listener");
        
//        System.out.println("Item count: " + items.getItems().size());
//        System.out.println(items);        
        try {
            List<ItemPublishEvent> its = items.getItems();
            Iterator itr = its.iterator();
            while (itr.hasNext()){
                Item item = (Item) itr.next();
                String itemId = item.getId();
    //            System.out.println("item id: " + itemId);

                String start = "";
                Matcher m = Pattern.compile("[0-9]{13}").matcher(itemId) ;
                if( m.find() ) {
                    start = m.group(0) ;
                } 
    //            System.out.println(start);

    //            String start = item.getId.substring(text.length() - 13);
    //            start = itemId.replace(target, "");

    //            Long itemTime = end - Long.parseLong(start);
                Long itemTime = end - Long.valueOf(start);
    //            logger.info(delay);
                System.out.println("start time: " +start);
                System.out.println("end time: " +end);
                System.out.println("elapsed time: " + itemTime);


                writer.append(subUser);
                writer.append(',');
                writer.append(itemTime.toString());
                writer.append(',');
                writer.append(Integer.toString(item.toString().length()));
                writer.append('\n');
                writer.flush();
                writer.close();
            }
        } catch (IOException e1) {
            System.out.println("error trying to write the file");
            e1.printStackTrace();
        }
            
          //TODO: put data into the RDF store
//        try {
//            System.out.println("item content: " + item.toXML());
//      
//        } catch (Exception e) {
//            logger.info("error printing item content");
//            e.printStackTrace();
//        } catch(SAXParseException e) {
//            
//        }
        
    }
}
