package org.deri.xmpppubsub;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Document;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author maciejdabrowski
 * @author Julia Anaya
 *
 */
public class ItemEventCoordinator implements ItemEventListener {
    static Logger logger = Logger.getLogger(ItemEventCoordinator.class);
//    String subUser;
    String fileName = "allTests.csv";
    FileWriter writer;
    SPARQLWrapper sw = new SPARQLWrapper();
    
    public ItemEventCoordinator(String fileName) throws IOException {
        logger.info("new itemeventcoordinator");
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
        try {
            writer = new FileWriter(fileName, true);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(ItemEventCoordinator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
        
//        System.out.println("Item count: " + items.getItems().size());
//        System.out.println(items);        
        try {
            List<ItemPublishEvent> its = items.getItems();
            Iterator itr = its.iterator();
            while (itr.hasNext()){
                Item item = (Item) itr.next();
                String itemId = item.getId();
                logger.info(itemId);
    //            System.out.println("item id: " + itemId);
//                String start = "";
//                Matcher m = Pattern.compile("[0-9]{13}").matcher(itemId) ;
//                if( m.find() ) {
//                    start = m.group(0) ;
//                } 
                String[] columns = new String[5];
                columns = itemId.split(",");
                logger.info("column 0: "+ columns[0]);
                String start = columns[4];
    //            System.out.println(start);

    //            String start = item.getId.substring(text.length() - 13);
    //            start = itemId.replace(target, "");

    //            Long itemTime = end - Long.parseLong(start);
                Long itemTime = end - Long.valueOf(start);
//                System.out.println("start time: " +start);
//                System.out.println("end time: " +end);
                logger.info("elapsed time: " + itemTime);

                String query="";
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                String itemXML = item.toXML().replace("INSERT", "<![CDATA[ INSERT");
                itemXML = itemXML.replace("</query>", "]]></query>");  
                logger.info(itemXML);
                InputSource is = new InputSource(new StringReader(itemXML));
                try {
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document dom = db.parse(is);	
                    Node queryNode = dom.getElementsByTagName("query").item(0);
                    Element queryElement = (Element)queryNode;
                    query = ((Node)queryElement.getChildNodes().item(0)).getNodeValue();
                }catch(ParserConfigurationException pce) {
                    pce.printStackTrace();
                }catch(SAXException se) {
                    se.printStackTrace();
                }catch(IOException ioe) {
                    ioe.printStackTrace();
                }
                logger.info(query);
                    
                String result = sw.executeQuery(query, 
                        "http://localhost:8000/sparql/", true);
                logger.info(result);               
                Long totalTime = Long.valueOf(columns[3]) + itemTime + sw.time;
                logger.info(totalTime);
                logger.info(columns[0]);
                
                writer.append(columns[0]);
                writer.append(',');
                writer.append(columns[1]);
                writer.append(',');
                writer.append(columns[2]);
                writer.append(',');
                writer.append(Integer.toString(item.toString().length()));
                writer.append(',');
                writer.append(columns[3]);
                writer.append(',');
                writer.append(itemTime.toString());
                writer.append(',');
                writer.append(sw.time.toString());
                writer.append(',');
                writer.append(totalTime.toString());
                writer.append('\n');
                
//                writer.append(itemId.replace(start, ""));
//                writer.append(',');
//                writer.append(itemTime.toString());
//                writer.append(',');
//                writer.append(Integer.toString(item.toString().length()));
//                writer.append(',');
//                writer.append(sw.time.toString());
//                writer.append('\n');
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
