package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.widget.TextFieldWidget;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Hemed Al Ruwehy
 * @author Øyvind Gjesdal
 *         The University of Bergen Library
 *         2017-04-10
 */
public class ClassHierarchyWidget extends TextFieldWidget {

    //TODO://Refactor class to use Enum

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        //Check if a slot accept values of type String,
        //if it does, then show this widget in the dropdown list as one of the options.
        boolean isString = cls.getTemplateSlotValueType(slot) == ValueType.STRING;

        return isString;
    }

    @Override
    public void setValues(Collection collection) {
        //TODO: Get value from identifier slot, if it does not exist, get UUID instance URI.

        //TODO:
        // Act on changes when identifier value is changed as well as when the direct
        // type changes (e.g. by drag-and-drop to a different hierarchy)
        System.out.println("Oyvind logic: " + rewriteURIFromClassAndIdentifier());
        super.setValues(collection);
    }

    private String rewriteURIFromClassAndIdentifier() {
        //Use identifier if it exists, fallback to ubbont:UUID if not.
        //TODO:This can be either Identifier or UUID respectively
        String identifier = "ubb-dummy".toLowerCase();
        //List<String> rdftype = convertToString(getInstance().getDirectTypes());
        String prefix = mapClassToString(getInstance().getDirectTypes());
        try {
            String fullURI = prefix + "/" + URLEncoder.encode(identifier, "UTF-8");
            return fullURI.toLowerCase();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }


    private List convertToString(Collection<Cls> rdftype) {
        List<String> rdfTypeList = new ArrayList<>();
        for (Cls clazz : rdftype) {
            rdfTypeList.add(clazz.getName());
        }
        return rdfTypeList;
    }


    private String mapClassToString(Collection<Cls> rdftype) {
        int iterations = 0;
        if (rdftype.size() >= 2) {
            System.out.println("Found classes: " + rdftype.toString() + " for instance " + getInstance().getName());
        }

        if (rdftype.size() == 0) {
            throw new IllegalArgumentException("Instance must be connected to a class");
        }
        for (Cls clazz : rdftype) {
            iterations++;
            String className = clazz.getName();

            //TODO: Extract these to a different method
            switch (className) {
                case "http://www.w3.org/2004/02/skos/core#Concept":
                    return "http://data.ub.uib.no/topic";
                case "http://www.w3.org/2004/02/skos/core#ConceptScheme":
                    return "http://data.ub.uib.no/conceptscheme";
                case "http://data.ub.uib.no/ontology/ProxyCollection":
                    return "http://data.ub.uib.no/instance/collection";
                case "http://data.ub.uib.no/ontology/Exhibition":
                    return "http://data.ub.uib.no/exhibition";
                default: {
                    if (iterations == rdftype.size()) {
                        // throw error if type does not have anything after a "#" or "/"
                        if (className.matches("[/#]$")) {
                            throw new IllegalArgumentException("RDFType does not have anything after a \"#\" or \"/\""
                                    + "You might want to change property name [" + className + "] in the ontology"
                            );
                        }
                        String typeName = className.replaceAll(".+[/#]([^/#]+)$", "$1");
                        return "http://data.ub.uib.no/instance/" + typeName;
                    }
                }
            }
        }
        return "";
    }


    private String rdfTypeToURL(List<String> rdftype) {
        if (rdftype.size() >= 2) {
            System.out.println("Found two classes for " + getInstance().getName() + " which are + " + rdftype.toString());
        }
        if (rdftype.size() == 0) {
            throw new IllegalArgumentException("Instance must be connected to a class");
        }
        if (rdftype.contains("http://www.w3.org/2004/02/skos/core#Concept")) {
            return "http://data.ub.uib.no/topic";
        } else if (rdftype.contains("http://www.w3.org/2004/02/skos/core#ConceptScheme")) {
            return "http://data.ub.uib.no/conceptscheme";
        } else if (rdftype.contains("http://data.ub.uib.no/ontology/ProxyCollection")) {
            return "http://data.ub.uib.no/instance/collection";
        } else if (rdftype.contains("http://data.ub.uib.no/ontology/Exhibition")) {
            return "http://data.ub.uib.no/exhibition";
        } else {
            //Cls clazz = (Cls)new ArrayList<>(rdftype).get(0);
            String firstItem = rdftype.get(0);
            // throw error if type does not have anything after a "#" or "/"
            if (firstItem.matches("[/#]$")) {
                throw new IllegalArgumentException("RDFtype does not have anything after a \"#\" or \"/\"" + "" +
                        " Change property name [" + firstItem + "] in the ontology");
            }
            String typename = firstItem.replaceAll(".+[/#]([^/#]+)$", "$1");
            return "http://data.ub.uib.no/instance/" + typename;
        }
    }
}
