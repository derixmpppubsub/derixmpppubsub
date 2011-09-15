package org.deri.xmpppubsub;

import java.util.HashMap;

public class Namespace {
    // FIXME: better to use just constants?
//    String DC = "http://purl.org/dc/terms/";
    HashMap<String, String> namespace = new HashMap<String, String>();
    
    public Namespace() {
        namespace.put("dc", "http://purl.org/dc/terms/");
        namespace.put("sioc", "http://rdfs.org/sioc/ns#");
        namespace.put("ctag", "http://commontag.org/ns#");
        namespace.put("ert", "http://www.cisco.com/ert/ns#"); 
        namespace.put("cisco", "http://www.cisco.com/ert/"); 
        namespace.put("wot", "http://xmlns.com/wot/0.1/");
        namespace.put("dcterms", "http://purl.org/dc/terms/");
        namespace.put("foaf", "http://xmlns.com/foaf/0.1/"); 
        namespace.put("dc", "http://purl.org/dc/elements/1.1/"); 
        namespace.put("owl", "http://www.w3.org/2002/07/owl#");
        namespace.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        namespace.put("owl2xml", "http://www.w3.org/2006/12/owl2-xml#");
        namespace.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#"); 
        namespace.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#"); 
        namespace.put("vs", "http://www.w3.org/2003/06/sw-vocab-status/ns#");
        namespace.put("siocT", "http://rdfs.org/sioc/types#");
        namespace.put("skos", "http://www.w3.org/2004/02/skos/core#"); 
        namespace.put("dbpedia2", "http://dbpedia.org/property/"); 
        namespace.put("dbpedia", "http://dbpedia.org/");
    }
    public String namespace(String prefixName) {
        return namespace.get(prefixName);
    }
    public String prefix(String prefixName) {
        // FIXME namespace is the prefix name or the actual namespace?
        return "PREFIX "+ prefixName + ": <" + namespace.get(prefixName) + ">";
    }
    public String allPrefixes() {
        String prefixes = "";
        for (String key : namespace.keySet()) {
            prefixes += prefix(key);
        }
        return prefixes;
    }
}
