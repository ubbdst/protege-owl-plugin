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

package edu.stanford.smi.protegex.owl.ui.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.ui.SlotSubslotNode;
import edu.stanford.smi.protege.util.LazyTreeNode;
import edu.stanford.smi.protegex.owl.model.RDFProperty;

public class OWLPropertySubpropertyNode extends SlotSubslotNode {

	public OWLPropertySubpropertyNode(LazyTreeNode parentNode, Slot parentSlot) {
		super(parentNode, parentSlot);
	}
	
	@Override
	protected Collection getChildObjects() {
		//TODO: make sorting configurable		
		ArrayList<RDFProperty> sortedPropeties = new ArrayList<RDFProperty>(getSlot().getDirectSubslots());
		Collections.sort(sortedPropeties, getComparator());
		
        return sortedPropeties;
    }
	

}
