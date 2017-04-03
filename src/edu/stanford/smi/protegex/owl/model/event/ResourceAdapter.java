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

import java.util.Collection;

import edu.stanford.smi.protege.event.FrameAdapter;
import edu.stanford.smi.protege.event.FrameEvent;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ResourceAdapter extends FrameAdapter implements ResourceListener {

    public void typeAdded(RDFResource resource, RDFSClass type, FrameEvent event) {
    	typeAdded(resource, type);
    }
	
    public void typeAdded(RDFResource resource, RDFSClass type) {
        // Do nothing
    }


    public void typeRemoved(RDFResource resource, RDFSClass type, FrameEvent event) {
    	typeRemoved(resource, type);
    }

    
    public void typeRemoved(RDFResource resource, RDFSClass type) {
        // Do nothing
    }

    
    /************* Deprecated methods **************/
    
    /*
     * @deprecated Use frame listeners directly if you know what you are doing
     * 				and are going to look below the advertised protege owl api interface.
     */
    @Deprecated
	@SuppressWarnings("unchecked")
	public void ownSlotValueChanged(FrameEvent event) {
		if (event.getSlot().getName().equals(RDFNames.Slot.TYPE) && 
				event.getFrame() instanceof RDFResource) {
			RDFResource resource = (RDFResource) event.getFrame();
			Collection types = resource.getRDFTypes();
			Collection oldTypes = event.getOldValues();
			
			for (Object newType : types) {
				if (newType instanceof RDFSClass) {
					if (oldTypes == null || !oldTypes.contains(newType)) {
						typeAdded(resource, (RDFSClass) newType, event);
					}					
				}
			}
			
			if (oldTypes == null) {
				return;
			}
			
			for (Object oldType : oldTypes) {
				if (oldType instanceof RDFSClass && !types.contains(oldType)) {					
					typeRemoved(resource, (RDFSClass) oldType, event);					
				}
			}
		}
	}
}
