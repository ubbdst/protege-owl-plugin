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

package edu.stanford.smi.protegex.owl.ui.metadatatab.imports.emptyimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.logging.Level;

import javax.swing.JComponent;

import edu.stanford.smi.protege.util.Log;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.jena.JenaOWLModel;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.util.ImportHelper;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.repository.impl.LocalFileRepository;
import edu.stanford.smi.protegex.owl.ui.wizard.OWLWizard;
import edu.stanford.smi.protegex.owl.writer.rdfxml.rdfwriter.OWLModelWriter;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 5, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EmptyImportWizard extends OWLWizard {

    private URI ontologyURI;

    private File localFile;

    private OWLModel owlModel;


    public EmptyImportWizard(JComponent parent, OWLModel owlModel) {
        super(parent, "Empty Import");
        this.owlModel = owlModel;
        addPage(new EmptyImportExplanationPage(this, owlModel));
    }


    protected void onFinish() {
        super.onFinish();
        try {
            if (localFile != null && ontologyURI != null) {
                JenaOWLModel model = ProtegeOWL.createJenaOWLModel();
                model.getNamespaceManager().setDefaultNamespace(ontologyURI + "#");
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(localFile));
                OWLModelWriter writer = new OWLModelWriter(model,
                        model.getTripleStoreModel().getActiveTripleStore(),
                        osw);
                writer.write();
                osw.flush();
                osw.close();
                RepositoryManager rm = owlModel.getRepositoryManager();
                rm.addProjectRepository(new LocalFileRepository(localFile));
                ImportHelper importHelper = new ImportHelper((JenaOWLModel) owlModel);
	            importHelper.addImport(ontologyURI);
	            importHelper.importOntologies();
            }
        }
        catch (Exception e) {
          Log.getLogger().log(Level.SEVERE, "Exception caught", e);
        }
    }


    public URI getOntologyURI() {
        return ontologyURI;
    }


    public void setOntologyURI(URI ontologyURI) {
        this.ontologyURI = ontologyURI;
    }


    public File getLocalFile() {
        return localFile;
    }


    public void setLocalFile(File localFile) {
        this.localFile = localFile;
    }
}

