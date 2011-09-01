package org.deri.xmpppubsub;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;

import org.deri.any23.Any23;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.FileDocumentSource;
import org.deri.any23.source.StringDocumentSource;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.TripleHandler;

import com.tecnick.htmlutils.htmlentities.HTMLEntities;
import org.apache.commons.lang3.StringEscapeUtils;
/**
 * @author Maciej Dabrowski
 * @author Julia Anaya
 *
 */
public class SPARQLQuery {
    String beginNS = "<query xmlns='http://www.w3.org/2005/09/xmpp-sparql-binding'>";
    String endNS = "</query>";
    
    //constants for testing
//    String triplesSource = "<http://example/book1> dc:title 'A new book' ; dc:creator 'A.N. Other' .";
//    String prefixes = "PREFIX dc:<http://purl.org/dc/elements/1.1/>";
    String triples;
	String query;
    String queryxml;
    static Logger logger = Logger.getLogger(SPARQLQuery.class);

	public SPARQLQuery(String method, String triplesSource) throws IOException, ExtractionException {
		//convert any serialized rdf to triples
//		triples = source23(triplesSource);
	    triples = getn3FromFile(triplesSource);
		logger.debug(triples);
		//create the query
		if (method.equals("insert")) {
			query = "INSERT DATA "+triples;
		} else if (method == "delete") {
			query = "DELETE DATA "+triples;
		} else if (method == "update") {
			query = "UPDATE DATA "+triples;
		}
//		query = prefixes + query;
		logger.debug(query);
		//toXML
	}

    public static String getn3FromFile(String fileName) throws IOException{
        logger.debug("Reading from file.");
        
//        File f = new File("C:\\test.txt");
//        FileReader fr = new FileReader(f);
//        BufferedReader br = new BufferedReader(fr);
//        StringBuffer sb = new StringBuffer();
//        String eachLine = br.readLine();
        
        
//        File file = new File("config/xmpppubsub.properties");
//        String filePath = file.getCanonicalPath();
//        logger.debug(filePath);
//        InputStream is = new FileInputStream(filePath);
        
        
//        FileInputStream fis = new FileInputStream("test.txt"); 
//        InputStreamReader in = new InputStreamReader(fis, "UTF-8");

        File f = new File("src/org/deri/xmpppubsub/data/"+fileName);
        String filePath = f.getCanonicalPath();
        logger.debug(filePath);
        FileReader fr = new FileReader(filePath);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder sb = new StringBuilder();
        
        String line = br.readLine();
        while (line != null) {
            logger.debug(line);
            sb.append(line + "\n");
            line = br.readLine();
        }
        br.close();
        
        logger.debug(sb.toString());
        return sb.toString();
    }
    
	public String source23(String triplesSource) throws IOException, ExtractionException {
		//String anyquery = "http://any23.org/best/"+url;
		//triples = url;
	    
	    // TODO: solve 
	    //Exception in thread "main" java.lang.NoClassDefFoundError: org/slf4j/LoggerFactory
	    //at org.deri.any23.configuration.DefaultConfiguration.<clinit>(DefaultConfiguration.java:44)
	    //at org.deri.any23.Any23.<clinit>(Any23.java:67)
	    
        Any23 runner = new Any23();
        // The second argument of StringDocumentSource() must be a valid URI.
//        try {
//            DocumentSource source = new FileDocumentSource(triplesSource);
//        } catch (){
//            String content = "";
//            DocumentSource source = new StringDocumentSource(content, triplesSource);
//        }
        File file = new File("org/deri/xmpppubsub/data/"+triplesSource);
        DocumentSource source = new FileDocumentSource(file);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TripleHandler handler = new NTriplesWriter(out);
        runner.extract(source, handler);
        String n3 = out.toString("UTF-8");
        return n3;
	}
	public void decodeEntities() {
//    	query = URLDecoder.decode(triples, "UTF-8");//
//		query = HTMLEntities.htmlAngleBrackets(triples);
//    	query = StringEscapeUtils.escapeHtml4(triples);
		query = StringEscapeUtils.escapeHtml4(query);
	}
	public String toXML(){
		//testing in different ways
        logger.debug("query attr inside toXML");
        logger.debug(query);
    	queryxml = beginNS+"<![CDATA["+query+"]]>"+endNS;
    	System.out.println(queryxml);
		return queryxml;
	}
	public String toXMLDecodingEntities(){
		//testing in different ways
		decodeEntities();
    	queryxml = beginNS+query+endNS;
    	System.out.println(queryxml);
		return queryxml;
	}
	public String toXMLDecodingEntitiesCDATA(){
		//testing in different ways
		decodeEntities();
    	queryxml = beginNS+"<![CDATA[ "+query+" ]]>"+endNS;
    	System.out.println(queryxml);
		return queryxml;
	}
    public static void main(String[] args){
        //main method for testing
        try {
            // Set up a simple configuration that logs on the console.
            BasicConfigurator.configure();
            
            String triplesSource = "example.n3";
            String method = "insert";
            SPARQLQuery query = new SPARQLQuery(method, triplesSource);
            System.out.println(query.toXML());
        } catch (IOException e){
            e.printStackTrace();
            logger.debug(e);
        } catch (ExtractionException e){
            e.printStackTrace();
            logger.debug(e);
        }
    }
}
