package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.widget.TextFieldWidget;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Hemed
 */
public class UBBSignatureWidget extends TextFieldWidget {

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {

        //check if a slot accept values of type String,
        // if it does then show this widget in the dropdown list as one of the it's options.
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

    @Override
    public void setValues(Collection values) {
        String id = (String) CollectionUtilities.getFirstItem(values);
        if (UUIDWidget.isValidUUID(id)) {
            //Identifier slot shall not have UUID
            setText("");
            //setInstanceValues();
        } else {
            super.setValues(values);
        }
    }

    /**
     * Validate signature. Signature is not UUID, it is UiB specific number
     */
    private void validateSignature(String signature) {
        //See if there is a need for more validation
        if (UUIDWidget.isValidUUID(signature)) {
                getTextField().setForeground(Color.RED);
                //Display error message
                JOptionPane.showMessageDialog(null, "UUID is not a valid signature", null, JOptionPane.ERROR_MESSAGE);
                //Do not continue
                throw new IllegalArgumentException("Invalid signature for value [" + signature + "]");
        }
    }


    /**
     * Prepare slot value change
     */
    private void prepareValueChange(String currentValue) {
        Slot slot = getKnowledgeBase().getSlot("http://data.ub.uib.no/ontology/classHierarchyURI");
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
     * @param oldValue an old value to be replaced
     * @param newValue a new value
     */
    private void replaceSlotValue(Slot slot, String oldValue, String newValue) {
        int lastPositionOfSeparator = oldValue.lastIndexOf('/');
        if (lastPositionOfSeparator != -1) {
            newValue = oldValue.substring(0, lastPositionOfSeparator + 1) + newValue;
            if (!oldValue.equals(newValue)) {
                if(!valueExists(slot, newValue)){
                    //Execute change
                    getInstance().setDirectOwnSlotValue(slot, newValue);
                }
                else
                    System.out.println("Property value [" + newValue + "] already exists for a class hierarchy URI");
            }
        }
    }


    /**
     * Check if a value exists for a slot in a given instance list.
     */
    private boolean valueExists (Slot slot,  String value) {
        //Restrict the call only to the instances of a selected class
        Collection<Instance> instances = getKnowledgeBase().getDirectInstances(getInstance().getDirectType());
        for (Instance instance : instances) {
            if(instance.hasOwnSlot(slot)){
                Object slotValue = instance.getDirectOwnSlotValue(slot);
                if(slotValue != null && slotValue.toString().equals(value)){
                    return true;
                }
            }
        }
        return false;
    }
}
