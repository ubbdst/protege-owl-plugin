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

package edu.stanford.smi.protegex.owl.ui.clsproperties;

import edu.stanford.smi.protege.model.*;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.model.impl.AbstractRDFSClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.actions.ResourceActionManager;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.code.OWLSymbolPanel;
import edu.stanford.smi.protegex.owl.ui.code.OWLTextField;
import edu.stanford.smi.protegex.owl.ui.code.SymbolEditorHandler;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTable;
import edu.stanford.smi.protegex.owl.ui.profiles.OWLProfiles;
import edu.stanford.smi.protegex.owl.ui.profiles.ProfilesManager;
import edu.stanford.smi.protegex.owl.ui.restrictions.RestrictionEditorPanel;
import edu.stanford.smi.protegex.owl.ui.restrictions.RestrictionKindRenderer;
import edu.stanford.smi.protegex.owl.ui.results.ResultsPanelManager;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import edu.stanford.smi.protegex.owl.util.ExpressionInfo;
import edu.stanford.smi.protegex.owl.util.ExpressionInfoUtils;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class PropertyRestrictionsTree extends SelectableTree implements Disposable {

    private OWLNamedClass cls;

    private ClassListener classListener = new ClassAdapter() {
        public void subclassAdded(RDFSClass cls, RDFSClass subclass) {
            refillLater();
        }


        public void subclassRemoved(RDFSClass cls, RDFSClass subclass) {
            refillLater();
        }


        public void superclassAdded(RDFSClass cls, RDFSClass superclass) {
            refillLater();
        }


        public void superclassRemoved(RDFSClass cls, RDFSClass superclass) {
            refillLater();
        }


        public void addedToUnionDomainOf(RDFSClass cls, RDFProperty property) {
            refillLater();
        }


        public void removedFromUnionDomainOf(RDFSClass cls, RDFProperty property) {
            refillLater();
        }
    };

    private AllowableAction createRestrictionAction = new AllowableAction("Create restriction...",
                                                                          OWLIcons.getCreateIcon(OWLIcons.OWL_RESTRICTION), this) {

        public void actionPerformed(ActionEvent e) {
            if (!isMixedClass() && (cls.isEditable() || cls.getDefinition() == null)) {
                createRestrictionFromDialog(null);
            }
        }


        public void onSelectionChange() {
            updateCreateRestrictionActionAllowed();
        }
    };


    private Action deleteRestrictionAction = new AllowableAction("Delete restriction",
                                                                 OWLIcons.getDeleteIcon(OWLIcons.OWL_RESTRICTION), this) {

        public void actionPerformed(ActionEvent e) {
            Instance sel = getSelectedInstance();
            OWLModel owlModel = (OWLModel) sel.getKnowledgeBase();
            try {
                owlModel.beginTransaction("Delete restriction " + sel.getBrowserText() + " from class " + cls.getBrowserText(), cls.getName());
                sel.delete();
                owlModel.commitTransaction();
            }
            catch (Exception ex) {
            	owlModel.rollbackTransaction();
                OWLUI.handleError(owlModel, ex);
            }
        }


        public void onSelectionChange() {
            Instance sel = getSelectedInstance();
            if (sel instanceof OWLRestriction) {
                if (sel.isEditable() && cls.equals(getHostCls((OWLRestriction) sel))) {
                    setAllowed(!isMixedClass() &&
                               (cls.isEditable() || cls.getDefinition() == null));
                    return;
                }
            }
            setAllowed(false);
        }
    };


    private DefaultTreeModel model;

    private RestrictionTreeNode newNode;

    private OWLModel owlModel;

    private OWLTextField owlTextField;

    private DefaultMutableTreeNode rootNode;

    private OWLSymbolPanel symbolPanel;


    public PropertyRestrictionsTree(OWLModel owlModel) {
        this(owlModel, null);
    }


    public PropertyRestrictionsTree(OWLModel owlModel, OWLNamedClass cls) {
        super(null);
        this.cls = cls;
        this.owlModel = owlModel;
        ComponentFactory.configureTree(this, new AbstractAction("View property/Edit restriction") {
            public void actionPerformed(ActionEvent e) {
                viewSelectedProperty();
            }
        });
        setLargeModel(true);
        rootNode = new DefaultMutableTreeNode("Root");
        setRootVisible(false);
        setShowsRootHandles(true);
        model = new DefaultTreeModel(rootNode);
        setModel(model);

        final int oldDelay = ToolTipManager.sharedInstance().getDismissDelay();
        setToolTipText(""); // Dummy to initialize the mechanism

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Instance instance = getSelectedInstance();
                    if (instance instanceof OWLRestriction && isEditable()) {
                        startEditing(e);
                    }
                }
            }

            public void mouseExited(MouseEvent e) {
                ToolTipManager.sharedInstance().setDismissDelay(oldDelay);
            }
        });

        addChildNodes();

        addSelectionListener(new SelectionListener() {
            public void selectionChanged(SelectionEvent event) {
                updateEditable();
            }
        });

        MyTreeCellEditor cellEditor = new MyTreeCellEditor();
        setCellEditor(cellEditor);
        cellEditor.addCellEditorListener(new CellEditorListener() {
            public void editingCanceled(ChangeEvent e) {
                hideSymbolPanel();
                if (newNode != null) {
                    refill();
                    newNode = null;
                }
            }


            public void editingStopped(ChangeEvent e) {
                hideSymbolPanel();
                newNode = null;
            }
        });

        symbolPanel = new OWLSymbolPanel(owlModel, true, true);
        owlTextField = new OWLTextField(owlModel, symbolPanel) {

            protected void checkExpression(String text) throws Throwable {
                RestrictionTreeNode node = (RestrictionTreeNode) getSelectedTreeNode();
                node.checkExpression(text);
            }

            public Dimension getMinimumSize() {
                Dimension dim = super.getMinimumSize();
                final JViewport parent = (JViewport) PropertyRestrictionsTree.this.getParent();
                int width = Math.max(150, parent.getWidth() - 60);
                return new Dimension(width, dim.height);
            }

            public Dimension getPreferredSize() {
                return getMinimumSize();
            }

            protected void stopEditing() {
                PropertyRestrictionsTree.this.stopEditing();
            }
        };
    }

    public String getToolTipText(MouseEvent event) {
        int row = getRowForLocation(event.getX(), event.getY());
        if (row >= 0) {
            TreePath path = getPathForRow(row);
            if (path != null) {
                Object last = path.getLastPathComponent();
                RDFResource res = null;
                if (last instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    res = (RDFResource) node.getUserObject();
                }
                if (res != null) {
                    ToolTipManager.sharedInstance().setDismissDelay(OWLTable.INFINITE_TIME);
                    return OWLUI.getOWLToolTipText(res);
                }
            }
        }
        return null;
    }

    private void addChildNodes() {
        Set<RDFProperty> doneProperties = new HashSet<RDFProperty>();
        if (cls != null && !cls.equals(cls.getOWLModel().getOWLThingClass())) {
            addRestrictionChildNodes(doneProperties);
            addDomainChildNodes(doneProperties);
            sortPropertyTreeNodes();
        }
    }
    
    private void addDomainChildNodes(Set<RDFProperty> doneProperties) {

        // Breadth-First Search into tree
        List<RDFSNamedClass> list = new ArrayList<RDFSNamedClass>();
        list.add(cls);
        Set<RDFSNamedClass> reachedClses = new HashSet<RDFSNamedClass>();
        boolean inherited = false;
        while (!list.isEmpty()) {

            RDFSNamedClass c = (RDFSNamedClass) list.get(0);
            reachedClses.add(c);
            list.remove(0);

            addNodesForDirectUnionDomainProperty(c, doneProperties, inherited);

            List superClses = getNextSuperclasses(c, reachedClses);
            list.addAll(superClses);
            inherited = true;
        }
    }
    
    private void addRestrictionChildNodes(Set<RDFProperty> doneProperties) {
        List<ExpressionInfo<OWLRestriction>> restrictions = new ArrayList<ExpressionInfo<OWLRestriction>>();

        Collection<RDFProperty> directProperties = new ArrayList<RDFProperty>();
        for (Slot s : ((Cls) cls).getDirectTemplateSlots()) {
            if (s instanceof RDFProperty) {
                directProperties.add((RDFProperty) s);
            }
        }
        for (RDFSNamedClass superClass : getNamedSuperclassesClosure(cls)) {
            if (superClass instanceof OWLNamedClass) {
                List<ExpressionInfo<OWLRestriction>> myRestrictions = 
                    ExpressionInfoUtils.getDirectContainingRestrictions((OWLNamedClass) superClass);
                restrictions.addAll(myRestrictions);
                if (superClass.equals(cls)) {
                    for (ExpressionInfo<OWLRestriction> myRestriction : myRestrictions) {
                        directProperties.add(myRestriction.getExpression().getOnProperty());
                    }
                }
            }
        }
        Map<RDFProperty, List<ExpressionInfo<OWLRestriction>>> propertyMap = 
            new HashMap<RDFProperty, List<ExpressionInfo<OWLRestriction>>>();
        for (ExpressionInfo<OWLRestriction> restrictionInfo : restrictions) {
            OWLRestriction restriction = restrictionInfo.getExpression();
            RDFProperty property = restriction.getOnProperty();
            List<ExpressionInfo<OWLRestriction>> propertyRestrictions = propertyMap.get(property);
            if (propertyRestrictions == null) {
                propertyRestrictions = new ArrayList<ExpressionInfo<OWLRestriction>>();
                propertyMap.put(property, propertyRestrictions);
            }
            propertyRestrictions.add(restrictionInfo);
        }
        for (RDFProperty property : propertyMap.keySet()) {
            if (!doneProperties.contains(property)) {
                doneProperties.add(property);
                PropertyTreeNode node = new PropertyTreeNode(this, 
                                                             cls, 
                                                             property, 
                                                             !directProperties.contains(property),
                                                             propertyMap.get(property));
                rootNode.add(node);
            }
        }
        expandPath(new TreePath(rootNode));
    }
    
    private Set<RDFSNamedClass> getNamedSuperclassesClosure(RDFSNamedClass c) {
      Set<RDFSNamedClass> results = new HashSet<RDFSNamedClass>();
      addNamedSuperclassesClosure(c, results);
      return results;
    }

    private void addNamedSuperclassesClosure(RDFSNamedClass c, Set<RDFSNamedClass> results) {
      OWLNamedClass owlThing = c.getOWLModel().getOWLThingClass();
      if (c.equals(owlThing)) {
        return;
      } else {
        results.add(c);
      }
      for (Cls cls : c.getDirectSuperclasses()) {
        if (cls instanceof RDFSNamedClass && !cls.equals(owlThing) && !results.contains(cls)) {
          addNamedSuperclassesClosure((RDFSNamedClass) cls, results);
        }
      }
    }

    void addCreateRestrictionActions(JPopupMenu menu) {
        if (!isMixedClass()) {
            OWLModel owlModel = cls.getOWLModel();
            Cls[] metaClses = ProfilesManager.getSupportedRestrictionMetaClses(owlModel);
            for (int i = 0; i < metaClses.length; i++) {
                Cls metaCls = metaClses[i];
                String restrictionName = RestrictionKindRenderer.getClsName(metaCls);
                String iconName = RestrictionKindRenderer.getClsIconName(metaCls);
                Icon icon = OWLIcons.getCreateIcon(iconName);
                menu.add(new CreateRestrictionAction(metaCls, restrictionName, icon));
            }
        }
    }


    private void addNavigationMenuItems(JPopupMenu menu, Set set) {
        if (!set.isEmpty()) {
            RDFResource[] instances = (RDFResource[]) set.toArray(new RDFResource[0]);
            Arrays.sort(instances);
            for (int i = 0; i < instances.length; i++) {
                final RDFResource resource = instances[i];
                menu.add(new AbstractAction("Navigate to " + resource.getBrowserText(),
                                            ProtegeUI.getIcon(resource)) {
                    public void actionPerformed(ActionEvent e) {
                        navigateTo(resource);
                    }
                });
            }
        }
    }


    private void addNodesForDirectUnionDomainProperty(RDFSNamedClass c, 
                                                      Set<RDFProperty> doneProperties, 
                                                      boolean inherited) {
        List<RDFProperty> properties = new ArrayList<RDFProperty>();
        for (Iterator it = c.getUnionDomainProperties().iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (!doneProperties.contains(property)) {
                if (!property.isAnnotationProperty()) {
                    properties.add(property);
                }
            }
        }
        for (Iterator it = properties.iterator(); it.hasNext();) {
            RDFProperty rdfProperty = (RDFProperty) it.next();
            PropertyTreeNode node = new PropertyTreeNode(this, cls, rdfProperty, inherited);
            rootNode.add(node);
            doneProperties.add(rdfProperty);
        }
    }


    private void addNodesForSubproperties(RDFProperty rdfProperty, Set doneProperties, Collection allowedProperties) {
        for (Iterator sit = rdfProperty.getSubproperties(true).iterator(); sit.hasNext();) {
            RDFProperty subProperty = (RDFProperty) sit.next();
            if (!doneProperties.contains(subProperty)) {
                if (allowedProperties.contains(subProperty)) {
                    PropertyTreeNode node = new PropertyTreeNode(this, cls, subProperty, true);
                    rootNode.add(node);
                    doneProperties.add(subProperty);
                }
            }
        }
    }


    private void addRestrictionToDefinition(OWLRestriction newRestriction) {
        OWLModel owlModel = cls.getOWLModel();
        AbstractRDFSClass definition = (AbstractRDFSClass) cls.getDefinition();
        if (definition instanceof OWLIntersectionClass) {
            OWLIntersectionClass intersectionClass = (OWLIntersectionClass) definition;
            String browserText = newRestriction.getBrowserText();
            if (intersectionClass.hasOperandWithBrowserText(browserText)) {
                displaySemanticError("The class " + browserText + " is already in the list.");
                return;
            }
            intersectionClass.addOperand(newRestriction);
        }
        else {
            OWLIntersectionClass intersectionClass = owlModel.createOWLIntersectionClass();
            intersectionClass.addOperand(definition.createClone());
            intersectionClass.addOperand(newRestriction);
            if (definition instanceof OWLAnonymousClass) {
                definition.delete();
            }
            definition.delete();
            cls.addEquivalentClass(intersectionClass);
        }
    }


    private void createRestrictionInline(Cls metaCls) {
        Instance sel = getSelectedInstance();
        if (sel != null) {
            RDFProperty property = sel instanceof RDFProperty ? (RDFProperty) sel : (RDFProperty) ((OWLRestriction) sel).getOnProperty();
            PropertyTreeNode propertyTreeNode = getPropertyTreeNode(property);
            expandPath(new TreePath(new Object[]{rootNode, propertyTreeNode}));
            newNode = new NewRestrictionTreeNode(metaCls, this);
            int index = -1;
            while (index + 1 < propertyTreeNode.getChildCount() &&
                   !propertyTreeNode.getRestrictionTreeNode(index + 1).isInherited()) {
                index++;
            }
            index++;
            propertyTreeNode.insert(newNode, index);
            model.nodesWereInserted(propertyTreeNode, new int[]{index});
            TreePath path = new TreePath(new Object[]{
                    rootNode,
                    propertyTreeNode,
                    newNode
            });
            setSelectionPath(path);
            scrollPathToVisible(path);            
            updateUI();
            startEditingAtPath(path);
            owlTextField.setCaretPosition(0);
        }
    }


    private void createRestrictionFromDialog(Cls restrictionMetaCls) {
        Instance sel = getSelectedInstance();
        RDFProperty property = null;
        if (sel != null) {
            property = sel instanceof RDFProperty ?
                    (RDFProperty) sel : ((OWLRestriction) sel).getOnProperty();
        }
        if (restrictionMetaCls == null) {
            if (property != null) {
                restrictionMetaCls = ((KnowledgeBase) property.getOWLModel()).getCls(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION);
            }
            else {
                restrictionMetaCls = cls.getOWLModel().getRDFSNamedClass(OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION);
            }
        }
        createRestrictionFromDialog(restrictionMetaCls, property);
    }


    private void createRestrictionFromDialog(Cls restrictionMetaCls, RDFProperty property) {
        OWLModel owlModel = cls.getOWLModel();
        try {
            owlModel.beginTransaction("Create restriction at " + cls.getBrowserText(), cls.getName());
            OWLRestriction newRestriction = RestrictionEditorPanel.showCreateDialog(this,
                                                                                    cls, restrictionMetaCls, property);
            if (newRestriction != null) {
                final String browserText = newRestriction.getBrowserText();
                Slot directSuperclassesSlot = ((KnowledgeBase) cls.getOWLModel()).getSlot(Model.Slot.DIRECT_SUPERCLASSES);
                if (((AbstractRDFSClass) cls).hasPropertyValueWithBrowserText(directSuperclassesSlot, browserText)) {
                    displaySemanticError("The restriction " + browserText + " is already in the list.");
                    newRestriction.delete();
                    return;
                }
                RDFSClass definition = cls.getDefinition();
                if (definition == null) {
                    cls.addSuperclass(newRestriction);
                }
                else {
                    addRestrictionToDefinition(newRestriction);
                }
            }
            owlModel.commitTransaction();           
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);
        }
    }


    public void displaySemanticError(String message) {
        ProtegeUI.getModalDialogFactory().showErrorMessageDialog(owlModel, message);
    }


    public void dispose() {
        removeListeners();
        disposeNodes();
    }


    private void disposeNodes() {
        PropertyTreeNode[] nodes = getPropertyTreeNodes();
        for (int i = 0; i < nodes.length; i++) {
            PropertyTreeNode node = nodes[i];
            node.dispose();
        }
        rootNode.removeAllChildren();
    }


    private void expandPropertyNodes(Set openProperties) {
        PropertyTreeNode[] newNodes = getPropertyTreeNodes();
        for (int i = 0; i < newNodes.length; i++) {
            PropertyTreeNode node = newNodes[i];
            if (openProperties.contains(node.getRDFProperty())) {
                expandPath(new TreePath(new Object[]{
                        rootNode,
                        node
                }));
            }
        }
    }


    public Action getCreateRestrictionAction() {
        return createRestrictionAction;
    }


    public Action getDeleteRestrictionAction() {
        return deleteRestrictionAction;
    }


    private Set getExpandedProperties() {
        Set expandedProperties = new HashSet();
        PropertyTreeNode[] oldNodes = getPropertyTreeNodes();
        for (int i = 0; i < oldNodes.length; i++) {
            PropertyTreeNode node = oldNodes[i];
            TreePath path = new TreePath(new Object[]{
                    rootNode,
                    node
            });
            if (isExpanded(path)) {
                expandedProperties.add(node.getRDFProperty());
            }
        }
        return expandedProperties;
    }


    private OWLNamedClass getHostCls(OWLRestriction restriction) {
        return restriction.getOwner();
    }


    // A helper method for addChildNodes()
    private List getNextSuperclasses(RDFSNamedClass c, Set reachedClses) {
        OWLNamedClass owlThing = c.getOWLModel().getOWLThingClass();
        List superClses = new ArrayList();
        for (Iterator it = c.getSuperclasses(false).iterator(); it.hasNext();) {
            Cls superCls = (Cls) it.next();
            if (superCls instanceof RDFSNamedClass &&
                !reachedClses.contains(superCls) &&
                !owlThing.equals(superCls)) {
                superClses.add(superCls);
            }
        }
        Collections.sort(superClses, new FrameComparator());
        return superClses;
    }


    private JLayeredPane getParentLayeredPane() {
        Component c = getParent();
        while (c != null && !(c instanceof JLayeredPane)) {
            c = c.getParent();
        }
        return (JLayeredPane) c;
    }


    public JPopupMenu getPopupMenu() {
        JPopupMenu menu = new JPopupMenu();
        RDFResource resource = (RDFResource) getSelectedInstance();
        if (resource instanceof RDFProperty) {
            addCreateRestrictionActions(menu);
        }
        else if (resource instanceof OWLRestriction) {
            if (resource instanceof OWLQuantifierRestriction) {
                OWLQuantifierRestriction qr = (OWLQuantifierRestriction) resource;
                if (qr.getFiller() instanceof RDFSClass) {
                    RDFSClass cls = (RDFSClass) qr.getFiller();
                    Set set = new HashSet();
                    cls.getNestedNamedClasses(set);
                    addNavigationMenuItems(menu, set);
                }
            }
            RestrictionTreeNode treeNode = (RestrictionTreeNode) getSelectedTreeNode();
            final RDFSClass definingClass = treeNode.getInheritedFromClass();
            if (treeNode.isInherited()) {
                menu.add(new AbstractAction("Navigate to defining class (" +
                                            definingClass.getBrowserText() + ")", ProtegeUI.getIcon(definingClass)) {
                    public void actionPerformed(ActionEvent e) {
                        navigateTo(definingClass);
                    }
                });
            }
            menu.add(deleteRestrictionAction);
        }
        ResourceActionManager.addResourceActions(menu, this, resource);
        if (menu.getSubElements().length > 0) {
            return menu;
        }
        else {
            return null;
        }
    }


    private PropertyTreeNode getPropertyTreeNode(RDFProperty property) {
        PropertyTreeNode[] nodes = getPropertyTreeNodes();
        for (int i = 0; i < nodes.length; i++) {
            PropertyTreeNode node = nodes[i];
            if (property.equals(node.getRDFProperty())) {
                return node;
            }
        }
        return null;
    }


    public PropertyTreeNode[] getPropertyTreeNodes() {
        int count = rootNode.getChildCount();
        PropertyTreeNode[] results = new PropertyTreeNode[count];
        for (int i = 0; i < count; i++) {
            results[i] = (PropertyTreeNode) rootNode.getChildAt(i);
        }
        return results;
    }


    public Instance getSelectedInstance() {
        if (getSelectionCount() == 1) {
            DefaultMutableTreeNode treeNode = getSelectedTreeNode();
            return (Instance) treeNode.getUserObject();
        }
        else {
            return null;
        }
    }


    public Collection getSelectedInstances() {
        Collection sels = new ArrayList();
        TreePath[] paths = getSelectionPaths();
        if (paths != null) {
            for (int i = 0; i < paths.length; i++) {
                TreePath path = paths[i];
                if (path.getPathCount() > 0) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    sels.add(node.getUserObject());
                }
            }
        }
        return sels;
    }


    private DefaultMutableTreeNode getSelectedTreeNode() {
        TreePath path = getSelectionPath();
        if (path != null && path.getPathCount() > 0) {
            return (DefaultMutableTreeNode) path.getLastPathComponent();
        }
        else {
            return null;
        }
    }


    public Collection getSelection() {
        return getSelectedInstances();
    }


    public void hideSymbolPanel() {
        if (symbolPanel != null) {
            Container parent = symbolPanel.getParent();
            if (parent != null) {
                parent.remove(symbolPanel);
                Container top = getTopLevelAncestor();
                if (top == null) {
                    for (Component c = parent; c.getParent() != null;) {
                        c = c.getParent();
                        c.repaint();
                    }
                }
                else if (top instanceof JFrame) {
                    ((JFrame) top).getContentPane().repaint();
                }
                else if (top instanceof JWindow) {
                    ((JWindow) top).getContentPane().repaint();
                }
                else if (top instanceof JDialog) {
                    ((JDialog) top).getContentPane().repaint();
                }
                else {
                    top.repaint();
                }
            }
        }
    }


    public void init(boolean displayRestrictions, boolean hideGlobalCharacteristics) {
        setCellRenderer(new PropertyRestrictionsTreeRenderer(displayRestrictions, hideGlobalCharacteristics));
    }


    public boolean isEditable() {
        return super.isEditable() && !isMixedClass();
    }


    public boolean isMixedClass() {
        RDFSClass definition = cls.getDefinition();
        return definition != null;
        /*
        if (definition != null) {
            Cls rootCls = definition.getKnowledgeBase().getRootCls();
            Collection cs = new HashSet();
            for (Iterator it = cls.getEquivalentClasses().iterator(); it.hasNext();) {
                Cls d = (Cls) it.next();
                if (d instanceof OWLIntersectionClass) {
                    cs.addAll(((OWLIntersectionClass) d).getOperands());
                }
                else {
                    cs.add(d);
                }
            }
            for (Iterator it = cls.getDirectSuperclasses().iterator(); it.hasNext();) {
                Cls superCls = (Cls) it.next();
                if (superCls instanceof RDFSClass &&
                        !owlThing.equals(superCls) &&
                        !cls.hasEquivalentClass((RDFSClass) superCls) &&
                        !cs.contains(superCls)) {
                    return true;
                }
            }
        }
        return false;
        */
    }


    protected void navigateTo(RDFResource instance) {
        OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(this);
        if (tab != null && instance instanceof RDFSNamedClass) {
            tab.setSelectedCls((RDFSNamedClass) instance);
        }
        else {
            ResultsPanelManager.showHostResource(instance);
        }
    }


    /**
     * Opens the restriction nodes if there is enough space in the visible area.
     */
    public void openNodesIfPossible() {
        if (getParent() instanceof JViewport) {
            JViewport viewPort = (JViewport) getParent();
            int rowHeight = getRowHeight();
            int i = rootNode.getChildCount() - 1;
            PropertyTreeNode recentNode = null;
            while (i >= 0 && getRowCount() * rowHeight < viewPort.getExtentSize().height) {
                PropertyTreeNode node = (PropertyTreeNode) rootNode.getChildAt(i);
                if (node.getChildCount() > 0) {
                    TreePath path = new TreePath(new Object[]{
                            rootNode,
                            node
                    });
                    expandPath(path);
                    recentNode = node;
                }
                i--;
            }
            if (recentNode != null && getRowCount() * rowHeight > viewPort.getExtentSize().height) {
                TreePath path = new TreePath(new Object[]{
                        rootNode,
                        recentNode
                });
                collapsePath(path);
            }
        }
    }


    void refill() {
        Set expandedProperties = getExpandedProperties();
        disposeNodes();
        addChildNodes();
        model.nodeStructureChanged(rootNode);
        expandPropertyNodes(expandedProperties);
    }


    private boolean refilling = false;


    void refillLater() {
        if (!refilling) {
            refilling = true;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    refill();
                    refilling = false;
                }
            });
        }
    }


    private void removeListeners() {
        if (cls != null) {
            cls.removeClassListener(classListener);
        }
    }


    public void setCls(OWLNamedClass cls) {
        if (cls != this.cls) {
            removeListeners();
            disposeNodes();
            this.cls = cls;
            addChildNodes();
            model.nodeStructureChanged(rootNode);
            if (cls != null) {
                cls.addClassListener(classListener);
            }
        }
    }


    public void setSelectedRestriction(OWLRestriction restriction) {
        RDFProperty property = restriction.getOnProperty();
        PropertyTreeNode propertyTreeNode = getPropertyTreeNode(property);
        if (propertyTreeNode != null) {
            final OldRestrictionTreeNode n = propertyTreeNode.getRestrictionTreeNode(restriction.getBrowserText());
            if (n != null) {
                TreePath path = new TreePath(new Object[]{
                        rootNode,
                        propertyTreeNode,
                        n
                });
                expandPath(path);
                setSelectionPath(path);
            }
        }
    }


    private void showSymbolPanel(RDFProperty property, Cls restrictionMetaCls, boolean errorFlag) {
        JLayeredPane desktop = getParentLayeredPane();
        Rectangle r = getRowBounds(getSelectionRows()[0]);
        Point tableLocation = getLocationOnScreen();
        Point desktopLocation = desktop.getLocationOnScreen();
        r.translate(tableLocation.x - desktopLocation.x, tableLocation.y - desktopLocation.y);
        Dimension pref = symbolPanel.getPreferredSize();
        int x = getX() + r.x;
        if (pref.width > r.width) {
            x = Math.max(0, getX() + r.x - (pref.width - r.width));
        }
        int y = r.y + r.height + 4;
        if (y + symbolPanel.getHeight() >= desktop.getHeight()) {
            y = r.y - symbolPanel.getHeight();
        }
        symbolPanel.setLocation(x, y);
        symbolPanel.displayError((Throwable) null);
        symbolPanel.setErrorFlag(errorFlag);
        symbolPanel.enableActions(property, restrictionMetaCls);
        desktop.setLayer(symbolPanel, JLayeredPane.POPUP_LAYER.intValue());
        desktop.add(symbolPanel);
    }


    private void sortPropertyTreeNodes() {
        int count = rootNode.getChildCount();
        PropertyTreeNode[] nodes = new PropertyTreeNode[count];
        for (int i = 0; i < count; i++) {
            nodes[i] = (PropertyTreeNode) rootNode.getChildAt(i);
        }
        Arrays.sort(nodes);
        rootNode.removeAllChildren();
        for (int i = 0; i < nodes.length; i++) {
            PropertyTreeNode node = nodes[i];
            rootNode.add(node);
        }
    }


    private void startEditing(MouseEvent e) {
        startEditingAtPath(getSelectionPath());
        int mouseX = e.getX() - getRowBounds(getSelectionRows()[0]).x;
        mouseX -= owlTextField.getX();
        String str = owlTextField.getText();
        FontMetrics fm = owlTextField.getFontMetrics(owlTextField.getFont());
        for (int index = 1; index < str.length(); index++) {
            if (fm.stringWidth(str.substring(0, index)) >= mouseX) {
                owlTextField.setCaretPosition(index - 1);
                break;
            }
        }
    }


    public void startEditingAtPath(TreePath path) {
        RestrictionTreeNode node = (RestrictionTreeNode) getSelectedTreeNode();
        Cls metaCls = node.getRestrictionMetaCls();
        super.startEditingAtPath(path);
        String metaClsName = metaCls.getName();
        OWLModel owlModel = (OWLModel) metaCls.getKnowledgeBase();
        if (ProfilesManager.isFeatureSupported(owlModel, OWLProfiles.Qualified_Cardinality_Restrictions) ||
            OWLNames.Cls.ALL_VALUES_FROM_RESTRICTION.equals(metaClsName) ||
            OWLNames.Cls.SOME_VALUES_FROM_RESTRICTION.equals(metaClsName) ||
            OWLNames.Cls.HAS_VALUE_RESTRICTION.equals(metaClsName)) {
            showSymbolPanel(node.getParentNode().getRDFProperty(), metaCls, false);
        }
    }


    private void updateCreateRestrictionActionAllowed() {
        boolean allowed = !isMixedClass() &&
                          (cls.isEditable() || cls.getDefinition() == null);
        createRestrictionAction.setAllowed(allowed);
    }


    private void updateEditable() {
        DefaultMutableTreeNode selNode = getSelectedTreeNode();
        boolean editable = false;
        if (selNode instanceof RestrictionTreeNode) {
            RestrictionTreeNode rtn = (RestrictionTreeNode) selNode;
            editable = !rtn.isInherited();
        }
        setEditable(editable);
    }


    private void viewSelectedProperty() {
        Instance instance = getSelectedInstance();
        if (instance instanceof RDFProperty) {
            ProtegeUI.show((RDFProperty) instance);
        }
    }


    private class CreateRestrictionAction extends AbstractAction {

        private Cls metaCls;


        CreateRestrictionAction(Cls metaCls, String restrictionName, Icon icon) {
            super("Create " + restrictionName + " restriction", icon);
            this.metaCls = metaCls;
        }


        public void actionPerformed(ActionEvent e) {
            createRestrictionInline(metaCls);
        }
    }


    private class MyTreeCellEditor extends DefaultTreeCellEditor {

        MyTreeCellEditor() {
            super(PropertyRestrictionsTree.this, new DefaultTreeCellRenderer());
        }


        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
            Component c = owlTextField;
            owlTextField.setSymbolEditorHandler(new SymbolEditorHandler() {
                public void stopEditing() {
                    MyTreeCellEditor.this.stopCellEditing();
                }


                public void cancelEditing() {
                    MyTreeCellEditor.this.cancelCellEditing();
                }
            });
            symbolPanel.setSymbolEditor(owlTextField);
            RestrictionTreeNode restrictionTreeNode = ((RestrictionTreeNode) value);
            owlTextField.setText(restrictionTreeNode.getFillerText());
            JPanel result = new JPanel(new BorderLayout(1, 0));
            result.setOpaque(false);
            result.add(BorderLayout.CENTER, c);
            Icon icon = restrictionTreeNode.getIcon();
            result.add(BorderLayout.WEST, new JLabel(icon));
            Dimension pref = result.getPreferredSize();
            result.setSize(new Dimension(pref.width + 100, pref.height));
            return result;
        }


        public Object getCellEditorValue() {
            return owlTextField.getText();
        }
    }
}
