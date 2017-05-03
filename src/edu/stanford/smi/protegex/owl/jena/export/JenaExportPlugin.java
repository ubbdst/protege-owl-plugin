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

package edu.stanford.smi.protegex.owl.jena.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.exception.OntologyLoadException;
import edu.stanford.smi.protege.exception.ProtegeException;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.plugin.ExportPlugin;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protege.util.MessageError;
import edu.stanford.smi.protege.util.ProtegeJob;
import edu.stanford.smi.protege.util.WaitCursor;
import edu.stanford.smi.protegex.owl.database.OWLDatabaseModel;
import edu.stanford.smi.protegex.owl.jena.JenaFilePanel;
import edu.stanford.smi.protegex.owl.jena.JenaKnowledgeBaseFactory;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.creator.NewOwlProjectCreator;
import edu.stanford.smi.protegex.owl.jena.writersettings.JenaWriterSettings;
import edu.stanford.smi.protegex.owl.jena.writersettings.WriterSettings;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.storage.ProtegeSaver;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelAllTripleStoresWriter;
import edu.stanford.smi.protegex.owl.writer.rdfxml.util.ProtegeWriterSettings;
import edu.stanford.smi.protegex.owl.writer.xml.XMLWriterPreferences;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaExportPlugin implements ExportPlugin {

    public void handleExportRequest(Project project) {
        JenaFilePanel panel = new JenaFilePanel();
        if (project.isMultiUserClient()) {
            panel.getUseNativeWriterCheckBox().setSelected(true);
            panel.getUseNativeWriterCheckBox().setEnabled(false);
        }

        int rval = ProtegeUI.getModalDialogFactory().showDialog(ProtegeUI.getTopLevelContainer(project),
                panel, "OWL File to Export", ModalDialogFactory.MODE_OK_CANCEL);
        if (rval == ModalDialogFactory.OPTION_OK) {
            String filePath = panel.getOWLFilePath();
            WaitCursor cursor = new WaitCursor(ProjectManager.getProjectManager().getMainPanel());
            try {
                exportProject(project.getKnowledgeBase(), filePath, panel.getUseNativeWriter());
            }
            catch (Exception ex) {
                Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(panel,
                        "Export failed. Please see console for details.\n" + ex);
            }
            finally {
                cursor.hide();
            }
        }
    }

    private void exportProject(KnowledgeBase kb, String filePath, boolean useNativeWriter) {
        Collection errors = new ArrayList();
        JenaKnowledgeBaseFactory factory = new JenaKnowledgeBaseFactory();
        JenaOWLModel newOWLModel = null;
        URI fileURI = new File(filePath).toURI();

        if (kb instanceof OWLModel) { //OWL -> OWL
            Project newProject = Project.createNewProject(factory, errors);
            newOWLModel = (JenaOWLModel) newProject.getKnowledgeBase();
            newProject.setProjectURI(fileURI);

            OWLModel oldOWLModel = (OWLModel)kb;
            WriterSettings writerSettings = oldOWLModel.getWriterSettings();
            oldOWLModel.setWriterSettings(useNativeWriter ? new ProtegeWriterSettings(newOWLModel) : new JenaWriterSettings(newOWLModel));
            if (oldOWLModel instanceof JenaOWLModel) {
                ((JenaOWLModel)oldOWLModel).save(fileURI, FileUtils.langXMLAbbrev, errors);
            } else if (oldOWLModel instanceof OWLDatabaseModel) { // export an OWL Database model
                if (useNativeWriter) { //native writer
                    exportWithNativeWriter(oldOWLModel, fileURI, errors);
                } else { // Jena writer
                    OntModel newModel = ((OWLDatabaseModel) oldOWLModel).getOntModel();
                    OWLDatabaseModel dbModel = (OWLDatabaseModel) oldOWLModel;
                    String xmlBase = dbModel.getTripleStoreModel().getActiveTripleStore().getOriginalXMLBase();
                    String defaultNS = dbModel.getNamespaceManager().getDefaultNamespace();
                    if (xmlBase == null) {
                        if (defaultNS != null && defaultNS.endsWith("#")) {
                            xmlBase = defaultNS.substring(0, defaultNS.length() -1);
                        }
                    }
                    try {
                        File file = new File(fileURI);
                        //TT: writing to langXMl rather than langXMLAbbrev might be more efficient for DB mode; to be checked
                        JenaOWLModel.save(file, newModel, FileUtils.langXML, defaultNS , xmlBase);
                    } catch (Throwable t) {
                        Log.getLogger().log(Level.SEVERE, "Errors at exporting the OWL Database to OWL file", t);
                    }
                }
            }
            oldOWLModel.setWriterSettings(writerSettings);
        } else {  // Any other Protege format
            NewOwlProjectCreator creator = new NewOwlProjectCreator();
            try {
                creator.create(errors);
            } catch (OntologyLoadException e) {
                Log.getLogger().log(Level.SEVERE, "Could not create new Jena OWL project", e);
            }
            newOWLModel = creator.getOwlModel();
            if (newOWLModel != null) {
                new ProtegeSaver(kb, newOWLModel, useNativeWriter).run();
                newOWLModel.save(fileURI, FileUtils.langXMLAbbrev, errors);
            }
        }

        if (errors.size() == 0) {
            ProtegeUI.getModalDialogFactory().showMessageDialog(newOWLModel,
                    "Project has been exported to:\n" + filePath);
        } else {
            ProtegeUI.getModalDialogFactory().showErrorMessageDialog(newOWLModel,
                    "Export failed.\nPlease see console and logs for more details.");
        }

        if (newOWLModel != null) {
            try {
                newOWLModel.getProject().dispose();
            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, "Errors at disposing temporary exported OWL model", e);
            }
        }

    }

    public void exportWithNativeWriter(OWLModel oldOWLModel, URI exportFileURI, Collection errors) {
        Log.getLogger().info("Started native export of " + getProjectName(oldOWLModel) + " on " + new Date());
        long t0 = System.currentTimeMillis();

        WriterSettings writerSettings = oldOWLModel.getWriterSettings();
        ProtegeWriterSettings newWriterSettings = null;
        if (writerSettings instanceof ProtegeWriterSettings) { //try to preserve settings from exported model
            newWriterSettings = (ProtegeWriterSettings)writerSettings;
        }
        try {
            boolean useEntities = newWriterSettings == null ? true : newWriterSettings.getUseXMLEntities();
            boolean sortAlphabetically = newWriterSettings == null ? true : newWriterSettings.isSortAlphabetically();

            String owlFileStr = (String) new NativeOWLExport(oldOWLModel, useEntities, sortAlphabetically).execute();

            writeStringToFile(owlFileStr, new File(exportFileURI));
        }
        catch (Exception ex) {
            String message = "Failed to save file " + exportFileURI;
            Log.getLogger().log(Level.SEVERE, message, ex);
            errors.add(new MessageError(ex, message));
        }

        Log.getLogger().info("Ended native export of " + getProjectName(oldOWLModel) + " in " + (System.currentTimeMillis() - t0) + " ms.");
    }

    private String getProjectName(OWLModel owlModel) {
        if (owlModel.getProject().isMultiUserClient()) {
            URI projectURI = owlModel.getProject().getProjectURI();
            return projectURI == null ? "(project name not set)" : projectURI.toString();
        }
        return owlModel.getProject().getProjectName();
    }


    public String getName() {
        return "OWL";
    }

    public void dispose() {}


    private void writeStringToFile(String str, File file) throws FileNotFoundException {
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(file));
            out.print(str);
        }
        finally {
            if (out != null) {
                out.close();
            }
        }
    }


    // inner classes

    static class NativeOWLExport extends ProtegeJob {

        private static final long serialVersionUID = 7267730259036687650L;

        private boolean useEntities;
        private boolean sortAlphabetically;

        public NativeOWLExport(KnowledgeBase kb, boolean useEntities, boolean sortAlphabetically) {
            super(kb);
            this.useEntities = useEntities;
            this.sortAlphabetically = sortAlphabetically;
        }

        @Override
        public Object run() throws ProtegeException {

            String fileContent = null;
            File temp;
            try {
                temp = File.createTempFile("owlExport",".owl");
                temp.deleteOnExit(); //TT: It is interesting to keep them on the server for a while. If space is an issue, we can delete them right away.

                //write to temp file
                XMLWriterPreferences.getInstance().setUseNamespaceEntities(useEntities);
                OWLModelAllTripleStoresWriter writer = new OWLModelAllTripleStoresWriter((OWLModel)getKnowledgeBase(),
                        temp.getAbsoluteFile().toURI(), sortAlphabetically);
                writer.write();

                fileContent = readFile(temp);

            } catch (Exception e) {
                Log.getLogger().log(Level.WARNING, e.getMessage(), e);
                throw new ProtegeException(e.getMessage(), e);
            }

            return fileContent;
        }


        private String readFile(File file) throws IOException {

            char[] buffer = null;

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            buffer = new char[(int)file.length()];

            int i = 0;
            int c = bufferedReader.read();

            while (c != -1) {
                buffer[i++] = (char)c;
                c = bufferedReader.read();
            }

            bufferedReader.close();

            return new String(buffer);
        }

    }

}
