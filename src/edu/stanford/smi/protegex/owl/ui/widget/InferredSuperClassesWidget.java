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

package edu.stanford.smi.protegex.owl.ui.widget;

import java.util.Collection;

import javax.swing.Action;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.widget.ClsListWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;

public class InferredSuperClassesWidget extends ClsListWidget {

	
	@Override
	public void initialize() {
		setLabel("Inferred superclasses");
		super.initialize();	
	}
	
	@Override
	public void setValues(Collection values) {
		Instance cls = getInstance();
		
		if (cls instanceof OWLNamedClass) {
			super.setValues(((OWLNamedClass)cls).getInferredSuperclasses());
		} else {
			super.setValues(values);
		}		
	}

	@Override
	protected void addButtons(LabeledComponent c, Action viewAction) {
		addButton(getViewInstanceAction());
	}	

	/**
	 * This is just tentative. More conditions can be added
	 */
	public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
		boolean isSuitable;
		if (cls == null || slot == null) {
			isSuitable = false;
		} else {
			if (!(cls.getKnowledgeBase() instanceof OWLModel)) {
				return false;
			}

			if (!cls.isMetaCls() || !(cls instanceof OWLNamedClass)) {
				return false;
			}	

			return ClsListWidget.isSuitable(cls, slot, facet);
		}
		return isSuitable;
	}

}
