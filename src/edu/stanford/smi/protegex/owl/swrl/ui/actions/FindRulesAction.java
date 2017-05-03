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

import java.awt.event.ActionEvent;
import java.util.Collection;

import edu.stanford.smi.protege.model.FrameSlotCombination;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.Selectable;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.swrl.ui.table.SWRLResultsPanel;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;

/**
 * An Action to search for SWRL rules for the selected frame. This will show up in bottom area of the ResourceDisplay.
 * 
 * @author Holger Knublauch <holger@knublauch.com>
 */
public class FindRulesAction extends AllowableAction
{
	public FindRulesAction(Selectable selectable)
	{
		this(selectable, "Find rules about displayed resource");
	}

	public FindRulesAction(Selectable selectable, String text)
	{
		super(text, SWRLIcons.getImpIcon(), selectable);
	}

	public void actionPerformed(ActionEvent e)
	{
		Collection<?> sel = getSelection();
		if (sel.size() == 1) {
			Object next = sel.iterator().next();
			RDFResource findInstance = null;
			if (next instanceof RDFResource) {
				findInstance = (RDFResource)sel.iterator().next();
			} else if (next instanceof FrameSlotCombination) {
				FrameSlotCombination c = (FrameSlotCombination)next;
				if (c.getSlot() instanceof RDFResource) {
					findInstance = (RDFResource)c.getSlot();
				}
			}
			if (findInstance != null) {
				SWRLResultsPanel panel = new SWRLResultsPanel(findInstance);
				ResultsPanelManager.addResultsPanel(findInstance.getOWLModel(), panel, true);
			}
		}
	}
}
