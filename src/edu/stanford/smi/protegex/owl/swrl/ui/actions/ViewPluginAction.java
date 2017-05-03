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

import javax.swing.AbstractAction;
import javax.swing.Icon;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.P3SWRLTabPluginManager;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;

public class ViewPluginAction extends AbstractAction
{
	private static final long serialVersionUID = 2115603073256549503L;
	private final String pluginName;
	private final OWLModel owlModel;
	private final SWRLTab swrlTab;

	public ViewPluginAction(String pluginName, String tip, Icon icon, SWRLTab swrlTab, OWLModel owlModel)
	{
		super(tip, icon);

		this.pluginName = pluginName;
		this.owlModel = owlModel;
		this.swrlTab = swrlTab;
	}

	public void actionPerformed(ActionEvent e)
	{
		if (P3SWRLTabPluginManager.isVisible(this.pluginName))
			P3SWRLTabPluginManager.hidePlugin(this.pluginName);
		else
			P3SWRLTabPluginManager.showPlugin(this.pluginName, this.swrlTab, this.owlModel);
	}
}
