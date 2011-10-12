package org.deri.xmpppubsub;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.xml.sax.SAXException;

/**
 * @author maciejdabrowski
 * @author Julia Anaya
 */
public class ItemEventCoordinator implements ItemEventListener {
    static Logger logger = Logger.getLogger(ItemEventCoordinator.class);
    String fileName;
    String endpoint;
    String subSeq;
    
    public ItemEventCoordinator(String subSeq, String fileName, String endpoint) {
        logger.debug("new itemeventcoordinator");
        this.fileName = fileName;
        this.endpoint = endpoint;
        this.subSeq = subSeq;
    }
    
    @Override
    public void handlePublishedItems(ItemPublishEvent items) {
        long end = System.currentTimeMillis();
//        long end = System.nanoTime();
        logger.debug("en listener");
        
        try {
            SPARQLQuery sq = new SPARQLQuery();
//            SPARQLWrapper sw = new SPARQLWrapper();
            FileWriter writer = new FileWriter(fileName, true);
            String[] columns = new String[4];
            String itemId,pubSeq, nTriples, pTime,  start, query, result, msgSize, line;
            Long insertTime, msgTime, totalTime;
                    
            List<Item> its = items.getItems();
            for(Item item : its) {
                itemId = item.getId();
                logger.debug("received item id" + itemId);
                
                // msgId format:
//                String msgId = "pub" + i + "of" + numberOfPublishers 
//                + ",triples" + numberOfTriples + ",ctime" + time.toString();
//                String msgId = "pub" + i + "of" + numberOfPublishers 
//                + "," + numberOfTriples + "," + time.toString();
        
                columns = itemId.split(",");
                pubSeq = columns[0];
                nTriples = columns[1];
                pTime = columns[2];
                start = columns[3];
                
                msgTime = end - Long.valueOf(start);
                //logger.debug("elapsed time: " + msgTime);

                query = sq.fromXML(item.toXML());
                //logger.debug("query: " + query);                
//                result = sw.runQuery(query, endpoint, true);
                Object[] ret = SPARQLWrapper.runQuery(query, endpoint, false);
                insertTime = (Long)ret[1];
//                result = (String)ret[0];
                
                totalTime = Long.valueOf(pTime) + msgTime + insertTime;
                //logger.debug("total time:" + totalTime);
                
                msgSize = Integer.toString(item.toString().length());
                // file header format:
                // "subscriber seq, publisher seq,triples/msg,msg size(chars),publisher store time (ms),publish time (ms),subscriber store time (ms),total time (msg)"
                line = subSeq + "," + pubSeq + "," + nTriples + "," + msgSize 
                      + "," + pTime + "," + msgTime.toString() + "," 
                      + insertTime.toString() + "," + totalTime.toString() + "\n";
                writer.write(line);
                writer.flush();
                writer.close();
            }
        } catch (NumberFormatException e) {
            logger.error(e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
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
