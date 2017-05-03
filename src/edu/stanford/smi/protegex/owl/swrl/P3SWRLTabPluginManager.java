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

// TODO: Jess and Drools rule engine and SQWRL query tab are loaded explicitly here. We need a discovery mechanism using the manifest.
// TODO: A bit sloppy. GUI code should be refactored to ui subdirectory.

package edu.stanford.smi.protegex.owl.swrl;

import java.awt.Container;
import java.awt.GridLayout;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import edu.stanford.smi.protege.event.ProjectAdapter;
import edu.stanford.smi.protege.event.ProjectEvent;
import edu.stanford.smi.protege.event.ProjectListener;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLNames;
import edu.stanford.smi.protegex.owl.swrl.ui.P3SWRLTabPluginCreator;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;

/**
 * This class provides mechanisms for Protege-OWL SWRLTab plugins to register themselves and to get screen real estate in the SWRLTab.
 * <p>
 * The {@link #setSelectedRuleName(String)} method is used by the {@link SWRLTable} to indicate the currently selected rule in the {@link SWRLTab}. The
 * {@link #getSelectedRuleName()} call can be used by plugins to determine which rule is selected. 
 * <p>
 * Documentation on using this class can be found <a
 * href="http://protege.stanford.edu/download/prerelease_javadoc_owl/edu/stanford/smi/protegex/owl/swrl/bridge/BridgePluginManager.html">here</a>.
 */
public class P3SWRLTabPluginManager {
	private static transient final Logger log = Log.getLogger(P3SWRLTabPluginManager.class);

	private static HashMap<String, P3SWRLTabPluginRegistrationRecord> registeredPlugins = new HashMap<String, P3SWRLTabPluginRegistrationRecord>();
	private static String visiblePluginName = "";
	private static String selectedRuleName = "";

	private static ProjectListener projectListener = new ProjectAdapter() {
		@Override
		public void projectClosed(ProjectEvent event)
		{
			Project project = (Project) event.getSource();
			project.removeProjectListener(projectListener);
			visiblePluginName = "";
			selectedRuleName = "";
		}
	};

	// TODO Hack: manually load SWRLTab plugins until we implement a proper class load with the manifest
	static {
		loadPlugins();
	}

	public static String getSelectedRuleName()
	{
		return selectedRuleName;
	}

	public static boolean hasSelectedRule()
	{
		return selectedRuleName.length() != 0;
	}

	public static void setSelectedRuleName(String ruleName)
	{
		selectedRuleName = ruleName;
	}

	public static Collection<P3SWRLTabPluginRegistrationRecord> getRegisteredPlugins()
	{
		return registeredPlugins.values();
	}

	public static void hideVisiblePlugin()
	{
		hidePlugin(visiblePluginName, true);
	}

	public static boolean hidePlugin(String pluginName)
	{
		return hidePlugin(pluginName, false);
	}

	public static boolean isVisible(String pluginName)
	{
		return visiblePluginName.length() != 0 && pluginName.equals(visiblePluginName);
	}

	/**
	 * This method is called by each plugin as it is loaded to inform the SWRLTab of its presence.
	 */
	public static void registerPlugin(String pluginName, String ruleEngineName, String toolTip, Icon pluginIcon, Icon ruleEngineIcon, Icon reasonerIcon,
			P3SWRLTabPluginCreator plugin)
	{
		if (registeredPlugins.containsKey(pluginName))
			registeredPlugins.remove(pluginName);
		registeredPlugins.put(pluginName, new P3SWRLTabPluginRegistrationRecord(pluginName, ruleEngineName, toolTip, pluginIcon, ruleEngineIcon, reasonerIcon,
				plugin));
		log.info("Plugin '" + pluginName + "' registered with the SWRLTab plugin manager.");
	}

