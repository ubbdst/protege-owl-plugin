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

package edu.stanford.smi.protegex.owl.ui.menu;

import edu.stanford.smi.protege.util.Wizard;
import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.jena.OWLCreateProjectPlugin;
import edu.stanford.smi.protegex.owl.ui.cls.SwitchClassDefinitionResourceDisplayPlugin;
import edu.stanford.smi.protegex.owl.ui.cls.SwitchableClassDefinitionWidget;
import edu.stanford.smi.protegex.owl.ui.cls.SwitchableType;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLViewWizardPage extends WizardPage {

    private Map button2Type;

    private OWLCreateProjectPlugin plugin;

    private final static String HELP_TEXT =
            "<P>On this page you can specify the initial user interface settings for the OWL Classes Tab. " +
                    "The preferred user interface depends on the language features and experience. " +
                    "You can change these settings later at any time, at the bottom of the tab.</P>";


    public OWLViewWizardPage(Wizard wizard, OWLCreateProjectPlugin plugin) {
        super("View Settings", wizard);
        this.plugin = plugin;

        button2Type = new HashMap();
        boolean selected = false;
        JPanel viewPanel = new JPanel();
        String defaultClassView = SwitchClassDefinitionResourceDisplayPlugin.getDefaultClassView();
        ButtonGroup group = new ButtonGroup();
        Iterator<SwitchableType> types = SwitchableClassDefinitionWidget.listSwitchableTypes();
        while (types.hasNext()) {
            SwitchableType type = types.next();
            JRadioButton button = new JRadioButton(type.getButtonText());
            button2Type.put(button, type);
            String name = type.getClass().getName();
            if (name.equals(defaultClassView)) {
                button.setSelected(true);
                plugin.setDefaultClassView(type.getClass());
                selected = true;
            }
            group.add(button);
            viewPanel.add(button);
        }
        if (!selected) {
            JRadioButton first = (JRadioButton) viewPanel.getComponent(0);
            first.setSelected(true);
        }
        viewPanel.setLayout(new GridLayout(button2Type.size(), 1));
        viewPanel.setBorder(BorderFactory.createTitledBorder("OWL Classes View"));

        setLayout(new BorderLayout());

        add(BorderLayout.NORTH, viewPanel);
        add(BorderLayout.SOUTH, OWLUI.createHelpPanel(HELP_TEXT,
                "Do you prefer a less complex user interface?",
                OWLUI.WIZARD_HELP_HEIGHT));
    }


    public void onFinish() {
        for (Iterator it = button2Type.keySet().iterator(); it.hasNext();) {
            JRadioButton button = (JRadioButton) it.next();
            if (button.isSelected()) {
                SwitchableType type = (SwitchableType) button2Type.get(button);
                plugin.setDefaultClassView(type.getClass());
            }
        }
    }
}
