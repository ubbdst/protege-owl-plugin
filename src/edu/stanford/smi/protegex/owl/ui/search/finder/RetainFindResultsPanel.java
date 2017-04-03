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

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.ProjectManager;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanel;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * A JPanel for modal dialogs that can be used to String-matching search for a certain
 * value of a selected Slot.
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         03-Oct-2005
 */
public class RetainFindResultsPanel extends ResultsPanel {

    private FindResultsPanel resultsPanel;

    private String tabName = "Search";


    private Action refreshAction = new AbstractAction("Refresh", OWLIcons.getImageIcon("Refresh")) {
        public void actionPerformed(ActionEvent e) {
            resultsPanel.refresh();
        }
    };

    public RetainFindResultsPanel(Find find, AbstractFindResultsView view) {
        super(find.getModel());

        resultsPanel = new FindResultsPanel(find, view);
        find.addResultListener(new SearchAdapter() {
            public void searchEvent(Find source) {
                rename(source.getSummaryText());
            }
        });

        setCenterComponent(resultsPanel);

        addButton(refreshAction);
    }

    public RetainFindResultsPanel(OWLModel owlModel, FindResultsPanel panel) {
        super(owlModel);

        resultsPanel = panel;
        setCenterComponent(resultsPanel);
        addButton(refreshAction);
    }

    public Icon getIcon() {
        return Icons.getFindIcon();
    }

    public String getTabName() {
        return tabName;
    }

    private void rename(String label) {
        tabName = label;
        OWLModel omodel = (OWLModel) ProjectManager.getProjectManager().getCurrentProject().getKnowledgeBase();
        JTabbedPane t = ResultsPanelManager.getTabbedPane(omodel);
        if (t != null) {
            for (int i = 0; i < t.getTabCount(); i++) {
                ResultsPanel tab = (ResultsPanel) t.getComponentAt(i);
                if (tab == this) {
                    t.setMnemonicAt(i, KeyEvent.VK_F);
                    t.setTitleAt(i, tabName);
                }
            }
        }
        revalidate();
    }

    public void requestDispose(JComponent source) {
        //ignore
    }
}
