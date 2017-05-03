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
 * The base class of all OWL restriction classes.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLRestriction extends OWLAnonymousClass {


    /**
     * Checks the filler from a textual expression.  This should be called prior to
     * assigning a new filler value.
     *
     * @param value the potential filler value
     * @throws Exception to indicate a parse exception
     */
    void checkFillerText(String value) throws Exception;


    /**
     * Gets the Unicode operator character that is typically used to represent this
     * type of restriction.
     *
     * @return the operator char
     */
    char getOperator();


    /**
     * Gets the Slot that is used to store the filler at this kind of restriction
     * (e.g., owl:cardinality).
     *
     * @return the filler slot
     */
    RDFProperty getFillerProperty();


    /**
     * Gets the filler of this restriction for display purposes.
     *
     * @return the filler text (never null)
     */
    String getFillerText();


    /**
     * Gets the Slot that is restricted by this restriction.
     *
     * @return the value of the owl:onProperty slot at this restriction
     */
    RDFProperty getOnProperty();


    /**
     * Checks whether this restriction has been completely defined already.
     * If this is false, then the user still has to define the restriction values.
     *
     * @return true  if this is completely defined
     */
    boolean isDefined();


    /**
     * Sets the filler from a (valid) textual expression.
     *
     * @param value the new filler value
     * @throws Exception to indicate a parse exception
     */
    void setFillerText(String value) throws Exception;


    /**
     * Sets the restricted property at this restriction.
     *
     * @param property the RDFProperty to restrict
     */
    void setOnProperty(RDFProperty property);
}
