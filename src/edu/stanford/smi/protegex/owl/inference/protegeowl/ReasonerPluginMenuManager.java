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

package edu.stanford.smi.protegex.owl.inference.protegeowl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import edu.stanford.smi.protegex.owl.model.OWLModel;

public class ReasonerPluginMenuManager {
	public final static String NONE_REASONER = "None";

	public static void fillReasoningMenu(OWLModel owlModel, JMenu reasoningMenu) {
		Map<String, String> name2ClassName = ReasonerPluginManager.getSuitableReasonerMap(owlModel);
		
		if (name2ClassName.size() == 0) {
			return;
		}
		
		ArrayList<String> names = new ArrayList<String>(name2ClassName.keySet());
		Collections.sort(names);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		
		buttonGroup.add(addMenuItem(owlModel, reasoningMenu, NONE_REASONER, null));
		
		for (String name : names) {
			buttonGroup.add(addMenuItem(owlModel, reasoningMenu, name, name2ClassName.get(name)));
		}
		
	}

	private static JRadioButtonMenuItem addMenuItem(final OWLModel owlModel, JMenu reasoningMenu, String name, final String className) {
		final JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(name);
		
		String defaultReasonerClassName = ReasonerManager.getInstance().getDefaultReasonerClassName();
		
		if (defaultReasonerClassName.equals(className)) {
			ReasonerManager.getInstance().setProtegeReasonerClass(owlModel, className);
			menuItem.setSelected(true);
		}
		
		menuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (menuItem.isSelected()) {
					ReasonerManager.getInstance().setDefaultReasonerClass(className);
					ReasonerManager.getInstance().setProtegeReasonerClass(owlModel, className);
				}				
			}			
		});
		
		reasoningMenu.add(menuItem);
		
		return menuItem;
	}
	
}
