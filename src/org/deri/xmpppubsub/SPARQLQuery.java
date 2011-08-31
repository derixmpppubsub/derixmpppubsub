package org.deri.xmpppubsub;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
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
    String triplesSource = "<http://example/book1> dc:title 'A new book' ; dc:creator 'A.N. Other' .";
    String prefixes = "PREFIX dc:<http://purl.org/dc/elements/1.1/>";
    String triples;
	String query;
    String queryxml;

	public SPARQLQuery(String method, String triplesSource) throws IOException, ExtractionException {
		//convert any serialized rdf to triples
		triples = source23(triplesSource);
		//how to get the prefixes?
		
		
		//create the query
		if (method == "insert") {
			query = "INSERT DATA "+triples;
		} else if (method == "delete") {
			query = "DELETE DATA "+triples;
		} else if (method == "update") {
			query = "UPDATE DATA "+triples;
		}
//		query = prefixes + query;
		
		//toXML
	}
	
	public String source23(String triplesSource) throws IOException, ExtractionException {
		//String anyquery = "http://any23.org/best/"+url;
		//triples = url;
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
    	queryxml = beginNS+"<![CDATA["+query+"]]>"+endNS;
    	System.out.println(queryxml);
		return queryxml;
	}
}
