package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.model.UBBSlotNames;

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
        } else if (slotValueExists(getKnowledgeBase().getInstances(), getSlot(), signature)) {
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
        int lastPositionOfSeparator = oldValue.lastIndexOf('/');
        if (lastPositionOfSeparator != -1) {
            newValue = oldValue.substring(0, lastPositionOfSeparator + 1) + newValue;
            if (!oldValue.equals(newValue)) {
                //Restrict the call only to the instances of a selected class
                Collection<Instance> instances = getKnowledgeBase().getDirectInstances(getInstance().getDirectType());
                if (!slotValueExists(instances, slot, newValue)) {
                    //Execute change
                    getInstance().setDirectOwnSlotValue(slot, newValue);
                } else
                    log.info("Slot value [" + newValue + "] already exists for a classHierarchyURI widget");
            }
        }
    }


    /**
     * Check if a value exists for a slot in a given instance list.
     * For JDK >= 1.7
     */
    /*private boolean slotValueExists (Collection<Instance> instances, Slot slot, String value) {
        //Collection<Instance> instances = getKnowledgeBase().getDirectInstances(getInstance().getDirectType());
        for (Instance instance : instances) {
            if(instance.hasOwnSlot(slot)){
                Object slotValue = instance.getDirectOwnSlotValue(slot);
                if(slotValue != null && slotValue.toString().equals(value)){
                    return true;
                }
            }
        }
        return false;
    }*/

    /**
     * Check if a value exists for a slot in a given instance list.
     * Using streams is faster for large collection.
     * For JDK >= 1.8
     */
    private boolean slotValueExists(Collection<Instance> instances, Slot slot, String value) {
        //Collection<Instance> instances = getKnowledgeBase().getDirectInstances(getInstance().getDirectType());
        return instances
                .parallelStream()
                .map(instance -> instance.getDirectOwnSlotValue(slot))
                .anyMatch(slotValue -> slotValue != null && slotValue.toString().equals(value));
    }


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
        ProtegeUI.getModalDialogFactory().showErrorMessageDialog((OWLModel) getKnowledgeBase(), msg);
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

