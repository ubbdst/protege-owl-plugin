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

package edu.stanford.smi.protegex.owl.ui.existential;

import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class EditableExistentialTreePanel extends ExistentialTreePanel {

    private AllowableAction addClassAction = new AllowableAction("Add class",
            OWLIcons.getAddIcon(OWLIcons.PRIMITIVE_OWL_CLASS), this) {
        public void actionPerformed(ActionEvent e) {
            addClass();
        }


        public void onSelectionChange() {
            OWLClass sel = getSelectedOWLClass();
            setAllowed(sel != null && sel.isEditable());
        }
    };

    private AllowableAction createClsAction = new AllowableAction("Create class",
            OWLIcons.getCreateIcon(OWLIcons.PRIMITIVE_OWL_CLASS), this) {
        public void actionPerformed(ActionEvent e) {
            createClass();
        }


        public void onSelectionChange() {
            OWLClass sel = getSelectedOWLClass();
            setAllowed(sel != null && sel.isEditable());
        }
    };

    private AllowableAction deleteClsAction = new AllowableAction("Delete class",
            OWLIcons.getDeleteIcon(OWLIcons.PRIMITIVE_OWL_CLASS), this) {
        public void actionPerformed(ActionEvent e) {
            deleteCls();
        }


        public void onSelectionChange() {
            OWLClass parent = getSelectedParentClass();
            OWLClass sel = getSelectedOWLClass();
            setAllowed(parent != null && parent.isEditable() &&
                    sel != null && sel.isEditable());
        }
    };

    private Action removeClsAction = new AllowableAction("Remove class",
            OWLIcons.getRemoveIcon(OWLIcons.PRIMITIVE_OWL_CLASS), this) {
        public void actionPerformed(ActionEvent e) {
            removeCls();
        }


        public void onSelectionChange() {
            OWLClass sel = getSelectedParentClass();
            setAllowed(sel != null);
        }
    };


    public EditableExistentialTreePanel(RDFSNamedClass root,
                                        Slot superclassesSlot,
                                        OWLObjectProperty transitiveSlot) {
        super(root, superclassesSlot, transitiveSlot);
        getLabeledComponent().addHeaderButton(createClsAction);
        getLabeledComponent().addHeaderButton(addClassAction);
        getLabeledComponent().addHeaderButton(removeClsAction);
        getLabeledComponent().addHeaderButton(deleteClsAction);
        createClsAction.setAllowed(true);
        addClassAction.setAllowed(true);
    }


    private void addClass() {
        OWLModel owlModel = getOWLModel();
        OWLObjectProperty property = getExistentialProperty();
        Collection cs = property.getUnionRangeClasses();
        if (cs.size() == 0) {
            cs = Collections.singleton(owlModel.getOWLThingClass());
        }
        RDFSNamedClass cls = ProtegeUI.getSelectionDialogFactory().selectClass(this, owlModel, cs, "Select a class to add");
        if (cls instanceof OWLNamedClass) {
            OWLClass selCls = getSelectedOWLClass();
            try {
                owlModel.beginTransaction("Add existential relationship " + getExistentialProperty().getBrowserText() +
                        " between " + selCls.getBrowserText() + " and " + cls.getBrowserText(), selCls.getName());
                selCls.addSuperclass(owlModel.createOWLSomeValuesFrom(getExistentialProperty(), (OWLNamedClass) cls));
                owlModel.commitTransaction();
            }
            catch (Exception ex) {
            	owlModel.rollbackTransaction();
                OWLUI.handleError(owlModel, ex);
            }
            selectChildNode(cls);
        }
    }


    private void createClass() {
        OWLModel owlModel = getOWLModel();
        OWLClass selCls = getSelectedOWLClass();
        OWLClass newCls = owlModel.createOWLNamedClass(null);
        newCls.removeSuperclass(owlModel.getOWLThingClass());
        for (Iterator it = selCls.getSuperclasses(false).iterator(); it.hasNext();) {
            RDFSClass superCls = (RDFSClass) it.next();
            if (superCls instanceof OWLNamedClass) {
                newCls.addSuperclass(superCls);
            }
        }
        selCls.addSuperclass(owlModel.createOWLSomeValuesFrom(getExistentialProperty(), newCls));
        selectChildNode(newCls);
    }


    private void deleteCls() {
        OWLClass cls = getSelectedOWLClass();
        cls.delete();
    }


    private void removeCls() {
        OWLClass cls = getSelectedOWLClass();
        OWLClass parentCls = getSelectedParentClass();
        System.err.println("The remove mothod has temporarily been disabled.");
        //Existential.removeExistentialDependent(parentCls, getExistentialProperty(), cls);
    }


    private void selectChildNode(RDFSClass cls) {
        JTree tree = getTree();
        ExistentialTreeNode node = getSelectedNode();
        if (node != null) {
            ExistentialTreeNode newNode = null;
            for (int i = 0; i < node.getChildObjectCount(); i++) {
                newNode = (ExistentialTreeNode) node.getChildAt(i);
                if (cls.equals(newNode.getOWLClass())) {
                    break;
                }
            }
            TreePath path = tree.getSelectionPath();
            TreePath newPath = path.pathByAddingChild(newNode);
            tree.setSelectionPath(newPath);
        }
    }
}
