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

package edu.stanford.smi.protegex.owl.ui.metadatatab;

import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.factory.FactoryUtils;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 30, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyURIDefaultSettingsPanel extends JPanel {


    private JTextField defaultURIBaseField;

    private JCheckBox yearCheckBox;

    private JCheckBox monthCheckBox;

    private JCheckBox dayCheckBox;

    private JTextField previewField;


    public OntologyURIDefaultSettingsPanel(boolean showHelpPanel) {
        createUI(showHelpPanel);
    }


    private void createUI(boolean showHelpPanel) {
        setLayout(new BorderLayout(12, 12));
        JPanel entryPanel = new JPanel(new BorderLayout(7, 7));
        defaultURIBaseField = new JTextField();
        defaultURIBaseField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updatePreviewState();
            }


            public void removeUpdate(DocumentEvent e) {
                updatePreviewState();
            }


            public void changedUpdate(DocumentEvent e) {
                updatePreviewState();
            }
        });
        LabeledComponent lc = new LabeledComponent("Ontology URI Domain and Path", defaultURIBaseField);
        entryPanel.add(lc, BorderLayout.NORTH);
        Box checkBoxPanel = new Box(BoxLayout.Y_AXIS);
        checkBoxPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        checkBoxPanel.add(yearCheckBox = new JCheckBox(new AbstractAction("Append year") {
            public void actionPerformed(ActionEvent e) {
                updateCheckBoxState();
            }
        }));
        checkBoxPanel.add(monthCheckBox = new JCheckBox(new AbstractAction("Append month") {
            public void actionPerformed(ActionEvent e) {
                updateCheckBoxState();
            }
        }));
        checkBoxPanel.add(dayCheckBox = new JCheckBox(new AbstractAction("Append day") {
            public void actionPerformed(ActionEvent e) {
                updateCheckBoxState();
            }
        }));
        entryPanel.add(checkBoxPanel, BorderLayout.SOUTH);
        JPanel entryPreviewPanel = new JPanel(new BorderLayout(7, 7));
        entryPreviewPanel.add(entryPanel, BorderLayout.NORTH);
        previewField = new JTextField();
        previewField.setEditable(false);
        LabeledComponent lc2 = new LabeledComponent("Preview", previewField);
        entryPreviewPanel.add(lc2, BorderLayout.SOUTH);
        add(entryPreviewPanel, BorderLayout.NORTH);
        if (showHelpPanel) {
            add(OWLUI.createHelpPanel(HELP_TEXT, "Ontology URI Default Settings"), BorderLayout.SOUTH);
        }
        updateUIFromAppProps();
        addHierarchyListener(new HierarchyListener() {
            public void hierarchyChanged(HierarchyEvent e) {
                if (isShowing()) {
                    defaultURIBaseField.requestFocus();
                    defaultURIBaseField.setCaretPosition(defaultURIBaseField.getText().length());
                }
            }
        });
    }


    private void updateUIFromAppProps() {
        defaultURIBaseField.setText(ApplicationProperties.getString(OntologyURIPanel.URI_BASE_PROPERTY));
        yearCheckBox.setSelected(ApplicationProperties.getBooleanProperty(OntologyURIPanel.URI_BASE_APPEND_YEAR_PROPERTY,
                false));
        monthCheckBox.setSelected(ApplicationProperties.getBooleanProperty(OntologyURIPanel.URI_BASE_APPEND_MONTH_PROPERTY,
                false));
        dayCheckBox.setSelected(ApplicationProperties.getBooleanProperty(OntologyURIPanel.URI_BASE_APPEND_DAY_PROPERTY,
                false));
        updateCheckBoxState();
    }


    private void updateAppPropsFromUI() {
        ApplicationProperties.setString(OntologyURIPanel.URI_BASE_PROPERTY, defaultURIBaseField.getText());
        ApplicationProperties.setBoolean(OntologyURIPanel.URI_BASE_APPEND_YEAR_PROPERTY, yearCheckBox.isSelected());
        ApplicationProperties.setBoolean(OntologyURIPanel.URI_BASE_APPEND_MONTH_PROPERTY, monthCheckBox.isSelected());
        ApplicationProperties.setBoolean(OntologyURIPanel.URI_BASE_APPEND_DAY_PROPERTY, dayCheckBox.isSelected());
    }


    private void updateCheckBoxState() {
        monthCheckBox.setEnabled(yearCheckBox.isSelected());
        if (yearCheckBox.isSelected() == false) {
            monthCheckBox.setSelected(false);
        }
        dayCheckBox.setEnabled(monthCheckBox.isSelected());
        if (monthCheckBox.isSelected() == false) {
            dayCheckBox.setSelected(false);
        }
        updatePreviewState();
    }


    private void updatePreviewState() {
        String text = defaultURIBaseField.getText();
        text = FactoryUtils.getOntologyURIBase(text,
                yearCheckBox.isSelected(),
                monthCheckBox.isSelected(),
                dayCheckBox.isSelected());
        if (text != null) {
            previewField.setText(text);
        }
        else {
            previewField.setText("");
        }
    }


    private static final String HELP_TEXT = "<p>These settings can be used to construct a default base<br>" +
            "URI that is used each time a new project is created in " +
            "Protege-OWL.</p>" +
            "<p><b>Ontology URI Base</b> is used as a prefix for the ontology URI</p>" +
            "<p><b>Append Year</b> includes the current year in the URI.</p>" +
            "<p><b>Append Month</b> includes the current month in the URI.</p>" +
            "<p><b>Append Day</b> includes the current day in the URI.</p>";


    public static void showDialog(Component parent) {
        OntologyURIDefaultSettingsPanel panel = new OntologyURIDefaultSettingsPanel(true);
        int ret = ProtegeUI.getModalDialogFactory().showDialog(parent, panel, "Ontology URI Default Settings", ModalDialogFactory.MODE_OK_CANCEL);
        if (ret == ModalDialogFactory.OPTION_OK) {
            panel.updateAppPropsFromUI();
        }
    }
}

