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

import edu.stanford.smi.protege.util.FileField;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.wizard.OWLWizard;
import edu.stanford.smi.protegex.owl.ui.wizard.OWLWizardPage;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.net.URI;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Oct 5, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LocalFileWizardPage extends OWLWizardPage {

    private File localFile;

    private OWLModel owlModel;


    public LocalFileWizardPage(OWLWizard wizard, OWLModel owlModel) {
        super("Local File", wizard);
        this.owlModel = owlModel;
        createUI();
        setPageComplete(validateData());
    }


    private String getSuggestedPath() {
        String path = "";
        if (owlModel.getProject() != null) {
            URI uri = owlModel.getProject().getProjectURI();
            if (uri != null) {
                File f = new File(new File(uri).getParentFile(), "Import" + System.currentTimeMillis() / 1000 + ".owl");
                path = f.toString();
            }
        }
        return path;
    }


    private boolean validateData() {
        return localFile != null;
    }


    private void createUI() {
        final FileField fileField = new FileField("Local file", getSuggestedPath(), "owl", "OWL Files");
        fileField.setDialogType(JFileChooser.SAVE_DIALOG);
        fileField.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                localFile = fileField.getFilePath();
                setPageComplete(validateData());
            }
        });
        localFile = fileField.getFilePath();
        getContentComponent().add(fileField, BorderLayout.NORTH);
	    setHelpText("Specifying a local file", HELP_TEXT);
    }


    public void onFinish() {
        super.onFinish();
        ((EmptyImportWizard) getWizard()).setLocalFile(localFile);
    }


    private static final String HELP_TEXT = "<p>Please specify a local file where the new empty ontology " +
            "will be stored.</p>" +
            "<p>(This local file will be added to the project ontology " +
            "repository so that the import is redirected to it)</p>";
}

