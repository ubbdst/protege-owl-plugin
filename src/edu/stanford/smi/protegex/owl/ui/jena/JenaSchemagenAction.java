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

package edu.stanford.smi.protegex.owl.ui.jena;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

import jena.schemagen;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.FileUtils;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.javacode.JavaCodeGeneratorAction;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.jena.OntModelProvider;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.project.OWLProject;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.AbstractOWLModelAction;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class JenaSchemagenAction extends AbstractOWLModelAction {

    public final static String JENA_SCHEMAGEN_FILE = "JENA-SCHEMAGEN-FILE";

    public final static String JENA_SCHEMAGEN_PACKAGE = "JENA-SCHEMAGEN-PACKAGE";


    public String getMenubarPath() {
        return CODE_MENU + PATH_SEPARATOR + JavaCodeGeneratorAction.GROUP;
    }


    public String getName() {
        return "Generate Java Schema class...";
    }


    public void run(OWLModel owlModel) {
        OntModelProvider ontModelProvider = (OntModelProvider) owlModel;
        OWLProject project = owlModel.getOWLProject();

        JenaSchemagenPanel panel = new JenaSchemagenPanel();
        panel.setFileName(project.getSettingsMap().getString(JENA_SCHEMAGEN_FILE));
        panel.setPackage(project.getSettingsMap().getString(JENA_SCHEMAGEN_PACKAGE));

        Component comp = ProtegeUI.getTopLevelContainer(owlModel.getProject());
        if (ProtegeUI.getModalDialogFactory().showDialog(comp, panel, getName(),
                ModalDialogFactory.MODE_OK_CANCEL) == ModalDialogFactory.OPTION_OK) {
            project.getSettingsMap().setString(JENA_SCHEMAGEN_FILE, panel.getFileName());
            project.getSettingsMap().setString(JENA_SCHEMAGEN_PACKAGE, panel.getPackage());
            File file = new File(panel.getFileName());
            try {
                generate(ontModelProvider, file, panel.getPackage());
                ProtegeUI.getModalDialogFactory().showMessageDialog(comp, "Schema generated to " + file + ".");
            }
            catch (Exception ex) {
                Log.getLogger().log(Level.SEVERE, "Exception caught", ex);
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(comp, "Error: " + ex);
            }
        }
    }


    public static void generate(OntModelProvider ontModelProvider, File outputFile, String packageName) throws Exception {
        OntModel model = ontModelProvider.getOntModel();
        String sBase = model.getNsPrefixURI("");
        File file = new File("schemagen-temp.owl");
        JenaOWLModel.save(file, model, FileUtils.langXMLAbbrev, sBase);

        ArrayList args = new ArrayList();
        args.add("-a");
        args.add(sBase);

        args.add("-n");
        String className = outputFile.getName();
        int index = className.lastIndexOf('.');
        if (index >= 0) {
            className = className.substring(0, index);
        }
        args.add(className);

        args.add("-i");
        args.add(file.toURI().toString());

        args.add("-o");
        args.add(outputFile.getAbsolutePath());

        if (packageName != null) {
            args.add("--package");
            args.add(packageName);
        }
        args.add("--ontology");
        args.add("--owl");

        try {
            schemagen.main((String[]) args.toArray(new String[0]));
        }
        catch (Exception ex) {
            file.delete();
            throw ex;
        }
        file.delete();
    }
}
