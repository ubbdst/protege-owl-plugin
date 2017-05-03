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

import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.classparser.OWLClassParser;

/**
 * An interface for objects that can display (and parse) class expressions.
 * Each OWLModel uses one instance of this interface in places such as the conditions
 * widget and the expression editor.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLClassDisplay {


    /**
     * Gets the display text for a given class expression.
     * This will be used in the <CODE>getBrowserText()</CODE> call of the class.
     *
     * @param cls the class to get the text for
     * @return the display text (not null)
     */
    String getDisplayText(RDFSClass cls);


    /**
     * Gets the keyword for a class of a given type.  Depending on the provided argument
     * this should fork into the corresponding helper methods such as <CODE>getOWLHasValueSymbol()</CODE>
     * if the argument is an OWLHasValue restriction.
     *
     * @param cls the class to get the key for
     * @return the key  (undefined for OWLEnumeratedClasses)
     */
    String getSymbol(OWLAnonymousClass cls);


    /**
     * Gets the keyword used for owl:allValuesFrom restrictions in this rendering.
     * Examples are the reverse A or "only".
     *
     * @return the key for owl:allValuesFrom
     */
    String getOWLAllValuesFromSymbol();


    String getOWLCardinalitySymbol();


    String getOWLComplementOfSymbol();


    String getOWLHasValueSymbol();


    String getOWLIntersectionOfSymbol();


    String getOWLMaxCardinalitySymbol();


    String getOWLMinCardinalitySymbol();


    String getOWLSomeValuesFromSymbol();


    String getOWLUnionOfSymbol();


    /**
     * Gets the associated parser that allows users to enter class expressions in the
     * defined rendering.
     *
     * @return the OWLClassParser
     */
    OWLClassParser getParser();
}
