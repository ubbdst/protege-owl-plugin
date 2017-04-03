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

package edu.stanford.smi.protegex.owl.ui.existential;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.ModelUtilities;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.cls.Hierarchy;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * A component that displays an existential relationship between classes.
 *
 * @author Holger Knublauch   <holger@knublauch.com>
 */
public class ExistentialTreePanel extends SelectableContainer implements Hierarchy {

    private OWLObjectProperty existentialProperty;

    private HeaderComponent headerComponent;

    private LabeledComponent lc;

    private JPanel mainPanel;

    private OWLModel owlModel;

    private final static int MAX_EXPANSIONS = 100;

    private Cls root;

    private Slot superclassesSlot;

    private Action viewAction;


    public ExistentialTreePanel(Cls root,
                                Slot superclassesSlot,
                                OWLObjectProperty existentialProperty) {
        this.root = root;
        this.superclassesSlot = superclassesSlot;
        this.existentialProperty = existentialProperty;
        setPreferredSize(new Dimension(260, 400));
        setMinimumSize(new Dimension(100, 100));
        owlModel = (OWLModel) root.getKnowledgeBase();
        viewAction = createViewClsAction();

        SelectableTree tree = createTree(root);

        setLayout(new BorderLayout());
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(BorderLayout.CENTER, new JScrollPane(tree));
        //mainPanel.add(new SubsumptionTreeFinder(okb, tree, "Find Class",
        //        superclassesSlot), BorderLayout.SOUTH);
        String label = getTitle();
        lc = new OWLLabeledComponent(label, mainPanel, true, false);
        viewAction.setEnabled(true);

        JLabel hlabel = ComponentFactory.createLabel(root.getProject().getName(), Icons.getProjectIcon(), SwingConstants.LEFT);
        headerComponent = new HeaderComponent("CLASS BROWSER", "For Project", hlabel);

        setLayout(new BorderLayout());
        add(BorderLayout.NORTH, headerComponent);
        add(BorderLayout.CENTER, lc);
    }


