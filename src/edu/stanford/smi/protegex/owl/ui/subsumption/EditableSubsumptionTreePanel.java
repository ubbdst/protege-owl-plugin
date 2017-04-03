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

package edu.stanford.smi.protegex.owl.ui.subsumption;

import edu.stanford.smi.protege.action.DeleteInstancesAction;
import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.AllowableAction;
import edu.stanford.smi.protege.util.CollectionUtilities;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * A SubsumptionTreePanel with additional support for editing the class hierarchy.
 * This adds create and delete buttons, and supports drag and drop.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public abstract class EditableSubsumptionTreePanel extends SubsumptionTreePanel {

    public EditableSubsumptionTreePanel(Cls root, Slot subclassesSlot,
                                        Slot superclassesSlot) {
        super(root, subclassesSlot, superclassesSlot, false);

        Action createAction = createCreateAction();
        if (createAction != null) {
            getLabeledComponent().addHeaderButton(createAction);
        }

        Action deleteAction = createDeleteAction();
        if (deleteAction != null) {
            getLabeledComponent().addHeaderButton(deleteAction);
        }
    }


    protected Action createCreateAction() {

        AllowableAction action = new AllowableAction("Create subclass", null,
                Icons.getCreateIcon(), this) {

            public void actionPerformed(ActionEvent e) {
                Collection parents = getSelection();
                if (!parents.isEmpty()) {
                    Cls cls = getOWLModel().createSubclass(null, parents);
                    extendSelection(cls);
                }
            }


            public void onSelectionChange() {
                super.onSelectionChange();
                setAllowed(!getSelection().isEmpty());
            }
        };
        action.setEnabled(false);
        return action;
    }


    protected Action createDeleteAction() {
        AllowableAction action = new DeleteInstancesAction(this) {
            public void onAboutToDelete() {
                removeSelection();
            }


            public void onSelectionChange() {
                boolean isEditable = false;
                Frame frame = (Frame) CollectionUtilities.getFirstItem(getSelection());
                if (frame != null) {
                    isEditable = frame.isEditable();
                }
                setAllowed(isEditable);
            }
        };
        action.setEnabled(false);
        return action;
    }
}
