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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Collections;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.ui.Finder;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.ui.ParentChildRoot;
import edu.stanford.smi.protege.ui.SelectInstancesPanel;
import edu.stanford.smi.protege.util.ComponentFactory;
import edu.stanford.smi.protege.util.ComponentUtilities;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protege.util.LazyTreeRoot;
import edu.stanford.smi.protege.util.SelectableTree;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class SelectResourcesPanel extends SelectInstancesPanel {

    private final static String DIRECT_ASSERTED = "Direct Asserted Instances";
    private final static String DIRECT_INFERRED = "Direct Inferred Instances";
    private final static String ALL_ASSERTED = "All Asserted Instances";
    private final static String CLASS_HIERARCHY = "Class Hierarchy";    
    private final static String ALL_INFERRED = "All Inferred Instances";

    private JComboBox instancesComboBox;
    private JTree _clsHierarchyTree;    
    private LabeledComponent instanceLabelComponent;    
    private Finder _instanceFinder;
    
    public SelectResourcesPanel(OWLModel owlModel, Collection classes) {
        this(owlModel, classes, false);
    }


    public SelectResourcesPanel(OWLModel owlModel, Collection classes, boolean allowsMultipleSelection) {
        super(owlModel, classes);
        if (!allowsMultipleSelection) {
            _instanceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
    }

    @Override
	protected LabeledComponent createClsesLabeledComponent(KnowledgeBase kb, Collection clses) {
        LabeledComponent lc = super.createClsesLabeledComponent(kb, clses);            
        return lc;
    }

    @Override
	protected JComboBox createDirectAllInstanceComboBox() {
        super.createDirectAllInstanceComboBox(); // Only not to break stuff
        instancesComboBox = ComponentFactory.createComboBox();
        instancesComboBox.addItem(DIRECT_ASSERTED);
        instancesComboBox.addItem(DIRECT_INFERRED);
        instancesComboBox.addItem(ALL_ASSERTED);
        instancesComboBox.addItem(ALL_INFERRED);
        instancesComboBox.addItem(CLASS_HIERARCHY);
        instancesComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                loadInstances();
            }
        });
        return instancesComboBox;
    }

    protected JComponent createClassHierarchyTree() {
        LazyTreeRoot root = new ParentChildRoot(Collections.EMPTY_LIST);
        _clsHierarchyTree = ComponentFactory.createSelectableTree(null, root);
        _clsHierarchyTree.setRootVisible(false);
        _clsHierarchyTree.setShowsRootHandles(false);
        FrameRenderer renderer = FrameRenderer.createInstance();
        renderer.setDisplayDirectInstanceCount(false);
        _clsHierarchyTree.setCellRenderer(renderer);
        return _clsHierarchyTree;
    }    
        
    @Override
	protected LabeledComponent createInstanceLabeledComponent() {
    	instanceLabelComponent = createOWLIndividualsLabledComponent();
        createClassHierarchyTree();
        // set the instance finder so that we can disable it later when
        // the class hierarchy tree is shown
        _instanceFinder = (Finder) instanceLabelComponent.getFooterComponent();
        return instanceLabelComponent;
    }
  
    protected LabeledComponent createOWLIndividualsLabledComponent() {
    	return super.createInstanceLabeledComponent();
    }
        
    
    @Override
	protected Collection getInstances(Cls cls) {
        Object selectedItem = instancesComboBox.getSelectedItem();
        
        // toggle the visibility of the instance list and the class hierarcy tree
        boolean treeVisible = CLASS_HIERARCHY.equals(selectedItem);
    	JScrollPane scrollPane = (JScrollPane) instanceLabelComponent.getCenterComponent();
        if (treeVisible && (scrollPane.getViewport().getView() != _clsHierarchyTree)) {
        	scrollPane.setViewportView(_clsHierarchyTree);
        	_instanceFinder.setEnabled(false);
        } else if (!treeVisible && (scrollPane.getViewport().getView() != _instanceList)) {
        	scrollPane.setViewportView(_instanceList);
        	_instanceFinder.setEnabled(true);
        }
        
        if (DIRECT_INFERRED.equals(selectedItem)) {
            if (cls instanceof RDFSClass) {
                RDFSClass c = (RDFSClass) cls;
                return c.getInferredInstances(false);
            }
        }
        else if (ALL_ASSERTED.equals(selectedItem)) {
            return getInstances(cls, false);
        }
        else if (ALL_INFERRED.equals(selectedItem)) {
            if (cls instanceof RDFSClass) {
                RDFSClass c = (RDFSClass) cls;
                return c.getInferredInstances(true);
            }
        } 
        else if (CLASS_HIERARCHY.equals(selectedItem)) {
        	LazyTreeRoot root = new ParentChildRoot(cls);
        	((SelectableTree)_clsHierarchyTree).setRoot(root);
        	// select the first class/instance
        	_clsHierarchyTree.setSelectionInterval(0, 0);
        	// return empty list of instances since the instance list is hidden
        	return Collections.EMPTY_LIST;
        }
    	return getInstances(cls, true);
    }
    
    protected Collection getInstances(Cls cls, boolean direct) {
    	return direct ? cls.getDirectInstances() : cls.getInstances();
    }
    
    @Override
	public Collection getSelection() {
        Object selectedItem = instancesComboBox.getSelectedItem();
        // if the class hierarchy is shown, then return the selection 
        // from the tree not the instance list 
        if (CLASS_HIERARCHY.equals(selectedItem)) {
        	return ComponentUtilities.getSelection(_clsHierarchyTree);
        } else {
        	return getInstanceSelection();
        }
    }
    
    protected Collection getInstanceSelection() {
    	return super.getSelection();
    }
    
}
