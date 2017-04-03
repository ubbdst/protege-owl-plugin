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

import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLImp;
import edu.stanford.smi.protegex.owl.swrl.ui.code.SWRLTextAreaPanel;
import edu.stanford.smi.protegex.owl.swrl.ui.icons.SWRLIcons;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

public class ViewRuleAction extends AbstractAction
{
	private final SWRLTable table;

	public ViewRuleAction(SWRLTable table)
	{
		super("Edit selected rule in multi-line editor...", OWLIcons.getViewIcon(SWRLIcons.IMP, SWRLIcons.class));
		this.table = table;
		table.addSelectionListener(new SelectionListener() {
			public void selectionChanged(SelectionEvent event)
			{
				updateEnabled();
			}
		});
		updateEnabled();
	}

	public void actionPerformed(ActionEvent e)
	{
		final SWRLImp imp = this.table.getSelectedImp();
		OWLModel owlModel = imp.getOWLModel();
		if (SWRLTextAreaPanel.showEditDialog(this.table, owlModel, imp)) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run()
				{
					ViewRuleAction.this.table.setSelectedRow(imp);
				}
			});
		}
	}

	private void updateEnabled()
	{
		setEnabled(this.table.getSelectedImp() != null && this.table.getSelectedImp().isEditable());
	}
}
