package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.TextFieldWidget;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * A widget that copies UUID from the instance URI to a given slot.
 * This widget is disabled for editing.
 * <p>
 *
 * @author Hemed Al Ruwehy
 *         2017-04-06
 *         University of Bergen Library
 */
public class UUIDWidget extends TextFieldWidget {

    private static final String PATH_SEPARATOR = "/";
    private static transient Logger log = Log.getLogger(UUIDWidget.class);

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        //Check if a slot accept values of type String,
        //if it does, then show this widget in the dropdown list as one of the options.
        boolean isString = cls.getTemplateSlotValueType(slot) == ValueType.STRING;

        return isString;
    }

    /**
     * Set value of the slot if it does not have one.
     */
    @Override
    public void setValues(final Collection values) {
        String uuid = (String)CollectionUtilities.getFirstItem(values);
        if (uuid == null) {
            setText(getUUIDFromInstanceURI(getInstance()));
            setInstanceValues();
        } else {
            super.setValues(values);
        }
        //Disable this widget for editing
        getTextField().setEnabled(false);
    }

    /**
     * Extract UUID from instance URI, if it exists
     */
    public static String getUUIDFromInstanceURI(Instance instance) {
        String instanceId = "";
        String instanceURI = instance.getName();
        int lastIndex = instanceURI.lastIndexOf(PATH_SEPARATOR);
        if (lastIndex != -1) {
            String lastPart = instanceURI.substring(lastIndex + 1, instanceURI.length());
            if (isValidUUID(lastPart)) {
                instanceId = lastPart;
            }
        }
        return instanceId;
    }

    /**
     * Check if a string is valid UUID
     */
    public static boolean isValidUUID(String s) {
        try {
            //Fail early
            if(s == null || s.trim().isEmpty()){
                return false;
            }
            //If parsing passes, then we know it is valid UUID
            UUID.fromString(s);
            return true;
        } catch (IllegalArgumentException ie) {
            //log.severe(s + " is not valid UUID. See " + ie.getLocalizedMessage());
            return false;
        }
    }
}
