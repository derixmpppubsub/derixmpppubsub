package org.deri.xmpppubsub;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.deri.any23.Any23;
import org.deri.any23.ExtractionReport;
import org.deri.any23.extractor.ExtractionException;
import org.deri.any23.source.DocumentSource;
import org.deri.any23.source.FileDocumentSource;
import org.deri.any23.writer.NTriplesWriter;
import org.deri.any23.writer.TripleHandler;

/**
 * @author Maciej Dabrowski
 * @author Julia Anaya
 * 
 */
public class SPARQLQuery {

	private String beginNS = "<query xmlns='http://www.w3.org/2005/09/xmpp-sparql-binding'>";
	private String endNS = "</query>";

	// triples to be wrapped in the SPARQL 1.1 Update query
	private String triples;

	// SPARQL 1.1 query
	private String query;

	// query type
	private SPARQLQueryType queryType;

	// default query type
	private static SPARQLQueryType defaultQueryType = SPARQLQueryType.INSERT;

	static Logger logger = Logger.getLogger(SPARQLQuery.class);

	/**
	 * 
	 * @param queryType
	 *            - type of the query to be issued
	 * @param triplesSource
	 *            - file containing the triples in n3 format
	 * @throws IOException
	 * @throws ExtractionException
	 */
	public SPARQLQuery(SPARQLQueryType queryType, String triplesSource)
			throws IOException, ExtractionException, QueryTypeException {

		// convert any serialized rdf to triples
		triples = getn3FromFile(triplesSource);

		if (!triples.isEmpty())
			logger.debug("some triples read from the file: " + triplesSource);
		else
			logger.debug("file was opened but no triples were read");

		// wrap the triples in the query of a given type
		query = wrapTriples(queryType, triples);

		logger.debug(query);
	}

	/**
	 * Method returns a query of a defined type that contains triples provided
	 * as a second parameter
	 * 
	 * @param queryType
	 * @param triples
	 * @return
	 * @throws QueryTypeException
	 */
	public String wrapTriples(SPARQLQueryType queryType, String triples)
			throws QueryTypeException {
		String wrappedTriples = null;

		// wrap the query
		if (queryType.equals(SPARQLQueryType.INSERT)) {
			wrappedTriples = "INSERT DATA {" + triples + "}";
		} else if (queryType.equals(SPARQLQueryType.DELETE)) {
			wrappedTriples = "DELETE DATA {" + triples + "}";
		} else if (queryType.equals(SPARQLQueryType.UPDATE)) {
			wrappedTriples = "UPDATE DATA {" + triples + "}";
		} else {
			throw new QueryTypeException("Unsupported SPARQL query type.");
		}

		// set actual query type
		this.queryType = queryType;

		return wrappedTriples;
	}

	/**
	 * Simple method that wraps the triples with default query type
	 * 
	 * @param triples
	 * @throws QueryTypeException
	 * @throws ExtractionException
	 * @throws IOException
	 */
	public SPARQLQuery(String triples) throws IOException, ExtractionException,
			QueryTypeException {
		this(defaultQueryType, triples);
	}

	/**
	 * Method that reads triples data from a file.
	 * 
	 * @param fileName
	 *            - name of the file that contains the triples
	 * @return
	 * @throws IOException
	 */
	public String getn3FromFile(String fileName) throws IOException {

		// TODO: path should be moved to configuration properties
		File f = new File(fileName);
		String filePath = f.getCanonicalPath();
		logger.debug("Reading file: " + filePath);
		FileReader fr = new FileReader(filePath);
		BufferedReader file = new BufferedReader(fr);
		StringBuilder extractedTriples = new StringBuilder();

		String line = file.readLine();
		while (line != null) {
			logger.debug(line);
			extractedTriples.append(line + "\n");
			line = file.readLine();
		}
		file.close();

		logger.debug(extractedTriples.toString());
		return extractedTriples.toString();
	}

	/**
	 * 
	 * @param triplesSource
	 * @return
	 * @throws IOException
	 * @throws ExtractionException
	 */
	public String source23(String triplesSource) throws IOException,
			ExtractionException {

		// TODO: this should be cleaned up
		// String anyquery = "http://any23.org/best/"+url;
		// triples = url;

		// TODO: solve
		// Exception in thread "main" java.lang.NoClassDefFoundError:
		// org/slf4j/LoggerFactory
		// at
		// org.deri.any23.configuration.DefaultConfiguration.<clinit>(DefaultConfiguration.java:44)
		// at org.deri.any23.Any23.<clinit>(Any23.java:67)

		Any23 runner = new Any23();
		// The second argument of StringDocumentSource() must be a valid URI.
		// try {
		// DocumentSource source = new FileDocumentSource(triplesSource);
		// } catch (){
		// String content = "";
		// DocumentSource source = new StringDocumentSource(content,
		// triplesSource);
		// }
		// File file = new File("org/deri/xmpppubsub/data/" + triplesSource);

		File file = new File(triplesSource);
		DocumentSource source = new FileDocumentSource(file);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TripleHandler handler = new NTriplesWriter(out);
		ExtractionReport report = runner.extract(source, handler);

		// TODO: is there anything useful in the extration report?

		String n3 = out.toString("UTF-8");
		return n3;
	}

	/**
	 * 
	 * @return xml compliant query
	 */
	public String toXML() {
		String queryxml;
		logger.debug("converting the query to XML compliant format");
		logger.debug(query);
		queryxml = beginNS + "<![CDATA[" + query + "]]>" + endNS;
		return queryxml;
	}

	/**
	 * 
	 * @return
	 */
	public String toXMLDecodingEntities() {

		String queryxml;

		// TODO: why not excapeXML(query)?
		query = StringEscapeUtils.escapeHtml4(query);
		queryxml = beginNS + query + endNS;

		logger.info("query converted to XML: " + queryxml);

		return queryxml;
	}

	/**
	 * 
	 * @return
	 */
	public String toXMLDecodingEntitiesCDATA() {
		String queryxml;
		// TODO: why not excapeXML(query)?
		query = StringEscapeUtils.escapeHtml4(query);
		queryxml = beginNS + "<![CDATA[ " + query + " ]]>" + endNS;
		logger.info("query: " + queryxml);

		return queryxml;
	}

	/**
	 * Get query RDF content
	 * 
	 * @return String that contains RDF data
	 */
	public String getTriples() {
		return triples;
	}

	/**
	 * Set query content
	 * 
	 * @param triples
	 */
	public void setTriples(String triples) {
		this.triples = triples;
		// TODO: shall we add here? this.query = wrapTriples(queryType,
		// triples);
	}

	/**
	 * Get the SPARQL 1.1 Query
	 * 
	 * @return
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Set the query string
	 * 
	 * @param query
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Get the query string
	 * 
	 * @return
	 */
	public SPARQLQueryType getQueryType() {
		return queryType;
	}

	/**
	 * 
	 * @param queryType
	 */
	public void setQueryType(SPARQLQueryType queryType) {
		this.queryType = queryType;
	}

	// TODO: this should be moved to test classes (jUnit?) rather than here
	public static void main(String[] args) {
		// main method for testing
		try {
			// Set up a simple configuration that logs on the console.
			BasicConfigurator.configure();

			String triplesSource = "example.n3";
			SPARQLQueryType method = SPARQLQueryType.INSERT;
			SPARQLQuery query = new SPARQLQuery(method, triplesSource);
			System.out.println(query.toXML());
		} catch (IOException e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		} catch (ExtractionException e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		} catch (QueryTypeException e) {
			e.printStackTrace();
			logger.debug(e.getMessage());
		}
	}
}
