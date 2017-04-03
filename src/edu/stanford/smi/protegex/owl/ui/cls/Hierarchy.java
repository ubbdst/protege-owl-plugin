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

package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.navigation.NavigationHistorySelectable;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface Hierarchy extends NavigationHistorySelectable {

    /**
     * Creates a clone of this, i.e. a Hierarchy with the same type and at
     * least a similar configuration.
     *
     * @return a clone of this
     */
    Hierarchy createClone();


    HeaderComponent getHeaderComponent();


    RDFSClass getSelectedClass();


    /**
     * Gets the title of this instance (e.g. "Asserted Hierarchy").
     *
     * @return the title for display purposes
     */
    String getTitle();


    /**
     * Gets the general type if this Hierarchy (e.g. "Subsumption").
     *
     * @return the type for display purposes
     */
    String getType();


    /**
     * Checks if this Hierarchy shall be by default synchronized with the asserted
     * class tree.
     *
     * @return true  if this hierarchy shall be synchronized by default
     */
    boolean isDefaultSynchronized();


    void setSelectedClass(RDFSClass cls);
}
