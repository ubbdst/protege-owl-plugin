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

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.project.SettingsMap;
import edu.stanford.smi.protegex.owl.ui.forms.AbsoluteFormsGenerator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class FormsSettingsPanel extends JPanel {

    private OWLModel owlModel;

    private JCheckBox saveFormsBox;

    private JCheckBox saveAllBox;


    public FormsSettingsPanel(OWLModel owlModel) {
        this.owlModel = owlModel;

        setBorder(BorderFactory.createTitledBorder("Forms"));
        saveFormsBox = new JCheckBox("Save forms to .forms files");
        saveFormsBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSettingsMap();
            }
        });
        saveAllBox = new JCheckBox("Also save uncustomized forms");
        saveAllBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSettingsMap();
            }
        });

        String key = getSettingsMap().getString(AbsoluteFormsGenerator.SAVE_FORMS_KEY);
        if(AbsoluteFormsGenerator.MODIFIED.equals(key)) {
            saveFormsBox.setSelected(true);
            saveAllBox.setEnabled(true);
        }
        else if(AbsoluteFormsGenerator.ALL.equals(key)) {
            saveFormsBox.setSelected(true);
            saveAllBox.setSelected(true);
            saveAllBox.setEnabled(true);
        }
        else {
            saveAllBox.setEnabled(false);
        }

        setLayout(new GridLayout(2, 1));
        add(saveFormsBox);
        add(saveAllBox);
    }


    private SettingsMap getSettingsMap() {
        return owlModel.getOWLProject().getSettingsMap();
    }


    private void updateSettingsMap() {
        SettingsMap map = getSettingsMap();
        String value = null;
        boolean selected = saveFormsBox.isSelected();
        saveAllBox.setEnabled(selected);
        if(selected) {
            if(saveAllBox.isSelected()) {
                value = AbsoluteFormsGenerator.ALL;
            }
            else {
                value = AbsoluteFormsGenerator.MODIFIED;
            }
        }
        map.setString(AbsoluteFormsGenerator.SAVE_FORMS_KEY, value);
    }
}
