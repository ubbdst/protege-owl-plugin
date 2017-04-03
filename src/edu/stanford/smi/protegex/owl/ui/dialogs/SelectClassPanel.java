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

package edu.stanford.smi.protegex.owl.ui.dialogs;

import edu.stanford.smi.protege.ui.ParentChildRoot;
import edu.stanford.smi.protege.util.*;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.RDFSNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLModel;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.cls.ClassNameTreeCellEditor;
import edu.stanford.smi.protegex.owl.ui.cls.ClassTree;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.search.ClassTreeFinder;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A general purpose component for selecting a class from a tree that also allows
 * classes to be added (and their names edited inline)
 *
 * @author Nick Drummond, Medical Informatics Group, University of Manchester
 *         18-Jan-2006
 */
public class SelectClassPanel extends SelectableContainer{

    private final ClassTree tree;

    private final OWLModel owlModel;

    private final Collection defaultRoots;

    private static final String SHOW_ALL = "Show all classes";
    private static final String SHOW_LIMITED = "Show sensible classes";

    public SelectClassPanel(OWLModel owlModel, Collection rootClses,
                            boolean multiple, boolean editable) {

        super();

        this.owlModel = owlModel;
        this.defaultRoots = rootClses;

        this.tree = new ClassTree(null, new ParentChildRoot(rootClses));
        setSelectable(tree);

        tree.setSelectionRow(0);
        tree.setLargeModel(true);
        tree.setAutoscrolls(true);
        tree.setEditable(false);
        tree.setShowsRootHandles(true);

        tree.setCellRenderer(ResourceRenderer.createInstance());

        if (multiple){
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
        }
        else{
            tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        }

        LabeledComponent lc = new LabeledComponent(null, new JScrollPane(tree));
        lc.setPreferredSize(new Dimension(400, 300));

        if (!rootClses.contains(owlModel.getOWLThingClass())){

            lc.addHeaderToggleButton(new AbstractAction(SHOW_ALL, OWLIcons.getImageIcon(OWLIcons.TOP)){

                boolean showingAll = false;

                public void actionPerformed(ActionEvent e) {

                    LazyTreeRoot newRoot = null;
                    JToggleButton b = (JToggleButton)e.getSource();

                    if (showingAll){
                        newRoot = new ParentChildRoot(defaultRoots);
                        b.setToolTipText(SHOW_ALL);
                    }
                    else{
                        OWLNamedClass owlThing = SelectClassPanel.this.owlModel.getOWLThingClass();
                        Collection clses = Collections.singleton(owlThing);
                        newRoot = new ParentChildRoot(clses);
                        b.setToolTipText(SHOW_LIMITED);
                    }

                    tree.setRoot(newRoot);
                    tree.setSelectionRow(0);
                    showingAll = !showingAll;
                }
            });

            if (editable){
                lc.addHeaderSeparator();
            }
        }

        if (editable){
            tree.setCellEditor(new ClassNameTreeCellEditor());
            tree.addFocusListener(new FocusAdapter(){
                public void focusLost(FocusEvent e) {
                    tree.stopEditing();
                }
            });

            lc.addHeaderButton(new CreateSubclassAction(tree));

            lc.addHeaderButton(new CreateSiblingClassAction(tree));
        }

        ClassTreeFinder finder = new ClassTreeFinder(owlModel, tree);
        lc.add(BorderLayout.SOUTH, finder);

        add(lc);
    }

    public Selectable getSelectable() {
        return tree;
    }

    public Collection getSelection() {
        return tree.getSelection();
    }

    // @@TODO replace with CreateSiblingClassAction when API upheave
    class CreateSubclassAction extends AllowableAction{

        public CreateSubclassAction(ClassTree selectableTree) {
            super("Create Subclass",
                  OWLIcons.getCreateIcon(OWLIcons.SUB_CLASS),
                  selectableTree);
            onSelectionChange();
        }

