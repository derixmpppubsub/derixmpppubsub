/**
 * Julia Anaya
 */
package org.deri.xmpppubsub;
import java.io.*;
//import java.lang.management.ManagementFactory;
//import java.lang.management.ThreadMXBean;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

//import java.util.ArrayList;
//import java.util.List;
//
//import com.hp.hpl.jena.query.Query;
//import com.hp.hpl.jena.query.QueryExecution;
//import com.hp.hpl.jena.query.QueryExecutionFactory;
//import com.hp.hpl.jena.query.QueryFactory;
//import com.hp.hpl.jena.query.QuerySolution;
//import com.hp.hpl.jena.query.ResultSet;
//import com.hp.hpl.jena.query.DatasetFactory;
//import com.hp.hpl.jena.query.Dataset;
//import com.hp.hpl.jena.query.QueryExecutionFactory;
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.Resource;
//import com.hp.hpl.jena.vocabulary.RDF;
//
//import com.hp.hpl.jena.query.ResultSetFormatter;
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.update.UpdateRequest;
//import com.hp.hpl.jena.util.FileManager;
//import com.hp.hpl.jena.vocabulary.DC;
//import com.hp.hpl.jena.vocabulary.RDFS;
//import com.hp.hpl.jena.vocabulary.XSD;
//import com.hp.hpl.jena.update.UpdateAction;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
//import sun.misc.IOUtils;
/**
 * @author Julia Anaya
 *
 */
public class SPARQLWrapper {
    public Long time;
//    public double usedtime_cpu, usedtime_sys;
//    ThreadMXBean tb_cpu = ManagementFactory.getThreadMXBean();
	static Logger logger = Logger.getLogger(SPARQLWrapper.class);

    public SPARQLWrapper() {
    }
    public static String runRequest(String targetURL, String urlParameters, 
            boolean isConstruct) {
      URL url;
      HttpURLConnection connection = null;  
      try {
        //Create connection
        url = new URL(targetURL);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        if (isConstruct) {
            connection.setRequestProperty("Accept","text/plain");
        }
        connection.setUseCaches (false);
        connection.setDoInput(true);
        connection.setDoOutput(true);

        //Send request
        DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
        wr.writeBytes (urlParameters);
        wr.flush ();
        wr.close ();

        //Get Response
        int status = connection.getResponseCode();
        String message = connection.getResponseMessage();
        logger.debug("response status [" + status + "] message [" + message + "]");

        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer(); 
        while((line = rd.readLine()) != null) {
          response.append(line);
          response.append('\r');
        }
        rd.close();
        logger.debug(response.toString());
        return response.toString();
//        }
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      } finally {
        if(connection != null) {
          connection.disconnect(); 
        }
      }
    }
    
//    public String runQuery(String queryString, String endpoint, 
//            boolean update) throws UnsupportedEncodingException {
     //Model model = ModelFactory.createMemModelMaker().createModel();
//     Query query = QueryFactory.create(queryString);
//     query.serialize(System.out) ;
//     System.out.println();
//
//     starttime_sys = System.nanoTime();
//     starttime_cpu = tb_cpu.getCurrentThreadCpuTime();
        
//     QueryExecution qexec = QueryExecutionFactory.sparqlService(endpoint, query);
//     //ResultSetFormatter.out(System.out, qExec.execSelect(), query) ;
        
//      try {
//          ResultSet results = qexec.execSelect();
//          endtime_cpu = tb_cpu.getCurrentThreadCpuTime();
//          endtime_sys = System.nanoTime();
//          usedtime_cpu = (endtime_cpu - starttime_cpu) * 1e-9;
//          usedtime_sys = (endtime_sys - starttime_sys) * 1e-9;
////          outputWriter.write("    Select timesued_cpu = " + usedtime_cpu + " sec.\n");
////          outputWriter.write("    Select timesued_sys = " + usedtime_sys + " sec.\n");
//          
//          for (; results.hasNext();) {
//              QuerySolution soln = results.nextSolution() ;
////              Resource r = soln.getResource("emp") ; // Get a result variable - must be a resource
////              employees.add(r.getURI());
////              logger.debug("Employee URI");
////              logger.debug(r.getURI());
//          // Result processing is done here.
//          }
//      }
//      finally {
//         qexec.close();
//      }
      
     // But updates do not support this kind of sparqlService method
     // Illegal:
     // But dataset is a Dataset object, not the uri.
     // I don't believe this is the correct way to overcome this:
//     List<String> uriList = new ArrayList<String>();
//     uriList.add(endpoint);
//     Dataset dataset = DatasetFactory.create(uriList);
    //java.io.IOException: Server returned HTTP response code: 500 for URL: http://192.168.1.8:8000/sparql/
//     DatasetGraphTxn dsg = sConn.begin(ReadWrite.WRITE) ;
//     UpdateRequest uquery = UpdateRequest.create(queryString);
//     UpdateProcessor proc = UpdateExecutionFactory.create(uquery, dsg) ;
    //}
    
    public String runQuery(String queryString, String endpoint, 
            boolean update) throws UnsupportedEncodingException {
     boolean isConstruct = queryString.startsWith("CONSTRUCT");
     String urlParameters;
     if (update) {
        urlParameters = "update=" + URLEncoder.encode(queryString, "UTF-8");
     } else {
        urlParameters = "query=" + URLEncoder.encode(queryString, "UTF-8");
     }
     logger.info(urlParameters);
//     long starttime_cpu, endtime_cpu, starttime_sys, endtime_sys, 
     long start, end;
//     starttime_sys = System.nanoTime();
//     starttime_cpu = tb_cpu.getCurrentThreadCpuTime();
     start = System.currentTimeMillis();
//     UpdateAction.parseExecute(queryString, dataset);
     String result = this.runRequest(endpoint, urlParameters, isConstruct);
     
//     endtime_cpu = tb_cpu.getCurrentThreadCpuTime();
//     endtime_sys = System.nanoTime();
     end = System.currentTimeMillis();
//     usedtime_cpu = (endtime_cpu - starttime_cpu) * 1e-9;
//     usedtime_sys = (endtime_sys - starttime_sys) * 1e-9;
     time = end - Long.valueOf(start);
     

     return result;
    }
    
    /**
     * @param args
     * void
     * @throws UnsupportedEncodingException 
     * 
     */
    public static void main(String[] args) {
		BasicConfigurator.configure();
        Writer outputWriter = null;
        File outputFile = new File("insert-times.txt");
        try {
            outputWriter = new BufferedWriter(new FileWriter(outputFile));
        } catch (Exception e) {
            System.out.println("Exception encountered while opening file writer:");
            System.out.println(e);
        }
        String triples = "<http://ecp-alpha/semantic/post/1> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://rdfs.org/sioc/ns#Post> .";
        String endpoint= "http://localhost:8000/update/";
        SPARQLWrapper sw = new SPARQLWrapper();
        String queryString = "INSERT DATA {" + triples + "}";
        logger.info(queryString);
        try {
            String result = sw.runQuery(queryString, endpoint, true);
            System.out.println(result);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
//        try {
//            outputWriter.write("    Select timesued_cpu = " + sw.usedtime_cpu + " sec.\n");
//            outputWriter.write("    Select timesued_sys = " + sw.usedtime_sys + " sec.\n");
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

}
