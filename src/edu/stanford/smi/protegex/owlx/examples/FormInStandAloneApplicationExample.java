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

package edu.stanford.smi.protegex.owlx.examples;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * An example that allows the user to open a pprj file (with form customizations)
 * and then asks for an individual to edit in a stand-alone window.  Finally,
 * the changes can be saved back.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class FormInStandAloneApplicationExample {

    public static void main(String[] args) {
        JFileChooser fileChooser = ComponentFactory.createFileChooser("Project Files", ".pprj");
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File projectFile = fileChooser.getSelectedFile();
            Collection errors = new ArrayList();
            Project project = Project.loadProjectFromFile(projectFile.getAbsolutePath(), errors);
            if (!errors.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Could not load " + projectFile, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            KnowledgeBase kb = project.getKnowledgeBase();
            if (!(kb instanceof OWLModel)) {
                JOptionPane.showMessageDialog(null, "This is not an OWL project.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            OWLModel owlModel = (OWLModel) kb;
            RDFResource resource = ProtegeUI.getSelectionDialogFactory().selectResourceByType(null, owlModel, Collections.singleton(owlModel.getOWLThingClass()));
            if (resource != null) {
                editResourceInForm(resource);
            }
            else {
                System.exit(0);
            }
        }
        else {
            System.exit(0);
        }
    }


    private static void handleClose(OWLModel owlModel) {
        if (JOptionPane.showConfirmDialog(null,
                "Do you want to save your changes?", "Confirm Save",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Collection errors = new ArrayList();
            owlModel.getProject().save(errors);
            if (!errors.isEmpty()) {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                        "Could not save project.");
            }
        }
        System.exit(0);
    }


    private static void editResourceInForm(final RDFResource resource) {
        final Project project = resource.getProject();
        JFrame frame = project.show(resource);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                handleClose(resource.getOWLModel());
            }
        });
    }
}
