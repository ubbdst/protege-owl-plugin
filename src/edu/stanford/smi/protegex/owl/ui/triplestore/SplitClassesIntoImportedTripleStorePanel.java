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

package edu.stanford.smi.protegex.owl.ui.triplestore;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStore;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreModel;
import edu.stanford.smi.protegex.owl.model.triplestore.TripleStoreUtil;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SplitClassesIntoImportedTripleStorePanel extends CreateTripleStorePanel {

    private static final String HELP_TITLE = "How to split an ontology?";

    private static final String HELP_TEXT =
            "<P>It is a common design pattern to have a schema/ontology file (consisting of classes " +
                    "and properties) and one of more individuals files, which instantiate the ontology.  " +
                    "The instance file(s) will import the classes file.  " +
                    "This function will extract all classes and properties from your project into an imported " +
                    "ontology file.</P>" +
                    "<P><strong>Ontology URI</strong>: Specifies the address of the new classes ontology.</P>" +
                    "<P><strong>Local File</strong>: Specified the file to where the classes will be written into.</P>" +
                    "<P><strong>Prefix</strong>: The prefix of the classes when used as import.</P>";


    public SplitClassesIntoImportedTripleStorePanel(OWLModel owlModel) {
        super(owlModel);
    }


    protected TripleStore performAction() {
        final TripleStoreModel tripleStoreModel = getOWLModel().getTripleStoreModel();
        TripleStore oldActiveTS = tripleStoreModel.getActiveTripleStore();
        TripleStore tripleStore = super.performAction();
        TripleStoreUtil.switchTripleStore(getOWLModel(), oldActiveTS);
        if (tripleStore != null) {
            Collection resourcesToMove = new ArrayList();
            TripleStore activeTS = tripleStoreModel.getActiveTripleStore();
            Iterator resources = activeTS.listHomeResources();
            while (resources.hasNext()) {
                RDFResource resource = (RDFResource) resources.next();
                if (resource instanceof RDFSClass ||
                        resource instanceof RDFProperty ||
                        resource instanceof OWLDataRange ||
                        resource instanceof RDFList) {
                    resourcesToMove.add(resource);
                }
            }
            TripleStoreUtil.moveResources(resourcesToMove, activeTS, tripleStore);
            tripleStoreModel.setActiveTripleStore(tripleStore);
            final String prefix = getPrefix();
            for (Iterator it = resourcesToMove.iterator(); it.hasNext();) {
                RDFResource resource = (RDFResource) it.next();
                if (!resource.isAnonymous() && resource.getName().indexOf(':') < 0) {  // Default namespace
                    String newName = prefix + ":" + resource.getName();
                    if (getOWLModel().getRDFResource(newName) == null) {
                        resource = (RDFResource) resource.rename(newName);
                    }
                }
            }
            tripleStoreModel.setActiveTripleStore(oldActiveTS);
            tripleStoreModel.updateEditableResourceState();
            ProtegeUI.reloadUI(getOWLModel().getProject());
        }
        return tripleStore;
    }


    public static void showDialog(OWLModel owlModel) {
        SplitClassesIntoImportedTripleStorePanel panel = new SplitClassesIntoImportedTripleStorePanel(owlModel);
        final JComponent helpPanel = OWLUI.createHelpPanel(HELP_TEXT, HELP_TITLE);
        panel.add(BorderLayout.SOUTH, helpPanel);
        helpPanel.setPreferredSize(new Dimension(600, 150));
        if (ProtegeUI.getModalDialogFactory().showDialog(ProtegeUI.getTopLevelContainer(owlModel.getProject()), panel,
                "Move Classes into Imported Sub-Ontology", ModalDialogFactory.MODE_OK_CANCEL) ==
                ModalDialogFactory.OPTION_OK) {
            panel.performAction();
        }
    }
}
