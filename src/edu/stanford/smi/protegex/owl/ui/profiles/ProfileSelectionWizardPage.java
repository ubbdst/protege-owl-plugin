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

package edu.stanford.smi.protegex.owl.ui.profiles;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.WaitCursor;
import edu.stanford.smi.protege.util.Wizard;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.jena.OWLFilesPlugin;
import edu.stanford.smi.protegex.owl.ui.menu.OWLViewWizardPage;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ProfileSelectionWizardPage extends WizardPage {

    public static boolean isBuild;

    public final static String DEFAULT_PROFILE_KEY = "ProfileSelectionWizardPage.default";

    private OWLFilesPlugin plugin;

    private Map profileRadioButtons2URI;

    private final static String HELP_TEXT =
            "<P>You can select which elements of OWL and RDF you want to use in your project. " +
                    "You can change these settings later at any time, using OWL/Preferences.</P>" +
                    "<P>For example, if you select OWL Lite, then you cannot create owl:unionOf classes, and " +
                    "if you select pure RDF then you can only create rdf:Properties and rdfs:Classes.</P>";

    private Box profilesButtonsPanel;


    public ProfileSelectionWizardPage(Wizard wizard, OWLFilesPlugin plugin) {
        super("View Settings", wizard);
        this.plugin = plugin;

        addComponentListener(new ComponentAdapter() {
            @Override
			public void componentShown(ComponentEvent e) {
                if (profileRadioButtons2URI == null) {
                    addProfilesButtons();
                    revalidate();
                }
            }
        });
        profilesButtonsPanel = Box.createVerticalBox();
        profilesButtonsPanel.setBorder(BorderFactory.createTitledBorder("Language Profile"));
        profilesButtonsPanel.add(new JLabel("Loading available profiles..."));

        setLayout(new BorderLayout());
        add(BorderLayout.NORTH, profilesButtonsPanel);
        add(BorderLayout.SOUTH, OWLUI.createHelpPanel(HELP_TEXT,
                "Which OWL/RDF dialect do you want to use?",
                OWLUI.WIZARD_HELP_HEIGHT));

        setPageComplete(ApplicationProperties.getString(DEFAULT_PROFILE_KEY) != null);

        addComponentListener(new ComponentAdapter() {
            @Override
			public void componentShown(ComponentEvent e) {
                setPageComplete(true);
            }
        });
    }


    private void addProfilesButtons() {
        WaitCursor waitCursor = new WaitCursor(this);
        waitCursor.show();
        OntModel defaultOntModel = ProfilesManager.getDefaultProfileOntModel();
        if (defaultOntModel != null) {
            ButtonGroup profileButtonsGroup = new ButtonGroup();
            profileRadioButtons2URI = new HashMap();
            profilesButtonsPanel.removeAll();
            String defaultURI = ApplicationProperties.getString(DEFAULT_PROFILE_KEY, OWLProfiles.OWL_DL.getURI());
            String[] uris = ProfileSelectionPanel.DEFAULT_PROFILES;
            for (String uri : uris) {
                OntClass ontClass = defaultOntModel.getOntClass(uri);
                if (ontClass != null) {
	                String label = ontClass.getLabel("");
	                JRadioButton radioButton = new JRadioButton(label);
	                if (uri.equals(defaultURI)) {
	                    radioButton.setSelected(true);
	                }
	                profileRadioButtons2URI.put(radioButton, uri);
	                profileButtonsGroup.add(radioButton);
	                profilesButtonsPanel.add(radioButton);
                }
            }
        }
        waitCursor.hide();
    }


    @Override
	public WizardPage getNextPage() {
        return new OWLViewWizardPage(getWizard(), plugin);
    }


    @Override
	public void onFinish() {
        if (profileRadioButtons2URI != null) {
            for (Iterator it = profileRadioButtons2URI.keySet().iterator(); it.hasNext();) {
                JRadioButton radioButton = (JRadioButton) it.next();
                if (radioButton.isSelected()) {
                    String uri = (String) profileRadioButtons2URI.get(radioButton);
                    plugin.setProfile(uri);
                    ApplicationProperties.setString(DEFAULT_PROFILE_KEY, uri);
                }
            }
        }
    }
}
