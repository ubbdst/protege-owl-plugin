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

package edu.stanford.smi.protegex.owl.model.classdisplay.compact;

import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.classdisplay.AbstractOWLClassDisplay;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.classparser.ParserUtils;
import edu.stanford.smi.protegex.owl.model.classparser.compact.CompactOWLClassParser;
import edu.stanford.smi.protegex.owl.model.impl.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CompactOWLClassDisplay extends AbstractOWLClassDisplay {

    private static OWLClassParser parser = new CompactOWLClassParser();


    // Overloaded to improve performance
    protected String getDisplayTextOfOWLRestriction(OWLRestriction restriction) {
        RDFProperty onProperty = restriction.getOnProperty();
        return (onProperty != null ? onProperty.getBrowserText() : "?") +
                " " + restriction.getOperator() +
                " " + getOWLRestrictionFillerText(restriction);
    }


    public String getOWLAllValuesFromSymbol() {
        return String.valueOf(DefaultOWLAllValuesFrom.OPERATOR);
    }


    public String getOWLCardinalitySymbol() {
        return String.valueOf(DefaultOWLCardinality.OPERATOR);
    }


    public String getOWLComplementOfSymbol() {
        return String.valueOf(DefaultOWLComplementClass.OPERATOR);
    }


    public String getOWLHasValueSymbol() {
        return String.valueOf(DefaultOWLHasValue.OPERATOR);
    }


    public String getOWLIntersectionOfSymbol() {
        return String.valueOf(DefaultOWLIntersectionClass.OPERATOR);
    }


    public String getOWLMaxCardinalitySymbol() {
        return String.valueOf(DefaultOWLMaxCardinality.OPERATOR);
    }


    public String getOWLMinCardinalitySymbol() {
        return String.valueOf(DefaultOWLMinCardinality.OPERATOR);
    }


    public String getOWLSomeValuesFromSymbol() {
        return String.valueOf(DefaultOWLSomeValuesFrom.OPERATOR);
    }


    public String getOWLUnionOfSymbol() {
        return String.valueOf(DefaultOWLUnionClass.OPERATOR);
    }


    public OWLClassParser getParser() {
        return parser;
    }
}
