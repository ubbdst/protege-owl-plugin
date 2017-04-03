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

package edu.stanford.smi.protegex.owl.ui.repository.wizard;

import edu.stanford.smi.protegex.owl.model.OWLModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 26, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SelectRepositoryTypeWizardPage extends AbstractRepositoryWizardPage {

    private ButtonGroup buttonGroup;

    private OWLModel owlModel;

	private RepositoryCreatorWizardPlugin selectedPlugin;


    public SelectRepositoryTypeWizardPage(RepositoryWizard wizard, OWLModel model) {
        super("Select repository type", wizard);
        this.owlModel = model;
        Box box = new Box(BoxLayout.Y_AXIS);
	    JPanel holder = new JPanel(new BorderLayout(7, 7));
        holder.add(new JLabel("Please select the type of repository that you would like to create"), BorderLayout.NORTH);
	    holder.add(box, BorderLayout.CENTER);
        buttonGroup = new ButtonGroup();
        String helpText = "";
        for (Iterator it = wizard.getPlugins().iterator(); it.hasNext();) {
            RepositoryCreatorWizardPlugin curPlugin = (RepositoryCreatorWizardPlugin) it.next();
            JRadioButton radioButton = new JRadioButton(new PluginSelectedAction(curPlugin));
            radioButton.setToolTipText(curPlugin.getDescription());
            radioButton.setEnabled(curPlugin.isSuitable(this.owlModel));
            box.add(radioButton);
            buttonGroup.add(radioButton);
            helpText += "<p>";
            helpText += "<b>" + curPlugin.getName() + "</b><font size=\"-2\"> " + curPlugin.getDescription();
            helpText += "</font></p>";


        }
	    box.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        getContentComponent().add(holder, BorderLayout.NORTH);
        setHelpText("Please select the type of repository that you would like to add:", helpText);
    }


    private class PluginSelectedAction extends AbstractAction {

        private RepositoryCreatorWizardPlugin plugin;


        public PluginSelectedAction(RepositoryCreatorWizardPlugin plugin) {
            super(plugin.getName());
            this.plugin = plugin;

        }


        public void actionPerformed(ActionEvent e) {
            selectedPlugin = this.plugin;
        }
    }

	public void nextPressed() {
		getRepositoryWizard().setSelectedPlugin(selectedPlugin);
	}


    public Dimension getPreferredSize() {
        return new Dimension(400, 300);
    }

}

