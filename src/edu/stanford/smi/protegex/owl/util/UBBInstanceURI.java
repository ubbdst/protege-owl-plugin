package edu.stanford.smi.protegex.owl.util;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * Generate Instance URI based on the University of Bergen Library requirements
 * UBB wants the instance URI/instance ID to be in the form of "default_namespace + UUID"
 * e.g http://data.ub.uib.no/instance/7673868d-231e-490d-9c4f-19288e7e668d
 * <p>
 * The generated name is ensured to be unique, because we are checking the knowledge base for every new name
 * and generate new name if one exists from before (though unlikely due to the use of UUID).
 *
 * @author Hemed Al Ruwehy,
 *         2017-04-04
 *         University of Bergen Library
 */
public class UBBInstanceURI {

    private static final String INSTANCE_LABEL = "instance";
    private static final String PATH_SEPARATOR = "/";
    private OWLModel owlModel;
    private static transient Logger log = Log.getLogger(UBBInstanceURI.class);

    public UBBInstanceURI(OWLModel owlModel) {
        this.owlModel = owlModel;
    }

    /**
     * A method to construct a namespace based on University of Bergen Library requirements
     *
     * @param defaultNamespace a default namespace to be appended at the beginning, can be <tt>null</tt>
     */
    public static String constructUBBNamespace(String defaultNamespace) {
        String individualNamespace = "";
        if (defaultNamespace != null && !defaultNamespace.isEmpty()) {
            if (!defaultNamespace.endsWith(PATH_SEPARATOR)) {
                defaultNamespace = defaultNamespace + PATH_SEPARATOR;
            }
            individualNamespace = defaultNamespace + INSTANCE_LABEL + PATH_SEPARATOR;
        }
        return individualNamespace;
    }

    /**
     * Generate frame name and ensure uniqueness
     *
     * @param namespace     a default namespace as prefix
     * @param knowledgeBase a knowledge base
     */
    public static String generateUBBUniqueFrameName(String namespace, KnowledgeBase knowledgeBase) {
        if (knowledgeBase == null) {
            log.severe("Encountered null knowledge base. This is not allowed");
            throw new IllegalArgumentException("KnowledgeBase cannot be null");
        }
        String fullName = constructUBBNamespace(namespace);
        do {
            fullName = fullName +  generateRandomUUID();
        }
        while (knowledgeBase.containsFrame(fullName));

        return fullName;
    }

    /**
     * A wrapper for generating new random UUID (version 4)
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
    private String getDefaultNamespace() {
        return owlModel.getTripleStoreModel().getActiveTripleStore().getDefaultNamespace();
    }

    /**
     * Check if a frame with given name exists in the KnowledgeBase
     */
    private boolean frameExists(String frameName) {
        return owlModel.getHeadFrameStore().getFrame(frameName) != null;
    }


    /**
     * Generate frame name and ensure uniqueness
     */
    public String generateUBBUniqueFrameName() {
        String instanceUri;

        do {
            //Keep generating new name until there is no such name in the knowledge base
            //(it is unlikely due to the use of UUID)
             instanceUri = constructUBBNamespace(getDefaultNamespace()) + generateRandomUUID();
        }
        while (frameExists(instanceUri));
        return instanceUri;
    }

}
