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

package edu.stanford.smi.protegex.owl.model.classdisplay.manchester;

import edu.stanford.smi.protegex.owl.model.classdisplay.AbstractOWLClassDisplay;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;
import edu.stanford.smi.protegex.owl.model.classparser.manchester.ManchesterOWLClassParser;
import edu.stanford.smi.protegex.owl.model.classparser.manchester.ManchesterOWLParserUtil;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ManchesterOWLClassDisplay extends AbstractOWLClassDisplay {


    private static ManchesterOWLClassParser parser;


    public ManchesterOWLClassDisplay() {
        ManchesterOWLParserUtil.setLowerCase(true);
    }


    public String getOWLAllValuesFromSymbol() {
        return ManchesterOWLParserUtil.getAllKeyword();
    }


    public String getOWLCardinalitySymbol() {
        return ManchesterOWLParserUtil.getExactKeyword();
    }


    public String getOWLComplementOfSymbol() {
        return ManchesterOWLParserUtil.getNotKeyword();
    }


    public String getOWLHasValueSymbol() {
        return ManchesterOWLParserUtil.getHasKeyword();
    }


    public String getOWLIntersectionOfSymbol() {
        return ManchesterOWLParserUtil.getAndKeyword();
    }


    public String getOWLMaxCardinalitySymbol() {
        return ManchesterOWLParserUtil.getMaxKeyword();
    }


    public String getOWLMinCardinalitySymbol() {
        return ManchesterOWLParserUtil.getMinKeyword();
    }


    public String getOWLSomeValuesFromSymbol() {
        return ManchesterOWLParserUtil.getSomeKeyword();
    }


    public String getOWLUnionOfSymbol() {
        return ManchesterOWLParserUtil.getOrKeyword();
    }


    public OWLClassParser getParser() {
        if (parser == null) {
            parser = new ManchesterOWLClassParser();
        }
        return parser;
    }


    public static String getUIDescription() {
        return "Manchester OWL Syntax";
    }
}
