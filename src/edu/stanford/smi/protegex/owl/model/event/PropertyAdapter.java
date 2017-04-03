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
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PropertyAdapter implements PropertyListener {

    public void subpropertyAdded(RDFProperty property, RDFProperty subproperty, SlotEvent event) {
    	subpropertyAdded(property, subproperty);
    }

    public void subpropertyAdded(RDFProperty property, RDFProperty subproperty) {
        // Do nothing
    }

    public void subpropertyRemoved(RDFProperty property, RDFProperty subproperty, SlotEvent event) {
    	subpropertyRemoved(property, subproperty);
    }

    public void subpropertyRemoved(RDFProperty property, RDFProperty subproperty) {
        // Do nothing
    }

    public void superpropertyAdded(RDFProperty property, RDFProperty superproperty, SlotEvent event) {
    	superpropertyAdded(property, superproperty);
    }
 

    public void superpropertyAdded(RDFProperty property, RDFProperty superproperty) {
        // Do nothing
    }

    public void superpropertyRemoved(RDFProperty property, RDFProperty superproperty, SlotEvent event) {
    	superpropertyRemoved(property, superproperty);
    }

    public void superpropertyRemoved(RDFProperty property, RDFProperty superproperty) {
        // Do nothing
    }

    public void unionDomainClassAdded(RDFProperty property, RDFSClass rdfsClass, SlotEvent event) {
    	unionDomainClassAdded(property, rdfsClass);
    }

    public void unionDomainClassAdded(RDFProperty property, RDFSClass rdfsClass) {
        // Do nothing
    }

    public void unionDomainClassRemoved(RDFProperty property, RDFSClass rdfsClass, SlotEvent event) {
    	unionDomainClassRemoved(property, rdfsClass);
    }
        
    public void unionDomainClassRemoved(RDFProperty property, RDFSClass rdfsClass) {
        // Do nothing
    }
        

    /************* Deprecated methods **************/
    
    /**
     * @deprecated
     */
    public final void templateSlotClsAdded(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getCls() instanceof RDFSClass) {
            unionDomainClassAdded((RDFProperty) event.getSlot(), (RDFSClass) event.getCls(), event);
        }
    }


    /**
     * @deprecated
     */
    public void templateSlotClsRemoved(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getCls() instanceof RDFSClass) {
            unionDomainClassRemoved((RDFProperty) event.getSlot(), (RDFSClass) event.getCls(), event);
        }
    }
    
    /**
     * @deprecated
     */
    public void directSubslotAdded(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getSubslot() instanceof RDFProperty) {
            subpropertyAdded((RDFProperty) event.getSlot(), (RDFProperty) event.getSubslot(), event);
        }
    }


    /**
     * @deprecated
     */
    public final void directSubslotMoved(SlotEvent event) {
        // Not supported in OWL
    }
    
    /**
     * @deprecated
     */
    public void directSubslotRemoved(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getSubslot() instanceof RDFProperty) {
            subpropertyRemoved((RDFProperty) event.getSlot(), (RDFProperty) event.getSubslot(), event);
        }
    }


    /**
     * @deprecated
     */
    public void directSuperslotAdded(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getSubslot() instanceof RDFProperty) {
            superpropertyAdded((RDFProperty) event.getSlot(), (RDFProperty) event.getSubslot(), event);
        }
    }


    /**
     * @deprecated
     */
    public void directSuperslotRemoved(SlotEvent event) {
        if (event.getSlot() instanceof RDFProperty && event.getSubslot() instanceof RDFProperty) {
            superpropertyRemoved((RDFProperty) event.getSlot(), (RDFProperty) event.getSubslot(), event);
        }
    }
    
}
