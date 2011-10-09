package org.deri.xmpppubsub;

import java.io.FileWriter;
import java.io.IOException;
//import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.lang.RuntimeException;
//import java.util.logging.Level;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

//import org.w3c.dom.*;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
//import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
//import org.xml.sax.SAXParseException;

/**
 * @author maciejdabrowski
 * @author Julia Anaya
 */
public class ItemEventCoordinator implements ItemEventListener {
    static Logger logger = Logger.getLogger(ItemEventCoordinator.class);
//    String subUser;
    String fileName;
    //String fileName = "allTests.csv";
    //FileWriter writer;
    //SPARQLWrapper sw = new SPARQLWrapper();
    String endpoint;
    String subSeq;
    
    public ItemEventCoordinator(String subSeq, String fileName, String endpoint) {
        logger.info("new itemeventcoordinator");
        this.fileName = fileName;
        this.endpoint = endpoint;
        this.subSeq = subSeq;
//        writer.append("publisher");
//        writer.append(',');
//        writer.append("msg");
//        writer.append(',');
//        writer.append("triples/msg");
//        writer.append(',');
//        writer.append("msgsize(chars)");
//        writer.append(',');
//        writer.append("construct time (ms)");
//        writer.append(',');
//        writer.append("publish time (ms)");
//        writer.append(',');
//        writer.append("insert time (ms)");
//        writer.append(',');
//        writer.append("total time (msg)");
//        writer.append('\n');
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
        logger.info("en listener");
        
        try {
            SPARQLQuery sq = new SPARQLQuery();
            SPARQLWrapper sw = new SPARQLWrapper();
            FileWriter writer = new FileWriter(fileName, true);
            
            List<ItemPublishEvent> its = items.getItems();
            Iterator itr = its.iterator();
            while (itr.hasNext()){
                Item item = (Item) itr.next();
                String itemId = item.getId();
                logger.info(itemId);
                
                // getting the start ms as the 13 last numbers in msgId
//                String start = "";
//                Matcher m = Pattern.compile("[0-9]{13}").matcher(itemId) ;
//                if( m.find() ) {
//                    start = m.group(0) ;
//                } 
                String[] columns = new String[5];
                columns = itemId.split(",");
                
                String start = columns[4];
                Long msgTime = end - Long.valueOf(start);
                logger.info("elapsed time: " + msgTime);

                String query = sq.fromXML(item.toXML());
                logger.info(query);
                    
                
                String result = sw.runQuery(query, endpoint, true);
                logger.info(result);   
                
                Long totalTime = Long.valueOf(columns[3].replace("ctime","") ) 
                        + msgTime + sw.time;
                logger.info(totalTime);

                writer.write(subSeq);
                writer.write(',');
                writer.write(columns[0]);
                writer.write(',');
                writer.write(columns[1]);
                writer.write(',');
                writer.write(columns[2].replace("triples",""));
                writer.write(',');
                writer.write(Integer.toString(item.toString().length()));
                writer.write(',');
                writer.write(columns[3].replace("ctime",""));
                writer.write(',');
                writer.write(msgTime.toString());
                writer.write(',');
                writer.write(sw.time.toString());
                writer.write(',');
                writer.write(totalTime.toString());
                writer.write('\n');
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            logger.error(e);
        } catch(ParserConfigurationException e) {
            logger.error(e);
        } catch(SAXException e) {
            logger.error(e);
        } catch (Exception e) {
            logger.error(e);
        }
        
    }
}
