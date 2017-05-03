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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.stanford.smi.protegex.owl.jena.writersettings.JenaWriterSettings;
import edu.stanford.smi.protegex.owl.jena.writersettings.WriterSettings;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.writer.rdfxml.util.ProtegeWriterSettings;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class WriterSettingsPanel extends JComponent {

    private OWLModel owlModel;

    private JRadioButton jenaButton;

    private JRadioButton protegeButton;

    private JCheckBox sortAlphabeticallyBox;

    private WriterSettings writerSettings;

    private JCheckBox useXMLEntitiesBox;


    public WriterSettingsPanel(OWLModel owlModel) {
        this.owlModel = owlModel;

        jenaButton = new JRadioButton("Default Jena writer");
        protegeButton = new JRadioButton("Native writer");

        writerSettings = owlModel.getWriterSettings();
        if (writerSettings instanceof JenaWriterSettings) {
            jenaButton.setSelected(true);
        }
        else {
            protegeButton.setSelected(true);
        }
        ButtonGroup group = new ButtonGroup();
        group.add(jenaButton);
        group.add(protegeButton);

        sortAlphabeticallyBox = new JCheckBox("Sort resources alphabetically");
        sortAlphabeticallyBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSortAlphabetically(sortAlphabeticallyBox.isSelected());
            }
        });
        useXMLEntitiesBox = new JCheckBox("Use XML entities");
        useXMLEntitiesBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setUseXMLEntities(useXMLEntitiesBox.isSelected());
            }
        });

        jenaButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setJenaWriterSettings();
            }
        });

        protegeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setProtegeWriterSettings();
            }
        });

        JPanel mainPanel = new JPanel();
        setBorder(BorderFactory.createTitledBorder("RDF/XML Writer Settings"));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(jenaButton);
        mainPanel.add(protegeButton);
        setLayout(new BorderLayout());

        Box protegePanel = Box.createVerticalBox();
        protegePanel.add(useXMLEntitiesBox);
        protegePanel.add(sortAlphabeticallyBox);

        updateProtegePanel();

        add(BorderLayout.WEST, mainPanel);
        add(BorderLayout.CENTER, new JPanel());
        add(BorderLayout.EAST, protegePanel);
    }


    private void setJenaWriterSettings() {
        owlModel.setWriterSettings(new JenaWriterSettings(owlModel));
        updateProtegePanel();
    }


    private void setProtegeWriterSettings() {
        owlModel.setWriterSettings(new ProtegeWriterSettings(owlModel));
        updateProtegePanel();
    }


    private void setSortAlphabetically(boolean selected) {
        ProtegeWriterSettings p = (ProtegeWriterSettings) owlModel.getWriterSettings();
        p.setSortAlphabetically(selected);
    }


    private void setUseXMLEntities(boolean selected) {
        ProtegeWriterSettings p = (ProtegeWriterSettings) owlModel.getWriterSettings();
        p.setUseXMLEntities(selected);
    }


    private void updateProtegePanel() {
        WriterSettings settings = owlModel.getWriterSettings();
        boolean enabled = settings instanceof ProtegeWriterSettings;
        if (enabled) {
            ProtegeWriterSettings p = (ProtegeWriterSettings) settings;
            sortAlphabeticallyBox.setSelected(p.isSortAlphabetically());
            useXMLEntitiesBox.setSelected(p.getUseXMLEntities());
        }
        sortAlphabeticallyBox.setEnabled(enabled);
        useXMLEntitiesBox.setEnabled(enabled);
    }
}
