package edu.stanford.smi.protegex.owl.util;

import com.hp.hpl.jena.shared.NotFoundException;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.actions.DeleteInstanceOrMoveToTrashAction;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static edu.stanford.smi.protegex.owl.model.impl.DefaultRDFSLiteral.*;
import static edu.stanford.smi.protegex.owl.util.UUIDInstanceName.appendPathSeperator;

/**
 * A static utility class for some convenience  methods
 *
 * @author Hemed Al Ruwehy
 * University of Bergen Library
 * <p>
 * 28-04-2017
 */
public class InstanceUtil {

    public static final String PATH_SEPARATOR = "/";
    private static transient Logger log = Log.getLogger(InstanceUtil.class);

    // Used for setting date modified for every instance
    private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private InstanceUtil() {
    }

    /**
     * Remove a given property value from an instance
     *
     * @param instance an instance to modify
     * @param property RDF property
     */
    public static void removePropertyValue(Instance instance, RDFProperty property) {
        if (instance.hasOwnSlot(property) && instance.getDirectOwnSlotValue(property) != null) {
            instance.setDirectOwnSlotValue(property, null);
        }
    }


    /**
     * Sets a given property value to an instance. The old value will be overriden by the new value
     *
     * @param instance an instance to modify
     * @param slot     RDF property
     */
    public static void setPropertyValue(Instance instance, Slot slot, Object value) {
        if (instance.hasOwnSlot(slot)) {
            instance.setDirectOwnSlotValue(slot, value);
        }
    }


    /**
     * Gets all references of type OWLIndividual of the given resource
     *
     * @param resource a resource in which its references need to be fetched
     * @return a map which contains resource and property to which this resource is referred to
     */
    @SuppressWarnings("unchecked")
    public static Map<RDFResource, RDFProperty> getInstanceReferences(RDFResource resource) {
        Map<RDFResource, RDFProperty> instancesAndProperties = new HashMap();
        Collection<Reference> references = resource.getReferences();
        for (Reference reference : references) {
            if (reference.getFrame() instanceof OWLIndividual) {
                RDFResource subject = (RDFResource) reference.getFrame();
                RDFProperty predicate = (RDFProperty) reference.getSlot();
                if (subject != null && predicate != null) {
                    instancesAndProperties.put(subject, predicate);
                }
            }
        }
        return instancesAndProperties;
    }

    /**
     * Check if a given property has inverse relation
     */
    public static boolean isInverseProperty(RDFProperty property) {
        return property.getInverseProperty() != null;
    }


    /**
     * Update a property value from an instance
     *
     * @param instance instance to modify
     * @param property RDF property
     * @param value    new value for the given property
     */
    public static void updatePropertyValue(Instance instance, RDFProperty property, Object value) {
        if (instance.hasOwnSlot(property) && instance.getDirectOwnSlotValue(property) != null) {
            instance.setDirectOwnSlotValue(property, value);
        }
    }

    /**
     * Modify instance properties
     *
     * @param instance      an instance to be modified
     * @param propertiesMap properties map which holds keys as RDF properties and values as RDF property values
     * @since 1.8
     */
    /*public static void modifyProperties(Instance instance, Map<RDFProperty, Optional<?>> propertiesMap) {
        for (Map.Entry<RDFProperty, Optional<?>> entry : propertiesMap.entrySet()) {
            if (entry.getValue().isPresent()) {
                updatePropertyValue(instance, entry.getKey(), entry.getValue().get());
            } else {
                removePropertyValue(instance, entry.getKey());
            }
        }
    }*/
    public static void modifyProperties(Instance instance, Map<RDFProperty, Object> propertiesMap) {
        for (Map.Entry<RDFProperty, Object> entry : propertiesMap.entrySet()) {
            if (entry.getValue() != null) {
                updatePropertyValue(instance, entry.getKey(), entry.getValue());
            } else {
                removePropertyValue(instance, entry.getKey());
            }
        }
    }


