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

package edu.stanford.smi.protegex.owl.ui.classform.form;

import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.PopupMenuMouseListener;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.classform.component.property.PropertyFormComponent;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.InferredModeWidget;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A JComponent displaying characteristics of a named class.
 *
 * @author Matthew Horridge  <matthew.horridge@cs.man.ac.uk>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ClassForm extends JComponent implements InferredModeWidget {

    private AddPropertyFormComponentAction addNecAction = new AddPropertyFormComponentAction(this);

    private AddPropertyFormComponentAction addSufAction = new AddPropertyFormComponentAction(this);

    private int colCount = 2;

    private OWLNamedClass namedClass;

    private List separators = new ArrayList();


    public ClassForm() {
        // setBorder(BorderFactory.createLineBorder(Color.CYAN, 3));
        addMouseListener(new PopupMenuMouseListener(this) {
            protected JPopupMenu getPopupMenu() {
                JPopupMenu menu = new JPopupMenu();
                menu.add(new JMenuItem("Test..."));
                return menu;
            }


            protected void setSelection(JComponent c, int x, int y) {
            }
        });
    }


    // Note: Junk implementation only!
    private void addComponents() {
        assert namedClass != null;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        Set sufProperties = getSufficientProperties();
        Set necProperties = getNecessaryProperties(sufProperties);
        ClassFormSeparator sufSeparator = new ClassFormSeparator(true);
        ClassFormSeparator necSeparator = new ClassFormSeparator(false);
        separators.add(sufSeparator);
        separators.add(necSeparator);

        add(sufSeparator);
        add(getComponentWrapper((RDFProperty[]) sufProperties.toArray(new RDFProperty[0]), addSufAction));
        add(necSeparator);
        add(getComponentWrapper((RDFProperty[]) necProperties.toArray(new RDFProperty[0]), addNecAction));
    }


    public void doLayout() {
        super.doLayout();
        for (int i = 0; i < getComponentCount(); i++) {
            Component comp = getComponent(i);
            comp.setBounds(0, comp.getY(), getWidth(), comp.getHeight());
        }
    }


    private Component getAddButtonComponent(ResourceSelectionAction addAction) {
        JToolBar toolBar = ComponentFactory.createToolBar();
        JButton button = ComponentFactory.addToolBarButton(toolBar, addAction);
        addAction.activateComboBox(button);
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(BorderLayout.WEST, toolBar);
        mainPanel.add(BorderLayout.CENTER, Box.createHorizontalGlue());
        return mainPanel;
    }


    // Note: This may need to change in case we put everything into the main component
    // and a native LayoutManager.  Here it's just more convenient to use a prefactored LayoutManager
    private Component getComponentWrapper(RDFProperty[] properties, AddPropertyFormComponentAction action) {
        if (properties.length == 0) {
            return getAddButtonComponent(action);
        }
        else {
            JPanel panel = new JPanel(new GridLayout(1, colCount));
            Container[] cont = new Container[colCount];
            for (int i = 0; i < colCount; i++) {
                cont[i] = Box.createVerticalBox();
                JPanel wrap = new JPanel(new BorderLayout());
                wrap.add(BorderLayout.NORTH, cont[i]);
                panel.add(wrap);
            }
            for (int i = 0; i < properties.length; i++) {
                RDFProperty property = properties[i];
                PropertyFormComponent formComponent = new PropertyFormComponent(property);
                formComponent.setNamedClass(namedClass);
                Container conti = cont[i % colCount];
                conti.add(formComponent);
            }
            Container container = cont[0];
            container.add(getAddButtonComponent(action));
            return panel;
        }
    }


    public OWLNamedClass getNamedClass() {
        return namedClass;
    }


    private Set getNecessaryProperties(Set sufficientProperties) {
        Set result = new HashSet();
        result.addAll(namedClass.getUnionDomainProperties(true));
        OWLNamedClass owlThingClass = namedClass.getOWLModel().getOWLThingClass();
        result.removeAll(owlThingClass.getUnionDomainProperties());
        result.removeAll(sufficientProperties);
        Iterator supers = namedClass.getSuperclasses(true).iterator();
        while (supers.hasNext()) {
            RDFSClass superclass = (RDFSClass) supers.next();
            if (superclass instanceof OWLRestriction) {
                result.add(((OWLRestriction) superclass).getOnProperty());
            }
        }
        return result;
    }


    private Set getSufficientProperties() {
        Set result = new HashSet();
        RDFSClass definition = namedClass.getDefinition();
        if (definition != null) {
            if (definition instanceof OWLRestriction) {
                result.add(((OWLRestriction) definition).getOnProperty());
            }
            else {
                Iterator deps = definition.getDependingClasses().iterator();
                while (deps.hasNext()) {
                    RDFSClass dep = (RDFSClass) deps.next();
                    if (dep instanceof OWLRestriction) {
                        result.add(((OWLRestriction) dep).getOnProperty());
                    }
                }
            }
        }
        return result;
    }


    private void refill() {
        removeAll();
        if (namedClass != null) {
            addComponents();
        }
    }


    public void setInferredMode(boolean value) {
        // TODO
    }


    /**
     * Sets the class that is currently displayed.
     *
     * @param namedClass the new class or null for no contents
     */
    public void setNamedClass(OWLNamedClass namedClass) {
        this.namedClass = namedClass;
        refill();
    }
}
