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

package edu.stanford.smi.protegex.owl.ui.results;

import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * An interface that is typically implemented by TabWidgets, allowing them to
 * interact with global actions such as searching.  For example, if a user
 * double clicks on the search results, a tab implementing this interface can
 * navigate to the selected resource.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface HostResourceDisplay {

    /**
     * Called by actions such as the view action in the FindUsagePanel or the
     * OWLTestResultsPanel.
     * This can be implemented for special handling of this action - otherwise the
     * system will pop up a new window showing the selected RDFResource.
     *
     * @param resource the resource to display
     * @return true if the navigation was handled, false for default behavior
     */
    boolean displayHostResource(RDFResource resource);
}
