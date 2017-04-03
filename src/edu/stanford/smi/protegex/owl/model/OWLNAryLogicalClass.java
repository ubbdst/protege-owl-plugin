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

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface OWLNAryLogicalClass extends OWLLogicalClass {

    /**
     * Adds an operand to this.  Note that the API only supports to add operands
     * during initialization.  Once the logical class is set up, it should not be
     * changed.
     *
     * @param operand the Operand to add
     * @see #removeOperand
     */
    void addOperand(RDFSClass operand);


    /**
     * Gets all operands which are named classes.
     *
     * @return the named operands (without duplicates)
     */
    Collection<RDFSNamedClass> getNamedOperands();


    /**
     * Gets the classes that are combined in this logical statement.
     * In the intersection A & B, this returns the classes A and B.
     *
     * @return a Collection of RDFSClass instances
     */
    Collection<RDFSClass> getOperands();


    boolean hasOperandWithBrowserText(String browserText);


    boolean hasSameOperands(OWLNAryLogicalClass other);


    /**
     * Gets the operand classes as an ordered Iterator.
     *
     * @return an Iterator of RDFSClass objects
     */
    Iterator listOperands();


    /**
     * Removes an operand that was previously added to this logical class.
     *
     * @param operand the operand to remove
     * @see #addOperand
     */
    void removeOperand(RDFSClass operand);
}
