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

import edu.stanford.smi.protegex.owl.model.visitor.Visitable;

/**
 * The common type of RDFSLiteral and RDFResource.
 * This can be used to ensure type safety for variables and method parameters.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface RDFObject extends Visitable {

    String getBrowserText();


    /**
     * Determines whether or not the specified class is structurally
     * equal to this class.  Note that this does not test for
     * structural equivalence using structural subsumption tests.
     *
     * @param object The class to test against.
     * @return <code>true</code> if the class is structurally equal
     *         to this, <code>false</code> if the class is not structurally
     *         equal to this.
     */
    boolean equalsStructurally(RDFObject object);

}
