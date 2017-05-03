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

package edu.stanford.smi.protegex.owl.ui.clsproperties;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.widget.SlotWidget;
import edu.stanford.smi.protege.widget.WidgetConfigurationPanel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.OWLRemoveAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractPropertyWidget;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

public class PropertyRestrictionsTreeWidget extends AbstractPropertyWidget {
    private static final long serialVersionUID = 7648920625842859833L;

    private ResourceSelectionAction addPropertyAction = new AddPropertyAction(this);

    private Action createDatatypePropertyAction =
            new AbstractAction("Create datatype property",
                    OWLIcons.getCreatePropertyIcon(OWLIcons.OWL_DATATYPE_PROPERTY)) {
                public void actionPerformed(ActionEvent e) {
                    createProperty(getOWLModel().getOWLDatatypePropertyClass());
                }
            };

    private Action createObjectPropertyAction =
            new AbstractAction("Create object property",
                    OWLIcons.getCreatePropertyIcon(OWLIcons.OWL_OBJECT_PROPERTY)) {
                public void actionPerformed(ActionEvent e) {
                    createProperty(getOWLModel().getOWLObjectPropertyClass());
                }
            };

    private Action createRDFPropertyAction =
            new AbstractAction("Create RDF property",
                    OWLIcons.getCreatePropertyIcon(OWLIcons.RDF_PROPERTY)) {
                public void actionPerformed(ActionEvent e) {
                    createProperty(getOWLModel().getRDFPropertyClass());
                }
            };

    private static final String HIDE_GLOBAL_CHARACTERISTICS = "HideGlobalCharacteristics";

    private static final String DISPLAY_RESTRICTIONS = "DisplayRestrictions";

    private AllowableAction removeAction;

    public final static String LABEL = "Label";

    private PropertyRestrictionsTree tree;


    private void createProperty(RDFSNamedClass propertyMetaclass) {
        RDFSClass cls = (RDFSClass) getEditedResource();
        if (((Cls) propertyMetaclass).isAbstract()) {
            Collection allowedClses = ((RDFSNamedClass) cls.getProtegeType()).getUnionRangeClasses(getRDFProperty());
            propertyMetaclass = (RDFSNamedClass) ProtegeUI.getSelectionDialogFactory().selectClass(this, getOWLModel(), allowedClses);
        }
        if (propertyMetaclass != null) {
            RDFProperty property = createProperty(propertyMetaclass, cls);
            if (property != null) {
                viewProperty(property);
            }
        }
    }


    private RDFProperty createProperty(RDFSNamedClass propertyMetaclass, RDFSClass cls) {
    	RDFProperty property = null;
        try {
            beginTransaction("Create new " + propertyMetaclass.getBrowserText());
            String baseName = propertyMetaclass.getName();
            int index = baseName.lastIndexOf(":");
            if (index >= 0) {
                baseName = baseName.substring(index + 1);
            }
            String name = ((AbstractOWLModel) getKnowledgeBase()).createNewResourceName(baseName);
            property = (RDFProperty) propertyMetaclass.createInstance(name);
            property.setDomainDefined(true);
            property.addUnionDomainClass(cls);
            commitTransaction();            
        }
        catch (Exception ex) {
        	rollbackTransaction();
            OWLUI.handleError(getOWLModel(), ex);            
        }
        return property;
        
    }


    public WidgetConfigurationPanel createWidgetConfigurationPanel() {
        return new MyWidgetConfigurationPanel();
    }


    private boolean getHideGlobalCharacteristics() {
        Boolean b = getPropertyList().getBoolean(HIDE_GLOBAL_CHARACTERISTICS);
        return b == null ? false : b.booleanValue();
    }


    public boolean getDisplayRestrictions() {
        Boolean b = getPropertyList().getBoolean(DISPLAY_RESTRICTIONS);
        return b == null ? false : b.booleanValue();
    }


    public String getLabel() {
        //String label = getPropertyList().getString(LABEL);
        //if (label == null || label.trim().length() == 0) {
        return getDisplayRestrictions() ? "Properties and Restrictions" : "Properties";
        //}
        //return label;
    }


    public Dimension getMinimumSize() {
        return new Dimension(100, 100);
    }


    public Collection getSelection() {
        return tree.getSelection();
    }


