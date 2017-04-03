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

package edu.stanford.smi.protegex.owl.ui.repository.wizard.impl;

import edu.stanford.smi.protege.util.WizardPage;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.RepositoryCreatorWizardPanel;

import java.awt.*;
import java.io.File;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 26, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class FileSelectionWizardPanel extends RepositoryCreatorWizardPanel {

    private FileBrowserPanel fileBrowserPanel;

    private WizardPage wizardPage;


    public FileSelectionWizardPanel(WizardPage wizardPage, boolean directoriesOnly, String helpText) {
        this.wizardPage = wizardPage;
        setLayout(new BorderLayout(12, 12));
        add(fileBrowserPanel = new FileBrowserPanel(wizardPage,
                directoriesOnly ? "Folder name" : "File name",
                directoriesOnly,
                helpText));

    }


    public abstract Repository createRepository(File f, boolean forceReadOnly, boolean recursive);


    public Repository createRepository() {
        return createRepository(fileBrowserPanel.getFile(), fileBrowserPanel.isForceReadOnly(),
                                fileBrowserPanel.isRecursive());
    }


    public boolean validateData() {
        return fileBrowserPanel.validateFileName();
    }
}

