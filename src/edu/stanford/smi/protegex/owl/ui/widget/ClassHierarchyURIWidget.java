package edu.stanford.smi.protegex.owl.ui.widget;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.util.InstanceUtil;
import org.apache.commons.validator.routines.UrlValidator;

import java.util.Collection;
import java.util.logging.Logger;

/**
 * A widget to generate class hierarchy URI for the individuals upon creation.
 *
 * @author Hemed Al Ruwehy
 * @author Øyvind Gjesdal
 * The University of Bergen Library
 * 2017-04-10
 */
public class ClassHierarchyURIWidget extends TextFieldWidget {
    private static String PATH_SEPARATOR = "/";
    private static transient Logger log = Log.getLogger(ClassHierarchyURIWidget.class);


    /*
      This widget will show up only if the range defined in the given slot is of datatype xsd:anyURI.
     */
    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        if(slot instanceof RDFProperty){
            RDFProperty classHierarchySlot = (RDFProperty)slot;
            if(classHierarchySlot.hasRange(false) &&
                    classHierarchySlot.getRangeDatatype().getName()
                            .equals(classHierarchySlot.getOWLModel().getXSDanyURI().getName())){
                return true;
            }
        }
        return false;
    }

    /**
     * Check for URI validity without fragment
     *
     * @param name a name to be checked for validity
     */
    public static boolean isValidUriAndWithoutSegment(String name) {
        String[] schemes = {"http", "https"};
        UrlValidator urlValidator = new UrlValidator(schemes, UrlValidator.NO_FRAGMENTS);
        //UrlValidator urlValidator = new UrlValidator(schemes);
        return urlValidator.isValid(name);

    }

    @Override
    public void initialize() {
        super.initialize(false, 4, 1);
    }

    /**
     * Get OWL model
     */
    public OWLModel getOWLModel() {
        return (OWLModel) getKnowledgeBase();
    }

    @Override
    public void setValues(Collection values) {
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


    /**
     * Assign slot value to the corresponding individual.
     */
    private void assignPropertyValueToInstance() {
        Object oldValue = getSubject().getPropertyValue(getPredicate());
        String text = getText();
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
         if (newValue == null) {
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
    private RDFResource getSubject() {
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
        return (prefix + InstanceUtil.encodeUrl(getId()));
    }

    /**
     * Get identifier. Check identifier slot if it has a value, otherwise return instance UUID.
     */
    private String getId() {
        //Process identifier slot
        Instance instance = this.getInstance();
        Slot identifierSlot = getKnowledgeBase().getSlot(UBBSlotNames.IDENTIFIER);
        if (identifierSlot != null) {
            Object slotValue = instance.getDirectOwnSlotValue(identifierSlot);
            if (slotValue != null) {
                return slotValue.toString();
            }
        }
        return UUIDWidget.getUUIDFromInstanceURI(instance);
    }

}