	/**
	 * This method is called by each plugin as it is loaded to inform the SWRLTab of its presence. The application-default rule engine is picked. If no default is
	 * specified, the Drools rule engine is selected.
	 */
	public static void registerPlugin(String pluginName, String toolTip, Icon pluginIcon, Icon ruleEngineIcon, Icon reasonerIcon, P3SWRLTabPluginCreator plugin)
	{
		String defaultRuleEngineName = ApplicationProperties.getString(SWRLNames.DEFAULT_RULE_ENGINE, "Drools");

		registerPlugin(pluginName, defaultRuleEngineName, toolTip, pluginIcon, ruleEngineIcon, reasonerIcon, plugin);
	}

	public static void unregisterPlugin(String pluginName)
	{
		if (registeredPlugins.containsKey(pluginName)) {
			if (isVisible(pluginName))
				hidePlugin(pluginName, true);
			registeredPlugins.remove(pluginName);
		}
	}

	public static void showPlugin(String pluginName, SWRLTab swrlTab, OWLModel owlModel)
	{
		if (!isVisible(pluginName)) {
			if (hidePlugin(visiblePluginName)) { // Hide may fail if user does not confirm it.

				if (registeredPlugins.containsKey(pluginName)) {
					P3SWRLTabPluginRegistrationRecord registrationRecord = registeredPlugins.get(pluginName);
					Container pluginGUI = registrationRecord.getSWRLTabPlugin().createSWRLPluginGUI(owlModel, pluginName, registrationRecord.getRuleEngineName(),
							registrationRecord.getRuleEngineIcon(), registrationRecord.getReasonerIcon());

					registrationRecord.setOWLModel(owlModel); // Set the owlModel so that we can unregister ourselves on deactivation.

					if (pluginGUI != null) {
						swrlTab.setVisible(false);
						swrlTab.setLayout(new GridLayout(2, 1));
						swrlTab.add(pluginGUI);
						swrlTab.setVisible(true);
						visiblePluginName = pluginName;
					} else
						makeTextPanel(swrlTab, "Unable to activate the '" + pluginName + "' plugin.");
				}
			}
		}
		owlModel.getProject().addProjectListener(projectListener);
	}

	public static void loadPlugins()
	{
		boolean pluginFound = false;

		try {
			Class.forName("jess.Rete");
			Class.forName("org.protege.swrltab.p3.ui.P3SWRLTabJessPluginCreator");
			pluginFound = true;
		} catch (ClassNotFoundException e) {
			log.info("Jess rule engine load failed: could not find jess.Rete - or an error occured on initialization: " + e.getMessage());
		}

		try {
			Class.forName("org.protege.swrltab.p3.ui.P3SWRLTabDroolsPluginCreator");
			pluginFound = true;
		} catch (ClassNotFoundException e) {
			log.info("Drools rule engine load failed: could not find Drools JARs - or an error occured on initialization: " + e.getMessage());
		}

		if (pluginFound) { // We only load the SQWRL plugin if there is a rule engine present to execute the queries.
			try {
				Class.forName("org.protege.swrltab.p3.ui.P3SWRLTabSQWRLPluginCreator");
			} catch (ClassNotFoundException e) {
				log.info("SQWRLQueryTab load failed: an error occured on initialization: " + e.getMessage());
			}
		}
	}

	private static boolean hidePlugin(String pluginName, boolean force)
	{
		if (isVisible(pluginName)) {

			if (!force
					&& (JOptionPane.showConfirmDialog(null, "Do you really want to close the " + pluginName + " plugin?", "Disable " + pluginName,
							JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION))
				return false;

			if (registeredPlugins.containsKey(pluginName)) {
				P3SWRLTabPluginRegistrationRecord registration = registeredPlugins.get(pluginName);
				Container pluginGUI = registration.getSWRLTabPlugin().getSWRLPluginGUI();
				SWRLTab swrlTab = (SWRLTab) pluginGUI.getParent();
				if (swrlTab != null) {
					swrlTab.setVisible(false);
					swrlTab.remove(pluginGUI);
					swrlTab.reconfigure();
					swrlTab.setVisible(true);
				}
				if (registration.hasOWLModel())
					registration.getOWLModel().getProject().removeProjectListener(projectListener);
				visiblePluginName = "";
			}
		}
		return true;
	}

	private static void makeTextPanel(SWRLTab swrlTab, String text)
	{
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);

		swrlTab.add(panel);
	}
}
