package org.deri.xmpppubsub;

import java.net.URLDecoder;
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
    String triples = "<http://example/book1> dc:title 'A new book' ; dc:creator 'A.N. Other' .";
    String prefixes = "PREFIX dc:<http://purl.org/dc/elements/1.1/>";

	String query;
    String queryxml;

	public SPARQLQuery(String method, String triples) {
		//convert any serialized rdf to triples
		
		//how to get the prefixes?
		
		
		//create the query
		if (method == "insert") {
			query = "INSERT DATA "+triples;
		} else if (method == "delete") {
			query = "DELETE DATA "+triples;
		} else if (method == "update") {
			query = "UPDATE DATA "+triples;
		}
		query = prefixes + query;
		
		//toXML
	}
	
	public void any23triples(String url) {
		//String anyquery = "http://any23.org/best/"+url;
		//triples = url;
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
