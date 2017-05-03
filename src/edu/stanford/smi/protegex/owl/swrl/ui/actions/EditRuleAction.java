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


package edu.stanford.smi.protegex.owl.swrl.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.ui.code.SWRLTextAreaPanel;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * A ResourceAction to edit a SWRLImp in a multi-line editor.
 * 
 * @author Holger Knublauch <holger@knublauch.com>
 */
public class EditRuleAction extends ResourceAction
{

	public EditRuleAction()
	{
		super("Edit rule in multi-line editor...", OWLIcons.getViewIcon(SWRLIcons.IMP, SWRLIcons.class));
	}

	public void actionPerformed(ActionEvent e)
	{
		SWRLImp oldImp = (SWRLImp)getResource();
		SWRLTextAreaPanel.showEditDialog(getComponent(), getOWLModel(), oldImp);
	}

	@Override
	public boolean isSuitable(Component component, RDFResource resource)
	{
		return resource instanceof SWRLImp;
	}
}
