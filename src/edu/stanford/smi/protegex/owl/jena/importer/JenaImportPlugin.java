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

package edu.stanford.smi.protegex.owl.jena.importer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.ImportPlugin;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.WaitCursor;
import edu.stanford.smi.protegex.owl.jena.JenaFilePanel;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.creator.OwlProjectFromUriCreator;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaImportPlugin implements ImportPlugin {
    private static transient Logger log = Log.getLogger(JenaImportPlugin.class);

    public void dispose() {
    }


    public String getName() {
        return "OWL File";
    }


    public Project handleImportRequest() {
        Project result = null;
        JenaFilePanel panel = new JenaFilePanel();
        int rval = ProtegeUI.getModalDialogFactory().showDialog(null, panel, "OWL File to Import", ModalDialogFactory.MODE_OK_CANCEL);
        if (rval == ModalDialogFactory.OPTION_OK) {
            String filePath = panel.getOWLFilePath();
            WaitCursor cursor = new WaitCursor(ProjectManager.getProjectManager().getMainPanel());
            try {
                result = importProject(new File(filePath).toURI());
            }
            finally {
                cursor.hide();
            }
        }
        return result;
    }


    @SuppressWarnings("unchecked")
    private Project importProject(URI uri) {
        java.util.Collection errors = new ArrayList();
        OwlProjectFromUriCreator creator  = new OwlProjectFromUriCreator();
        creator.setOntologyUri(uri.toString());
        try {
            creator.create(errors);
        }
        catch (OntologyLoadException ioe) {
            errors.add(ioe);
        }
        JenaOWLModel owlModel = creator.getOwlModel();
        if (errors.isEmpty()) {
            Project project = Project.createNewProject(null, new ArrayList());
            KnowledgeBase kb = project.getKnowledgeBase();
            new OWLImporter(owlModel, kb);
            return project;
        }
        else {
            for (Object error : errors) {
                if (error instanceof Throwable) {
                    log.log(Level.WARNING, "Exception caught", (Throwable) error);
                }
                else {
                    log.warning("Error found " + error);
                }
            }
            return null;
        }
    }
}
