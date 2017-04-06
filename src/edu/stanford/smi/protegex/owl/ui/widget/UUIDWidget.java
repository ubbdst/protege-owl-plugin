package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.ValueType;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.TextFieldWidget;

import java.util.Collection;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * A slot widget that will copy instance UUID to the slot.
 * The widget is disabled for editing.
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

        //check if a slot accept values of type String,
        // if it does then show this widget in the dropdown list as one of the it's options.
        boolean isString = cls.getTemplateSlotValueType(slot) == ValueType.STRING;

        return isString;
    }

    /**
     * Set value of the slot if it does not have one.
     *
     * @param values
     **/
    @Override
    public void setValues(final Collection values) {
        String uuid = (String)CollectionUtilities.getFirstItem(values);
        if (uuid == null) {
            setText(getUUIDFromInstanceURI());
            getTextField().setEnabled(false);
            setInstanceValues();
        } else
            super.setValues(values);
    }

    /**
     * Extract UUID from instance URI, if it exists
     */
    private String getUUIDFromInstanceURI() {
        String instanceId = "";
        String instanceURI = getInstance().getName();
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
    private boolean isValidUUID(String s) {
        try {
            UUID.fromString(s);
            return true;
        } catch (StringIndexOutOfBoundsException se) {
            //log.severe(s + " is not valid UUID. See " + se.getLocalizedMessage());
            return false;
        } catch (IllegalArgumentException ie) {
            //log.severe(s + " is not valid UUID. See " + ie.getLocalizedMessage());
            return false;
        }
    }
}
