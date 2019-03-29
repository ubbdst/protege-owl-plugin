package edu.stanford.smi.protegex.owl.model;

/**
 * These property names are used throughout the system, for copying, merging and so forth.
 * The expectation is that, imported ontology should contain these property names and
 * their corresponding namespaces
 *
 * @author Hemed Al Ruwehy
 *         University of Bergen Library
 *         2017-04-28
 *
 */
public class UBBOntologyNames {

    //Namespaces
    public static final String DEFAULT_NAMESPACE =    "http://data.ub.uib.no/";
    public static final String MOMAYO_MMO =           "http://purl.org/momayo/mmo/";
    public static final String MOMAYO_MMDR =          "http://purl.org/momayo/mmdr/";
    public static final String DCT_TERMS_NAMESPACE =  "http://purl.org/dc/terms/";
    public static final String UBBONT_NAMESPACE =     "http://data.ub.uib.no/ontology/";

    //Momayo Properties
    public static final String UUID =                  MOMAYO_MMO + "uuid";
    public static final String PREVIOUS_IDENTIFIER =   MOMAYO_MMO + "previousIdentifier";
    public static final String TRASH_CLASS_NAME =      MOMAYO_MMO + "Trash";
    public static final String CLASS_HIERARCHY_URI =   MOMAYO_MMO + "classHierarchyURI";
    public static final String HAS_BEEN_MERGED_WITH =  MOMAYO_MMO + "hasBeenMergedWith";
    public static final String HAS_THUMBNAIL_MMDR =    MOMAYO_MMDR + "hasThumbnail";

    //Ubbont namespaces
    public static final String HAS_THUMBNAIL =         UBBONT_NAMESPACE + "hasThumbnail";

    //Dublin Core Properties
    public static final String TITLE =                 DCT_TERMS_NAMESPACE + "title";
    public static final String IDENTIFIER  =           DCT_TERMS_NAMESPACE + "identifier";
    public static final String MODIFIED    =           DCT_TERMS_NAMESPACE + "modified";

}
