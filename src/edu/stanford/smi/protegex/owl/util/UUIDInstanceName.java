package edu.stanford.smi.protegex.owl.util;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * A class for generating instance (individual) URI using UUID due to the University of Bergen Library (UBB) requirements.
 * UBB wants the instance URI to be in the form of "default_namespace" + "id" + "uuid"
 * e.g http://data.ub.uib.no/id/7673868d-231e-490d-9c4f-19288e7e668d
 * <p>
 * The generated name (URI) is ensured to be unique, because we are checking the knowledge base for every new name
 * and if one exists from before (which is unlikely due to the use of UUID), new name is generated.
 *
 * @author Hemed Al Ruwehy,
 *         2017-04-04
 *         University of Bergen Library
 */
public class UUIDInstanceName implements InstanceNameGenerator {

    private static final String ID_HOLDER             = "id/" ;
    private static final String PATH_SEPARATOR        = "/";
    private static final String UNDEFINED_NAMESPACE   = "undefined_namespace/";
    private static final String DEFAULT_UBB_NAMESPACE = "http://data.ub.uib.no/";
    private static transient Logger log = Log.getLogger(UUIDInstanceName.class);
    private OWLModel owlModel;

    public UUIDInstanceName(OWLModel owlModel) {
        this.owlModel = owlModel;
    }

    /**
     * A method to construct a namespace based on University of Bergen Library requirements
     *
     * @param defaultNamespace a default namespace to be appended at the beginning, can be <tt>null</tt>
     */
    public static String appendPathSeperator(String defaultNamespace) {
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
        String instanceUri;
        if (knowledgeBase == null) {
            throw new IllegalArgumentException("Knowledge base cannot be null");
        }
        String fullName = appendPathSeperator(namespace);
        do {
            instanceUri = fullName + ID_HOLDER + generateRandomUUID();
        }
        while (knowledgeBase.containsFrame(instanceUri));

        return instanceUri;
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
     * Get default namespace and throw exception if such namespace is not set.
     */
    public String getNamespaceForActiveProject() {
        if(owlModel != null ) {
            String namespace = owlModel.getTripleStoreModel().getActiveTripleStore().getDefaultNamespace();
            if(namespace == null) {
                showErrorMessage(owlModel, "No default namespace found. Please add default namespace before creating new instance");
                throw new IllegalArgumentException("No default namespace found");
                //return UNDEFINED_NAMESPACE;
            }
            return appendPathSeperator(namespace).toLowerCase();
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
            throw new IllegalArgumentException("OWLModel is null hence cannot check the knowledge base");
        }
        return owlModel.getHeadFrameStore().getFrame(frameName) != null;
    }

    /**
     * Get default namespace for the University of Bergen
     */
    public static String getUBBDefaultNamespace(){
        return DEFAULT_UBB_NAMESPACE;
    }


    /**
     * Generate frame name and ensure uniqueness
     */
    @Override
    public String generateUniqueName() {
        String instanceUri;
        do {
            //Keep generating new name until there is no such name in the knowledge base
            //(though unlikely due to the use of UUID)
            instanceUri = getNamespaceForActiveProject() + ID_HOLDER + generateRandomUUID();
        }
        while (instanceExists(instanceUri));
        return instanceUri;
    }

    @Override
    public String toString() {
        return "Instance URI generator for the University of Bergen Library";
    }


    /**
     * Display popup error window with a given message
     */
    private void showErrorMessage(OWLModel model, String msg) {
        //Display error message in a popup window
        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(model, msg);
    }
}
