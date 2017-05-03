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

package edu.stanford.smi.protegex.owl.model.classdisplay;

import java.util.Collection;
import java.util.Iterator;

import edu.stanford.smi.protegex.owl.model.OWLAllValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLCardinality;
import edu.stanford.smi.protegex.owl.model.OWLComplementClass;
import edu.stanford.smi.protegex.owl.model.OWLEnumeratedClass;
import edu.stanford.smi.protegex.owl.model.OWLHasValue;
import edu.stanford.smi.protegex.owl.model.OWLIntersectionClass;
import edu.stanford.smi.protegex.owl.model.OWLMaxCardinality;
import edu.stanford.smi.protegex.owl.model.OWLMinCardinality;
import edu.stanford.smi.protegex.owl.model.OWLNAryLogicalClass;
import edu.stanford.smi.protegex.owl.model.OWLQuantifierRestriction;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.model.OWLUnionClass;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;

/**
 * A basic implementation of OWLClassRenderer which uses infix notation based on the
 * keys defined by the various methods in the implementing classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class AbstractOWLClassDisplay implements OWLClassDisplay {

	public AbstractOWLClassDisplay() {

	}

    public String getDisplayText(RDFSClass cls) {
        if (cls instanceof RDFSNamedClass) {
            return cls.getBrowserText();
        }
        else if (cls instanceof OWLRestriction) {
            return getCommentText(cls) + getDisplayTextOfOWLRestriction((OWLRestriction) cls);
        }
        else if (cls instanceof OWLNAryLogicalClass) {
            return getCommentText(cls) + getDisplayTextOfOWLNAryLogicalClass((OWLNAryLogicalClass) cls);
        }
        else if (cls instanceof OWLComplementClass) {
            return getCommentText(cls) + getDisplayTextOfOWLComplementClass((OWLComplementClass) cls);
        }
        else if (cls instanceof OWLEnumeratedClass) {
            return getCommentText(cls) + getDisplayTextOfOWLEnumeratedClass((OWLEnumeratedClass) cls);
        }
        else {
            return null;
        }
    }

	protected String getCommentText(RDFSClass cls) {
		if(cls.isAnonymous()) {			
			RDFProperty isCommmentedOut = cls.getOWLModel().getRDFProperty(ProtegeNames.Slot.IS_COMMENTED_OUT);
			if(isCommmentedOut != null && cls.getPropertyValue(isCommmentedOut) != null) {
				return "// ";
			}			
		}
		return "";
	}


    public String getSymbol(OWLAnonymousClass cls) {
        if (cls instanceof OWLRestriction) {
            if (cls instanceof OWLSomeValuesFrom) {
                return getOWLSomeValuesFromSymbol();
            }
            else if (cls instanceof OWLAllValuesFrom) {
                return getOWLAllValuesFromSymbol();
            }
            else if (cls instanceof OWLHasValue) {
                return getOWLHasValueSymbol();
            }
            else if (cls instanceof OWLCardinality) {
                return getOWLCardinalitySymbol();
            }
            else if (cls instanceof OWLMaxCardinality) {
                return getOWLMaxCardinalitySymbol();
            }
            else if (cls instanceof OWLMinCardinality) {
                return getOWLMinCardinalitySymbol();
            }
            else {
                throw new IllegalArgumentException("Unknown restriction type " + cls.getClass());
            }
        }
        else if (cls instanceof OWLIntersectionClass) {
            return getOWLIntersectionOfSymbol();
        }
        else if (cls instanceof OWLUnionClass) {
            return getOWLUnionOfSymbol();
        }
        else if (cls instanceof OWLComplementClass) {
            return getOWLComplementOfSymbol();
        }
        else {
            throw new IllegalArgumentException("Unexpected class type " + cls.getClass());
        }
    }


    protected String getDisplayTextOfOWLComplementClass(OWLComplementClass cls) {
        String key = getOWLComplementOfSymbol();
        if (key.length() > 1) {
            return key + " " + getNestedDisplayText(cls.getComplement());
        }
        else {
            return key + getNestedDisplayText(cls.getComplement());
        }
    }


    protected String getDisplayTextOfOWLEnumeratedClass(OWLEnumeratedClass cls) {
        Collection values = cls.getOneOf();
        String str = "{";
        for (Iterator it = values.iterator(); it.hasNext();) {
            RDFResource resource = (RDFResource) it.next();
            str += resource.getBrowserText();
            if (it.hasNext()) {
                str += " ";
            }
        }
        return str + "}";
    }


    protected String getDisplayTextOfOWLRestriction(OWLRestriction restriction) {
        RDFProperty onProperty = restriction.getOnProperty();
        return (onProperty != null ? onProperty.getBrowserText() : "?")
                + " " + getSymbol(restriction) + " " + getOWLRestrictionFillerText(restriction);
    }


    protected String getOWLRestrictionFillerText(OWLRestriction restriction) {
        if (restriction instanceof OWLQuantifierRestriction) {
            RDFResource filler = ((OWLQuantifierRestriction) restriction).getFiller();
            if (filler instanceof RDFSClass) {
                return getNestedDisplayText((RDFSClass) filler);
            }
            else {
                return restriction.getFillerText();
            }
        }
        else {
            return restriction.getFillerText();
        }
    }


    protected String getDisplayTextOfOWLNAryLogicalClass(OWLNAryLogicalClass cls) {
        final Collection operands = cls.getOperands();
        if (operands.size() == 0) {
            return "<empty " + cls.getClass() + ">";
        }
        String operator = getSymbol(cls);
        String text = "";
        for (Iterator it = operands.iterator(); it.hasNext();) {
            RDFSClass operand = (RDFSClass) it.next();
            text += getNestedDisplayText(operand);
            if (it.hasNext()) {
                text += " " + operator + " ";
            }
        }
        return text;
    }


    protected String getNestedDisplayText(RDFSClass cls) {
        if (cls instanceof RDFSNamedClass || cls instanceof OWLEnumeratedClass || cls instanceof OWLComplementClass) {
            return getDisplayText(cls);
        }
        else {
            return "(" + getDisplayText(cls) + ")";
        }
    }
}
