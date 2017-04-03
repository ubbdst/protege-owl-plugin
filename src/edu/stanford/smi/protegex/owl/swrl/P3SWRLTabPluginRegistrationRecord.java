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

package edu.stanford.smi.protegex.owl.swrl;

import javax.swing.Icon;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.ui.P3SWRLTabPluginCreator;

public class P3SWRLTabPluginRegistrationRecord
{
	private final String pluginName;
	private final String ruleEngineName;
	private final String toolTip;
	private final P3SWRLTabPluginCreator swrlTabPlugin;
	private final Icon pluginIcon;
	private final Icon ruleEngineIcon;
	private final Icon reasonerIcon;
	private OWLModel owlModel;

	public P3SWRLTabPluginRegistrationRecord(String pluginName, String ruleEngineName, String toolTip, Icon pluginIcon, Icon ruleEngineIcon, Icon reasonerIcon,
			P3SWRLTabPluginCreator swrlTabPlugin)
	{
		this.pluginName = pluginName;
		this.ruleEngineName = ruleEngineName;
		this.toolTip = toolTip;
		this.swrlTabPlugin = swrlTabPlugin;
		this.pluginIcon = pluginIcon;
		this.ruleEngineIcon = ruleEngineIcon;
		this.reasonerIcon = reasonerIcon;
		this.owlModel = null; // An OWL model is supplied when a GUI associated with the plugin is activated.
	}

	public void setOWLModel(OWLModel owlModel)
	{
		this.owlModel = owlModel;
	}

	public String getPluginName()
	{
		return this.pluginName;
	}

	public String getRuleEngineName()
	{
		return this.ruleEngineName;
	}

	public String getToolTip()
	{
		return this.toolTip;
	}

	public P3SWRLTabPluginCreator getSWRLTabPlugin()
	{
		return this.swrlTabPlugin;
	}

	public Icon getPluginIcon()
	{
		return this.pluginIcon;
	}

	public Icon getRuleEngineIcon()
	{
		return this.ruleEngineIcon;
	}

	public Icon getReasonerIcon()
	{
		return this.reasonerIcon;
	}

	public OWLModel getOWLModel()
	{
		return this.owlModel;
	}

	public boolean hasOWLModel()
	{
		return this.owlModel != null;
	}
}
