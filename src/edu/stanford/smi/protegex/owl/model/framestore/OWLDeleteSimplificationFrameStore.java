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

package edu.stanford.smi.protegex.owl.model.framestore;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.model.Transaction;
import edu.stanford.smi.protege.model.framestore.DeleteSimplificationFrameStore;

/**
 * A modified DeleteSimplificationFrameStore that does not automatically delete
 * own slot values if the domain of a slot has changed.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLDeleteSimplificationFrameStore extends DeleteSimplificationFrameStore {

    @Override
    public void removeDirectTemplateSlot(Cls cls, Slot slot) {
        if (slot.isBeingDeleted()) {
            super.removeDirectTemplateSlot(cls, slot);
        }
        else {
        	//TT: This is a little bit cheating, but harmless...
        	boolean success = false;
            beginTransaction("Remove template slot from class " + cls + Transaction.APPLY_TO_TRAILER_STRING +
            		(cls == null ? null : cls.getName()));
            try {
            	getDelegate().removeDirectTemplateSlot(cls, slot);
            	success = true;
            	commitTransaction();
            } finally {
            	if (!success) {
            		rollbackTransaction();
            	}
            }
        }
    }
}
