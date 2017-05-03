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

package edu.stanford.smi.protegex.owl.ui.repository;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 27, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RepositoryManagerPanel extends JPanel {

    private OWLModel model;

    private RepositoryManager repositoryManager;

    private AbstractRepositoriesPanel globalRepositoriesPanel;

    private AbstractRepositoriesPanel projectRepositoriesPanel;

    private ArrayList cachedRepositories;


    public RepositoryManagerPanel(OWLModel model) {
        this.model = model;
        this.repositoryManager = model.getRepositoryManager();
        cachedRepositories = new ArrayList();
        for (Iterator it = repositoryManager.getAllRepositories().iterator(); it.hasNext();) {
            cachedRepositories.add(it.next());
        }
        createUI();
    }


    public Dimension getPreferredSize() {
        return new Dimension(700, 500);
    }


    private void createUI() {
        setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        projectRepositoriesPanel = new ProjectRepositoriesPanel(model, repositoryManager);
        tabbedPane.add(projectRepositoriesPanel.getRepositoriesTitle(), projectRepositoriesPanel);
        globalRepositoriesPanel = new GlobalRepositoriesPanel(model, repositoryManager);
        tabbedPane.add(globalRepositoriesPanel.getRepositoriesTitle(), globalRepositoriesPanel);
        add(tabbedPane);
    }


    private boolean repositoriesChanged() {
        Collection repositories = repositoryManager.getAllRepositories();
        if (repositories.size() != cachedRepositories.size()) {
            return true;
        }
        Iterator cachedRepIt = cachedRepositories.iterator();
        for (Iterator it = repositories.iterator(); it.hasNext();) {
            if (it.next().equals(cachedRepIt.next()) == false) {
                return true;
            }
        }
        return false;
    }


    public static void showDialog(Component parent, OWLModel owlModel) {
        RepositoryManagerPanel pan = new RepositoryManagerPanel(owlModel);
        int ret = ProtegeUI.getModalDialogFactory().showDialog(parent, pan,
                "Repository Manager",
                ModalDialogFactory.MODE_CLOSE);
        if (owlModel.getAllImports().size() > 0) {
            if (pan.repositoriesChanged()) {
                final Object [] options = new String []{"Reload", "Cancel"};
                int val = JOptionPane.showOptionDialog(pan, "<html><body>The order of repositories has changed.<br>" +
                        "The system needs to reload the current project. Press 'reload' " +
                        "to save and reload, or cancel to do a manual reload.</body></html>",
                        "Save and reload required",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null, options, options[0]);
                if (val == 0) {
                    OWLUtil.saveAndReloadProject();
                }
            }
        }
    }

}

