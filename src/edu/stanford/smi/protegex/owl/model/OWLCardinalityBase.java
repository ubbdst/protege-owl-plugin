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

package edu.stanford.smi.protegex.owl.model;

/**
 * The base interface of the various cardinality restrictions.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLCardinalityBase extends OWLRestriction {

    /**
     * Gets the cardinality value in this restriction.
     *
     * @return a positive integer
     */
    int getCardinality();


    /**
     * Gets the qualifier class.  If this is a qualified cardinality restriction, then
     * this is the value of the owl:valuesFrom property.  Otherwise, this method returns
     * owl:Thing.
     *
     * @return owl:Thing or the result of <CODE>getValuesFrom()</CODE>.
     */
    RDFSClass getQualifier();


    /**
     * If this is a qualified cardinality restriction, then this gets the
     * owl:valuesFrom property value.
     *
     * @return the qualifier class or null if this is not a qualified cardinality restriction
     */
    RDFSClass getValuesFrom();


    /**
     * Checks if this is a qualified cardinality restriction.
     * This is true if this has a value for the owl:valuesFrom property.
     *
     * @return true  if this is a qualified cardinality restriction
     */
    boolean isQualified();


    /**
     * Sets the cardinality value in this restriction.
     *
     * @param value the new cardinality value
     */
    void setCardinality(int value);


    void setValuesFrom(RDFSClass value);
}
