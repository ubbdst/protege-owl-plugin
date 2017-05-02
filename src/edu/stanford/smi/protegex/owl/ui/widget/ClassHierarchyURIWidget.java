package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.util.InstanceUtil;
import edu.stanford.smi.protegex.owl.model.UBBSlotNames;

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
        String prefix = InstanceUtil.getClassURIPrefix(getInstance());
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
        Instance instance = this.getInstance();
        Slot identifierSlot = getKnowledgeBase().getSlot(UBBSlotNames.IDENTIFIER);
        if (identifierSlot != null) {
            Object slotValue = instance.getDirectOwnSlotValue(identifierSlot);
            if(slotValue != null ){
                return slotValue.toString();
            }
        }
        return UUIDWidget.getUUIDFromInstanceURI(instance);
    }


}
