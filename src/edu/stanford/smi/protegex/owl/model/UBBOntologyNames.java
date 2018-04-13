package edu.stanford.smi.protegex.owl.model;

/**
 * @author Hemed Al Ruwehy
 *         University of Bergen Library
 *         2017-04-28
 *
 * I think this is not an elegent way to access namespaces,
 * we should have a mechanism to automatically retrieve these from the active ontology.
 */
public class UBBOntologyNames {

    //Default UBB namespace
    public static final String DEFAULT_NAMESPACE =    "http://data.ub.uib.no/";
    public static final String UBBONT_NAMESPACE =     "http://data.ub.uib.no/ontology/";
    public static final String MOMAYO_NAMESPACE =     "http://purl.org/momayo/mmo/";

    public static final String UUID =                  MOMAYO_NAMESPACE + "uuid";
    public static final String PREVIOUS_IDENTIFIER =   MOMAYO_NAMESPACE + "previousIdentifier";
    public static final String TRASH_CLASS_NAME =      MOMAYO_NAMESPACE + "Trash";
    public static final String CLASS_HIERARCHY_URI =   MOMAYO_NAMESPACE + "classHierarchyURI";
    public static final String HAS_BEEN_MERGED_WITH =  MOMAYO_NAMESPACE + "hasBeenMergedWith";
    public static final String TITLE =                 "http://purl.org/dc/terms/title";
    public static final String IDENTIFIER  =           "http://purl.org/dc/terms/identifier";

}
