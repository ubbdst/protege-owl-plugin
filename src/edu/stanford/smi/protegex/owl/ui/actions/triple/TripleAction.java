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

package edu.stanford.smi.protegex.owl.ui.actions.triple;

import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.ui.actions.IconOwner;

/**
 * An object representing an "action" that can be performed on a given Triple.
 * Instances of this class can be added to context menus of single property values.
 * A typical use case for this is to add right-click menu items to the TriplesTable.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface TripleAction extends IconOwner {

    /**
     * Gets an (optional) group name, allowing tools to display action items in groups.
     *
     * @return a group name or null
     */
    String getGroup();


    /**
     * Gets the name of the action to appear on screen.
     *
     * @return the name of this action (should not be null)
     */
    String getName();


    /**
     * Tests if this action can be applied to a given Triple.
     *
     * @param triple the Triple to apply this to
     * @return true if this can be applied to triple
     */
    boolean isSuitable(Triple triple);


    /**
     * Performs the action to a given Triple.
     *
     * @param triple the Triple to operate on
     */
    void run(Triple triple);
}
