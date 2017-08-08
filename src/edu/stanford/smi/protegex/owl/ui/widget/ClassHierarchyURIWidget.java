package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.util.InstanceUtil;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * @author Hemed Al Ruwehy
 * @author Øyvind Gjesdal
 *         The University of Bergen Library
 *         2017-04-10
 */
public class ClassHierarchyURIWidget extends TextFieldWidget {
    private static String PATH_SEPARATOR = "/";
    private static transient Logger log = Log.getLogger(ClassHierarchyURIWidget.class);

    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        //Check if a slot accept values of type ANY,
        //if it does, then show this widget in the dropdown list as one of the options.
        boolean isAnyURIDatatype = cls.getTemplateSlotValueType(slot) == ValueType.ANY;
        return isAnyURIDatatype;
    }


    @Override
    public void initialize() {
        super.initialize(false, 1, 1);
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
            setText(constructClassHierarchyURI());
            assignPropertyValueToInstance();
            //setInstanceValues();
        } else {
            super.setValues(values);
        }
        //Disable this widget from being edited by user
        getTextField().setEnabled(false);
    }


    private void assignPropertyValueToInstance() {
        Object oldValue = getSubject().getPropertyValue(getPredicate());
        String text = getText().trim();
        Object newValue = getOWLModel().createRDFSLiteral(text, getDefaultDatatype());
        /*if (text.length() > 0) {
            RDFSDatatype datatype = getOWLModel().getXSDanyURI();
            if (getOWLModel().getXSDstring().equals(datatype)) {
                String language = null;
                if (oldValue instanceof RDFSLiteral) {
                    RDFSLiteral oldLiteral = (RDFSLiteral) oldValue;
                    if (oldLiteral.getLanguage() != null) {
                        language = oldLiteral.getLanguage();
                        newValue = getOWLModel().createRDFSLiteral(text, language);
                    }
                    else {
                        newValue = text;
                    }
                }
                else {
                    newValue = text;
                }
            }
            else {
                newValue = getOWLModel().createRDFSLiteral(text, datatype);
            }
            newValue = getOWLModel().createRDFSLiteral(text, datatype);

        }
        /if (newValue == null) {
            getSubject().setPropertyValue(getPredicate(), null);
        }*/
        if (newValue != null) {
            //newValue = DefaultRDFSLiteral.getPlainValueIfPossible(newValue);
            Collection oldValues = getSubject().getPropertyValues(getPredicate(), true);
            if (!oldValues.contains(newValue)) {
                getSubject().setPropertyValue(getPredicate(), newValue);
            }
        }

    }


    /**
     * Get RDF resource of this slot
     */
    private RDFResource getSubject(){
        return (RDFResource) getInstance();
    }

    /**
     * Get RDF property (this slot)
     */
    private RDFProperty getPredicate() {
        return getOWLModel().getRDFProperty(UBBSlotNames.CLASS_HIERARCHY_URI);
    }


    /**
     * Get default datatype for this widget
     */
    private RDFSDatatype getDefaultDatatype() {
        return getOWLModel().getXSDanyURI();
    }


    /**
     * Build URI for the class hierarchy
     */
    private String constructClassHierarchyURI() {
        String prefix = InstanceUtil.getClassURIPrefix(getInstance());
        try {
            String fullURI = prefix + URLEncoder.encode(getID(), "UTF-8");
            return fullURI.toLowerCase();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return prefix;
    }

    /**
     * Get identifier. Check identifier slot if it has a value, otherwise return instance UUID.
     */
    private String getID() {
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


    /**
     * Check the validity of the URL
     * @param text a text to parse
     */
    public static boolean isValidURI (String text) {
        /*if (isDuplicateURL(text)) {
            log.warning("This URL is already used elsewhere.");
            return false;
        }*/
        try {
            if (text.startsWith("http:") || text.startsWith("file:") ||
                    text.startsWith("mailto:") || text.startsWith("urn:")) {
                new URL(text).toURI();
                return true;
            }
            return false;
        }
        catch (Exception ex) {
            log.warning("Invalid URI. " + ex.getLocalizedMessage());
            return false;
        }

    }


    private boolean isDuplicateURL(String text) {
        Frame frame = getKnowledgeBase().getFrame(text);
        return frame != null && !getInstance().equals(frame);
    }


}
