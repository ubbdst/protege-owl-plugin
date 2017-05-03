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

package edu.stanford.smi.protegex.owl.model.event;

import edu.stanford.smi.protege.event.FrameListener;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;

import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface PropertyValueListener extends FrameListener {

    /**
     * Called when the browser text of an RDFResource has changed.
     * This is typically used to trigger repaints in a user interface.
     *
     * @param resource the resource that has changed its browser text
     */
    void browserTextChanged(RDFResource resource);


    /**
     * Called after the name of a resource has changed.
     *
     * @param resource the resource that has changed its name
     * @param oldName  the old name of the resource
     */
    void nameChanged(RDFResource resource, String oldName);


    /**
     * Called after the value of a property has changed at a given resource.
     *
     * @param resource  the resource that has changed its value
     * @param property  the property that has changed at resource
     * @param oldValues a Collection of old values (may not be available for all values)
     */
    void propertyValueChanged(RDFResource resource, RDFProperty property, Collection oldValues);


    /**
     * Called after the visibility of a resource has changed.
     *
     * @param resource the resource that has changed its visibility
     */
    void visibilityChanged(RDFResource resource);
}
