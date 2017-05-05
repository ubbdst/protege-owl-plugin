package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.UBBSlotNames;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.util.InstanceUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * A widget that holds identifier value and
 * modifies class hierarchy URI based on the change of it's value
 *
 * @author Hemed Al Ruwehy
 *         University of Bergen Library
 *         2017-04-27
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
        String slotValue = getText();
        // System.out.println("Slot values: " + slotValue);
        validateSignature(slotValue);
        prepareValueChange(slotValue);
        return CollectionUtilities.createList(slotValue);
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


    private RDFProperty getRDFProperty(String name){
        return getOWLModel().getRDFProperty(name);
    }


    private OWLModel getOWLModel(){
        return (OWLModel)getKnowledgeBase();
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
     * Validate signature. Signature is not UUID, it is UiB specific number
     */
    private void validateSignature(String signature) {
        if (UUIDWidget.isValidUUID(signature)) {
            showErrorMessage("Invalid signature for UUID value [" + signature + "]. Try another one");
            //Do not continue
            throw new IllegalArgumentException("Invalid signature for value [" + signature + "]");
        } else if (slotValueExists(getRDFProperty(UBBSlotNames.IDENTIFIER), signature)) {
                showErrorMessage("Signature \"" + signature + "\" already exists. Please try another one");
                //Do not continue
                throw new IllegalArgumentException("Invalid signature for value [" + signature + "]");
        }
    }


    /**
     * Prepare slot value change
     */
    private void prepareValueChange(String currentValue) {
        Slot slot = getKnowledgeBase().getSlot(UBBSlotNames.CLASS_HIERARCHY_URI);
        if (slot != null) {
            Object slotValue = getInstance().getDirectOwnSlotValue(slot);
            //Update value of a given slot iff it is not the same with this value
            if (slotValue != null) {
                //Fire change
                replaceSlotValue(slot, slotValue.toString(), currentValue);
            }
        }
    }


    /**
     * Replace slot value
     *
     * @param oldValue an old value to be replaced which is in the form
     *                 of "http://data.ub.uib.no/{class_name}/{id}"
     * @param newValue a new value
     */
    private void replaceSlotValue(Slot slot, String oldValue, String newValue) {
        Instance instance = getInstance();
        //Get UUID from instance URI
        String uuid = UUIDWidget.getUUIDFromInstanceURI(instance);
        //Get corresponding class URI prefix
        String classHierarchyPrefix = InstanceUtil.getClassURIPrefix(instance).toLowerCase();

        if (newValue == null) {//if the value is null, replace with default UUID
            instance.setOwnSlotValue(slot, classHierarchyPrefix + uuid);
        }
        else {//Else, perform a background check
            String currentSlotValue = newValue.toLowerCase();
            if (!oldValue.equalsIgnoreCase(classHierarchyPrefix + currentSlotValue)) {
                if (!slotValueExists(getRDFProperty(UBBSlotNames.CLASS_HIERARCHY_URI), classHierarchyPrefix + currentSlotValue)) {
                    //Execute change
                    instance.setDirectOwnSlotValue(slot, classHierarchyPrefix + currentSlotValue);
                }
            }
        }
    }


    /**
     * Check if a value exists for a slot in a given instance list.
     */
    @SuppressWarnings("unchecked")
    private boolean slotValueExists (RDFProperty property, Object value) {
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
     * Check if a value exists for a slot in a given instance list.
     * @deprecated use #slotValueExists(RDFProperty property, Object value)
     */
    @SuppressWarnings("unchecked")
    private boolean slotValueExists (Collection<Instance> instances, Slot slot, String value) {
        //Collection<Instance> instances = getKnowledgeBase().getDirectInstances(getInstance().getDirectType());
        for (Instance instance : instances) {
            Object slotValue = instance.getDirectOwnSlotValue(slot);
            if(slotValue != null) {
                if (slotValue.toString().equals(value)) {
                    return true;
                }
            }
        }
        return false;
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
     * Display popup window with a given message
     */
    private void showErrorMessage(String msg) {
        //Set red color font
        getTextField().setForeground(Color.RED);
        //Set red borders
        setInvalidValueBorder();
        //Display error message in a popup window
        //JOptionPane.showMessageDialog(null, msg, null, JOptionPane.ERROR_MESSAGE);
        ProtegeUI.getModalDialogFactory().showErrorMessageDialog((OWLModel)getKnowledgeBase(), msg);
    }


    /**
     * Display confirm message
     */
    private void showConfirmMessage(String msg) {
        //Set red color font
        getTextField().setForeground(Color.RED);
        //Set red borders
        setInvalidValueBorder();
        //Display error message in a popup window
        int result = JOptionPane.showConfirmDialog(null, msg, null, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            setText("");
            getInstance().setOwnSlotValue(getSlot(), null);
            setInstanceValues();
        } else {
            // Do nothing
        }
    }
}

