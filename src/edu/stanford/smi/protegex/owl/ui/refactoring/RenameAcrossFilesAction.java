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

package edu.stanford.smi.protegex.owl.ui.refactoring;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hp.hpl.jena.graph.impl.SimpleGraphMaker;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.jena.Jena;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.ProtegeNames;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.ModalProgressBarManager;

/**
 * An Action that can be used to rename a Resource across multiple files.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RenameAcrossFilesAction extends RefactorResourceAction {
    private static transient final Logger log = Log.getLogger(RenameAcrossFilesAction.class);

    private final static String PROPERTY = "RenameAcrossFiles";


    public RenameAcrossFilesAction() {
        super("Rename across files...", OWLIcons.getImageIcon("RenameAcrossFiles"));
    }


    public void actionPerformed(ActionEvent e) {
        RDFResource resource = getResource();
        String oldPropertyValue = resource.getOWLModel().getOWLProject().getSettingsMap().getString(PROPERTY);
        String[] files = new String[0];
        if (oldPropertyValue != null) {
            files = oldPropertyValue.split(",");
        }
        RenameAcrossFilesPanel panel = new RenameAcrossFilesPanel(resource, files);
        if (ProtegeUI.getModalDialogFactory().showDialog(ProtegeUI.getTopLevelContainer(resource.getProject()), panel,
                "Rename " + resource.getName() + " across files...", ModalDialogFactory.MODE_OK_CANCEL) == ModalDialogFactory.OPTION_OK) {
            Iterator it = panel.getSelectedFiles();
            String newPropertyValue = "";
            while (it.hasNext()) {
                File file = (File) it.next();
                String f = file.getAbsolutePath();
                newPropertyValue += f;
                if (it.hasNext()) {
                    newPropertyValue += ",";
                }
            }
            resource.getOWLModel().getOWLProject().getSettingsMap().setString(PROPERTY, newPropertyValue);

            String newName = panel.getNewName();
            if (!newName.equals(resource.getName())) {
                OWLModel owlModel = resource.getOWLModel();
                if (owlModel.isValidResourceName(newName, resource)) {
                    performAction(resource, newName, panel.getSelectedFiles());
                }
                else {
                    ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel,
                            "This is not a valid name.");
                }
            }
        }
    }


    @Override
    public boolean isSuitable(Component component, RDFResource resource) {
        if (resource instanceof RDFResource) {
            return !resource.isSystem() && !resource.isAnonymous();
        }
        else {
            return false;
        }
    }


    public static void performAction(RDFResource resource, String newName, Iterator files) {
        OWLModel owlModel = resource.getOWLModel();
        String oldURI = resource.getURI();
        resource = (RDFResource) resource.rename(newName);
        String newURI = resource.getURI();
        ModalProgressBarManager man = new ModalProgressBarManager("Renaming across files...");
        man.setProgressValue(0);
        Set filesSet = Jena.set(files);
        int step = 0;
        man.start();
        Collection okFiles = new HashSet();
        Collection errorFiles = new HashSet();
        for (Iterator it = filesSet.iterator(); it.hasNext(); step++) {
            File file = (File) it.next();
            man.setProgressText(file.getAbsolutePath());
            man.setProgressValue((double) step / filesSet.size());
            log.info("[RenameAcrossFilesAction] " + file);
            try {
                performAction(resource.getProject(), file, oldURI, newURI, okFiles);
            }
            catch (Exception ex) {
                Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                errorFiles.add(file);
            }
        }
        man.stop();
        String msg = "";
        if (okFiles.isEmpty()) {
            msg += "No files were changed.\n";
        }
        else {
            msg += "The following files were changed:\n";
            for (Iterator it = okFiles.iterator(); it.hasNext();) {
                File file = (File) it.next();
                msg += " - " + file.getAbsolutePath() + "\n";
            }
        }
        if (!errorFiles.isEmpty()) {
            msg += "The following files had an error:\n";
            for (Iterator it = errorFiles.iterator(); it.hasNext();) {
                File file = (File) it.next();
                msg += " - " + file.getAbsolutePath() + "\n";
            }
        }
        ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                "The resource " + oldURI +
                        "\nwas renamed to " + newURI + "\n" + msg);
    }


    public static void performAction(Project project, File file, String oldURI, String newURI, Collection okFiles) throws IOException {
        OntModelSpec spec = new OntModelSpec(OntModelSpec.OWL_MEM);
        spec.setReasoner(null);
        OntDocumentManager dm = new OntDocumentManager();

        // Temporary hack to circumnavigate a Jena bug
        //SimpleGraphMaker sgm = (SimpleGraphMaker) ((ModelMakerImpl) spec.getModelMaker()).getGraphMaker();
        //TODO: TT - check if this is the right call. Had to change at transition from Jena 2.5.4 -> Jena 2.5.5 
        SimpleGraphMaker sgm = (SimpleGraphMaker) spec.getBaseModelMaker().getGraphMaker();
        Collection toGo = Jena.set(sgm.listGraphs());
        for (Iterator i = toGo.iterator(); i.hasNext(); sgm.removeGraph((String) i.next()))
            ;
        dm.clearCache();

        String path = new File(Jena.getOntPolicyFilePath(project)).toURI().toString() + ";" +
                new File(new File("etc"), "ont-policy.rdf").toURI().toString();
        // String path = "file:" + JenaLoader.getOntPolicyFilePath() + ";file:etc./ont-policy.rdf";
        dm.setMetadataSearchPath(path, true);
        spec.setDocumentManager(dm);
        OntModel ontModel = ModelFactory.createOntologyModel(spec, null);

        ontModel.read(new FileInputStream(file),
                ProtegeNames.DEFAULT_DEFAULT_NAMESPACE,
                FileUtils.langXMLAbbrev);
        Resource oldResource = ontModel.getResource(oldURI);
        if (ontModel.contains(oldResource, null, (RDFNode) null) ||
                ontModel.contains(null, null, oldResource) ||
                (oldResource.canAs(Property.class) &&
                        ontModel.contains(null, ((Property) oldResource.as(Property.class)), (RDFNode) null))) {
            log.info("[RenameAcrossFilesAction]   References found, now renaming...");
            Jena.renameResource(ontModel, oldResource, newURI);
            JenaOWLModel.save(file, ontModel, FileUtils.langXMLAbbrev,
                    ontModel.getNsPrefixURI(""));
            okFiles.add(file);
        }
    }
}
