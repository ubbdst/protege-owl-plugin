package edu.stanford.smi.protegex.owl.util;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * A class for generating instance (individual) URI using UUID due to the University of Bergen Library (UBB) requirements.
 * UBB wants the instance URI (ID/name) to be in the form of "default_namespace + UUID"
 * e.g http://data.ub.uib.no/7673868d-231e-490d-9c4f-19288e7e668d
 * <p>
 * The generated name ((URI) is ensured to be unique, because we are checking the knowledge base for every new name
 * and if one exists from before (which is unlikely due to the use of UUID), new name is generated.
 *
 * @author Hemed Al Ruwehy,
 *         2017-04-04
 *         University of Bergen Library
 */
public class UUIDInstanceURI implements InstanceURI {

    private static final String INSTANCE_LABEL = "instance";
    private static final String PATH_SEPARATOR = "/";
    private static final String DEFAULT_UBB_NAMESPACE = "http://data.ub.uib.no/";
    private static transient Logger log = Log.getLogger(UUIDInstanceURI.class);
    private OWLModel owlModel;

    public UUIDInstanceURI(OWLModel owlModel) {
        this.owlModel = owlModel;
    }

    /**
     * A method to construct a namespace based on University of Bergen Library requirements
     *
     * @param defaultNamespace a default namespace to be appended at the beginning, can be <tt>null</tt>
     */
    public static String constructNamespace(String defaultNamespace) {
        String individualNamespace = defaultNamespace;
        if (defaultNamespace != null && !defaultNamespace.isEmpty()) {
            if (!defaultNamespace.endsWith(PATH_SEPARATOR)) {
                individualNamespace = defaultNamespace + PATH_SEPARATOR;
            }
        }
        return individualNamespace;
    }

    /**
     * Generate frame name and ensure uniqueness
     *
     * @param namespace     a default namespace as prefix
     * @param knowledgeBase a knowledge base
     */
    public static String generateUniqueInstanceName(String namespace, KnowledgeBase knowledgeBase) {
        if (knowledgeBase == null) {
            log.severe("Encountered null knowledge base. This is not allowed");
            throw new IllegalArgumentException("Knowledge base cannot be null");
        }
        String fullName = constructNamespace(namespace);
        do {
            fullName = fullName + generateRandomUUID();
        }
        while (knowledgeBase.containsFrame(fullName));

        return fullName;
    }

    /**
     * A static wrapper for generating random UUID (type 4)
     */
    public static String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Get OWL model
     */
    public OWLModel getOWLModel() {
        return owlModel;
    }

    /**
     * Get default namespace
     */
    private String getNamespaceForActiveProject() {
        if(owlModel != null ) {
            return owlModel.getTripleStoreModel().getActiveTripleStore().getDefaultNamespace();
        }
        return DEFAULT_UBB_NAMESPACE;
    }

    /**
     * Check if an instance (frame) with given name exists in the knowledge base
     *
     * @return {@code true} if instance exists, otherwise {@code false}
     */
    private boolean instanceExists(String frameName) {
        if(owlModel == null){
            throw new IllegalArgumentException("OWLModel is null hence cannot check the knowledge");
        }
        return owlModel.getHeadFrameStore().getFrame(frameName) != null;
    }


    /**
     *Get default namespace for the University of Bergen
     */
    public static String getUBBDefaultNamespace(){
        return DEFAULT_UBB_NAMESPACE;
    }


    /**
     * Generate frame name and ensure uniqueness
     */
    @Override
    public String generateUniqueInstanceName() {
        String instanceUri;

        do {
            //Keep generating new name until there is no such name in the knowledge base
            //(though unlikely due to the use of UUID)
            instanceUri = constructNamespace(getNamespaceForActiveProject()) + generateRandomUUID();
        }
        while (instanceExists(instanceUri));
        return instanceUri;
    }

    @Override
    public String toString() {
        return "Instance URI generator for the University of Bergen Library";
    }
}
