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

import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.Wizard;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.repository.Repository;
import edu.stanford.smi.protegex.owl.repository.RepositoryManager;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.repository.wizard.RepositoryWizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.List;

/**
 * User: matthewhorridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Sep 18, 2005<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public abstract class AbstractRepositoriesPanel extends JPanel {

    private RepositoryManager manager;

    private Action addRepAction;

    private OWLModel model;

    private Box repositoriesHolder;


    public AbstractRepositoriesPanel(final OWLModel model, RepositoryManager man) {
        this.manager = man;
        this.model = model;
        addRepAction = new AbstractAction("Add repository", OWLIcons.getAddIcon()) {
            public void actionPerformed(ActionEvent e) {
                RepositoryWizard w = new RepositoryWizard(null, model);
                int ret = w.execute();
                if (ret == Wizard.RESULT_FINISH) {
                    if (w.getRepository() != null) {
                        addRepository(w.getRepository());
                    }
                }
                reloadUI();
            }
        };
        createUI();
    }


    protected void setAddRepositoryEnabled(boolean b) {
        addRepAction.setEnabled(b);
    }


    public abstract void addRepository(Repository repository);


    public RepositoryManager getRepositoryManager() {
        return manager;
    }


    public abstract List getRepositories();


    public void reloadUI() {
        List list = getRepositories();
        repositoriesHolder.removeAll();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Repository curRep = (Repository) it.next();
            repositoriesHolder.add(new RepositoryPanel(model, manager, curRep, this));
        }
        revalidate();
    }


    public abstract String getRepositoriesTitle();


    private void createUI() {
        repositoriesHolder = new Box(BoxLayout.Y_AXIS);
        reloadUI();
        JPanel packingPanel = new JPanel(new BorderLayout());
        packingPanel.setOpaque(false);
        packingPanel.add(repositoriesHolder, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(packingPanel);
        sp.getViewport().setBackground(Color.WHITE);
        LabeledComponent lc = new LabeledComponent(getRepositoriesTitle(), sp);
        lc.addHeaderButton(addRepAction);
        setLayout(new BorderLayout());
        add(lc, BorderLayout.CENTER);
        setOpaque(false);
    }
}