    private SelectableTree createTree(Cls root) {

        SelectableTree tree = createSelectableTree(viewAction,
                new ExistentialTreeRoot(root, superclassesSlot, existentialProperty));
        tree.setSelectionRow(0);
        tree.setAutoscrolls(true);
        setSelectable(tree);

        tree.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                updateActions();
            }
        });
        tree.addMouseListener(new TreePopupMenuMouseListener(tree) {
            public JPopupMenu getPopupMenu() {
                return ExistentialTreePanel.this.getPopupMenu();
            }
        });
        return tree;
    }


    public Hierarchy createClone() {
        return new ExistentialTreePanel(root, superclassesSlot, existentialProperty);
    }


    private Action createCollapseAllAction() {
        return new AbstractAction("Collapse", Icons.getBlankIcon()) {
            public void actionPerformed(ActionEvent event) {
                ComponentUtilities.fullSelectionCollapse(getTree());
            }
        };
    }


    private Action createExpandAllAction() {
        return new AbstractAction("Expand", Icons.getBlankIcon()) {
            public void actionPerformed(ActionEvent event) {
                ComponentUtilities.fullSelectionExpand(getTree(), MAX_EXPANSIONS);
            }
        };
    }


    protected JPopupMenu createPopupMenu(Cls cls) {
        JPopupMenu menu = new JPopupMenu();
        if (cls.isVisible()) {
            Action action = getHideClsAction();
            if (cls.equals(owlModel.getOWLThingClass())) {
                action.setEnabled(false);
            }
            menu.add(action);
        }
        else {
            menu.add(getUnhideClsAction());
        }
        menu.addSeparator();
        menu.add(createExpandAllAction());
        menu.add(createCollapseAllAction());
        if (cls instanceof RDFResource) {
            ResourceActionManager.addResourceActions(menu, this, (RDFResource) cls);
        }
        return menu;
    }


    protected SelectableTree createSelectableTree(Action viewAction, LazyTreeRoot root) {
        SelectableTree tree = new SelectableTree(viewAction, root);
        tree.setCellRenderer(FrameRenderer.createInstance());
        return tree;
    }


    protected Action createViewClsAction() {
        return new ViewAction("View selected class", this) {
            public void onView(Object o) {
                owlModel.getProject().show((Cls) o);
            }
        };
    }


    public void expandRoot() {
        setExpandedCls(owlModel.getOWLThingClass(), true);
    }


    public void extendSelection(Cls cls) {
        ComponentUtilities.extendSelection(getTree(), cls);
    }


    public JTree getClsesTree() {
        return getTree();
    }


    public Cls getDisplayParent() {
        TreePath path = getTree().getSelectionModel().getLeadSelectionPath().getParentPath();
        LazyTreeNode node = (LazyTreeNode) path.getLastPathComponent();
        Object o = node.getUserObject();
        return (o instanceof Cls) ? (Cls) o : null;
    }


    protected OWLObjectProperty getExistentialProperty() {
        return existentialProperty;
    }


    public HeaderComponent getHeaderComponent() {
        return headerComponent;
    }


    private Action getHideClsAction() {
        return new AbstractAction("Hide class", Icons.getBlankIcon()) {
            public void actionPerformed(ActionEvent event) {
                Iterator i = getSelection().iterator();
                while (i.hasNext()) {
                    Cls cls = (Cls) i.next();
                    cls.setVisible(false);
                }
            }
        };
    }


    protected LabeledComponent getLabeledComponent() {
        return lc;
    }


    protected OWLModel getOWLModel() {
        return owlModel;
    }


    private JPopupMenu getPopupMenu() {
        JPopupMenu menu = null;
        Collection selection = getSelection();
        if (selection.size() == 1) {
            Cls cls = (Cls) CollectionUtilities.getFirstItem(selection);
            menu = createPopupMenu(cls);
        }
        return menu;
    }


    public RDFSClass getSelectedClass() {
        return getSelectedOWLClass();
    }


    public OWLClass getSelectedOWLClass() {
        JTree tree = getTree();
        if (tree.getSelectionCount() == 1) {
            TreePath path = tree.getSelectionPath();
            ExistentialTreeNode node = (ExistentialTreeNode) path.getLastPathComponent();
            return node.getOWLClass();
        }
        return null;
    }


    protected ExistentialTreeNode getSelectedNode() {
        JTree tree = getTree();
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            return (ExistentialTreeNode) path.getLastPathComponent();
        }
        return null;
    }


    public OWLClass getSelectedParentClass() {
        JTree tree = getTree();
        if (tree.getSelectionCount() == 1) {
            TreePath path = tree.getSelectionPath();
            int count = path.getPathCount();
            if (count > 2) {
                ExistentialTreeNode node = (ExistentialTreeNode) path.getPathComponent(count - 2);
                return node.getOWLClass();
            }
        }
        return null;
    }


    public String getTitle() {
        return StringUtilities.capitalize(existentialProperty.getBrowserText());
    }


    public String getType() {
        return "Existential Relationship";
    }


    protected JTree getTree() {
        return (JTree) getSelectable();
    }


    private Action getUnhideClsAction() {
        return new AbstractAction("Make class visible", Icons.getBlankIcon()) {
            public void actionPerformed(ActionEvent event) {
                Iterator i = getSelection().iterator();
                while (i.hasNext()) {
                    Cls cls = (Cls) i.next();
                    cls.setVisible(true);
                }
            }
        };
    }


    public boolean isDefaultSynchronized() {
        return false;
    }


    public void navigateToResource(RDFResource resource) {
        if (resource instanceof RDFSClass) {
            setSelectedClass((RDFSClass) resource);
        }
    }


    public void removeSelection() {
        ComponentUtilities.removeSelection(getTree());
    }


    public void setDisplayParent(Cls cls) {
        // AbstractTreeWidget.setDisplayParent(getTree(), cls);
        SelectableTree tree = createTree(cls);
        Component oldTree = mainPanel.getComponent(0);
        mainPanel.removeAll();
        ComponentUtilities.dispose(oldTree);
        mainPanel.add(BorderLayout.CENTER, new JScrollPane(tree));
    }


    public void setExpandedCls(Cls cls, boolean expanded) {
        Collection path = ModelUtilities.getPathToRoot(cls);
        ComponentUtilities.setExpanded(getTree(), path, expanded);
    }


    public void setFinderComponent(JComponent c) {
        add(c, BorderLayout.SOUTH);
    }


    public void setRenderer(DefaultRenderer renderer) {
        getTree().setCellRenderer(renderer);
    }


    public void setSelectedClass(RDFSClass cls) {
        setDisplayParent(cls);
    }


    public String toString() {
        return "SubsumptionTreePanel";
    }


    protected void updateActions() {
    }


    public static interface SuperClsesPanel {

        void setCls(OWLNamedClass cls);
    }
}
