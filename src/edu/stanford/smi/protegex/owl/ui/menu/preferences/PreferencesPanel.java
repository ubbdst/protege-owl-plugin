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

package edu.stanford.smi.protegex.owl.ui.menu.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.ValidatableTabComponent;
import edu.stanford.smi.protegex.owl.inference.util.ReasonerPreferences;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.forms.AbsoluteFormsGenerator;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfileSelectionPanel;
import edu.stanford.smi.protegex.owl.ui.projectview.ConfigureTabsPanel;
import edu.stanford.smi.protegex.owl.ui.testing.OWLTestSettingsPanel;

/**
 * This dialog allows the user to edit general owl settings.
 * This is currently rather ugly in so far as it directly assigns every change
 * without providing the option to cancel.
 *
 * @author Daniel Stoeckli <stoeckli@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class PreferencesPanel extends ValidatableTabComponent {

    private DatatypeSettingsPanel datatypeSettingsPanel;

    private JCheckBox encodeTagCheckBox;

    private ProfileSelectionPanel owlProfilePanel;

    private ConfigureTabsPanel configureTabsPanel;

    private UISettingsPanel uiSettingsPanel;

    private VisibilityPanel visibilityPanel;
    
    private RenderingPanel renderingPanel;

    private OWLTestSettingsPanel testsPanel;


    public PreferencesPanel(OWLModel owlModel) {
        addComponents(owlModel);
    }

    private void addComponents(OWLModel owlModel) {

        JPanel generalTab = createGeneralTab(owlModel);
        renderingPanel = new  RenderingPanel(owlModel);
        JPanel encodingTab = createEncodingTab();
        visibilityPanel = new VisibilityPanel(owlModel);
        datatypeSettingsPanel = new DatatypeSettingsPanel(owlModel);
        configureTabsPanel = new ConfigureTabsPanel(ProtegeUI.getProjectView(owlModel.getProject()));
        testsPanel = new OWLTestSettingsPanel(owlModel);

        addTab("General", generalTab);
        addTab("Rendering", renderingPanel);
        addTab("Visibility", visibilityPanel);
        addTab("Datatypes", datatypeSettingsPanel);
        addTab("Searching", new SearchSettingsPanel(owlModel));
        addTab("Annotations", new AnnotationsViewSettingsPanel(owlModel));
        addTab("Encoding", encodingTab);
        addTab("Tabs", configureTabsPanel);
        addTab("Tests", testsPanel);
    }


    private JPanel createGeneralTab(OWLModel owlModel) {
        JPanel generalTab = new JPanel();
        generalTab.setLayout(new BoxLayout(generalTab, BoxLayout.Y_AXIS));

        uiSettingsPanel = new UISettingsPanel(owlModel);
        generalTab.add(uiSettingsPanel);

        JPanel reasonerPanel = new JPanel(new BorderLayout(0, 8));
        reasonerPanel.setBorder(BorderFactory.createTitledBorder("Reasoning"));
        reasonerPanel.add(BorderLayout.CENTER,
                new LabeledComponent("Reasoner URL", createValidatorField(), false));
        generalTab.add(reasonerPanel);
        if (owlModel instanceof JenaOWLModel) {
            generalTab.add(Box.createVerticalStrut(8));
            generalTab.add(new ProtegeSettingsPanel((JenaOWLModel) owlModel));
        }
        owlProfilePanel = new ProfileSelectionPanel(owlModel);
        generalTab.add(Box.createVerticalStrut(8));
        generalTab.add(owlProfilePanel);
        
        generalTab.add(Box.createVerticalStrut(8));
        generalTab.add(new WriterSettingsPanel(owlModel));

        if(AbsoluteFormsGenerator.optional) {
            generalTab.add(Box.createVerticalStrut(8));
            generalTab.add(new FormsSettingsPanel(owlModel));
        }

        JPanel container = new JPanel(new BorderLayout());
        container.add(BorderLayout.NORTH, generalTab);
        container.add(BorderLayout.CENTER, new JPanel());
        return container;
    }


    private JPanel createEncodingTab() {
        encodeTagCheckBox = new JCheckBox("Don't write <?xml...> tag into RDF files",
                Jena.isXMLTagHidden());
        encodeTagCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Jena.setXMLTagHidden(encodeTagCheckBox.isSelected());
            }
        });
        JPanel encodingTab = new JPanel();
        encodingTab.setLayout(new BoxLayout(encodingTab, BoxLayout.Y_AXIS));
        encodingTab.add(encodeTagCheckBox);
        return encodingTab;
    }


    private JTextField createValidatorField() {

        String reasonerURL = ReasonerPreferences.getInstance().getReasonerURL();

        final JTextField validatorField = new JTextField(reasonerURL);

        validatorField.setPreferredSize(new Dimension(300, 25));

        validatorField.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent arg0) {
            }


            public void focusLost(FocusEvent arg0) {
                String newUrl = validatorField.getText();
                ReasonerPreferences.getInstance().setReasonerURL(newUrl);
            }
        });

        return validatorField;
    }


    public boolean getRequiresReloadUI() {
        return visibilityPanel.getRequiresReloadUI() ||
                renderingPanel.getRequiresReloadUI() || 
                owlProfilePanel.getRequiresReloadUI() ||
                datatypeSettingsPanel.getRequiresReloadUI() ||
                uiSettingsPanel.getRequiresReloadUI() ||
                configureTabsPanel.getRequiresReloadUI();
    }


    public void ok() {
        configureTabsPanel.saveContents();
    }
}
