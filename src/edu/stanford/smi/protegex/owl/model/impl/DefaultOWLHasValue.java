/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License");  you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is Protege-2000.
 *
 * The Initial Developer of the Original Code is Stanford University. Portions
 * created by Stanford University are Copyright (C) 2007.  All Rights Reserved.
 *
 * Protege was developed by Stanford Medical Informatics
 * (http://www.smi.stanford.edu) at the Stanford University School of Medicine
 * with support from the National Library of Medicine, the National Science
 * Foundation, and the Defense Advanced Research Projects Agency.  Current
 * information about Protege can be obtained at http://protege.stanford.edu.
 *
 */

package edu.stanford.smi.protegex.owl.model.impl;

import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParseException;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.visitor.OWLModelVisitor;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import java.util.Set;

/**
 * A Cls representing a hasValue restriction.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DefaultOWLHasValue extends AbstractOWLRestriction
        implements OWLHasValue {

    /**
     * The unicode operator symbol for this kind of restriction
     */
    public final static char OPERATOR = '\u220B';


    public DefaultOWLHasValue(KnowledgeBase kb, FrameID id) {
        super(kb, id);
    }


    public DefaultOWLHasValue() {
    }


    public void accept(OWLModelVisitor visitor) {
        visitor.visitOWLHasValue(this);
    }


    public void checkFillerText(String text) throws Exception {
        checkFillerText(text, getOnProperty());
    }


    public static void checkFillerText(String text, RDFProperty onProperty) throws OWLClassParseException {
        if (onProperty != null) {
            if (!(onProperty instanceof OWLDatatypeProperty)) {
                OWLModel owlModel = onProperty.getOWLModel();
                OWLClassParser parser = owlModel.getOWLClassDisplay().getParser();
                parser.checkHasValueFiller(owlModel, text);
            }
        }
    }


    public boolean equalsStructurally(RDFObject object) {
        if (object instanceof OWLHasValue) {
            OWLHasValue compCls = (OWLHasValue) object;
            RDFObject compVal;
            if (compCls.getHasValue() instanceof RDFResource) {
                compVal = (RDFResource) compCls.getHasValue();
            }
            else {
                compVal = (RDFObject) compCls.getOWLModel().asRDFSLiteral(compCls.getHasValue());
            }
            RDFObject val;
            if (getHasValue() instanceof RDFResource) {
                val = (RDFResource) getHasValue();
            }
            else {
                val = (RDFObject) compCls.getOWLModel().asRDFSLiteral(getHasValue());
            }
            return getOnProperty().equalsStructurally(compCls.getOnProperty()) &&
                    val.equalsStructurally(compVal);

        }
        return false;
    }

    /*public String getBrowserText() {
       return getBrowserTextPropertyName() + " " + OPERATOR + " " + getBrowserTextFiller();
   } */


    public RDFProperty getFillerProperty() {
        return getOWLModel().getRDFProperty(OWLNames.Slot.HAS_VALUE);
    }


    public String getFillerText() {
        Object value = getHasValue();
        if (value == null) {
            return "";
        }
        else {
            if (value instanceof RDFResource) {
                return ((RDFResource) value).getBrowserText();
            }
            else {
                if (value instanceof String) {
                    return "\"" + value + "\"";
                }
                else {
                    return value.toString();
                }
            }
        }
    }


    public Object getHasValue() {
        return getPropertyValue(getFillerProperty());
    }


    public String getIconName() {
        return OWLIcons.OWL_HAS_VALUE;
    }


    public void getNestedNamedClasses(Set set) {
        if (getHasValue() instanceof RDFSClass) {
            ((RDFSClass) getHasValue()).getNestedNamedClasses(set);
        }
    }


    public char getOperator() {
        return OPERATOR;
    }


    public void setFillerText(String text) throws Exception {
        OWLModel owlModel = getOWLModel();
        OWLClassParser parser = owlModel.getOWLClassDisplay().getParser();
        if (getOnProperty() instanceof OWLDatatypeProperty) {
            Object value = text;
            try {
                value = parser.parseHasValueFiller(owlModel, text);
            }
            catch (Exception ex) {
                // Ignore -> use string as it is
            }
            setHasValue(value);
        }
        else {
            Object value = parser.parseHasValueFiller(owlModel, text);
            setHasValue(value);
        }
    }


    public void setHasValue(Object value) {
        if (value instanceof Double) {
            value = new Float(((Double) value).doubleValue());
        }
        if (value instanceof Long) {
            value = new Integer(((Long) value).intValue());
        }
        setDirectOwnSlotValue(getFillerProperty(), value);
    }
}
