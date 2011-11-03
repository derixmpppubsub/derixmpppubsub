package org.deri.xmpppubsub.data;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;

/**
 * @author Julia Anaya
 * @author Maciej Dabrowski
 * 
 */

public class SPARQLWrapper {

	static Logger logger = Logger.getLogger(SPARQLWrapper.class);

	/**
	 * 
	 * @param targetURL
	 * @param urlParameters
	 * @param isConstruct
	 * @return
	 */
	public static Object[] runRequest(String targetURL, String urlParameters,
			boolean isConstruct) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			if (isConstruct) {
				connection.setRequestProperty("Accept", "text/plain");
			}
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			long start = System.currentTimeMillis();
			DataOutputStream wr = new DataOutputStream(connection
					.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			// Get Response
			int status = connection.getResponseCode();
			String message = connection.getResponseMessage();
			long end = System.currentTimeMillis();
			logger.debug("response status [" + status + "] message [" + message
					+ "]");

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			// logger.debug(response.toString());
			// return response.toString();
			// Object[] ret = {response.toString(), end-start};
			// return ret;
			return new Object[] { response.toString(), end - start };
			// }
		} catch (Exception e) {
			logger.error(e);
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	// public String runQuery(String queryString, String endpoint,
	// boolean update) throws UnsupportedEncodingException {
	// Model model = ModelFactory.createMemModelMaker().createModel();
	// Query query = QueryFactory.create(queryString);
	// query.serialize(System.out) ;
	// System.out.println();
	//
	// starttime_sys = System.nanoTime();
	// starttime_cpu = tb_cpu.getCurrentThreadCpuTime();

	// QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint,
	// query);
	// //ResultSetFormatter.out(System.out, qExec.execSelect(), query) ;

	// try {
	// ResultSet results = qexec.execSelect();
	// endtime_cpu = tb_cpu.getCurrentThreadCpuTime();
	// endtime_sys = System.nanoTime();
	// usedtime_cpu = (endtime_cpu - starttime_cpu) * 1e-9;
	// usedtime_sys = (endtime_sys - starttime_sys) * 1e-9;
	// // outputWriter.write("    Select timesued_cpu = " + usedtime_cpu +
	// " sec.\n");
	// // outputWriter.write("    Select timesued_sys = " + usedtime_sys +
	// " sec.\n");
	//
	// for (; results.hasNext();) {
	// QuerySolution soln = results.nextSolution() ;
	// // Resource r = soln.getResource("emp") ; // Get a result variable - must
	// be a resource
	// // employees.add(r.getURI());
	// // logger.debug("Employee URI");
	// // logger.debug(r.getURI());
	// // Result processing is done here.
	// }
	// }
	// finally {
	// qexec.close();
	// }

	// But updates do not support this kind of sparqlService method
	// Illegal:
	// But dataset is a Dataset object, not the uri.
	// I don't believe this is the correct way to overcome this:
	// List<String> uriList = new ArrayList<String>();
	// uriList.add(endpoint);
	// Dataset dataset = DatasetFactory.create(uriList);
	// java.io.IOException: Server returned HTTP response code: 500 for URL:
	// http://192.168.1.8:8000/sparql/
	// DatasetGraphTxn dsg = sConn.begin(ReadWrite.WRITE) ;
	// UpdateRequest uquery = UpdateRequest.create(queryString);
	// UpdateProcessor proc = UpdateExecutionFactory.create(uquery, dsg) ;
	// }

	/**
	 * 
	 * @param queryString
	 * @param endpoint
	 * @param update
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static Object[] runQuery(String queryString, String endpoint,
			boolean update) throws UnsupportedEncodingException {
		boolean isConstruct = queryString.startsWith("CONSTRUCT");
		String urlParameters;
		if (update) {
			urlParameters = "update=" + URLEncoder.encode(queryString, "UTF-8");
		} else {
			urlParameters = "query=" + URLEncoder.encode(queryString, "UTF-8");
		}

		return SPARQLWrapper.runRequest(endpoint, urlParameters, isConstruct);
	}

}