    public void initialize() {
        final OWLModel owlModel = (OWLModel) getKnowledgeBase();
        tree = new PropertyRestrictionsTree(owlModel, null);

        removeAction = new OWLRemoveAction("Remove this class from the domain of the selected property",
                OWLIcons.getRemoveIcon(OWLIcons.RDF_PROPERTY), tree) {


            public void onRemove(Collection objects) {
                for (Iterator it = objects.iterator(); it.hasNext();) {
                    Object o = (Object) it.next();
                    if (o instanceof RDFProperty) {
                        onRemove(o);
                    }
                }
            }


            public void onRemove(Object o) {
                RDFProperty property = (RDFProperty) o;
                RDFSClass cls = (RDFSClass) getEditedResource();
                try {
                    owlModel.beginTransaction("Remove " + cls.getBrowserText() + " from the domain of " + property.getBrowserText(), property.getName());
                    property.removeUnionDomainClass(cls);
                    owlModel.commitTransaction();
                }
                catch (Exception ex) {
                	owlModel.rollbackTransaction();
                    OWLUI.handleError(owlModel, ex);
                }
            }


            public void onSelectionChange() {
                Collection sels = getSelection();
                boolean en = false;
                for (Iterator it = sels.iterator(); it.hasNext();) {
                    Object o = (Object) it.next();
                    if (o instanceof RDFProperty) {
                        RDFProperty property = (RDFProperty) o;
                        RDFSClass cls = (RDFSClass) getEditedResource();
                        if (property.isEditable() && property.getUnionDomain().contains(cls)) {
                            en = true;
                        }
                        else {
                            en = false;
                            break;
                        }
                    }
                }
                setAllowed(en);
            }
        };
        JScrollPane scrollPane = new JScrollPane(tree);
        OWLLabeledComponent lc = new OWLLabeledComponent(getLabel(), scrollPane, true, true);
        lc.setHeaderIcon(OWLIcons.getPropertiesIcon());
        if (ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.CreateRDFProperty)) {
            lc.addHeaderButton(createRDFPropertyAction);
        }
        if (ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Create_DatatypeProperty)) {
            lc.addHeaderButton(createDatatypePropertyAction);
        }
        if (ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Create_ObjectProperty)) {
            lc.addHeaderButton(createObjectPropertyAction);
        }
        lc.addHeaderButton(addPropertyAction);
        lc.addHeaderButton(removeAction);
        lc.addHeaderSeparator();
        lc.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    setHideGlobalCharacteristics(!getHideGlobalCharacteristics());
                }
            }
        });
        JButton createRestrictionButton = lc.addHeaderButton(tree.getCreateRestrictionAction());
        createRestrictionButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                if (tree.getSelectedInstance() != null && SwingUtilities.isRightMouseButton(e) && isEnabled()) {
                    JPopupMenu menu = new JPopupMenu();
                    tree.addCreateRestrictionActions(menu);
                    Component button = (Component) e.getSource();
                    menu.show(button, e.getX(), button.getHeight());
                }
            }
        });
        lc.addHeaderButton(tree.getDeleteRestrictionAction());
        tree.init(getDisplayRestrictions(), getHideGlobalCharacteristics());
        add(lc);
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return cls.getKnowledgeBase() instanceof OWLModel &&
                slot.getName().equals(Model.Slot.DIRECT_TEMPLATE_SLOTS);
    }


    public void removeNotify() {
        super.removeNotify();
        if (tree.isEditing()) {
            tree.cancelEditing();
        }
    }


    private void setHideGlobalCharacteristics(boolean value) {
        if (value != getHideGlobalCharacteristics()) {
            if (value) {
                getPropertyList().setBoolean(HIDE_GLOBAL_CHARACTERISTICS, true);
            }
            else {
                getPropertyList().remove(HIDE_GLOBAL_CHARACTERISTICS);
            }
            updateTreeDisplayRestrictions();
        }
    }


    public void setDisplayRestrictions(boolean value) {
        if (value != getDisplayRestrictions()) {
            if (value) {
                getPropertyList().setBoolean(DISPLAY_RESTRICTIONS, true);
            }
            else {
                getPropertyList().remove(DISPLAY_RESTRICTIONS);
            }
            updateTreeDisplayRestrictions();
        }
    }


    public void setInstance(Instance newInstance) {
        super.setInstance(newInstance);
        if (newInstance instanceof OWLNamedClass) {
            tree.setCls((OWLNamedClass) newInstance);
            tree.openNodesIfPossible();
            boolean thing = newInstance.equals(getOWLModel().getOWLThingClass());
            createDatatypePropertyAction.setEnabled(!thing);
            createObjectPropertyAction.setEnabled(!thing);
            createRDFPropertyAction.setEnabled(!thing);
            addPropertyAction.setEnabled(!thing);
            tree.getCreateRestrictionAction().setEnabled(!thing);
        }
        else {
            tree.setCls(null);
        }
    }


    private void updateTreeDisplayRestrictions() {
        tree.init(getDisplayRestrictions(), getHideGlobalCharacteristics());
    }


    protected void viewProperty(RDFProperty property) {
        property.getProject().show(property);
    }


    private class MyWidgetConfigurationPanel extends WidgetConfigurationPanel {

        private JCheckBox displayRestrictionsCheckBox;

        private JTextField labelTextField;


        MyWidgetConfigurationPanel() {
            super(PropertyRestrictionsTreeWidget.this);
        }


        public void addGeneralTab(SlotWidget widget) {
            labelTextField = new JTextField(getLabel());
            displayRestrictionsCheckBox = new JCheckBox("Display restrictions behind property name");
            displayRestrictionsCheckBox.setSelected(getDisplayRestrictions());
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(displayRestrictionsCheckBox);
            panel.add(new LabeledComponent("Label", labelTextField));
            addTab("General", panel);
        }


        public void saveContents() {
            getPropertyList().setString(LABEL, labelTextField.getText());
            boolean selected = displayRestrictionsCheckBox.isSelected();
            setDisplayRestrictions(selected);
            updateTreeDisplayRestrictions();
        }
    }
}
