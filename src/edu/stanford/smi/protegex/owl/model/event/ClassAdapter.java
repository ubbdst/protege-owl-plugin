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
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassAdapter implements ClassListener {

	public void addedToUnionDomainOf(RDFSClass cls, RDFProperty property, ClsEvent event) {
		addedToUnionDomainOf(cls, property);
    }
	
    public void addedToUnionDomainOf(RDFSClass cls, RDFProperty property) {
        // Do nothing
    }

    public void removedFromUnionDomainOf(RDFSClass cls, RDFProperty property, ClsEvent event) {
    	removedFromUnionDomainOf(cls, property);
    }
    
    public void removedFromUnionDomainOf(RDFSClass cls, RDFProperty property) {
        // Do nothing
    }
    
    public void instanceAdded(RDFSClass cls, RDFResource instance, ClsEvent event) {
    	instanceAdded(cls, instance);
    }


    public void instanceAdded(RDFSClass cls, RDFResource instance) {
        // Do nothing
    }

    public void instanceRemoved(RDFSClass cls, RDFResource instance, ClsEvent event) {
    	instanceRemoved(cls, instance);
    }


    public void instanceRemoved(RDFSClass cls, RDFResource instance) {
        // Do nothing
    }

    public void subclassAdded(RDFSClass cls, RDFSClass subclass, ClsEvent event) {
    	subclassAdded(cls, subclass);
    }
    
    public void subclassAdded(RDFSClass cls, RDFSClass subclass) {
        // Do nothing
    }

    public void subclassRemoved(RDFSClass cls, RDFSClass subclass, ClsEvent event) {
    	subclassRemoved(cls, subclass);
    }

    public void subclassRemoved(RDFSClass cls, RDFSClass subclass) {
        // Do nothing
    }

    public void superclassAdded(RDFSClass cls, RDFSClass superclass, ClsEvent event) {
    	superclassAdded(cls, superclass);
    }
    
    public void superclassAdded(RDFSClass cls, RDFSClass superclass) {
        // Do nothing
    }

    public void superclassRemoved(RDFSClass cls, RDFSClass superclass, ClsEvent event) {
    	superclassRemoved(cls, superclass);
    }

    public void superclassRemoved(RDFSClass cls, RDFSClass superclass) {
        // Do nothing
    }

    /************* Deprecated methods **************/

    /**
     * @deprecated
     */
    public final void directSubclassRemoved(ClsEvent event) {
        if (event.getCls() instanceof RDFSClass && event.getSubclass() instanceof RDFSClass) {
            subclassRemoved((RDFSClass) event.getCls(), (RDFSClass) event.getSubclass(), event);
        }
    }
    
    /**
     * @deprecated
     */
    public final void directSuperclassAdded(ClsEvent event) {
        if (event.getCls() instanceof RDFSClass && event.getSuperclass() instanceof RDFSClass) {
            superclassAdded((RDFSClass) event.getCls(), (RDFSClass) event.getSuperclass(), event);
        }
    }

    /**
     * @deprecated
     */
    public final void directSuperclassRemoved(ClsEvent event) {
        if (event.getCls() instanceof RDFSClass && event.getSuperclass() instanceof RDFSClass) {
            superclassRemoved((RDFSClass) event.getCls(), (RDFSClass) event.getSuperclass(), event);
        }
    }

    /**
     * @deprecated
     */
    public final void directInstanceAdded(ClsEvent event) {
        if (event.getCls() instanceof RDFSClass && event.getInstance() instanceof RDFResource) {
            instanceAdded((RDFSClass) event.getCls(), (RDFResource) event.getInstance(), event);
        }
    }

    /**
     * @deprecated
     */
    public final void directInstanceRemoved(ClsEvent event) {
        if (event.getCls() instanceof RDFSClass && event.getInstance() instanceof RDFResource) {
            instanceRemoved((RDFSClass) event.getCls(), (RDFResource) event.getInstance(), event);
        }
    }

    /**
     * @deprecated
     */
    public final void directSubclassAdded(ClsEvent event) {
        if (event.getCls() instanceof RDFSClass && event.getSubclass() instanceof RDFSClass) {
            subclassAdded((RDFSClass) event.getCls(), (RDFSClass) event.getSubclass(), event);
        }
    }
 
    /**
     * @deprecated
     */
    public final void templateSlotAdded(ClsEvent event) {
        if (event.getCls() instanceof RDFSClass && event.getSlot() instanceof RDFProperty) {
            addedToUnionDomainOf((RDFSClass) event.getCls(), (RDFProperty) event.getSlot(), event);
        }
    }

    /**
     * @deprecated
     */
    public final void templateSlotRemoved(ClsEvent event) {
        if (event.getCls() instanceof RDFSClass && event.getSlot() instanceof RDFProperty) {
            removedFromUnionDomainOf((RDFSClass) event.getCls(), (RDFProperty) event.getSlot(), event);
        }
    }

    /**
     * @deprecated
     */
    public final void templateSlotValueChanged(ClsEvent event) {
    }
    
    /**
     * @deprecated
     */
    public final void directSubclassMoved(ClsEvent event) {
    }
    
    /**
     * @deprecated
     */
    public final void templateFacetAdded(ClsEvent event) {
    }

    /**
     * @deprecated
     */
    public final void templateFacetRemoved(ClsEvent event) {
    }

    /**
     * @deprecated
     */
    public final void templateFacetValueChanged(ClsEvent event) {
    }

}