    /**
     * Return a default class URI prefix
     */
    private static String getDefaultClassURIPrefix(OWLModel model, String className) {
        String regex = ".+[/#]([^/#]+)$";
        if (!className.matches(regex)) {
            throw new IllegalArgumentException("RDFType does not have anything after a \"#\" or \"/\""
                    + "You might want to change property name [" + className + "] in the ontology");
        }
        String typeName = className.replaceAll(regex, "$1");
        //Lowercase the class label
        if (typeName != null && !typeName.isEmpty()) {
            typeName = typeName.toLowerCase();
        }
        return getNamespace(model) + "instance" + PATH_SEPARATOR + typeName + PATH_SEPARATOR;
    }


    /**
     * Return corresponding class URI for a given RDF class type.
     */
    @SuppressWarnings("unchecked")
    public static String getClassURIPrefix(Instance instance) {
        OWLModel owlModel = (OWLModel) instance.getKnowledgeBase();
        Collection<Cls> rdfTypes = instance.getDirectTypes();
        int iterations = 0;
         /*
         if (rdfTypes.size() >= 2)
            log.info("Found classes: " + rdfTypes.toString() + " for instance " +  instance.getName());
         */
        for (Cls clazz : rdfTypes) {
            iterations++;
            String className = clazz.getName();
            if (className.equals(RDFClassType.CONCEPT.getName())) {
                return getNamespace(owlModel) + "topic" + PATH_SEPARATOR;
            } else if (className.equals(RDFClassType.CONCEPT_SCHEME.getName())) {
                return getNamespace(owlModel) + "conceptscheme" + PATH_SEPARATOR;
            } else if (className.equals(RDFClassType.PROXY_COLLECTION.getName())) {
                return getNamespace(owlModel) + "instance/collection" + PATH_SEPARATOR;
            } else if (className.equals(RDFClassType.EXHIBITION.getName())) {
                return getNamespace(owlModel) + "exhibition" + PATH_SEPARATOR;
            } else {
                if (iterations == rdfTypes.size()) {
                    //If we reach the end of the list, and none of the above class names are present,
                    //then fallback to a default class URI prefix
                    return getDefaultClassURIPrefix(owlModel, className);
                }
            }
        }
        return getNamespace(owlModel);
    }

    /**
     * Gets active namespace for the active project
     */
    private static String getNamespace(OWLModel model) {
        return new UUIDInstanceName(model).getNamespaceForActiveProject();
    }


    /**
     * Encode a given URL string
     *
     * @param url URL to be encoded.
     */
    public static String encodeUrl(String url) {
        String encodedUrl = url.toLowerCase();
        try {
            encodedUrl = URLEncoder.encode(url, "UTF-8");
            return encodedUrl;
        } catch (UnsupportedEncodingException e) {
            log.warning("(UnsupportedEncodingException: " + e.getLocalizedMessage());
        }
        return encodedUrl;
    }


    /**
     * Gets Trash class with Momayo namespace or null if it does not exist
     */
    public static OWLNamedClass getMomayoTrashClass(OWLModel model) {
        return model.getOWLNamedClass(UBBOntologyNames.TRASH_CLASS_NAME);
    }

    /**
     * Gets Trash class or null if it does not exist
     */
    public static OWLNamedClass getOWLClass(OWLModel model, String name) {
        return model.getOWLNamedClass(name);
    }

    /**
     * Gets RDF property or throw an exception if it's name cannot be found in the knowledgebase
     */
    public static RDFProperty getRDFProperty(OWLModel model, String name) {
        RDFProperty property = model.getRDFProperty(name);
        if (property == null) {
            throw new NotFoundException("Cannot find property with name [" + name + "] in the ontology");
        }
        return property;
    }


    /**
     * Gets or creates RDF property. It will only be created if
     * it does not exist in the knowledge base
     */
    public static RDFProperty getOrCreateRDFProperty(OWLModel model, String name) {
        RDFProperty property = model.getRDFProperty(name);
        if (property == null) {
            property = model.createRDFProperty(name);
        }
        return property;
    }


