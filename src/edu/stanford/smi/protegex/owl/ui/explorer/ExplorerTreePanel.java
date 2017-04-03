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

package edu.stanford.smi.protegex.owl.ui.explorer;

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.ui.HeaderComponent;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.cls.Hierarchy;
import edu.stanford.smi.protegex.owl.ui.existential.ExistentialTreeNode;
import edu.stanford.smi.protegex.owl.ui.subsumption.TooltippedSelectableTree;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A component that displays a generic explorer tree for classes.
 *
 * @author Holger Knublauch   <holger@knublauch.com>
 */
public class ExplorerTreePanel extends SelectableContainer implements Hierarchy {

    private ExplorerFilter filter;

    private HeaderComponent headerComponent;

    private JLabel hlabel;

    private LabeledComponent lc;

    private JPanel mainPanel;

    private OWLModel owlModel;

    private final static int MAX_EXPANSIONS = 100;

    private RDFSClass root;

    private TooltippedSelectableTree tree;

    private String title;

    private Action viewAction;

    private ModelListener modelListener = new ModelAdapter() {
        public void classDeleted(RDFSClass cls) {
            if (cls.equals(root)) {
                OWLNamedClass newRoot = owlModel.getOWLThingClass();
                setRoot(newRoot);
            }
        }
    };


    public ExplorerTreePanel(RDFSClass root, ExplorerFilter filter, String title, boolean withHeader) {
        this.filter = filter;
        this.root = root;
        this.title = title;
        setPreferredSize(new Dimension(260, 200));
        setMinimumSize(new Dimension(100, 100));
        owlModel = root.getOWLModel();
        viewAction = createViewClassAction();

        tree = createTree();

        owlModel.addModelListener(modelListener);

        setLayout(new BorderLayout());
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(BorderLayout.CENTER, new JScrollPane(tree));
        String label = getTitle();
        lc = new OWLLabeledComponent(label, mainPanel, true, false);
        viewAction.setEnabled(true);

        setLayout(new BorderLayout());
        if (withHeader) {
            hlabel = ComponentFactory.createLabel(root.getBrowserText(), ProtegeUI.getIcon(root), SwingConstants.LEFT);
            headerComponent = new HeaderComponent("SUPERCLASS EXPLORER", "For Class: ", hlabel);
            add(BorderLayout.NORTH, headerComponent);
        }

        add(BorderLayout.CENTER, lc);
    }


    private TooltippedSelectableTree createTree() {

        ExplorerTreeRoot root = new ExplorerTreeRoot(this.root, filter);
        TooltippedSelectableTree tree = createSelectableTree(viewAction, root);
        tree.setSelectionRow(0);
        tree.setAutoscrolls(true);
        setSelectable(tree);
        tree.setCellRenderer(new ExplorerRenderer());

        tree.addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                updateActions();
            }
        });
        tree.addMouseListener(new TreePopupMenuMouseListener(tree) {
            public JPopupMenu getPopupMenu() {
                return ExplorerTreePanel.this.getPopupMenu();
            }
        });
        return tree;
    }


    public Hierarchy createClone() {
        return new ExplorerTreePanel(root, filter, title, true);
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


    protected JPopupMenu createPopupMenu(RDFSClass cls) {
        JPopupMenu menu = new JPopupMenu();
        menu.add(createExpandAllAction());
        menu.add(createCollapseAllAction());
        if (cls instanceof RDFResource) {
            ResourceActionManager.addResourceActions(menu, this, (RDFResource) cls);
        }
        return menu;
    }


    protected TooltippedSelectableTree createSelectableTree(Action viewAction, LazyTreeRoot root) {
        TooltippedSelectableTree tree = new TooltippedSelectableTree(viewAction, root);
        tree.setCellRenderer(FrameRenderer.createInstance());
        return tree;
    }


    protected Action createViewClassAction() {
        return new ViewAction("View selected class", this) {
            public void onView(Object o) {
                owlModel.getProject().show((RDFSClass) o);
            }
        };
    }


    public void dispose() {
        super.dispose();
        owlModel.removeModelListener(modelListener);
    }


    public void expandToFillSpace() {
        java.util.List nodes = new ArrayList();
        int height = tree.getParent().getHeight();
        height /= tree.getRowHeight();
        LazyTreeNode rootNode = (LazyTreeNode) tree.getModel().getRoot();
        nodes.add(rootNode);
        TreeNode rootChild = rootNode.getChildAt(0);
        nodes.add(rootChild);
        for (int i = 2; i < height; i++) {
            if (rootChild.getChildCount() > 0 && rootChild.getChildAt(0).getChildCount() > 0) {
                nodes.add(rootChild.getChildAt(0));
            }
            else {
                break;
            }
            rootChild = rootChild.getChildAt(0);
        }
        TreePath path = new TreePath(nodes.toArray());
        tree.expandPath(path);
    }


    public void extendSelection(RDFSClass cls) {
        ComponentUtilities.extendSelection(getTree(), cls);
    }


    public JTree getClassesTree() {
        return getTree();
    }


    public RDFSClass getDisplayParent() {
        TreePath path = getTree().getSelectionModel().getLeadSelectionPath().getParentPath();
        LazyTreeNode node = (LazyTreeNode) path.getLastPathComponent();
        Object o = node.getUserObject();
        return (o instanceof RDFSClass) ? (RDFSClass) o : null;
    }


    public HeaderComponent getHeaderComponent() {
        return headerComponent;
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
            RDFSClass cls = (RDFSClass) CollectionUtilities.getFirstItem(selection);
            menu = createPopupMenu(cls);
        }
        return menu;
    }


    public RDFSClass getSelectedClass() {
        JTree tree = getTree();
        if (tree.getSelectionCount() == 1) {
            TreePath path = tree.getSelectionPath();
            ExplorerTreeNode node = (ExplorerTreeNode) path.getLastPathComponent();
            return node.getRDFSClass();
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
        return title;
    }


    protected JTree getTree() {
        return (JTree) getSelectable();
    }


    public String getType() {
        return "Superclass Explorer";
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


    public void setDisplayParent(RDFSClass cls) {
        // AbstractTreeWidget.setDisplayParent(getTree(), cls);
        Component oldTree = tree;
        mainPanel.removeAll();
        ComponentUtilities.dispose(oldTree);
        tree = createTree();
        mainPanel.add(BorderLayout.CENTER, new JScrollPane(tree));
    }


    public void setFinderComponent(JComponent c) {
        add(c, BorderLayout.SOUTH);
    }


    public void setRenderer(DefaultRenderer renderer) {
        getTree().setCellRenderer(renderer);
    }


    public void setRoot(RDFSClass newRoot) {
        root = newRoot;
        LazyTreeNode rootNode = (LazyTreeNode) tree.getModel().getRoot();
        rootNode.reload(Collections.singleton(root));
    }


    public void setSelectedClass(RDFSClass cls) {
        // Do nuthin'
    }


    public String toString() {
        return "ExplorerTreePanel";
    }


    protected void updateActions() {
    }
}
