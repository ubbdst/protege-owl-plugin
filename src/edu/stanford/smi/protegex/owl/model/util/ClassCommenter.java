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

package edu.stanford.smi.protegex.owl.model.util;

import edu.stanford.smi.protegex.owl.model.*;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Jan 16, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ClassCommenter {

	private RDFProperty isCommentedOutProperty;

	public ClassCommenter(OWLModel owlModel) {		
		isCommentedOutProperty = owlModel.getRDFProperty(ProtegeNames.Slot.IS_COMMENTED_OUT);		
	}

	public boolean isCommentedOut(RDFSClass cls) {
		if(isCommentedOutProperty != null) {
			return cls.getPropertyValue(isCommentedOutProperty) != null;
		}
		return false;
	}

	public void setCommentedOut(RDFSClass cls, boolean b) {
		if(isCommentedOutProperty != null) {
			if(b) {
				cls.setPropertyValue(isCommentedOutProperty, Boolean.toString(b));
			}
			else {
				Object val = cls.getPropertyValue(isCommentedOutProperty);
				if(val != null) {
					cls.removePropertyValue(isCommentedOutProperty, val);
				}
			}
		}
	}
}

