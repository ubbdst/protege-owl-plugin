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

package edu.stanford.smi.protegex.owl.javacode;

import java.util.logging.Level;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.actions.OWLModelActionConstants;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JavaCodeGeneratorAction extends AbstractOWLModelAction {
    
    public static final String GROUP = OWLModelActionConstants.JAVA_CODE_GROUP;


    public String getMenubarPath() {
        return CODE_MENU + PATH_SEPARATOR + GROUP;
    }


    public String getName() {
        return "Generate Protege-OWL Java Code...";
    }


    public void run(OWLModel owlModel) {
        EditableJavaCodeGeneratorOptions options = new ProjectBasedJavaCodeGeneratorOptions(owlModel);
        JavaCodeGeneratorPanel panel = new JavaCodeGeneratorPanel(options);
        if (ProtegeUI.getModalDialogFactory().showDialog(ProtegeUI.getTopLevelContainer(owlModel.getProject()), panel,
                getName(), ModalDialogFactory.MODE_OK_CANCEL) == ModalDialogFactory.OPTION_OK) {
            panel.ok();
            JavaCodeGenerator creator = new JavaCodeGenerator(owlModel, options);
            try {
                creator.createAll();
                ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel, "Java code successfully generated.");
            }
            catch (Exception ex) {
                Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                        "Could not create Java code:\n" + ex);
            }
        }
    }
}
