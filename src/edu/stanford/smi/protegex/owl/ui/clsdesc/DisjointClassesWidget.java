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

package edu.stanford.smi.protegex.owl.ui.clsdesc;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Facet;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLNames;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableModel;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.util.*;

/**
 * An AbstractSlotWidget that displays the disjoint classes in a table.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DisjointClassesWidget extends ClassDescriptionWidget {

    private boolean mutuallyIsDefault = true;


    private void addAllSiblings(boolean noDialog) {
        SiblingsPanel siblingsPanel = new SiblingsPanel(mutuallyIsDefault);
        if (noDialog || ProtegeUI.getModalDialogFactory().showDialog(this, siblingsPanel,
                "Add siblings to disjoints", ModalDialogFactory.MODE_OK_CANCEL) == ModalDialogFactory.OPTION_OK) {
            try {
                beginTransaction("Add siblings to disjoints of " + getEditedCls().getBrowserText(), getEditedCls().getName());
                mutuallyIsDefault = siblingsPanel.isMutuallySelected();
                OWLNamedClass editedCls = getEditedCls();
                if (mutuallyIsDefault) {
                    for (Iterator sit = editedCls.getSuperclasses(false).iterator(); sit.hasNext();) {
                        Cls superCls = (Cls) sit.next();
                        if (superCls instanceof OWLNamedClass) {
                            OWLUtil.ensureSubclassesDisjoint((OWLNamedClass) superCls);
                        }
                    }
                }
                else {
                    Set ds = getPotentialDisjoints(editedCls);
                    final Collection disjointClasses = editedCls.getDisjointClasses();
                    for (Iterator it = ds.iterator(); it.hasNext();) {
                        OWLNamedClass namedCls = (OWLNamedClass) it.next();
                        if (!namedCls.equals(editedCls) && !disjointClasses.contains(namedCls)) {
                            editedCls.addDisjointClass(namedCls);
                            namedCls.addDisjointClass(editedCls);
                        }
                    }
                }
                commitTransaction();
            }
            catch (Exception ex) {
            	rollbackTransaction();
                OWLUI.handleError(getOWLModel(), ex);
            }
        }
    }


    protected ResourceSelectionAction createAddAction(ClassDescriptionTable table) {
        return new AddRowAction(getTable(), "Add disjoint class...", false) {
            public Collection pickResources() {
                Cls self = getEditedCls();
                self.setVisible(false);
                Collection results = super.pickResources();
                self.setVisible(true);
                return results;
            }
        };
    }


    protected Action createCreateAction(final ClassDescriptionTable table) {
        return new CreateRowAction(getTable(), "Create disjoint class from OWL expression");
    }


    protected Icon createHeaderIcon() {
        return OWLIcons.getImageIcon(OWLIcons.OWL_DISJOINT_CLASSES);
    }


    protected List createCustomActions(ClassDescriptionTable table) {
        Action addAction = new AbstractAction("Add all siblings...",
                OWLIcons.getAddIcon("SiblingClasses")) {
            public void actionPerformed(ActionEvent e) {
                addAllSiblings((e.getModifiers() & ActionEvent.CTRL_MASK) != 0);
            }
        };
        Action removeAction = new AbstractAction("Remove all siblings...",
                OWLIcons.getRemoveIcon("SiblingClasses")) {
            public void actionPerformed(ActionEvent e) {
                removeAllSiblings((e.getModifiers() & ActionEvent.CTRL_MASK) != 0);
            }
        };
        List list = new ArrayList();
        list.add(addAction);
        list.add(removeAction);
        return list;
    }


    protected OWLTableModel createTableModel() {
        return new DisjointClassesTableModel();
    }


    protected String getLabelText() {
        return "Disjoints";
    }


    private Set getPotentialDisjoints(OWLNamedClass editedCls) {
        Set ds = new HashSet();
        for (Iterator sit = editedCls.getSuperclasses(false).iterator(); sit.hasNext();) {
            Cls superCls = (Cls) sit.next();
            if (superCls instanceof OWLNamedClass) {
                ds.addAll(OWLUtil.getPotentiallyDisjointSubclasses((OWLNamedClass) superCls));
            }
        }
        return ds;
    }


    public void initialize() {
        super.initialize();
        getTable().getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        TableColumn owlColumn = getTable().getColumnModel().getColumn(0);
        owlColumn.setCellRenderer(new ResourceRenderer() {
            protected Icon getClsIcon(Cls cls) {
                DisjointClassesTableModel tableModel = (DisjointClassesTableModel) getTable().getModel();
                if (cls instanceof RDFResource) {
                    return tableModel.getIcon((RDFResource) cls);
                }
                else {
                    return super.getClsIcon(cls);
                }
            }
        });
    }


    public static boolean isSuitable(Cls cls, Slot slot, Facet facet) {
        return slot.getName().equals(OWLNames.Slot.DISJOINT_WITH);
    }


    private void removeAllSiblings(boolean noDialog) {
        SiblingsPanel siblingsPanel = new SiblingsPanel(mutuallyIsDefault);
        if (noDialog || ProtegeUI.getModalDialogFactory().showDialog(this, siblingsPanel,
                "Remove siblings from disjoints", ModalDialogFactory.MODE_OK_CANCEL) == ModalDialogFactory.OPTION_OK) {
            try {
                beginTransaction("Remove siblings from disjoints of " +
                        getEditedCls().getBrowserText(), getEditedCls().getName());
                mutuallyIsDefault = siblingsPanel.isMutuallySelected();
                OWLNamedClass editedCls = getEditedCls();
                if (mutuallyIsDefault) {
                    for (Iterator sit = editedCls.getSuperclasses(false).iterator(); sit.hasNext();) {
                        Cls superCls = (Cls) sit.next();
                        if (superCls instanceof OWLNamedClass) {
                            OWLUtil.removeSubclassesDisjoint((OWLNamedClass) superCls);
                        }
                    }
                }
                else {
                    TripleStoreModel tsm = editedCls.getOWLModel().getTripleStoreModel();
                    RDFProperty owlDisjointWithProperty = editedCls.getOWLModel().getOWLDisjointWithProperty();
                    Set ds = getPotentialDisjoints(editedCls);
                    for (Iterator it = ds.iterator(); it.hasNext();) {
                        OWLNamedClass otherCls = (OWLNamedClass) it.next();
                        if (tsm.isEditableTriple(editedCls, owlDisjointWithProperty, otherCls)) {
                            editedCls.removeDisjointClass(otherCls);
                        }
                        if (tsm.isEditableTriple(otherCls, owlDisjointWithProperty, editedCls)) {
                            otherCls.removeDisjointClass(editedCls);
                        }
                    }
                }
                commitTransaction();
            }
            catch (Exception ex) {
            	rollbackTransaction();
                OWLUI.handleError(getOWLModel(), ex);
            }
        }
    }


    public void setEditable(boolean b) {
        super.setEditable(b);
        //LabeledComponent lc = (LabeledComponent) getComponent(0);
        //for (Iterator it = lc.getHeaderButtonActions().iterator(); it.hasNext();) {
        //    Action action = (Action) it.next();
        //    action.setEnabled(b);
        //}
        //getTable().setEnabled(b);
        //getTable().enableActions();
    }


    private class SiblingsPanel extends JPanel {

        private JRadioButton mutuallyRadioButton;

        private JRadioButton locallyRadioButton;


        SiblingsPanel(boolean mutuallyIsDefault) {
            mutuallyRadioButton = new JRadioButton("Mutually between all siblings", mutuallyIsDefault);
            locallyRadioButton = new JRadioButton("Only between this class and its siblings", !mutuallyIsDefault);
            ButtonGroup group = new ButtonGroup();
            group.add(mutuallyRadioButton);
            group.add(locallyRadioButton);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(mutuallyRadioButton);
            add(locallyRadioButton);
        }


        boolean isMutuallySelected() {
            return mutuallyRadioButton.isSelected();
        }
    }
}
