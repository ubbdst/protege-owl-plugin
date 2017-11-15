package edu.stanford.smi.protegex.owl.model;

/**
 * @author Hemed Al Ruwehy
 *         University of Bergen Library
 *         2017-04-28
 *
 * I think this is a dirty way to access namespaces,
 * we should have a mechanism to automatically retrive these from the active ontology.
 */
public class UBBOntologyNames {

    //Namespace for UBB ontology
    private static final String UBBONT_NAMESPACE =     "http://data.ub.uib.no/ontology/";

    public static final String UUID =                  UBBONT_NAMESPACE + "uuid";
    public static final String TRASH_CLASS_NAME =      UBBONT_NAMESPACE + "Trash";
    public static final String CLASS_HIERARCHY_URI =   UBBONT_NAMESPACE + "classHierarchyURI";
    public static final String PREVIOUS_IDENTIFIER =   UBBONT_NAMESPACE + "previousIdentifier";
    public static final String HAS_BEEN_MERGED_WITH =  UBBONT_NAMESPACE + "hasBeenMergedWith";

    public static final String TITLE =                 "http://purl.org/dc/terms/title";
    public static final String IDENTIFIER  =           "http://purl.org/dc/terms/identifier";

}
