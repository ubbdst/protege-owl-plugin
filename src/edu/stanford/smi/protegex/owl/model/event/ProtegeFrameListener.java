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

import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protege.event.FrameListener;

/**
 * A wrapper to deprecate most methods of the Protege FrameListener interface.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 * @see PropertyValueListener
 */
public interface ProtegeFrameListener extends FrameListener {


    /**
     * @see PropertyValueListener#browserTextChanged
     * @deprecated
     */
    public void browserTextChanged(FrameEvent event);


    /**
     * @deprecated did not work in Protege anyway
     */
    public void deleted(FrameEvent event);


    /**
     * @see PropertyValueListener#nameChanged
     * @deprecated
     */
    public void nameChanged(FrameEvent event);


    /**
     * @deprecated not supported in OWL
     */
    public void ownFacetAdded(FrameEvent event);


    /**
     * @deprecated not supported in OWL
     */
    public void ownFacetRemoved(FrameEvent event);


    /**
     * @deprecated not supported in OWL
     */
    public void ownFacetValueChanged(FrameEvent event);


    /**
     * @deprecated not needed
     */
    public void ownSlotAdded(FrameEvent event);


    /**
     * @deprecated not needed
     */
    public void ownSlotRemoved(FrameEvent event);


    /**
     * @see PropertyValueListener#propertyValueChanged
     * @deprecated
     */
    public void ownSlotValueChanged(FrameEvent event);


    /**
     * @see PropertyValueListener#visibilityChanged
     * @deprecated
     */
    public void visibilityChanged(FrameEvent event);
}
