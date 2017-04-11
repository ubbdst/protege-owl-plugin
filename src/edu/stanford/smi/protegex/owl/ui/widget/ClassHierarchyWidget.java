package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.util.RDFClassType;
import edu.stanford.smi.protegex.owl.util.UUIDInstanceURI;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collection;

/**
 * @author Hemed Al Ruwehy
 * @author Øyvind Gjesdal
 *         The University of Bergen Library
 *         2017-04-10
 */
public class ClassHierarchyWidget extends TextFieldWidget {
    private static String PATH_SEPARATOR = "/";

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        //Check if a slot accept values of type String,
        //if it does, then show this widget in the dropdown list as one of the options.
        boolean isString = cls.getTemplateSlotValueType(slot) == ValueType.STRING;

        return isString;
    }

    //TODO:Refactor class to use Enum
    //TODO: Set this widget a default for hierarchy slot?


    /**
     * Get OWL model
     */
    public OWLModel getOWLModel() {
        return (OWLModel) getKnowledgeBase();
    }


    /**
     * Get active namespace for the active project
     */
    public String getNamespace() {
        String activeNamespace = new UUIDInstanceURI(getOWLModel()).getNamespaceForActiveProject();
        return UUIDInstanceURI.constructNamespace(activeNamespace);
    }

    @Override
    public void setValues(Collection collection) {
        /*
          TODO:
          1) Get value from identifier slot, if it does not exist, get UUID from instance URI.
        */

        /*
         TODO:
          2) Act on changes when identifier value is changed as well as when the direct type changes (e.g. by drag-and-drop to a different hierarchy)
         */
        System.out.println("Oyvind logic: " + rewriteURIFromClassAndIdentifier());
        super.setValues(collection);
    }

    private String rewriteURIFromClassAndIdentifier() {
        //Use identifier if it exists, fallback to ubbont:UUID if not.
        //TODO:
        //This can be either Identifier or UUID respectively
        //Create a method to retrieve those
        String identifier = "ubb-dummie".toLowerCase();
        //List<String> rdftype = convertToString(getInstance().getDirectTypes());
        String prefix = getClassURIPrefix(getInstance().getDirectTypes()) + PATH_SEPARATOR;
        //String prefix = rdfTypeToURL(rdftype);
        try {
            String fullURI = prefix  +  URLEncoder.encode(identifier, "UTF-8");
            return fullURI.toLowerCase();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return prefix;
    }


    /**
     * Return a default class URI prefix
     */
    private String getDefaultClassURIPrefix(String className) {
        String regex = ".+[/#]([^/#]+)$";
        if (!className.matches(regex)) {
            throw new IllegalArgumentException("RDFType does not have anything after a \"#\" or \"/\""
                    + "You might want to change property name [" + className + "] in the ontology");
        }
        String typeName = className.replaceAll(regex, "$1");
        return getNamespace() + "instance" + PATH_SEPARATOR + typeName;
    }


    /**
     * Return corresponding class URI for a given RDF class type.
     */
    private String getClassURIPrefix(Collection<Cls> rdfTypes) {
        int iterations = 0;
        if (rdfTypes.size() >= 2) {
            System.out.println("Found classes: " + rdfTypes.toString() + " for instance " + getInstance().getName());
        }
        for (Cls clazz : rdfTypes) {
            iterations++;
            String className = clazz.getName();
            if (className.equals(RDFClassType.CONCEPT.getName())) {
                return getNamespace() + "topic";
            } else if (className.equals(RDFClassType.CONCEPT_SCHEME.getName())) {
                return getNamespace() + "conceptscheme";
            } else if (className.equals(RDFClassType.PROXY_COLLECTION.getName())) {
                return getNamespace() + "instance/collection";
            } else if (className.equals(RDFClassType.EXHIBITION.getName())) {
                return getNamespace() + "exhibition";
            } else {
                if (iterations == rdfTypes.size()) {
                    //If we reach the end of the list, and none of the above class names are present,
                    //then fallback to a default class URI prefix
                    return getDefaultClassURIPrefix(className);
                }
            }
        }
        return getNamespace();
    }
}
