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

package edu.stanford.smi.protegex.owl.ui.matrix;

import edu.stanford.smi.protegex.owl.model.RDFResource;

import java.util.Collection;

/**
 * An interface for objects that can determine whether a certain
 * Frame shall appear in a MatrixTable.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface MatrixFilter {

    /**
     * Gets the values that shall be taken to build up the contents
     * of the matrix.  These values will not be filtered using the isSuitable
     * method.
     *
     * @return the initial values (Frames)
     */
    Collection getInitialValues();


    /**
     * Gets the name of the filter for display purposes (the tab name).
     *
     * @return the name
     */
    String getName();


    /**
     * Checks whether a given Frame meets the requirements of this filter.
     *
     * @param instance the Frame to test
     * @return true  if the Frame shall be included, false otherwise
     */
    boolean isSuitable(RDFResource instance);
}
