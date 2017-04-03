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

import edu.stanford.smi.protege.resource.LocalizedText;
import edu.stanford.smi.protege.resource.ResourceKey;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceAction;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A ResourceAction for deleted the selected named classes inside a ClassTreePanel.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DeleteClassAction extends ResourceAction {

    public final static String TEXT = "Delete class";


    public DeleteClassAction() {
        super(TEXT, OWLIcons.getDeleteClsIcon());
    }


    public void actionPerformed(ActionEvent e) {
        ClassTreePanel classTreePanel = (ClassTreePanel) getComponent();
        Collection<RDFSNamedClass> clses = ComponentUtilities.getSelection(classTreePanel.getTree());
        performAction(clses, classTreePanel);
    }


    private static boolean confirmDelete(Component parent) {
        boolean delete = true;
        if (ProjectManager.getProjectManager().getCurrentProject().getDisplayConfirmationOnRemove()) {
            String text = LocalizedText.getText(ResourceKey.DIALOG_CONFIRM_DELETE_TEXT);
            delete = ProtegeUI.getModalDialogFactory().showConfirmDialog(parent, text, "Confirm Delete");
        }
        return delete;
    }

    public boolean isSuitable(Component component, RDFResource resource) {
        return component instanceof ClassTreePanel &&
               resource instanceof RDFSNamedClass &&
               resource.isEditable();
    }


    public static void performAction(RDFSNamedClass cls, ClassTreePanel classTreePanel) {
        performAction(Collections.singleton(cls), classTreePanel);
    }


    public static void performAction(Collection<RDFSNamedClass> clses, ClassTreePanel classTreePanel) {

        if (confirmDelete((Component) classTreePanel)) {
            for (Iterator<RDFSNamedClass> i = clses.iterator(); i.hasNext();) {
                RDFSNamedClass cls = i.next();

                if (cls.getInstanceCount(true) > 0) {
                    String text = LocalizedText.getText(ResourceKey.DELETE_CLASS_FAILED_DIALOG_TEXT);
                    text += "\n " + cls.getBrowserText();
                    ProtegeUI.getModalDialogFactory().showMessageDialog(cls.getOWLModel(), text);
                }
                else {
                    JTree tree = classTreePanel.getTree();
                    ComponentUtilities.removeSelection(tree);
                    //TODO: should this be done in a transaction?
                    try {
                    	cls.delete();
					} catch (Exception e) {						
						OWLUI.handleError(null, e);
					}                    
                }
            }
        }
    }
}
