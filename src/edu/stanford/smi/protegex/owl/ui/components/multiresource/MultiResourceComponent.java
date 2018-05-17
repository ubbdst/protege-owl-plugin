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

package edu.stanford.smi.protegex.owl.ui.components.multiresource;

import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.components.AddResourceAction;
import edu.stanford.smi.protegex.owl.ui.components.AddResourcesWithBrowserTextAction;
import edu.stanford.smi.protegex.owl.ui.components.AddablePropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class MultiResourceComponent extends AddablePropertyValuesComponent {

    private Action createAction;
    private Action addAction;
    private MultiResourceList list;
     

    private Action removeAction = new AbstractAction("Remove selected values", OWLIcons.getRemoveIcon(OWLIcons.RDF_INDIVIDUAL)) {
        public void actionPerformed(ActionEvent e) {
            handleRemove();
        }
    };


    private Action moveToTrashAction = new AbstractAction("Move to trash",  OWLIcons.getTrashClassIcon()) {
        public void actionPerformed(ActionEvent e) {
            handleMoveToTrash();
        }
    };

    public MultiResourceComponent(RDFProperty predicate, boolean symmetric) {
    	this(predicate, symmetric, null);
    }
    
    
    public MultiResourceComponent(RDFProperty predicate, boolean symmetric, String label) {
       this(predicate, symmetric, label, false);
    }


    public MultiResourceComponent(RDFProperty predicate, boolean symmetric, String label, boolean isReadOnly) {
    	 super(predicate, label, isReadOnly);
    	     	 
         list = new MultiResourceList(predicate, symmetric);
         list.addListSelectionListener(new ListSelectionListener() {
             public void valueChanged(ListSelectionEvent e) {
                 updateActions();
             }
         });
         
         OWLLabeledComponent lc = new OWLLabeledComponent((label == null ? getLabel():label), new JScrollPane(list));
         createAction = createCreateAction();
         if (createAction != null) {
             lc.addHeaderButton(createAction);
         }
         
         addAction = createAddAction(symmetric);
         if (addAction != null) {
             lc.addHeaderButton(addAction);
         }
         
         lc.addHeaderButton(removeAction);
         lc.addHeaderButton(moveToTrashAction);
         add(BorderLayout.CENTER, lc);
         updateActions();
	}

	protected AddResourceAction createAddAction(boolean symmetric) {
        return new AddResourcesWithBrowserTextAction(this, symmetric);
    }


    protected Action createCreateAction() {
        return new AbstractAction("Create new resource...", OWLIcons.getCreateIndividualIcon(OWLIcons.RDF_INDIVIDUAL)) {
            public void actionPerformed(ActionEvent e) {
                list.handleCreate();
            }
        };
    }


    protected Object[] getSelectedObjects() {
        return list.getSelectedValues();
    }


    protected void handleRemove() {
        list.handleRemove();
    }

    /**
     * Handles moving the instance to Trash class
     */
    protected void handleMoveToTrash() {
        list.handleMovingToTrash();
    }


    public boolean isCreateEnabled() {
        return !isEnumerationProperty();
    }


    @Override
	public void setSubject(RDFResource subject) {
        super.setSubject(subject);
        list.getListModel().setSubject(subject);
        updateActions();
    }


    public void valuesChanged() {
        list.getListModel().updateValues();
    }


    private void updateActions() {    	
    	boolean isReadOnly = isReadOnly();
        if (createAction != null) {
            createAction.setEnabled(!isReadOnly && isCreateEnabled());
        }
        removeAction.setEnabled(!isReadOnly && list.isRemoveEnabled());
        moveToTrashAction.setEnabled(!isReadOnly && list.isRemoveEnabled());
        addAction.setEnabled(!isReadOnly);        
    }
    
    
    
}
