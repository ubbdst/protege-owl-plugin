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

package edu.stanford.smi.protegex.owl.ui.cls;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.plugin.PluginUtilities;
import edu.stanford.smi.protege.util.ApplicationProperties;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.PropertyList;
import edu.stanford.smi.protege.widget.SlotWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.project.OWLProject;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplayPlugin;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SwitchClassDefinitionResourceDisplayPlugin implements ResourceDisplayPlugin {
    private final static Logger log = Log.getLogger(SwitchClassDefinitionResourceDisplayPlugin.class);

    private final static String PROPERTY = "SwitchableClassDefinitionType";

    public static boolean showWarning = true;


    private SwitchableClassDefinitionWidget getSwitchableClassDefinitionWidget(JPanel hostPanel) {
        Component c = hostPanel.getParent();
        while (c != null && !(c instanceof ResourceDisplay)) {
            c = c.getParent();
        }
        if (c == null) return null;
        return searchForWidget(c);
    }


    private SwitchableClassDefinitionWidget searchForWidget(Component component) {
        if(component instanceof SwitchableClassDefinitionWidget) {
            return (SwitchableClassDefinitionWidget) component;
        }
        if(component instanceof Container) {
            Component [] childComponents = ((Container) component).getComponents();
            for(int i = 0; i < childComponents.length; i++) {
                if(childComponents[i] instanceof SwitchableClassDefinitionWidget) {
                    return (SwitchableClassDefinitionWidget) childComponents[i];
                }
            }
            for(int i = 0; i < childComponents.length; i++) {
                SwitchableClassDefinitionWidget widget = searchForWidget(childComponents[i]);
                if(widget != null) {
                    return widget;
                }
            }
        }
        return null;
    }

    public static String getDefaultClassView() {
        return ApplicationProperties.getString(PROPERTY,
                LogicClassDefinitionWidgetType.class.getName());
    }


    public void initResourceDisplay(RDFResource resource, JPanel hostPanel) {
        if (log.isLoggable(Level.FINE)) {
          log.fine("Entering initResourceDisplay - " + resource + ", " + hostPanel);
        }
        if (resource instanceof OWLNamedClass) {
            SwitchableClassDefinitionWidget widget = getSwitchableClassDefinitionWidget(hostPanel);
            if (widget != null) {
                OWLProject project = resource.getOWLModel().getOWLProject();
                String className = project.getSettingsMap().getString(PROPERTY);
                try {
                  if (className != null) {
                    Class cls = PluginUtilities.forName(className);
                    if (cls != null) { 
                      if (log.isLoggable(Level.FINE)) {
                        log.fine("New class = " + className);
                      }
                      SwitchableType type = (SwitchableType) cls.newInstance();
                      widget.setActiveType(type.getWidgetClassType());
                    }
                  } else if (log.isLoggable(Level.FINE)){
                      log.fine("No SwitchableType class found for resource" + resource);
                  }
                }
                catch (Exception ex) {
                  Log.emptyCatchBlock(ex);
                }
                SwitchPanel switchPanel = new SwitchPanel(widget);
                switchPanel.updateStatus();
                hostPanel.add(switchPanel);
            }
        }
    }


    public static void setClassesView(PropertyList sources, String activeClassName) {
        sources.setString(PROPERTY, activeClassName);
    }


    public static void setDefaultClassesView(String activeClassName) {
        ApplicationProperties.setString(PROPERTY, activeClassName);
    }


    private class SwitchPanel extends JPanel {

        private Map types2RadioButton = new HashMap();

        private SwitchableClassDefinitionWidget widget;


        SwitchPanel(final SwitchableClassDefinitionWidget widget) {
            this.widget = widget;
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            ButtonGroup group = new ButtonGroup();
            final OWLModel owlModel = widget.getOWLModel();
            for (Iterator<SwitchableType> it = widget.listSwitchableTypes(); it.hasNext();) {
                final SwitchableType type = it.next();
                if (type.isSuitable(owlModel)) {
                    JRadioButton button = new JRadioButton(type.getButtonText());
                    group.add(button);
                    button.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            switchTo(type);
                            owlModel.getOWLProject().getSettingsMap().setString(PROPERTY, type.getWidgetClassType().getName());         
                            widget.setInstance(widget.getEditedResource());
                        }
                    });
                    add(button);
                    add(Box.createHorizontalStrut(8));
                    if (widget.getActiveWidgetClass() == type.getWidgetClassType()) {
                        button.setSelected(true);
                    }
                    types2RadioButton.put(type, button);
                }
            }
        }


        private void switchTo(SwitchableType type) {
            widget.setActiveType(type.getWidgetClassType());
        }


        public void updateStatus() {
            RDFSNamedClass namedClass = (RDFSNamedClass) widget.getEditedResource();
            Iterator it = types2RadioButton.keySet().iterator();
            while (it.hasNext()) {
                SwitchableType type = (SwitchableType) it.next();
                JRadioButton button = getRadioButton(type);
                if (namedClass != null && !type.isSufficientlyExpressive(namedClass)) {
                    button.setEnabled(false);
                    button.setToolTipText("The selected class uses OWL features that cannot be displayed with the " + type.getButtonText() + ".");
                    if (button.isSelected()) {
                        Iterator<SwitchableType> types = widget.listSwitchableTypes();
                        while (types.hasNext()) {
                            SwitchableType otherType = types.next();
                            if (otherType != type && otherType.isSufficientlyExpressive(namedClass)) {
                                JRadioButton otherButton = getRadioButton(otherType);
                                otherButton.setSelected(true);
                                switchTo(otherType);
                                if (showWarning) {
                                    ProtegeUI.getModalDialogFactory().showMessageDialog(namedClass.getOWLModel(),
                                            "The class " + namedClass.getBrowserText() +
                                                    " uses OWL features\nthat cannot be displayed with the " +
                                                    type.getButtonText() + ".\nWe therefore switch to the " +
                                                    otherType.getButtonText() + ".");
                                    showWarning = false;
                                }
                                break;
                            }
                        }
                    }
                }
                else {
                    button.setEnabled(true);
                    button.setToolTipText(null);
                }
            }
        }


        private JRadioButton getRadioButton(SwitchableType type) {
            return (JRadioButton) types2RadioButton.get(type);
        }

        //public void actionPerformed(ActionEvent e) {
        //    boolean propertyMode = propertyViewButton.isSelected();
        //    widget.setPropertyMode(propertyMode);
        //    final Project project = widget.getProject();
        //    setClassesView(project.getSources(), propertyMode);
        //}
        
    
    }
}
