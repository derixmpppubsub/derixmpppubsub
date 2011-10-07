/**
 * duy
 */
package org.deri.xmpppubsub;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.DatasetFactory;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.hp.hpl.jena.vocabulary.XSD;
import com.hp.hpl.jena.update.UpdateAction;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import sun.misc.IOUtils;
/**
 * @author duy
 *
 */
public class SPARQLWrapper {
    public Long time;
//    public double usedtime_cpu, usedtime_sys;
//    ThreadMXBean tb_cpu = ManagementFactory.getThreadMXBean();
	static Logger logger = Logger.getLogger(SPARQLWrapper.class);

    public SPARQLWrapper() {
    }
    public static String excutePost(String targetURL, String urlParameters, boolean isConstruct)
    {
      URL url;
      HttpURLConnection connection = null;  
      try {
        //Create connection
        url = new URL(targetURL);
        connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
//        if (isConstruct) {
//            connection.setRequestProperty("Accept", "application/rdf+xml");
//            connection.setRequestProperty("Content-type", "text/plain");
//            OutputStream out = connection.getOutputStream();
//            IOUtils.write("The text to enhance", out);
//            IOUtils.closeQuietly(out);
//            connection.connect(); //send the request
//            if(connection.getResponseCode() > 299){ //assume an error
//                //error response
//                InputStream errorStream = connection.getErrorStream();
//                if(errorStream != null){
//                    String errorMessage = IOUtils.toString(errorStream);
//                    IOUtils.closeQuietly(errorStream);
//                    //write a error message
//                } else { //no error data
//                    //write default error message with the status code
//                }
//            } else { //get the enhancement results
//                InputStream enhancementResults = connection.getInputStream();
//            }
//            
//            
//            post.setDoInput(true);
//            post.setDoOutput(true);
//            post.setRequestProperty("Content-Type","application/rdf+xml; charset=utf-8");
//            //post.setRequestProperty("Content-Encoding","UTF-8");
//            // read the response
//            int status = post.getResponseCode();
//            String message = post.getResponseMessage();
//            Log.debug(logger, "SWSConnection > sendSWSRequest > response status [" + status + "] message [" + message + "]");
//            
//            reader = new BufferedReader(new InputStreamReader(post.getInputStream()));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line).append('\n');
//            }
//            Log.debug(logger, "SWSConnector > sendSWSRequest > response: " + sb.toString());
//            String returnValue = "";
//            if (status != 200) {
//                returnValue = sb.toString();
//            }
//            return returnValue;
//
//        } else {
//            connection.setRequestProperty("Content-Type", 
//                 "application/x-www-form-urlencoded");
//
//            connection.setRequestProperty("Content-Length", "" + 
//                     Integer.toString(urlParameters.getBytes().length));
//            connection.setRequestProperty("Content-Language", "en-US");  
        
//            connection.setRequestProperty("Content-Type","application/rdf+xml; charset=utf-8");
//            connection.setRequestProperty("Content-type", "text/plain");
//            //connection.setRequestProperty("Content-Encoding","UTF-8");
            
//            connection.setRequestProperty("Accept", "application/rdf+xml; charset=utf-8");
//            connection.setRequestProperty("Accept", "application/sparql-results+xml");
//            connection.setRequestProperty("Accept","text/rdf+n3");
            //if (isConstruct) {
                connection.setRequestProperty("Accept","text/plain");
            //}
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
        
//        StringBuffer sbContent = new StringBuffer();
//        sbContent.append("X=");
//        sbContent.append(URLEncoder.encode("ABC", "UTF-8"));
//        DataOutputStream stream = new
//        DataOutputStream(connection.getOutputStream ());
//        stream.writeBytes(sbContent.toString());
//        stream.flush();
//        stream.close();
//        InputStream inputStream =
//        connection.getInputStream();
//        inputStream.close();
//        } catch (Throwable t) {
//        }
    }
    public String createQuery(String triples) {
//
//        String prolog = "PREFIX rdf: <"+RDF.getURI()+"> \n" ;
////        prolog += nm.prefix("cisco")+"\n";
//        prolog += "PREFIX cisco: <http://www.cisco.com/ert/> \n" ;
//        String queryString = prolog +
//                "SELECT ?emp WHERE {" +
//                "?emp a cisco:Employee ." +
//                "}" ;         
//        logger.debug("Execute query=\n"+queryString) ;
        String queryString = "INSERT DATA { " +
        		triples +
        		" }"; 
        return queryString;
    }
    
    public String executeQuery(String queryString, String endpoint, 
            boolean update) throws UnsupportedEncodingException {

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
     boolean isConstruct = queryString.startsWith("query=CONSTRUCT");
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
     String result = this.excutePost(endpoint, urlParameters, isConstruct);
     
//     endtime_cpu = tb_cpu.getCurrentThreadCpuTime();
//     endtime_sys = System.nanoTime();
     end = System.currentTimeMillis();
//     usedtime_cpu = (endtime_cpu - starttime_cpu) * 1e-9;
//     usedtime_sys = (endtime_sys - starttime_sys) * 1e-9;
     time = end - Long.valueOf(start);

//     DatasetGraphTxn dsg = sConn.begin(ReadWrite.WRITE) ;
//     UpdateRequest uquery = UpdateRequest.create(queryString);
//     UpdateProcessor proc = UpdateExecutionFactory.create(uquery, dsg) ;
     

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
        String queryString = sw.createQuery(triples);
        logger.info(queryString);
        try {
            String result = sw.executeQuery(queryString, endpoint, true);
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