    /**
     * Gets Trash class or create new one if it does not exist
     */
    public static OWLNamedClass getOrCreateTrashClass(OWLModel model) {
        OWLNamedClass trashClass = getMomayoTrashClass(model);
        //If it does not exist, create the default version of the Trash
        if (trashClass == null) {
            trashClass = getDefaultTrashClass(model);
        }
        return trashClass;
    }


    /**
     * Gets default Trash class. If Momayo Trash class is not defined in the ontology, generate new Trash class
     * based on the active ontology
     */
    public static OWLNamedClass getDefaultTrashClass(OWLModel model) {
        OWLNamedClass trashClass = getOWLClass(model, "Trash");
        //If it does not exists, create one.
        if (trashClass == null) {
            trashClass = model.createOWLNamedClass("Trash");
        }
        return trashClass;
    }

    /**
     * Moves instance to a Trash class
     *
     * @param instance an instance to move
     */
    public static void moveInstanceToTrash(RDFResource instance) {
        DeleteInstanceOrMoveToTrashAction.moveInstance(instance, getOrCreateTrashClass(instance.getOWLModel()));
    }


    /**
     * Check if the given individual belongs to a Momayo class Trash
     *
     * @param resource a resource to check
     */
    public static boolean isInTrash(RDFIndividual resource) {
        if (resource != null) {
            OWLNamedClass trashClass = getMomayoTrashClass(resource.getOWLModel());
            return trashClass != null && resource.getProtegeTypes().contains(trashClass);
        }
        return false;
    }

    /**
     * Validates language tag based on xsd:language specification.
     * See http://www.datypic.com/sc/xsd/t-xsd_language.html.
     *
     * @param language a language tag to validate
     * @return true if language tag is valid, otherwise false.
     */
    public static boolean isValidXSDLanguage(String language) {
        String xsdLangRegex = "^[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*$";
        if (language == null || language.trim().isEmpty()) {
            return false;
        }
        return language.matches(xsdLangRegex);
    }

    /**
     * We know that value of this widget is of data type xsd:anyURI and there is no need for users to see
     * the full URI including datatype in the UI. Therefore, we are stripping out datatype part for easy readability.
     *
     * @param rawValue a string with data type
     *                 example: ~@http://www.w3.org/2001/XMLSchema#anyURI http://data.ub.uib.no/instance/document/ubb-ms-02
     * @return a string where datatype is stripped.
     * example: http://data.ub.uib.no/instance/document/ubb-ms-02
     */
    public static String stripDatatype(String rawValue) {
        if (rawValue == null) {
            return "";
        }
        if (rawValue.startsWith(LANGUAGE_PREFIX) || rawValue.startsWith(DATATYPE_PREFIX)) {
            return rawValue.substring(rawValue.indexOf(SEPARATOR) + 1).trim();
        }
        return rawValue;
    }

    /**
     * Updates the date modified for a particular resource. The date will be formatted
     * to dateTime.
     * <br>
     * The method updates or creates (if it does not exist) dct:modified value
     */
    public static void updateDateModified(RDFResource resource, long timeInMillis) {
        if(resource != null) {
            OWLModel model = resource.getOWLModel();
            // Create date-time literal
            RDFSLiteral dateTimeLiteral = model.createRDFSLiteral(
                    dateFormatter.format(timeInMillis),
                    model.getXSDdateTime()
            );

            // Execute change
            OWLUtil.setPropertyValue(
                    resource,
                    getOrCreateRDFProperty(model, UBBOntologyNames.MODIFIED),
                    dateTimeLiteral
            );
        }
    }

    /**
     * Get namespace for the active project, otherwise return default namespace
     */
    public String getNamespaceForActiveProject(OWLModel owlModel) {
        if (owlModel != null) {
            String namespace = owlModel.getTripleStoreModel().getActiveTripleStore().getDefaultNamespace();
            if (namespace != null) {
                return appendPathSeperator(namespace).toLowerCase();
            }
        }
        return UBBOntologyNames.DEFAULT_NAMESPACE;
    }


    /**
     * Checks if a collection is null or empty
     */
    public static boolean isNullOrEmpty(Collection collection) {
        return collection == null || collection.isEmpty();
    }

}



