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


package edu.stanford.smi.protegex.owl.swrl.ui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.Icon;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.swrl.P3SWRLTabPluginManager;
import edu.stanford.smi.protegex.owl.swrl.ui.tab.SWRLTab;

/**
 * This Protege-OWL-specific interface must be implemented by a SWRLTab plugin to create a Java Swing {@link Container} that represents the plugin's GUI. Thus
 * GUI will be activated and gear screen real estate in the SWRLTab tab and provide a means of interacting with the plugin.
 * <p>
 * Each plugin should register itself with the bridge using the
 * {@link P3SWRLTabPluginManager#registerPlugin(String, String, Icon, Icon, Icon, P3SWRLTabPluginCreator)} method of the {@link P3SWRLTabPluginManager} class.
 * In addition to a plugin display name, a tool tip string, and a set of icons, this method is expecting an instance of a class that implements this interface.
 * The plugin manager uses this implementation to get the GUI for the plugin, which will be displayed in the {@link SWRLTab} when the plugin is activated.
 * <p>
 * The {@link #createSWRLPluginGUI(OWLModel, String, String, Icon, Icon)} method is called once when the plugin is registered and is expecting a Java Swing
 * {@link Component} that is a subclass of a Java AWT {@link Container} class. The {@link #getSWRLPluginGUI()} method may be called repeatedly after
 * registration and should return the {@link Container} instance created on initialization.
 * <p>
 * See <a href= "http://protege.cim3.net/cgi-bin/wiki.pl?SWRLRuleEngineBridgeFAQ#nid6QJ" >here</a> for a discussion on using this interface.
 */
public interface P3SWRLTabPluginCreator
{
	Container createSWRLPluginGUI(OWLModel owlModel, String pluginName, String ruleEngineName, Icon ruleEngineIcon, Icon reasonerIcon);

	Container getSWRLPluginGUI();
}
