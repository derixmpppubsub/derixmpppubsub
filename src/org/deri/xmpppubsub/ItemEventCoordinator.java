package org.deri.xmpppubsub;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;
import org.deri.xmpppubsub.data.SPARQLQuery;
import org.deri.xmpppubsub.data.SPARQLWrapper;
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
    String subName;
//    String fileName;
//    String endpoint;
//    public static String fileHeadersTemplate = "nTests,nSubs,nPubs,nTriples,"
//            + "subName,pubName,tPubStore, tPushMsg, tSubStore, tTotal\n";
//    public static String msgIdTemplate = "%s,%s,%s,%s,%s,%s,%s";
//                    //nTests,nSubs,nPubs,nTriples,pubName,tPubStore,tStartMsg
//    public static int nColMsgId = 7;

    public ItemEventCoordinator(String subName) {
//        logger.debug("new itemeventcoordinator");
        this.subName = subName;
    }

//    /**
//     *
//     * @param subSeq
//     * @param fileName
//     * @param endpoint
//     */
//    public ItemEventCoordinator(String subName, String fileName, String endpoint) {
////        logger.debug("new itemeventcoordinator");
//        this.subName = subName;
//        this.fileName = fileName;
//        this.endpoint = endpoint;
//    }

    /**
     *
     * @param items
     */
    public void handlePublishedItems(ItemPublishEvent items) {
        long end = System.currentTimeMillis();
//        long end = System.nanoTime();
//        logger.debug("en listener");

        String fileHeadersTemplate = "nTests,nTest,nSubs,nPubs,nTriples,sizeMsg,"
            + "subName,pubName,sTimestamp,eTimestamp,tPubStore,tPushMsg,tSubStore,tTotal\n";
        String fileNameTemplate = "results/nTests%snSubs%snPubs%snTriples%s.csv";
        String msgIdTemplate = "%s,%s,%s,%s,%s,%s,%s";
            //nTests, nTest, nSubs, nPubs, nTriples,pubName, tPubStore
        String fileLineTemplate = "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n";
            //nTests, nTest, nSubs, nPubs, nTriples,sizeMsg.toString(),
            //subName, pubName, stimestamp,etimestamp
            //tPubStore, tMsg.toString(), tSubStore.toString(), tTotal.toString()
        String fileName;
        
        //TODO: move t
        String endpoint = "http://localhost:8000/update/";
        int nColMsgId = 8;
        String[] columns = new String[nColMsgId];
        String nTests, nTest, nSubs, nPubs, nTriples, pubName, tPubStore, tStartMsg;
        String itemId, query, sizeMsg, line;
        Long tSubStore, tMsg, tTotal;

        try {
            SPARQLQuery sq = new SPARQLQuery();

            List<Item> its = items.getItems();
            for(Item item : its) {
                itemId = item.getId();

                columns = itemId.split(",");
                nTests = columns[0];
                nTest = columns[1];
                nSubs = columns[2];
                nPubs = columns[3];
                nTriples = columns[4];
                pubName = columns[5];
                tPubStore = columns[6];
                tStartMsg = columns[7];

                tMsg = end - Long.valueOf(tStartMsg);
                //logger.debug("elapsed time: " + msgTime);

                query = sq.fromXML(item.toXML());
                //logger.debug("query: " + query);
//                result = sw.runQuery(query, endpoint, true);

                Object[] ret = SPARQLWrapper.runQuery(query, endpoint, true);
                tSubStore = (Long)ret[1];
//                result = (String)ret[0];

                tTotal = Long.valueOf(tPubStore) + tMsg + tSubStore;
                //logger.debug("total time:" + totalTime);

                sizeMsg = Integer.toString(item.toString().length());

//                line = nSubs + "," + nPubs + "," + nTriples + "," +
//                        subName + "," + pubName + "," + sizeMsg.toString() +
//                        "," + tPubStore + "," + tMsg.toString() + ","+
//                        tSubStore.toString() + "," + tTotal.toString() + "\n";

                line = String.format(fileLineTemplate, nTests, nTest, nSubs, nPubs, nTriples,
                        sizeMsg.toString(), subName, pubName, tStartMsg, end, tPubStore,
                        tMsg.toString(), tSubStore.toString(), tTotal.toString());

                fileName = String.format(fileNameTemplate, nTests, nSubs, nPubs, nTriples);
                // if file doesnt exist, create headers
                File file = new File(fileName);
//                FileWriter writer = new FileWriter(fileName, true);
                FileWriter writer;
                if (!file.exists()) {
                    writer = new FileWriter(file, true);
                    writer.write(fileHeadersTemplate);
                } else {
                    writer = new FileWriter(file, true);
                }
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
