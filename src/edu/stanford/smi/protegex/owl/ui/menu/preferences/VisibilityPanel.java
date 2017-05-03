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
import java.awt.Component;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;

/**
 * A JComponent that allows to specify which system classes are hidden or visible.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class VisibilityPanel extends JComponent {

    private boolean requiresReloadUI;


    public VisibilityPanel(final OWLModel owlModel) {

        Cls[] metaClses = new Cls[]{
                owlModel.getRDFSNamedClassClass(),
                owlModel.getOWLNamedClassClass(),
                owlModel.getRDFPropertyClass(),
                owlModel.getOWLDatatypePropertyClass(),
                owlModel.getOWLObjectPropertyClass(),
                owlModel.getRDFSNamedClass(OWLNames.Cls.ANNOTATION_PROPERTY),
                owlModel.getRDFSNamedClass(OWLNames.Cls.DEPRECATED_CLASS),
                owlModel.getRDFSNamedClass(OWLNames.Cls.DEPRECATED_PROPERTY),
                owlModel.getRDFSNamedClass(OWLNames.Cls.FUNCTIONAL_PROPERTY),
                owlModel.getRDFSNamedClass(OWLNames.Cls.INVERSE_FUNCTIONAL_PROPERTY),
                owlModel.getRDFSNamedClass(OWLNames.Cls.SYMMETRIC_PROPERTY),
                owlModel.getRDFSNamedClass(OWLNames.Cls.TRANSITIVE_PROPERTY),
        };

        Cls[] clses = new Cls[]{
                owlModel.getRDFSNamedClass(RDFSNames.Cls.DATATYPE),
                owlModel.getRDFListClass(),
                owlModel.getRDFSNamedClass(RDFSNames.Cls.CONTAINER),
                owlModel.getRDFSNamedClass(RDFNames.Cls.ALT),
                owlModel.getRDFSNamedClass(RDFNames.Cls.BAG),
                owlModel.getRDFSNamedClass(RDFNames.Cls.SEQ),
                owlModel.getRDFSNamedClass(RDFNames.Cls.STATEMENT),
                owlModel.getRDFSNamedClass(OWLNames.Cls.ALL_DIFFERENT),
                owlModel.getOWLNothing(),
                owlModel.getOWLDataRangeClass()
        };
        RDFProperty[] rdfProperties = new RDFProperty[]{
                owlModel.getRDFProperty(RDFNames.Slot.FIRST),
                owlModel.getRDFProperty(RDFNames.Slot.REST),
                owlModel.getRDFProperty(RDFSNames.Slot.RANGE),
                owlModel.getRDFProperty(RDFSNames.Slot.MEMBER),
                owlModel.getRDFProperty(RDFNames.Slot.TYPE),
                owlModel.getRDFProperty(RDFNames.Slot.OBJECT),
                owlModel.getRDFProperty(RDFNames.Slot.PREDICATE),
                owlModel.getRDFProperty(RDFNames.Slot.SUBJECT),
                owlModel.getRDFProperty(RDFNames.Slot.VALUE)
        };
        RDFProperty[] owlProperties = new RDFProperty[]{
                owlModel.getRDFProperty(OWLNames.Slot.SAME_AS),
                owlModel.getRDFProperty(OWLNames.Slot.DIFFERENT_FROM),
                owlModel.getRDFProperty(OWLNames.Slot.DISJOINT_WITH),
                owlModel.getRDFProperty(OWLNames.Slot.DISTINCT_MEMBERS),
                owlModel.getRDFProperty(OWLNames.Slot.ALL_VALUES_FROM),
                owlModel.getRDFProperty(OWLNames.Slot.SOME_VALUES_FROM),
                owlModel.getRDFProperty(OWLNames.Slot.HAS_VALUE),
                owlModel.getRDFProperty(OWLNames.Slot.CARDINALITY),
                owlModel.getRDFProperty(OWLNames.Slot.MIN_CARDINALITY),
                owlModel.getRDFProperty(OWLNames.Slot.MAX_CARDINALITY),
                owlModel.getRDFProperty(OWLNames.Slot.ONE_OF),
                owlModel.getRDFProperty(OWLNames.Slot.ON_PROPERTY)
        };
        Instance[] protegeInstances = new Instance[]{
                owlModel.getSystemFrames().getDirectedBinaryRelationCls(),
                owlModel.getSystemFrames().getFromSlot(),
                owlModel.getSystemFrames().getToSlot()
                //((KnowledgeBase)owlModel).getCls(Model.Cls.PAL_CONSTRAINT)
        };
        Component metaClsesPanel = createCheckBoxesPanel("Metaclasses", metaClses);
        Component clsesPanel = createCheckBoxesPanel("Other Classes", clses);
        Component rdfPropertiesPanel = createCheckBoxesPanel("RDF Properties", rdfProperties);
        Component owlPropertiesPanel = createCheckBoxesPanel("OWL Properties", owlProperties);
        Component protegePanel = createCheckBoxesPanel("Native Protege Resources", protegeInstances);
        Component annotationsPanel = createCheckBoxesPanel("Annotation Properties",
                owlModel.getSystemAnnotationProperties());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(BorderLayout.CENTER, metaClsesPanel);
        leftPanel.add(BorderLayout.SOUTH, clsesPanel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(BorderLayout.NORTH, rdfPropertiesPanel);
        centerPanel.add(BorderLayout.SOUTH, owlPropertiesPanel);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(BorderLayout.NORTH, annotationsPanel);
        rightPanel.add(BorderLayout.CENTER, protegePanel);
        // rightPanel.add(BorderLayout.SOUTH, importsPanel);

        setLayout(new GridLayout(1, 3));
        add(leftPanel);
        add(centerPanel);
        add(rightPanel);
    }


    private Component createCheckBoxesPanel(String title, Instance[] instances) {
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < instances.length; i++) {
            Instance instance = instances[i];
            innerPanel.add(new VisibilityCheckBox(instance));
        }

        JCheckBox allCheckBox = new AllCheckBox(innerPanel);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(BorderLayout.NORTH, allCheckBox);
        panel.add(BorderLayout.WEST, Box.createHorizontalStrut(12));
        panel.add(BorderLayout.CENTER, innerPanel);
        return panel;
    }


    public boolean getRequiresReloadUI() {
        return requiresReloadUI;
    }


    private class AllCheckBox extends JCheckBox {

        private Container container;


        AllCheckBox(Container container) {
            super("All");
            this.container = container;
            setSelected(isAllSelected());
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateDependents();
                    requiresReloadUI = true;
                }
            });
        }


        private boolean isAllSelected() {
            for (int i = 0; i < container.getComponentCount(); i++) {
                if (container.getComponent(i) instanceof VisibilityCheckBox) {
                    VisibilityCheckBox checkBox = (VisibilityCheckBox) container.getComponent(i);
                    if (!checkBox.isSelected()) {
                        return false;
                    }
                }
            }
            return true;
        }


        private void updateDependents() {
            boolean value = isSelected();
            for (int i = 0; i < container.getComponentCount(); i++) {
                if (container.getComponent(i) instanceof VisibilityCheckBox) {
                    VisibilityCheckBox checkBox = (VisibilityCheckBox) container.getComponent(i);
                    checkBox.setSelected(value);
                    checkBox.getInstance().setVisible(value);
                }
            }
        }
    }


    private class VisibilityCheckBox extends JCheckBox {

        private Instance instance;


        VisibilityCheckBox(Instance anInstance) {
            super(anInstance.getBrowserText(), anInstance.isVisible());
            this.instance = anInstance;
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateVisibility();
                }
            });
        }


        Instance getInstance() {
            return instance;
        }


        private void updateVisibility() {
            boolean visible = isSelected();
            instance.setVisible(visible);
            if (instance instanceof Cls) {
                Cls cls = ((Cls) instance);
                Collection dependents = visible ? cls.getSuperclasses() : cls.getSubclasses();
                setDependentCheckBoxesSelected(dependents, visible);
            }
            requiresReloadUI = true;
        }


        private void setDependentCheckBoxesSelected(Collection dependents, boolean value) {
            for (Iterator it = dependents.iterator(); it.hasNext();) {
                Cls superCls = (Cls) it.next();
                if (superCls instanceof RDFSClass) {
                    superCls.setVisible(value);
                }
                Container cont = getParent();
                for (int i = 0; i < cont.getComponentCount(); i++) {
                    if (cont.getComponent(i) instanceof VisibilityCheckBox) {
                        VisibilityCheckBox checkBox = (VisibilityCheckBox) cont.getComponent(i);
                        if (superCls.equals(checkBox.instance)) {
                            checkBox.setSelected(value);
                        }
                    }
                }
            }
        }
    }
}
