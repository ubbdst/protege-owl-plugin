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

package edu.stanford.smi.protegex.owl.ui.search.finder;

import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.dialogs.ModalDialogFactory;
import edu.stanford.smi.protegex.owl.ui.results.HostResourceDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Oct-2005
 */
public class FindInDialogAction extends AbstractFindAction {

    private static Dimension savedSize = new Dimension(400, 200);

    AbstractFindResultsView view;

    FindResultsPanel resultsPanel;


    public FindInDialogAction(ResultsViewModelFind find, Icon icon, HostResourceDisplay hrd, boolean allowSave) {
        super(find, icon, hrd, allowSave);
    }


    protected void showResults(AbstractFindResultsView view) {

        this.view = view;

        resultsPanel = new FindResultsPanel(findModel.getFind(), view);
        findModel.getFind().addResultListener(new SearchAdapter() {
            public void searchEvent(Find source) {
                rename(source.getSummaryText());
            }
        });

        // retain the size as changed by the user
        resultsPanel.setPreferredSize(savedSize);
        resultsPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                savedSize.setSize(e.getComponent().getWidth(),
                                  e.getComponent().getHeight());
            }
        });

        resultsPanel.setSaveResultsEnabled(allowSave);

        Component win = ProtegeUI.getTopLevelContainer(ProjectManager.getProjectManager().getCurrentProject());

        ModalDialogFactory fac = ProtegeUI.getModalDialogFactory();
        int result = fac.showDialog(win, resultsPanel, findModel.getFind().getSummaryText(),
                                    ModalDialogFactory.MODE_OK_CANCEL,
                                    new ModalDialogFactory.CloseCallback() {
                                        public boolean canClose(int result) {
                                            boolean canClose = true;
                                            if (result == ModalDialogFactory.OPTION_OK) {
                                                canClose = (FindInDialogAction.this.view.getSelectedResource() != null);
                                            }
                                            return canClose;
                                        }
                                    });

        switch (result) {
            case ModalDialogFactory.OPTION_OK:
                resultsPanel.selectResource();
                break;
            case ModalDialogFactory.OPTION_CANCEL:
                synchronized (findModel) {
                    findModel.getFind().cancelSearch();
                    findModel.getFind().waitForSearchComplete();
                    findModel.getFind().reset();
                }
                break;
        }
    }

    private void rename(String name) {
        Object ancestor = resultsPanel.getTopLevelAncestor();
        if (ancestor != null && ancestor instanceof JDialog) {
            ((JDialog) ancestor).setTitle(name);
        }
    }
    
    
}
