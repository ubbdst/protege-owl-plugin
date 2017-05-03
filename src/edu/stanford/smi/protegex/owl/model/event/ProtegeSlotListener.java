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

import edu.stanford.smi.protege.event.SlotEvent;
import edu.stanford.smi.protege.event.SlotListener;

/**
 * An interface that wraps the core Protege SlotListener to declare
 * those methods deprecated that should not be used with OWL.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public interface ProtegeSlotListener extends SlotListener {


    /**
     * @see PropertyListener#unionDomainClassAdded
     * @deprecated
     */
    void templateSlotClsAdded(SlotEvent event);


    /**
     * @see PropertyListener#unionDomainClassRemoved
     * @deprecated
     */
    void templateSlotClsRemoved(SlotEvent event);


    /**
     * @see PropertyListener#subpropertyAdded
     * @deprecated
     */
    void directSubslotAdded(SlotEvent event);


    /**
     * @see PropertyListener#subpropertyRemoved
     * @deprecated
     */
    void directSubslotRemoved(SlotEvent event);


    /**
     * @deprecated not supported in OWL
     */
    void directSubslotMoved(SlotEvent event);


    /**
     * @see PropertyListener#superpropertyAdded
     * @deprecated
     */
    void directSuperslotAdded(SlotEvent event);


    /**
     * @see PropertyListener#superpropertyRemoved
     * @deprecated
     */
    void directSuperslotRemoved(SlotEvent event);
}
