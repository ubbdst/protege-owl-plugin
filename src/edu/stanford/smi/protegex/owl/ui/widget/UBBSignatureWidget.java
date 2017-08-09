package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.util.InstanceUtil;

import java.awt.*;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * A widget that holds identifier value and
 * modifies value for classhierarchyURI slot (http://data.ub.uib.no/ontology/classHierarchyURI")
 * based on the change of it's value
 *
 * @author Hemed Al Ruwehy
 * University of Bergen Library
 * 2017-04-27
 */
public class UBBSignatureWidget extends TextFieldWidget {
    private static transient Logger log = Log.getLogger(UBBSignatureWidget.class);

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {

        //Check if a slot accept values of type String,
        //if it does then show this widget in the dropdown list as one of the it's options.
        boolean isString = cls.getTemplateSlotValueType(slot) == ValueType.STRING;

        return isString;
    }

    /*
      This method is called on value change.
     */
    @Override
    public Collection getValues() {
        String newValue = getText();
        //System.out.println("Slot values: " + slotValue);
        validateSignature(newValue);
        //Value change will be executed in this given slot
        Slot classHierarchySlot = getKnowledgeBase().getSlot(UBBSlotNames.CLASS_HIERARCHY_URI);
        prepareValueChange(classHierarchySlot, newValue, getDefaultDatatype());
        return CollectionUtilities.createList(newValue);
    }

    /**
     * Identifier slot will get it's value when editing,
     * we don't have to do anything when instance is created
     */
    @Override
    public void setValues(Collection collection) {
        //System.out.println("Signature Collection values: " + getSlotValues());
        super.setValues(getSlotValues());
    }

    /**
     * Get values for this slot, or null if no value
     */
    private Collection getSlotValues() {
        return getInstance().getDirectOwnSlotValues(getSlot());
    }


    private RDFProperty getPredicate(String name) {
        return getOWLModel().getRDFProperty(name);
    }


    private OWLModel getOWLModel() {
        return (OWLModel) getKnowledgeBase();
    }

    /*public void setValues(Collection values) {
        String id = (String) CollectionUtilities.getFirstItem(values);
        //Identifier slot shall not have UUID
        if (UUIDWidget.isValidUUID(id)) {
            setText("");
        }
        else {
            super.setValues(values);
        }
    }*/

    /**
     * Validate signature. Signature is not UUID, and is not a URI, it is UiB specific number
     */
    private void validateSignature(String signature) {
        if (UUIDWidget.isValidUUID(signature)) {
            showErrorMessage("Encountered UUID [" + signature + "] which is not valid signature. Try another one");
            //Do not continue
            throw new IllegalArgumentException("Encounted UUID [" + signature + "] which is not valid signature");
        } else if (ClassHierarchyURIWidget.isValidUriAndWithoutSegment(signature)) {
            showErrorMessage("Encountered URI [" + signature + "] which is not valid signature. Try another one");
            //Do not continue
            throw new IllegalArgumentException("URI [" + signature + "] is not a valid signature");
        } else if (slotValueExists(getPredicate(UBBSlotNames.IDENTIFIER), signature)) {
            showErrorMessage("Signature \"" + signature + "\" already exists. Try another one");
            //Do not continue
            throw new IllegalArgumentException("Signature already exists for value [" + signature + "]");
        }
    }


    /**
     * Prepare value change for the classHierarchyURI widget
     *
     * @param slot  a slot that it's value need to be changed
     * @param value a new value
     */
    private void prepareValueChange(Slot slot, String value, RDFSDatatype datatype) {
        if (slot != null) {
            Object slotValue = getInstance().getDirectOwnSlotValue(slot);
            //Update value of a given slot iff it is not the same with this value
            if (slotValue != null && slotValue instanceof String) {
                //Fire change
                replaceSlotValue(slot, (String) slotValue, value, datatype);
            }
        }
    }


    /**
     * Replace old value to a new value for a given slot
     *
     * @param oldValue an old value to be replaced which is in the form
     *                 of "http://data.ub.uib.no/{class_name}/{id}"
     * @param newValue a new value
     * @param datatype an optional data type to be applied to a new value. Can be <tt>null</tt>
     */
    private void replaceSlotValue(Slot slot, String oldValue, String newValue, RDFSDatatype datatype) {
        Instance instance = getInstance();
        //Get UUID from instance URI
        String uuid = UUIDWidget.getUUIDFromInstanceURI(instance);
        //Get corresponding class URI prefix
        String classHierarchyPrefix = InstanceUtil.getClassURIPrefix(instance).toLowerCase();

        if (newValue == null) {//if value is null, replace with default UUID
            //Create a value of XSD:anyURI datatype
            Object defaultVal = classHierarchyPrefix + uuid;
            if (datatype != null) {
                defaultVal = getOWLModel().createRDFSLiteral(classHierarchyPrefix + uuid, datatype);
            }

            instance.setOwnSlotValue(slot, defaultVal);
        }
        else {//Else, perform a background check
            String currentSlotValue = classHierarchyPrefix + newValue.toLowerCase();
            if (!oldValue.equalsIgnoreCase(currentSlotValue)) {
                if (!slotValueExists(getPredicate(UBBSlotNames.CLASS_HIERARCHY_URI), currentSlotValue) &&
                        ClassHierarchyURIWidget.isValidUriAndWithoutSegment(currentSlotValue)) {
                    Object newVal = currentSlotValue;
                    if (datatype != null) {
                        //Create a value of XSD:anyURI datatype
                        newVal = getOWLModel().createRDFSLiteral(currentSlotValue, datatype);
                    }
                    //If all is well, then execute change
                    RDFProperty predicate = getPredicate(UBBSlotNames.CLASS_HIERARCHY_URI);
                    getSubject().setPropertyValue(predicate, newVal);
                }
            }
        }
    }


    /**
     * Check if a value exists for a slot in a given instance list.
     */
    @SuppressWarnings("unchecked")
    private boolean slotValueExists(RDFProperty property, Object value) {
        if (value != null) {
            Collection<RDFResource> resources = getOWLModel().getRDFResourcesWithPropertyValue(property, value);
            //Check if slot value exists for the resource other than this one.
            for (RDFResource resource : resources) {
                if (!resource.getName().equalsIgnoreCase(getInstance().getName())) {
                    log.info("Value [" + value + "] already exists for property " +
                            "[" + property.getName() + "]" + " of resource [" + resource.getName() + "]");
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Get RDF resource
     */
    private RDFResource getSubject() {
        return (RDFResource) getInstance();
    }


    /**
     * Get default datatype for this widget
     */
    private RDFSDatatype getDefaultDatatype() {
        return getOWLModel().getXSDanyURI();
    }


    /**
     * Check if a value exists for a slot in a given instance list.
     * Using streams is faster for large collection.
     * For JDK >= 1.8
     */
     /*private boolean slotValueExists(Collection<Instance> instances, Slot slot, String value) {
        //Collection<Instance> instances = getKnowledgeBase().getDirectInstances(getInstance().getDirectType());
        return instances
                .parallelStream()
                .map(instance -> instance.getDirectOwnSlotValue(slot))
                .anyMatch(slotValue -> slotValue != null && slotValue.toString().equalsIgnoreCase(value));
     }*/


    /**
     * Display popup error window with a given message
     */
    private void showErrorMessage(String msg) {
        //Set red color font
        getTextField().setForeground(Color.RED);
        //Set red borders
        setInvalidValueBorder();
        //Display error message in a popup window
        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(getOWLModel(), msg);
    }

}

