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

import java.awt.Component;
import java.awt.Container;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTree;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Reference;
import edu.stanford.smi.protege.server.framestore.RemoteClientFrameStore;
import edu.stanford.smi.protege.ui.ProjectView;
import edu.stanford.smi.protege.util.CollectionUtilities;
import edu.stanford.smi.protege.util.Selectable;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.model.OWLAnonymousClass;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.server.metaproject.OwlMetaProjectConstants;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.clsdesc.DisjointClassesWidget;
import edu.stanford.smi.protegex.owl.ui.conditions.ConditionsWidget;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.navigation.NavigationHistoryTabWidget;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourceDisplay;
import edu.stanford.smi.protegex.owl.ui.resourcedisplay.ResourcePanel;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;
import edu.stanford.smi.protegex.owl.ui.subsumption.ChangedClassesPanel;
import edu.stanford.smi.protegex.owl.ui.subsumption.InferredSubsumptionTreePanel;
import edu.stanford.smi.protegex.owl.ui.widget.AbstractTabWidget;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

/**
 * A tab for editing OWL/RDF classes, consisting of a class hierarchy tree and a resource display.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class OWLClassesTab extends AbstractTabWidget
        implements NavigationHistoryTabWidget, HierarchiesHost {

    protected AssertedClassesPanel clsesPanel;

    protected HierarchiesPanel hierarchiesPanel;

    protected JSplitPane mainSplitPane;

    protected ResourcePanel resourcePanel;


    protected JComponent createAssertedClsesPane() {
        clsesPanel = createClsesPanel();
        return clsesPanel;
    }


    protected ResourcePanel createResourcePanel() {
        return ProtegeUI.getResourcePanelFactory().createResourcePanel(getOWLModel(), ResourcePanel.DEFAULT_TYPE_CLASS);
    }


    private AssertedClassesPanel createClsesPanel() {

        AssertedClassesPanel assertedClsesPanel = new AssertedClassesPanel(getOWLModel(), hierarchiesPanel);

        assertedClsesPanel.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                transmitSelection();
            }
        });
        return assertedClsesPanel;
    }


    protected JSplitPane createMainSplitPane() {
        mainSplitPane = createLeftRightSplitPane("ClsesTab.left_right", 250);
        resourcePanel = createResourcePanel();
        JComponent rightComponent = (JComponent)resourcePanel;
        hierarchiesPanel = new HierarchiesPanel(this);
        clsesPanel = (AssertedClassesPanel) createAssertedClsesPane();
        HierarchyPanel primaryPanel = new HierarchyPanel(clsesPanel, hierarchiesPanel, false, getOWLModel());
        hierarchiesPanel.addHierarchyPanel(primaryPanel);
        mainSplitPane.setLeftComponent(hierarchiesPanel);
        mainSplitPane.setRightComponent(rightComponent);
        mainSplitPane.setDividerLocation(250);
        return mainSplitPane;
    }


    public boolean displayHostResource(RDFResource resource) {
        if (resource instanceof RDFSNamedClass) {
            clsesPanel.setSelectedClass((RDFSNamedClass) resource);
            return true;
        }
        else if (resource instanceof OWLAnonymousClass) {
            OWLAnonymousClass cls = (OWLAnonymousClass) resource;
            OWLAnonymousClass rootCls = cls.getExpressionRoot();
            Collection refs = ((KnowledgeBase)getOWLModel()).getReferences(rootCls, 100000);
            Set ignoreSlots = new HashSet();
            OWLModel owlModel = cls.getOWLModel();
            ignoreSlots.add(((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_TYPES));
            ignoreSlots.add(owlModel.getRDFTypeProperty());
            ignoreSlots.add(((KnowledgeBase) owlModel).getSlot(Model.Slot.DIRECT_INSTANCES));
            for (Iterator it = refs.iterator(); it.hasNext();) {
                Reference reference = (Reference) it.next();
                if (!ignoreSlots.contains(reference.getSlot())) {
                    if (reference.getFrame() instanceof RDFSNamedClass && reference.getFrame().isVisible()) {
                        clsesPanel.setSelectedClass((RDFSNamedClass) reference.getFrame());
                        ConditionsWidget conditionsWidget = getConditionsWidget();
                        if (conditionsWidget != null) {
                            conditionsWidget.getCurrentConditionsWidget().getTable().setSelectedRow(rootCls);
                        }
                        DisjointClassesWidget disjointClassesWidget = getDisjointClassesWidget();
                        if (disjointClassesWidget != null) {
                            disjointClassesWidget.getTable().setSelectedRow(rootCls);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * @deprecated
     */
    public void ensureClsSelected(Cls cls, int oldConditionsRow) {
        ensureClassSelected((RDFSNamedClass) cls, oldConditionsRow);
    }


    public void ensureClassSelected(RDFSNamedClass cls, int oldConditionsRow) {
        if (cls != null && !clsesPanel.getSelection().contains(cls)) {
            clsesPanel.setExpandedClass(cls, true);
            clsesPanel.setSelectedClass(cls);
            if (oldConditionsRow >= 0 && cls.equals(resourcePanel.getResource())) {
                ConditionsWidget newConditionsWidget = getConditionsWidget();
                if (newConditionsWidget != null) {
                    newConditionsWidget.setSelectedRow(oldConditionsRow);
                }
            }
        }
    }


    public static OWLClassesTab getOWLClassesTab(Component comp) {
        while (comp != null) {
            if (comp instanceof OWLClassesTab) {
                return (OWLClassesTab) comp;
            }
            comp = comp.getParent();
        }
        return null;
    }


    /**
     * @deprecated use getOWLClassesTab instead
     */
    public static OWLClassesTab getClsesTab(Component comp) {
        return getOWLClassesTab(comp);
    }


    public ConditionsWidget getConditionsWidget() {
        return (ConditionsWidget) OWLUI.findComponent((Container)resourcePanel, ConditionsWidget.class);
    }


    public JTree getClsTree() {
        return clsesPanel.getClsesTree();
    }


    public DisjointClassesWidget getDisjointClassesWidget() {
        return (DisjointClassesWidget) OWLUI.findComponent((Container)resourcePanel, DisjointClassesWidget.class);
    }


    protected HierarchiesPanel getHierarchiesPanel() {
        return hierarchiesPanel;
    }


    public HierarchyManager getHierarchyManager() {
        return hierarchiesPanel;
    }


    protected JSplitPane getMainSplitPane() {
        return mainSplitPane;
    }


    public Selectable getNestedSelectable() {
        return clsesPanel;
    }


    public OWLModel getOWLModel() {
        return (OWLModel) getProject().getKnowledgeBase();
    }


    /**
     * @deprecated
     * @see #getResourcePanel
     */
    protected ResourceDisplay getResourceDisplay() {
        return (ResourceDisplay) resourcePanel;
    }


    public ResourcePanel getResourcePanel() {
        return resourcePanel;
    }


    public RDFSNamedClass getSelectedClass() {
        Collection sels = clsesPanel.getSelection();
        if (sels.size() == 1) {
            return (RDFSNamedClass) sels.iterator().next();
        }
        return null;
    }


    public void hierarchiesChanged(int newPreferredWidth) {
        hierarchiesPanel.revalidate();
        mainSplitPane.setDividerLocation(newPreferredWidth);
        mainSplitPane.revalidate();
    }


    public void initialize() {

        setIcon(OWLIcons.getClassesIcon());
        setLabel("OWLClasses");

        mainSplitPane = createMainSplitPane();
        add(mainSplitPane);
        setInitialSelection();
        setClsTree(clsesPanel.getClsesTree());
    }


    @SuppressWarnings("unchecked")
    public static boolean isSuitable(Project p, Collection errors) {
        if (!(p.getKnowledgeBase() instanceof OWLModel)) {
            errors.add("This tab can only be used with OWL projects.");
            return false;
        }
        else if (p.isMultiUserClient() &&
                !RemoteClientFrameStore.isOperationAllowed(p.getKnowledgeBase(), 
                                                           OwlMetaProjectConstants.USE_OWL_CLASSES_TAB)) {
            errors.add("Don't have permission to access the owl classes tab");
            return false;
        }
        else {
            return true;
        }
    }


    public void refreshChangedClses() {
        ChangedClassesPanel ccp = ChangedClassesPanel.get(getOWLModel());
        ccp.refresh();
        if (ccp.getChangeCount() > 0) {
            ResultsPanelManager.addResultsPanel(getOWLModel(), ccp, true);
        }
        hierarchiesPanel.expandRootsOfInferredTrees();
    }


    protected void setMainSplitPane(JSplitPane splitpanel) {
        mainSplitPane = splitpanel;
    }


    protected void setHierarchiesPanel(HierarchiesPanel hierPanel) {
        hierarchiesPanel = hierPanel;
    }


    public void setInferredClsesVisible(boolean visible) {
        ProjectView projectView = ProtegeUI.getProjectView(getProject());
        projectView.setSelectedTab(this);
        if (visible) {
            hierarchiesPanel.showInferredHierarchy(getOWLModel());
        }
        else {
            hierarchiesPanel.closeInferredHierarchies();
        }
    }


    protected void setInitialSelection() {
        if (clsesPanel != null) {
            transmitSelection();
        }
    }


    /**
     * @deprecated please replace with the one with RDFSNamedClass please
     */
    public void setSelectedCls(OWLNamedClass cls) {
        clsesPanel.setSelectedClass(cls);
    }


    public void setSelectedCls(RDFSNamedClass aClass) {
        clsesPanel.setSelectedClass(aClass);
    }


    /**
     * @deprecated
     */
    public void showCls(Cls cls) {
        if (cls instanceof RDFSNamedClass) {
            showClass((RDFSNamedClass) cls);
        }
    }


    public void showClass(RDFSNamedClass cls) {
        clsesPanel.setSelectedClass(cls);
    }


    public void showInferredHierarchy() {
        OWLModel owlModel = getOWLModel();
        InferredSubsumptionTreePanel inferredTreePanel = new InferredSubsumptionTreePanel(owlModel);
        hierarchiesPanel.addHierarchy(inferredTreePanel);
    }


    protected void transmitSelection() {
        Collection selection = clsesPanel.getSelection();
        Instance selectedInstance = null;
        if (selection.size() == 1) {
            selectedInstance = (Instance) CollectionUtilities.getFirstItem(selection);
            OWLModel owlModel = getOWLModel();
            if (//selectedInstance.equals(owlModel.getRootCls()) ||
                    selectedInstance.equals(owlModel.getOWLNothing()) ||
                            selectedInstance.equals(owlModel.getSystemFrames().getDirectedBinaryRelationCls()) ||
                            selectedInstance.equals(owlModel.getRDFListClass()) ||
                            selectedInstance.equals(owlModel.getRDFUntypedResourcesClass())) {
                selectedInstance = null;
            }
        }
        if(selectedInstance == null || selectedInstance instanceof RDFResource) {
            resourcePanel.setResource((RDFResource) selectedInstance);
        }
        else if(resourcePanel instanceof ResourceDisplay) {
            ((ResourceDisplay)resourcePanel).setInstance(selectedInstance);
        }
    }
}
