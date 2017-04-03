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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.swrl.P3SWRLTabPluginManager;
import edu.stanford.smi.protegex.owl.swrl.P3SWRLTabPluginRegistrationRecord;
import edu.stanford.smi.protegex.owl.swrl.ui.actions.ViewPluginAction;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;

/**
 * A JPanel consisting of a SWRLTable and buttons to view, clone, create and delete rules. It may also have buttons to activate/deactivate any registered rule
 * engines.
 */
public class SWRLTablePanel extends JPanel implements Disposable
{
	private SWRLTable table;

	public SWRLTablePanel(OWLModel owlModel, RDFResource resource)
	{
		initialize(owlModel, resource);
	}

	public SWRLTablePanel(OWLModel owlModel, RDFResource resource, SWRLTab swrlTab)
	{
		LabeledComponent lc = initialize(owlModel, resource);

		// Iterate through all registered rule engine and add an enable button for each one.
		for (P3SWRLTabPluginRegistrationRecord registration : P3SWRLTabPluginManager.getRegisteredPlugins()) {
			lc.addHeaderButton(new ViewPluginAction(registration.getPluginName(), registration.getToolTip(), registration.getPluginIcon(), swrlTab, owlModel));
			add(BorderLayout.CENTER, lc);
		}
	}

	public void dispose()
	{
		if (this.table != null)
			this.table.dispose();
	}

	private LabeledComponent initialize(OWLModel owlModel, RDFResource RDFResource)
	{
		SWRLTableModel tableModel = RDFResource == null ? new SWRLTableModel(owlModel) : new SWRLTableModel(RDFResource);

		this.table = new SWRLTable(tableModel, owlModel);

		JScrollPane scrollPane = new JScrollPane(this.table);
		JViewport viewPort = scrollPane.getViewport();
		viewPort.setBackground(this.table.getBackground());

		LabeledComponent lc = new OWLLabeledComponent("SWRL Rules", scrollPane);
		lc.addHeaderButton(new ViewRuleAction(this.table));
		lc.addHeaderButton(new CreateRuleAction(this.table, owlModel));
		lc.addHeaderButton(new CloneRuleAction(this.table, owlModel));
		lc.addHeaderButton(new DeleteRuleAction(this.table));

		setLayout(new BorderLayout());
		add(BorderLayout.CENTER, lc);

		return lc;
	}
}
