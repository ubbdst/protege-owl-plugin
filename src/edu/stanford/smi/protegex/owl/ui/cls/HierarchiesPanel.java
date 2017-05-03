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

package edu.stanford.smi.protegex.owl.ui.cls;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.ui.subsumption.InferredSubsumptionTreePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class HierarchiesPanel extends JPanel implements HierarchyManager {

    private List hierarchyPanels = new ArrayList();

    private HierarchiesHost host;


    public HierarchiesPanel(HierarchiesHost host) {
        this.host = host;
    }


    public void addHierarchy(Hierarchy hierarchy) {
        SecondaryHierarchyPanel panel = new SecondaryHierarchyPanel(hierarchy, this);
        if (hierarchy.isDefaultSynchronized()) {
            panel.startSynchronize();
        }
        addHierarchyPanel(panel);
    }


    public void addHierarchyPanel(final HierarchyPanel panel) {
        hierarchyPanels.add(panel);
        setLayout(new BorderLayout());
        HierarchyPanel defaultPanel = (HierarchyPanel) hierarchyPanels.get(0);
        int width = defaultPanel.getWidth();
        panel.setSize(width, defaultPanel.getHeight());
        refillPanels();
        host.hierarchiesChanged(getWidth() + width);
        panel.getHierarchy().addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                if (panel.isSynchronized()) {
                    Hierarchy hierarchy = panel.getHierarchy();
                    Collection sels = hierarchy.getSelection();
                    if (sels.size() == 1) {
                        Cls cls = (Cls) sels.iterator().next();
                        if (cls instanceof RDFSNamedClass) {
                            synchronizePanels((RDFSNamedClass) cls);
                        }
                    }
                }
            }
        });
    }


    public void addHierarchyWindow(final Hierarchy panel) {
        HierarchyPanel defaultPanel = (HierarchyPanel) hierarchyPanels.get(0);
        int width = defaultPanel.getWidth();
        int height = defaultPanel.getHeight();
        final JFrame frame = ComponentFactory.createFrame();
        frame.setTitle(panel.getTitle());
        Container cp = frame.getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(BorderLayout.CENTER, (Component) panel);
        frame.setBounds(10, 10, width, height);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                ComponentUtilities.dispose(frame);
            }
        });
    }


    public void close(Hierarchy hierarchy) {
        HierarchyPanel panel = getHierarchyPanel(hierarchy);
        hierarchyPanels.remove(panel);
        ComponentUtilities.dispose(panel);
        int oldWidth = getWidth();
        refillPanels();
        host.hierarchiesChanged(oldWidth - panel.getWidth());
    }


    public void closeInferredHierarchies() {
        // We need to collect the hierarchies in an array
        // list and then remove them, so that we don't
        // get a concurrent modification exception
        ArrayList hierarchies = new ArrayList();
        for (Iterator it = hierarchyPanels.iterator(); it.hasNext();) {
            HierarchyPanel hierarchyPanel = (HierarchyPanel) it.next();
            Hierarchy hierarchy = hierarchyPanel.getHierarchy();
            if (hierarchy instanceof InferredSubsumptionTreePanel) {
                hierarchies.add(hierarchy);
            }
        }
        for (Iterator it = hierarchies.iterator(); it.hasNext();) {
            Hierarchy hierarchy = (Hierarchy) it.next();
            close(hierarchy);
        }
    }


    public void expandRootsOfInferredTrees() {
        for (Iterator it = hierarchyPanels.iterator(); it.hasNext();) {
            HierarchyPanel hierarchyPanel = (HierarchyPanel) it.next();
            Hierarchy hierarchy = hierarchyPanel.getHierarchy();
            if (hierarchy instanceof InferredSubsumptionTreePanel) {
                ((InferredSubsumptionTreePanel) hierarchy).expandRoot();
            }
        }
    }


    private HierarchyPanel getHierarchyPanel(Hierarchy hierarchy) {
        for (Iterator it = hierarchyPanels.iterator(); it.hasNext();) {
            HierarchyPanel hierarchyPanel = (HierarchyPanel) it.next();
            if (hierarchyPanel.getHierarchy() == hierarchy) {
                return hierarchyPanel;
            }
        }
        return null;
    }


    private boolean inferredHierarchyExists() {
        for (Iterator it = hierarchyPanels.iterator(); it.hasNext();) {
            HierarchyPanel hierarchyPanel = (HierarchyPanel) it.next();
            if (hierarchyPanel.getHierarchy() instanceof InferredSubsumptionTreePanel) {
                return true;
            }
        }
        return false;
    }


    private void refillPanels() {
        removeAll();
        Component comp = refillPanels(hierarchyPanels.iterator());
        add(BorderLayout.CENTER, comp);
        revalidate();
    }


    private Component refillPanels(Iterator it) {
        HierarchyPanel panel = (HierarchyPanel) it.next();
        if (it.hasNext()) {
            Component nextComponent = refillPanels(it);
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                  panel, nextComponent);
            splitPane.setDividerLocation(panel.getWidth());
            splitPane.setOneTouchExpandable(true);
            return splitPane;
        }
        else {
            return panel;
        }
    }


    public void showInferredHierarchy(OWLModel owlModel) {
        if (!inferredHierarchyExists()) {
            InferredSubsumptionTreePanel inferredTreePanel = new InferredSubsumptionTreePanel(owlModel);
            addHierarchy(inferredTreePanel);
            getTopLevelAncestor().validate();
        }
    }


    public void spawnHierarchy(Hierarchy hierarchy) {
        addHierarchyWindow(hierarchy);
    }


    private void synchronizePanels(RDFSNamedClass aClass) {
        for (Iterator it = hierarchyPanels.iterator(); it.hasNext();) {
            HierarchyPanel hierarchyPanel = (HierarchyPanel) it.next();
            if (hierarchyPanel.isSynchronized()) {
                hierarchyPanel.getHierarchy().setSelectedClass(aClass);
            }
        }
    }
}
