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

import edu.stanford.smi.protege.event.ClsEvent;
import edu.stanford.smi.protege.event.ClsListener;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ProtegeClsListener extends ClsListener {

    /**
     * @see ClassListener#instanceAdded
     * @deprecated
     */
    void directInstanceAdded(ClsEvent event);


    /**
     * @see ClassListener#instanceRemoved
     * @deprecated
     */
    void directInstanceRemoved(ClsEvent event);


    /**
     * @see ClassListener#subclassAdded
     * @deprecated
     */
    void directSubclassAdded(ClsEvent event);


    /**
     * @deprecated not supported by OWL
     */
    void directSubclassMoved(ClsEvent event);


    /**
     * @see ClassListener#subclassRemoved
     * @deprecated
     */
    void directSubclassRemoved(ClsEvent event);


    /**
     * @see ClassListener#superclassAdded
     * @deprecated
     */
    void directSuperclassAdded(ClsEvent event);


    /**
     * @see ClassListener#subclassRemoved
     * @deprecated
     */
    void directSuperclassRemoved(ClsEvent event);


    /**
     * @deprecated no OWL equivalent
     */
    void templateFacetAdded(ClsEvent event);


    /**
     * @deprecated no OWL equivalent
     */
    void templateFacetRemoved(ClsEvent event);


    /**
     * @deprecated no OWL equivalent
     */
    void templateFacetValueChanged(ClsEvent event);


    /**
     * @see ClassListener#addedToUnionDomainOf
     * @deprecated
     */
    void templateSlotAdded(ClsEvent event);


    /**
     * @see ClassListener#removedFromUnionDomainOf
     * @deprecated
     */
    void templateSlotRemoved(ClsEvent event);


    /**
     * @deprecated no OWL equivalent
     */
    void templateSlotValueChanged(ClsEvent event);
}
