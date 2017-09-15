package edu.stanford.smi.protegex.owl.ui.widget;


import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.widget.TextFieldWidget;
import edu.stanford.smi.protegex.owl.model.*;

import javax.swing.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.awt.*;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.stanford.smi.protegex.owl.ui.widget.ClassHierarchyURIWidget.stripDatatype;

/**
 * @author Hemed Ali (hemed.ruwehy@uib.no)
 * @version 1.0
 * @since 04-11-2015
 * <br />
 * A Protege Slot Widget plugin for validating xsd:gYear.
 */

public class UBBgYearWidget extends TextFieldWidget {

    private static final Logger logger = Logger.getLogger(UBBgYearWidget.class.getName());
    private static final String INVALID_INPUT_MSG = "Invalid input";
    private static final String LABEL_GYEAR_FORMAT = " (yyyy)";
    private static final String INVALID_GYEAR_MSG = "Invalid value for gYear: ";


    /**
     * A place where SingleLiteralWidget is applicable, this also is applicable
     */
    /*public static boolean isSuitable(Class clazz, Cls cls, Slot slot) {
       return true;
    }*/

    //Always show the widget in the dropdownlist
    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return true;
    }

    //A Protege main methord to allow easy debuging
    public static void main(String[] args) {
        edu.stanford.smi.protege.Application.main(args);
    }

    //Initialize widget
    @Override
    public void initialize() {
        super.initialize();
        getTextField().setHorizontalAlignment(SwingConstants.RIGHT);
        setPreferredColumns(1);
        setPreferredRows(1);
    }

    @Override
    public String getLabel() {
        return super.getLabel().concat(LABEL_GYEAR_FORMAT);
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

    /*
     * A method to validate xsd:gYear based on the input string.
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
            //Negative gYear with of atmost 4 digits number is allowed. e.g -0160 for 160BC
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

            //System.out.println("gYear: " + gCalendar.getYear()
            //+ "\ngYear full format: " + gCalendar.toXMLFormat());

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

    /*
     This method is called on the value change.
     The idea is to validate the input whenever there is a change in value.
     The method returns the current value displayed by the widget.
    */
    @Override
    public Collection getValues() {
        //Get the current input value
        String currentSlotValue = stripDatatype(getText());
        String gYear = getValidGYear(currentSlotValue);

        if (gYear == null && currentSlotValue != null) {

            getTextField().setForeground(Color.RED);
            //Display error message
            JOptionPane.showMessageDialog(
                    null,
                    getBoldTextLabel(currentSlotValue),
                    INVALID_INPUT_MSG,
                    JOptionPane.ERROR_MESSAGE
            );

            logger.log(Level.SEVERE, "Invalid input for gYear: " + currentSlotValue);
        }

        System.out.println("Gyear: " + createGyearLiteral(stripDatatype(gYear)).toString());


        assignPropertyValueToInstance(createGyearLiteral(gYear));

        return super.getValues();
    }


    /**
     * Assign slot value to a corresponding individual.
     */
    private void assignPropertyValueToInstance(Object newValue) {
        if (newValue != null) {
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
        return getOWLModel().getRDFProperty(getSlot().getName());
    }


    @Override
    public void setValues(Collection collection) {
        //System.out.println("gYear Collection values: " + getSlotValues());
        setText("");
        super.setValues(getSlotValues());
    }

    /**
     * Get values for this slot, or null if no value
     */
    private Collection getSlotValues() {
        return getInstance().getDirectOwnSlotValues(getSlot());
    }


    @Override
    public void setText(String s) {
        String plainText = stripDatatype(s);
        super.setText(plainText);
    }



    private RDFSDatatype getGyearDatatype() {
        System.out.println("gYear: " + getOWLModel().getRDFSDatatypeByURI(XSDDatatype.XSDgYear.getURI()).getURI() );
        return  getOWLModel().getRDFSDatatypeByURI(XSDDatatype.XSDgYear.getURI());

    }



    /**
     * Create literal with XSD:gYear datatype
     */
    public RDFSLiteral createGyearLiteral(String text){
        return getOWLModel().createRDFSLiteral(text, getGyearDatatype());

    }


    private OWLModel getOWLModel() {
        return (OWLModel)getKnowledgeBase();
    }


}