        public void actionPerformed(ActionEvent e) {

            OWLNamedClass superclass = ((OWLNamedClass)CollectionUtilities.getFirstItem(getSelection()));
            OWLModel owlModel = superclass.getOWLModel();
            try {
            	String name = owlModel.createNewResourceName(AbstractOWLModel.DEFAULT_CLASS_NAME);
            	
                owlModel.beginTransaction("Create subclass of class " + superclass.getBrowserText(), name);
                
                RDFSClass superclassType = superclass.getRDFType();
                if(superclassType == null) {
                    superclassType = superclass.getProtegeType();
                }
                RDFSNamedClass cls = owlModel.createRDFSNamedClass(name,
                                                                   Collections.singleton(superclass),
                                                                   superclassType);

                ClassTree tree = (ClassTree)getSelectable();
                OWLUI.selectResource(cls, tree);
                int row = tree.getSelectionRows()[0];
                tree.setEditable(true);
                tree.startEditingAtPath(tree.getPathForRow(row));

                owlModel.commitTransaction();				
			} catch (Exception ex) {
				owlModel.rollbackTransaction();
				OWLUI.handleError(owlModel, ex);
			}
        }

        public void onSelectionChange() {        
        	RDFSClass superclass = ((RDFSClass)CollectionUtilities.getFirstItem(getSelection()));
            setAllowed(superclass instanceof RDFSNamedClass);
        }
    }

    // @@TODO replace with CreateSiblingClassAction when API upheave
    class CreateSiblingClassAction extends AllowableAction{

        public CreateSiblingClassAction(ClassTree selectableTree) {
            super("Create Sibling Class",
                  OWLIcons.getCreateIcon(OWLIcons.SIBLING_CLASS),
                  selectableTree);
            onSelectionChange();
        }

        public void actionPerformed(ActionEvent e) {
            OWLNamedClass sibling = ((OWLNamedClass)CollectionUtilities.getFirstItem(getSelection()));
            Collection parents = sibling.getNamedSuperclasses();
            if (!parents.isEmpty()) {
                OWLModel owlModel = sibling.getOWLModel();              
                try {
                	String name = owlModel.createNewResourceName(AbstractOWLModel.DEFAULT_CLASS_NAME);
                	
                    owlModel.beginTransaction("Create sibling of class " + sibling.getBrowserText(), name);                    
                    RDFSClass siblingType = sibling.getRDFType();
                    if(siblingType == null) {
                        siblingType = sibling.getProtegeType();
                    }
                    RDFSNamedClass cls = owlModel.createRDFSNamedClass(name, parents, siblingType);
                    if (cls instanceof OWLNamedClass) {
                        for (Iterator it = parents.iterator(); it.hasNext();) {
                            RDFSNamedClass s = (RDFSNamedClass) it.next();
                            ((OWLNamedClass) cls).addInferredSuperclass(s);
                        }
                    }

                    ClassTree tree = (ClassTree)getSelectable();
                    OWLUI.selectResource(cls, tree);
                    int row = tree.getSelectionRows()[0];
                    tree.setEditable(true);
                    tree.startEditingAtPath(tree.getPathForRow(row));                    
					owlModel.commitTransaction();
				} catch (Exception ex) {
					//TODO: check if exception is not treated somewhere else in code
					owlModel.rollbackTransaction();
					OWLUI.handleError(owlModel, ex);
				}
            }
        }

        public void onSelectionChange() {
            RDFSClass siblingclass = ((RDFSClass)CollectionUtilities.getFirstItem(getSelection()));
            ClassTree tree = (ClassTree)getSelectable();
            LazyTreeRoot root = (LazyTreeRoot)tree.getModel().getRoot();
            Collection roots = (Collection)root.getUserObject();
            setAllowed((siblingclass instanceof RDFSNamedClass) &&
                       (!roots.contains(siblingclass)));
        }
    }
}
