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

package edu.stanford.smi.protegex.owl.ui.results;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.ui.StatusBar;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.triplestore.Triple;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;

import javax.swing.*;
import java.awt.*;

/**
 * A Singleton that manages the ResultsPanels in the current ProjectView.
 * ResultsPanels appear at the bottom of the screen and display tab-independent
 * data such as search and classification results.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ResultsPanelManager {

    /**
     * @deprecated use the version with the owlModel argument
     */
    public static void addResultsPanel(ResultsPanel resultsPanel, boolean replace) {
        addResultsPanel(null, resultsPanel, replace);
    }


    /**
     * Adds a ResultsPanel or replaces an existing one.
     *
     * @param resultsPanel the ResultsPanel to add
     * @param replace      true to replace an existing panel
     */
    public static void addResultsPanel(OWLModel owlModel, ResultsPanel resultsPanel, boolean replace) {
        if (replace) {
            JTabbedPane tabbedPane = getTabbedPane(owlModel);
            if (tabbedPane != null) {
                int index = tabbedPane.getTabCount() - 1;
                for (; index >= 0; index--) {
                    ResultsPanel other = (ResultsPanel) tabbedPane.getComponentAt(index);
                    if (resultsPanel == other) {
                        tabbedPane.setSelectedIndex(index);
                        return;
                    }
                    if (other.isReplaceableBy(resultsPanel)) {
                        tabbedPane.remove(index);
                        tabbedPane.insertTab(resultsPanel.getTabName(), resultsPanel.getIcon(), resultsPanel, null, index);
                        tabbedPane.setSelectedIndex(index);
                        return;
                    }
                }
            }
        }
        addResultsPanelToEnd(owlModel, resultsPanel);
    }


    private static void addResultsPanelToEnd(OWLModel owlModel, ResultsPanel resultsPanel) {
        JSplitPane splitPane = getSplitPane(owlModel);
        if (splitPane == null) {
            ProjectView projectView = ProtegeUI.getProjectView(owlModel.getProject());
            Component currentComp = projectView.getComponent(0);
            if (currentComp instanceof StatusBar) {
                currentComp = projectView.getComponent(1);
            }
            JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
            tabbedPane.addTab(resultsPanel.getTabName(), resultsPanel.getIcon(), resultsPanel);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
            splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, currentComp, tabbedPane);
            splitPane.setOneTouchExpandable(true);
            splitPane.setDividerLocation(currentComp.getHeight() - 180);
            projectView.add(BorderLayout.CENTER, splitPane);
            revalidate(projectView);
        }
        else {
            JTabbedPane tabbedPane = getTabbedPane(owlModel);
            tabbedPane.addTab(resultsPanel.getTabName(), resultsPanel.getIcon(), resultsPanel);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        }
    }


    /**
     * @deprecated use the other version instead
     */
    public static void closeResultsPanel(ResultsPanel resultsPanel) {
        closeResultsPanel(null, resultsPanel);
    }


    public static void closeResultsPanel(OWLModel owlModel, ResultsPanel resultsPanel) {
        Component comp = resultsPanel;
        while (!(comp instanceof JTabbedPane)) {
            comp = comp.getParent();
        }
        JTabbedPane tabbedPane = (JTabbedPane) comp;
        tabbedPane.remove(resultsPanel);
        resultsPanel.dispose();
        if (tabbedPane.getTabCount() == 0) {
            JSplitPane splitPane = getSplitPane(owlModel);
            Component mainTabs = splitPane.getLeftComponent();
            ProjectView projectView = ProtegeUI.getProjectView(tabbedPane);
            projectView.remove(splitPane);
            projectView.add(BorderLayout.CENTER, mainTabs);
            revalidate(projectView);
        }
    }


    /**
     * @deprecated use the other version please
     */
    public static ResultsPanel getResultsPanelByName(String tabName) {
        return getResultsPanelByName(null, tabName);
    }


    public static ResultsPanel getResultsPanelByName(OWLModel owlModel, String tabName) {
        JTabbedPane tabbedPane = getTabbedPane(owlModel);
        if (tabbedPane != null) {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                ResultsPanel tab = (ResultsPanel) tabbedPane.getComponentAt(i);
                if (tabName.equals(tab.getTabName())) {
                    return tab;
                }
            }
        }
        return null;
    }


    public static JSplitPane getSplitPane(OWLModel owlModel) {
        ProjectView projectView = ProtegeUI.getProjectView(owlModel != null ? owlModel.getProject() : null);
        for (int i = 0; i < projectView.getComponentCount(); i++) {
            Component currentComp = projectView.getComponent(i);
            if (currentComp instanceof JSplitPane) {
                return (JSplitPane) currentComp;
            }
        }
        return null;
    }


    public static JTabbedPane getTabbedPane(OWLModel owlModel) {
        JSplitPane splitPane = getSplitPane(owlModel);
        if (splitPane != null && splitPane.getComponentCount() > 1) {
            return (JTabbedPane) splitPane.getComponent(1);
        }
        else {
            return null;
        }
    }


    private static void revalidate(ProjectView projectView) {
        projectView.getParent().invalidate();
        projectView.getParent().validate();
    }


    public static void setSelectedResultsPanel(OWLModel owlModel, ResultsPanel resultsPanel) {
        getTabbedPane(owlModel).setSelectedComponent(resultsPanel);
    }


    /**
     * @deprecated use showHostResource instead
     */
    public static void showHostInstance(Instance hostInstance) {
        if (hostInstance instanceof RDFResource) {
            showHostResource((RDFResource) hostInstance);
        }
    }


    public static void showHostResource(RDFResource hostResource) {
        JSplitPane splitPane = getSplitPane(hostResource.getOWLModel());
        JTabbedPane tabbedPane = null;
        if (splitPane != null) {
            tabbedPane = (JTabbedPane) splitPane.getLeftComponent();
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Component tab = tabbedPane.getComponentAt(i);
                if (tab instanceof HostResourceDisplay) {
                    if (((HostResourceDisplay) tab).displayHostResource(hostResource)) {
                        tabbedPane.setSelectedComponent(tab);
                        ((JComponent) tab).requestFocusInWindow();
                        return;
                    }
                }
            }
        }

        hostResource.getProject().show(hostResource);
    }


    public static TripleDisplay showTriple(Triple triple) {
        RDFResource subject = triple.getSubject();
        JSplitPane splitPane = (JSplitPane) getSplitPane(subject.getOWLModel());
        JTabbedPane tabbedPane = null;
        if (splitPane != null) {
            tabbedPane = (JTabbedPane) splitPane.getLeftComponent();
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Component tab = tabbedPane.getComponentAt(i);
                TripleDisplay tripleDisplay = TripleDisplayUtil.displayTriple(tab, triple);
                if (tripleDisplay != null) {
                    tabbedPane.setSelectedComponent((Component) tab);
                    ((JComponent) tab).requestFocusInWindow();
                    return tripleDisplay;
                }
            }
        }

        // If everything fails: Show at least the subject
        showHostResource(subject);

        return null;
    }
}
