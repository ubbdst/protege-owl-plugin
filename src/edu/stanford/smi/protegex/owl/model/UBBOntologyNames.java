package edu.stanford.smi.protegex.owl.model;

/**
 * These property names are used throughout the system, for example, for generating instance URI, copying,
 * merging, assigning UUID and so forth.
 * The expectation is that, the imported ontology should contain these properties and
 * their corresponding namespaces
 *
 * @author Hemed Al Ruwehy
 * University of Bergen Library
 * 2019-04-03
 */
public class UBBOntologyNames {

    //Namespaces
    public static final String DEFAULT_NAMESPACE = "http://data.ub.uib.no/";
    public static final String UBBONT_NAMESPACE = "http://data.ub.uib.no/ontology/";
    public static final String DCT_TERMS_NAMESPACE = "http://purl.org/dc/terms/";

    // UBBOntology properties
    public static final String UUID = UBBONT_NAMESPACE + "uuid";
    public static final String PREVIOUS_IDENTIFIER = UBBONT_NAMESPACE + "previousIdentifier";
    public static final String TRASH_CLASS_NAME = UBBONT_NAMESPACE + "Trash";
    public static final String CLASS_HIERARCHY_URI = UBBONT_NAMESPACE + "classHierarchyURI";
    public static final String HAS_BEEN_MERGED_WITH = UBBONT_NAMESPACE + "hasBeenMergedWith";
    public static final String HAS_THUMBNAIL = UBBONT_NAMESPACE + "hasThumbnail";

    //Dublin Core properties
    public static final String TITLE = DCT_TERMS_NAMESPACE + "title";
    public static final String IDENTIFIER = DCT_TERMS_NAMESPACE + "identifier";
    public static final String MODIFIED = DCT_TERMS_NAMESPACE + "modified";

}
