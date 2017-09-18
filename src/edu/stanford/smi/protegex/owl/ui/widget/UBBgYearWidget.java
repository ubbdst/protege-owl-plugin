package edu.stanford.smi.protegex.owl.ui.widget;


import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.widget.NumberFieldWidget;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.util.InstanceUtil;

import javax.swing.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.awt.*;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author Hemed Ali (hemed.ruwehy@uib.no)
 * @version 1.0
 * @since 2015-11-04
 * Modified: 2017-09-18
 * <p>
 * A Protege OWL Slot Widget plugin for validating xsd:gYear. The widget will be shown to any slot wherever SingleLiteralWidget is
 * applicable to that slot.
 */

public class UBBgYearWidget extends NumberFieldWidget {

    private static final Logger logger = Logger.getLogger(UBBgYearWidget.class.getName());
    private static final String INVALID_INPUT_MSG = "Invalid input";
    private static final String LABEL_GYEAR_FORMAT = " (yyyy)";
    private static final String INVALID_GYEAR_MSG = "Invalid value for gYear: ";


    //A place where SingleLiteralWidget is applicable, this also is applicable
    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return SingleLiteralWidget.isSuitable(cls, slot, facet);
    }

    //A Protege main methord to allow easy debuging
    public static void main(String[] args) {
        edu.stanford.smi.protege.Application.main(args);
    }


    @Override
    public String getLabel() {
        return super.getLabel().concat(LABEL_GYEAR_FORMAT);
    }


    /**
     *  TODO: Do a better validation and do not return null.
     *
     * A method to validate xsd:gYear based on the input string.
     *
     * @param yearString  a string to validate
     *
     */
    private String getValidGYear(String yearString) {
        String xmlGYear;
        try {
            int inputYear = Integer.parseInt(yearString);

            /**
             * Input year should be at most 4 digits number.
             * Note: I had to make sure about this because
             * if input year is greater than 4 digits, it will be truncated by toXMLFormat() method anyway.
             **/
            if (inputYear > 0 && String.valueOf(inputYear).length() > 4) {
                return null;
            }
            //Negative gYear with at most 4 digits is allowed. e.g -0160 for 160BC
            if (inputYear < 0 && String.valueOf(inputYear).length() > 5) {
                return null;
            }

            XMLGregorianCalendar gCalendar = DatatypeFactory
                    .newInstance()
                    .newXMLGregorianCalendarDate(
                            inputYear,
                            DatatypeConstants.FIELD_UNDEFINED,
                            DatatypeConstants.FIELD_UNDEFINED,
                            DatatypeConstants.FIELD_UNDEFINED
                    );
            xmlGYear = gCalendar.toXMLFormat();

        } catch (DatatypeConfigurationException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage());
            xmlGYear = null;
        } catch (NumberFormatException nfe) {
            logger.log(Level.SEVERE, "gYear must be a number: ", nfe.getLocalizedMessage());
            xmlGYear = null;
        } catch (IllegalArgumentException ie) {
            logger.log(Level.SEVERE, ie.getLocalizedMessage());
            xmlGYear = null;
        }
        return xmlGYear;
    }


    /**
     *  This method is called on the value change. The idea is to validate the input value for this slot
     *  whenever there is a change in value.
     */

    @Override
    public Collection getValues() {
        String slotValueWithoutDatatype = InstanceUtil.stripDatatype(getText());
        String gYear = getValidGYear(slotValueWithoutDatatype);
        RDFSLiteral gYearLiteral = null;

        if (gYear == null && slotValueWithoutDatatype != null) {

            //Display error message
            JOptionPane.showMessageDialog(
                    null, "Invalid value \"" + slotValueWithoutDatatype + "\" for gYear",
                    INVALID_INPUT_MSG,
                    JOptionPane.ERROR_MESSAGE
            );
            logger.log(Level.SEVERE, "Invalid input for gYear: [" + slotValueWithoutDatatype + "]");
        }
        if (gYear != null) {
            gYearLiteral = getOWLModel().createRDFSLiteral(gYear, getXSDgYear());
        }

        assignPropertyValueToInstance(gYearLiteral);
        return CollectionUtilities.createCollection(gYearLiteral);
    }


    @Override
    public void setText(String s) {
        String plainText = InstanceUtil.stripDatatype(s);
        super.setText(plainText);
    }


    /**
     * Assign slot value to a corresponding individual.
     */
    private void assignPropertyValueToInstance(Object newValue) {
        if (newValue != null) {
            Collection oldValues = getSubject().getPropertyValues(getPredicate());
            if (!oldValues.contains(newValue)) {
                getSubject().setPropertyValue(getPredicate(), newValue);
            }
        }
    }


    @Override
    public void setInvalidValueBorder() {
        //super.setInvalidValueBorder();
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
        return getOWLModel().getRDFProperty(getSlot().getName());
    }


    /**
     * Get values for this slot, or null if no value
     */
    private Collection getSlotValues() {
        return getInstance().getDirectOwnSlotValues(getSlot());
    }


    private RDFSDatatype getXSDgYear() {
        return getOWLModel().getRDFSDatatypeByURI(XSDDatatype.XSDgYear.getURI());

    }

    private OWLModel getOWLModel() {
        return (OWLModel) getKnowledgeBase();
    }


    /**
     * Invalidate slot widget
     */
    private void invalidateSlot() {
        getTextField().setForeground(Color.RED);
        super.setInvalidValueBorder();
    }


    /**
     * Get text label in bold.
     */
    private String getBoldTextLabel(String inputString) {
        StringBuilder s = new StringBuilder();
        s.append("<html>")
                .append("<strong>")
                .append(INVALID_GYEAR_MSG)
                .append("</strong>")
                .append("\"")
                .append(inputString)
                .append("\"")
                .append("</html>");

        return s.toString();
    }
}



