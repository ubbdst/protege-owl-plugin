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

import edu.stanford.smi.protege.event.KnowledgeBaseEvent;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ModelAdapter implements ModelListener {

    public void classCreated(RDFSClass cls, KnowledgeBaseEvent event) {
    	classCreated(cls);
    }

	
    public void classCreated(RDFSClass cls) {
        // Do nothing
    }
    

    public void classDeleted(RDFSClass cls, KnowledgeBaseEvent event) {
    	classDeleted(cls);
    }


    public void classDeleted(RDFSClass cls) {
        // Do nothing
    }


    public void propertyCreated(RDFProperty property, KnowledgeBaseEvent event) {
    	propertyCreated(property);
    }

    
    public void propertyCreated(RDFProperty property) {
        // Do nothing
    }

    public void propertyDeleted(RDFProperty property, KnowledgeBaseEvent event) {
    	propertyDeleted(property);
    }

    
    public void propertyDeleted(RDFProperty property) {
        // Do nothing
    }


    public void individualCreated(RDFResource resource, KnowledgeBaseEvent event) {
    	individualCreated(resource);
    }
    
    public void individualCreated(RDFResource resource) {
        // Do nothing
    }


    public void individualDeleted(RDFResource resource, KnowledgeBaseEvent event) {
    	individualDeleted(resource);
    }

    
    public void individualDeleted(RDFResource resource) {
        // Do nothing
    }

    
    public void resourceReplaced(RDFResource oldResource, RDFResource newResource, String oldName) {    	
		resourceNameChanged(oldResource, oldName);
	}    
    

    /************* Deprecated methods **************/
    
    /**
     * @deprecated Use {@link #resourceReplaced(RDFResource, RDFResource, String)}
     */
    public void resourceNameChanged(RDFResource resource, String oldName) {
        // Do nothing
    }
    
    /**
     * @deprecated
     */
    public final void clsCreated(KnowledgeBaseEvent event) {
        if (event.getCls() instanceof RDFSClass) {
            classCreated((RDFSClass) event.getCls(), event);
        }
    }


    /**
     * @deprecated
     */
    public final void clsDeleted(KnowledgeBaseEvent event) {
        if (event.getCls() instanceof RDFSClass) {
            classDeleted((RDFSClass) event.getCls(), event);
        }
    }


    /**
     * @deprecated
     */
    public final void frameNameChanged(KnowledgeBaseEvent event) {
        if (event.getFrame() instanceof RDFResource) {        	
        	resourceReplaced((RDFResource) event.getFrame(),(RDFResource) event.getNewFrame(), event.getOldName());           
        }
    }


	/**
     * @deprecated
     */
    public final void instanceCreated(KnowledgeBaseEvent event) {
        if (event.getFrame() instanceof RDFResource) {
            individualCreated((RDFResource) event.getFrame(), event);
        }
    }


    /**
     * @deprecated
     */
    public final void instanceDeleted(KnowledgeBaseEvent event) {
        if (event.getFrame() instanceof RDFResource) {
            individualDeleted((RDFResource) event.getFrame(), event);
        }
    }
    
    
    /**
     * @deprecated
     */
    public final void slotCreated(KnowledgeBaseEvent event) {
        if (event.getSlot() instanceof RDFProperty) {
            propertyCreated((RDFProperty) event.getSlot(), event);
        }
    }


    /**
     * @deprecated
     */
    public final void slotDeleted(KnowledgeBaseEvent event) {
        if (event.getSlot() instanceof RDFProperty) {
            propertyDeleted((RDFProperty) event.getSlot(), event);
        }
    }
    
    /**
     * @deprecated
     */
    public final void defaultClsMetaClsChanged(KnowledgeBaseEvent event) {
    }


    /**
     * @deprecated
     */
    public final void defaultFacetMetaClsChanged(KnowledgeBaseEvent event) {
    }


    /**
     * @deprecated
     */
    public final void defaultSlotMetaClsChanged(KnowledgeBaseEvent event) {
    }


    /**
     * @deprecated
     */
    public final void facetCreated(KnowledgeBaseEvent event) {
    }


    /**
     * @deprecated
     */
    public final void facetDeleted(KnowledgeBaseEvent event) {
    }
    
}
