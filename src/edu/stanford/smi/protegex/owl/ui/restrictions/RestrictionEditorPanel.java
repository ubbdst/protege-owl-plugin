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

package edu.stanford.smi.protegex.owl.ui.restrictions;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.widget.ClsWidget;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.OWLProperty;
import edu.stanford.smi.protegex.owl.model.OWLRestriction;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.code.OWLSymbolPanel;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory.CloseCallback;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.InstanceNameComponent;
import edu.stanford.smi.protegex.owl.ui.search.ResourceListFinder;

/**
 * A panel that allows to completely define a restriction.
 * This displays a searchable propertyList of slots, radiobuttons to select the restriction kind,
 * and a textfield that is supported by a symbol panel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RestrictionEditorPanel extends JComponent implements ModalDialogFactory.CloseCallback {

    private Action createDatatypePropertyAction;

    private Action createObjectPropertyAction;

    private Action createRDFPropertyAction;

    private FillerTextArea fillerTextArea;

    private JList kindList;

    private OWLModel owlModel;

    private JList propertyList;

    private OWLSymbolPanel symbolPanel;

    private RDFSClass targetClass;

    private Action viewAction;

    private static String recentPropertyName;


    public RestrictionEditorPanel(OWLModel owlModel,
                                  edu.stanford.smi.protege.model.Cls metaCls,
                                  RDFProperty property, String fillerText, RDFSClass targetClass) {

        this.owlModel = owlModel;
        this.targetClass = targetClass;
        createActions();

        propertyList = ComponentFactory.createList(null);
        propertyList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        propertyList.setCellRenderer(FrameRenderer.createInstance());
        propertyList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                changeProperty((RDFProperty) propertyList.getSelectedValue());
                enableActions();
            }
        });
        propertyList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && viewAction.isEnabled()) {
                    viewAction.actionPerformed(null);
                }
            }
        });

        updatePropertiesList();

        JPanel propertyPanel = new JPanel(new BorderLayout());
        propertyPanel.setLayout(new BorderLayout());
        JScrollPane propertyScrollPane = new JScrollPane(propertyList);
        propertyScrollPane.setPreferredSize(new Dimension(240, 150));
        propertyPanel.add(BorderLayout.CENTER, propertyScrollPane);
        propertyPanel.add(BorderLayout.SOUTH, new ResourceListFinder(propertyList, "Find"));

        edu.stanford.smi.protege.model.Cls[] metaClses = ProfilesManager.getSupportedRestrictionMetaClses(owlModel);
        kindList = new JList(metaClses);
        kindList.setCellRenderer(new RestrictionKindRenderer());
        kindList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        kindList.setSelectedValue(metaCls, true);
        JScrollPane kindScrollPane = new JScrollPane(kindList);
        kindScrollPane.setPreferredSize(new Dimension(160, 150));
        kindList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                changeKind((edu.stanford.smi.protege.model.Cls) kindList.getSelectedValue());
            }
        });
        JPanel kindPanel = new JPanel();
        kindPanel.setLayout(new BorderLayout());
        kindPanel.add(BorderLayout.NORTH, new LabeledComponent("Restriction", kindScrollPane));
        kindPanel.add(BorderLayout.CENTER, new JPanel());

        symbolPanel = new OWLSymbolPanel(owlModel, false);
        if (fillerText == null) {
            symbolPanel.setErrorFlag(true);
        }
        enableSymbolPanel();
        fillerTextArea = new FillerTextArea(owlModel, symbolPanel);
        fillerTextArea.setRestrictionProperty(getRestrictionProperty(metaCls));
        if (fillerText != null) {
            fillerTextArea.setText(fillerText);
        }
        symbolPanel.setSymbolEditor(fillerTextArea);
        JPanel fillerPanel = new JPanel(new BorderLayout(4, 4));
        fillerTextArea.setPreferredSize(new Dimension(200, 64));
        fillerTextArea.setLineWrap(true);
        fillerTextArea.setWrapStyleWord(true);
        fillerPanel.add(BorderLayout.NORTH, new JScrollPane(fillerTextArea));
        fillerPanel.add(BorderLayout.CENTER, symbolPanel);

        setLayout(new BorderLayout(10, 10));
        LabeledComponent lc = new LabeledComponent("Restricted Property", propertyPanel);
        // lc.addHeaderButton(viewAction);
        if (OWLUtil.hasRDFProfile(owlModel)) {
            lc.addHeaderButton(createRDFPropertyAction);
        }
        lc.addHeaderButton(createDatatypePropertyAction);
        lc.addHeaderButton(createObjectPropertyAction);
        add(BorderLayout.CENTER, lc);
        add(BorderLayout.EAST, kindPanel);
        add(BorderLayout.SOUTH, new LabeledComponent("Filler", fillerPanel));

        if (property != null) {
            propertyList.setSelectedValue(property, true);
        }
        else if (propertyList.getModel().getSize() > 0) {
            propertyList.setSelectedIndex(0);
        }
    }


    private void addNewProperty(RDFProperty newProperty) {
        newProperty.setFunctional(false);
        newProperty.setDomainDefined(false);
        showModalPropertyWidget(newProperty);
        updatePropertiesList();
        propertyList.setSelectedValue(newProperty, true);
    }


    public boolean canClose(int result) {
        if (result == ModalDialogFactory.OPTION_OK) {
            RDFProperty selectedProperty = (RDFProperty) propertyList.getSelectedValue();
            if (selectedProperty != null) {
                String uniCodeText = fillerTextArea.getText();
                if (uniCodeText.length() == 0) {
                    symbolPanel.displayError("Please enter a filler");
                    return false;
                }
                else {
                    try {
                        fillerTextArea.checkExpression(uniCodeText);
                        return true;
                    }
                    catch (Throwable ex) {
                        symbolPanel.displayError(ex);
                        return false;
                    }
                }
            }
            else {
                symbolPanel.displayError("Please select a property");
                return false;
            }
        }
        else {
            return true;
        }
    }


    private void changeKind(edu.stanford.smi.protege.model.Cls metaclass) {
        fillerTextArea.setRestrictionProperty(getRestrictionProperty(metaclass));
        fillerTextArea.displayError();
        enableSymbolPanel();
    }


    private void changeProperty(RDFProperty property) {
        fillerTextArea.setOnProperty(property);
        fillerTextArea.displayError();
        enableSymbolPanel();
        fillerTextArea.requestFocus();
        if (property != null) {
            recentPropertyName = property.getName();
        }
    }


    private void createActions() {

        viewAction = new AbstractAction("View Property...", OWLIcons.getViewIcon()) {

            public void actionPerformed(ActionEvent arg0) {
                RDFProperty property = (RDFProperty) propertyList.getSelectedValue();
                showModalPropertyWidget(property);
            }
        };
        viewAction.setEnabled(false);

        createDatatypePropertyAction = new AbstractAction("Create datatype property...",
                OWLIcons.getCreatePropertyIcon(OWLIcons.OWL_DATATYPE_PROPERTY)) {

            public void actionPerformed(ActionEvent arg0) {
                OWLProperty newProperty = owlModel.createOWLDatatypeProperty(null);
                addNewProperty(newProperty);
            }
        };

        createObjectPropertyAction = new AbstractAction("Create object property...",
                OWLIcons.getCreatePropertyIcon(OWLIcons.OWL_OBJECT_PROPERTY)) {

            public void actionPerformed(ActionEvent arg0) {
                OWLProperty newProperty = owlModel.createOWLObjectProperty(null);
                addNewProperty(newProperty);
            }
        };

        createRDFPropertyAction = new AbstractAction("Create RDF property...",
                OWLIcons.getCreatePropertyIcon(OWLIcons.RDF_PROPERTY)) {

            public void actionPerformed(ActionEvent arg0) {
                RDFProperty newProperty = owlModel.createRDFProperty(null);
                addNewProperty(newProperty);
            }
        };
    }


    private OWLRestriction createRestriction(edu.stanford.smi.protege.model.Cls metaCls,
                                             RDFProperty property, String text) throws Exception {
        Collection parents = CollectionUtilities.createCollection(((KnowledgeBase) owlModel).getCls(OWLNames.Cls.ANONYMOUS_ROOT));
        KnowledgeBase kb = owlModel;
        OWLRestriction restriction = (OWLRestriction) kb.createCls(null, parents, metaCls);
        restriction.setOnProperty(property);
        restriction.setFillerText(text);
        return restriction;
    }


    private void enableSymbolPanel() {
        edu.stanford.smi.protege.model.Cls metaCls = getSelectedMetaCls();
        symbolPanel.setEnabled(true);
        symbolPanel.enableActions(getSelectedProperty(), metaCls);
    }


    private void enableActions() {
        viewAction.setEnabled(true);
    }


    OWLRestriction getResult() {
        try {
            String text = fillerTextArea.getText();
            return createRestriction(getSelectedMetaCls(), getSelectedProperty(), text);
        }
        catch (Exception ex) {
            return null;
        }
    }


    private RDFProperty getRestrictionProperty(Cls metaCls) {
        if (metaCls.getName().equals(OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION)) {
            return owlModel.getRDFProperty(OWLNames.Slot.ALL_VALUES_FROM);
        }
        else if (metaCls.getName().equals(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION)) {
            return owlModel.getRDFProperty(OWLNames.Slot.SOME_VALUES_FROM);
        }
        else if (metaCls.getName().equals(OWLNames.Cls.HAS_VALUE_RESTRICTION)) {
            return owlModel.getRDFProperty(OWLNames.Slot.HAS_VALUE);
        }
        else if (metaCls.getName().equals(OWLNames.Cls.MAX_CARDINALITY_RESTRICTION)) {
            return owlModel.getRDFProperty(OWLNames.Slot.MAX_CARDINALITY);
        }
        else if (metaCls.getName().equals(OWLNames.Cls.MIN_CARDINALITY_RESTRICTION)) {
            return owlModel.getRDFProperty(OWLNames.Slot.MIN_CARDINALITY);
        }
        else {
            return owlModel.getRDFProperty(OWLNames.Slot.CARDINALITY);
        }
    }


    edu.stanford.smi.protege.model.Cls getSelectedMetaCls() {
        return (edu.stanford.smi.protege.model.Cls) kindList.getSelectedValue();
    }


    RDFProperty getSelectedProperty() {
        return (RDFProperty) propertyList.getSelectedValue();
    }


    public static OWLRestriction showCreateDialog(Component parent,
                                                  RDFSClass targetClass,
                                                  edu.stanford.smi.protege.model.Cls metaCls,
                                                  RDFProperty property) {
        return showCreateDialog(parent, targetClass, metaCls, property, null);
    }


    public static OWLRestriction showCreateDialog(Component parent,
                                                  RDFSClass targetClass,
                                                  edu.stanford.smi.protege.model.Cls metaCls,
                                                  RDFProperty property,
                                                  String fillerText) {
        OWLModel owlModel = targetClass.getOWLModel();
        if (property == null && recentPropertyName != null) {
            RDFResource resource = owlModel.getRDFResource(recentPropertyName);
            if (resource instanceof RDFProperty) {
                property = (RDFProperty) resource;
            }
        }
        RestrictionEditorPanel panel = new RestrictionEditorPanel(owlModel, metaCls, property, fillerText, targetClass);
        panel.getFillerTextArea().addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
                    ProtegeUI.getModalDialogFactory().attemptDialogClose(ModalDialogFactory.OPTION_OK);
                }
            }
        });
        if (ProtegeUI.getModalDialogFactory().showDialog(parent, panel, "Create Restriction", ModalDialogFactory.MODE_OK_CANCEL, (CloseCallback) panel) == ModalDialogFactory.OPTION_OK) {
            return panel.getResult();
        }
        else {
            return null;
        }
    }


    private FillerTextArea getFillerTextArea() {
        return fillerTextArea;
    }


    public static OWLRestriction showCreateDialog(Component parent, RDFSClass targetClass, edu.stanford.smi.protege.model.Cls metaCls) {
        return showCreateDialog(parent, targetClass, metaCls, null);
    }


    private void showModalPropertyWidget(RDFProperty property) {
        ClsWidget widget = owlModel.getProject().createRuntimeClsWidget(property);
        InstanceNameComponent nameComponent = new InstanceNameComponent();
        nameComponent.setInstance(property);

        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.add(nameComponent, BorderLayout.NORTH);
        panel.add((JComponent)widget, BorderLayout.CENTER);

        ProtegeUI.getModalDialogFactory().showDialog(this, panel, "Property " + property.getBrowserText(),
                ModalDialogFactory.MODE_CLOSE);
    }


    private void updatePropertiesList() {
        Collection allProperties = owlModel.getVisibleUserDefinedRDFProperties();
        Collection<RDFProperty> selectableProperties = new ArrayList();
        for (Iterator it = allProperties.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (!property.isAnnotationProperty()) {
                selectableProperties.add(property);
            }
        }
        RDFSNamedClass dbrClass = owlModel.getSystemFrames().getDirectedBinaryRelationCls();
        if (targetClass != null && ((Cls) targetClass).hasSuperclass(dbrClass)) {
            RDFProperty fromProperty = owlModel.getSystemFrames().getFromSlot();
            if (fromProperty.isVisible()) {
                selectableProperties.add(fromProperty);
            }
            RDFProperty toProperty = owlModel.getSystemFrames().getToSlot();
            if (toProperty.isVisible()) {
                selectableProperties.add(toProperty);
            }
        }
        RDFProperty[] propertiesArray = selectableProperties.toArray(new RDFProperty[0]);
        Arrays.sort(propertiesArray, new FrameComparator());
        java.util.List propertiesList = Arrays.asList(propertiesArray);
        propertyList.setListData(propertiesList.toArray());
    }
}
