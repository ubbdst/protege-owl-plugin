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

package edu.stanford.smi.protegex.owl.ui.explorer;

import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * An interface for objects that define the policy by which child nodes
 * are added to an ExplorerNode.  Objects of this type essentially determine
 * whether a child node shall appear or not, and whether asserted or inferred
 * class relationships shall be taken into account.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ExplorerFilter {

    /**
     * Checks if the tree should display inferred relationships instead
     * of asserted ones.
     *
     * @return true  if inferred superclasses shall be displayed
     */
    boolean getUseInferredSuperclasses();


    /**
     * Checks whether a given superclass (childClass) shall be be displayed
     * as a child of a given subclass node (parentClass).
     *
     * @param parentClass the class displayed by the parent node
     * @param childClass  the class displayed by the potential child node
     * @return true if childClass shall be used as child node for parentClass
     */
    boolean isValidChild(RDFSClass parentClass, RDFSClass childClass);
}
