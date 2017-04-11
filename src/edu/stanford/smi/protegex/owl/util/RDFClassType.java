package edu.stanford.smi.protegex.owl.util;

/**
 * @author Hemed Al Ruwehy
 *         University of Bergen Library
 *         2017-04-11
 */
public enum RDFClassType {

    CONCEPT("http://www.w3.org/2004/02/skos/core#Concept"),

    CONCEPT_SCHEME("http://www.w3.org/2004/02/skos/core#ConceptScheme"),

    PROXY_COLLECTION("http://data.ub.uib.no/ontology/ProxyCollection"),

    EXHIBITION("http://data.ub.uib.no/ontology/Exhibition")

    ;

    private String name;

    RDFClassType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
