package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.CollectionUtilities;
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
public class ClassHierarchyURIWidget extends TextFieldWidget {
    private static String PATH_SEPARATOR = "/";

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        //Check if a slot accept values of type String,
        //if it does, then show this widget in the dropdown list as one of the options.
        boolean isString = cls.getTemplateSlotValueType(slot) == ValueType.STRING;

        return isString;
    }

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
        return new UUIDInstanceURI(getOWLModel()).getNamespaceForActiveProject();
    }

    @Override
    public void setValues(Collection values) {
        /*
         TODO:
          2) Act on changes when identifier value is changed as well as when the direct type changes (e.g. by drag-and-drop to a different hierarchy)
        */
        String savedValue = (String) CollectionUtilities.getFirstItem(values);
        if (savedValue == null) {
            setText(writeClassHierarchyURI());
            setInstanceValues();
        } else {
            super.setValues(values);
        }
        //Disable this widget from being edited by user
        getTextField().setEnabled(false);
    }

    /**
     * Write URI for the class hierarchy
     */
    private String writeClassHierarchyURI() {
        String prefix = getClassURIPrefix(getInstance().getDirectTypes()) + PATH_SEPARATOR;
        try {
            String fullURI = prefix + URLEncoder.encode(getIdentifier(), "UTF-8");
            return fullURI.toLowerCase();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return prefix;
    }

    /**
     * Get identifier. Check identifier slot if it has a value, if it does return it
     * If not, return instance UUID.
     */
    private String getIdentifier() {
        //Process identifier slot
        Slot identifierSlot = getKnowledgeBase().getSlot("http://purl.org/dc/terms/identifier");
        if (identifierSlot != null) {
            Object slotValue = getInstance().getDirectOwnSlotValue(identifierSlot);
            if(slotValue != null ){
                return slotValue.toString();
            }
        }
        return UUIDWidget.getUUIDFromInstanceURI(this.getInstance());
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
