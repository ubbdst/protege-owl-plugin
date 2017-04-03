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


package edu.stanford.smi.protegex.owl.swrl.ui.table;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLFactory;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.ui.code.SWRLTextAreaPanel;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

public class CreateRuleAction extends AbstractAction
{
	private final OWLModel owlModel;
	private final SWRLTable table;
	private final SWRLFactory factory;

	public CreateRuleAction(SWRLTable table, OWLModel owlModel)
	{
		super("Create new rule...", OWLIcons.getCreateIcon(SWRLIcons.IMP, SWRLIcons.class));
		this.owlModel = owlModel;
		this.table = table;
		this.factory = new SWRLFactory(owlModel);
	}

	public void actionPerformed(ActionEvent e)
	{
		final SWRLImp newImp = this.factory.createImp();
		if (SWRLTextAreaPanel.showEditDialog(this.table, this.owlModel, newImp)) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					CreateRuleAction.this.table.setSelectedRow(newImp);
				}
			});
		} else
			newImp.delete();
	}
}
