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

package edu.stanford.smi.protegex.owl.ui.navigation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;

import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.SelectionEvent;
import edu.stanford.smi.protege.util.SelectionListener;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ModelAdapter;
import edu.stanford.smi.protegex.owl.model.event.ModelListener;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

/**
 * An object that manages the navigation history across in a certain context.
 * This object stores all visited resources in a list so that back and forward
 * buttons can be used to navigate.  It also registers itself as a ModelListener
 * to the OWLModel to be able to delete entries from the list if they have been deleted.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class NavigationHistoryManager extends DefaultListModel implements ComboBoxModel, Disposable {

    private ResourceSelectionAction backAction = new ResourceSelectionAction("Back",
            OWLIcons.getNavigateBackIcon()) {
        @Override
		public void actionPerformed(ActionEvent e) {
            back();
        }


        public void resourceSelected(RDFResource resource) {
            while (!resource.equals(getResource(index))) {
                index--;
            }
            setSelectedResourceByCurrentIndex();
            updateActionStatus();
        }


        @Override
		public Collection getSelectableResources() {
            Set set = new HashSet();
            for (int i = index - 1; i >= 0; i--) {
                set.add(getResource(i));
            }
            List subList = new ArrayList(set);
            Collections.sort(subList);
            return subList;
        }


        @Override
		public RDFResource pickResource() {
            return null;
        }
    };

    private ModelListener deleteListener = new ModelAdapter() {
        @Override
		public void classDeleted(RDFSClass cls) {
            if (resourcesSet.contains(cls)) {
                removeResource(cls);
            }
        }


        @Override
		public void individualDeleted(RDFResource resource) {
            if (resourcesSet.contains(resource)) {
                removeResource(resource);
            }
        }


        @Override
		public void propertyDeleted(RDFProperty property) {
            if (resourcesSet.contains(property)) {
                removeResource(property);
            }
        }
    };


    private ResourceSelectionAction forwardAction = new ResourceSelectionAction("Forward",
            OWLIcons.getNavigateForwardIcon()) {
        @Override
		public void actionPerformed(ActionEvent e) {
            forward();
        }


        public void resourceSelected(RDFResource resource) {
            while (!resource.equals(getResource(index))) {
                index++;
            }
            setSelectedResourceByCurrentIndex();
            updateActionStatus();
        }


        @Override
		public Collection getSelectableResources() {
            Set set = new HashSet();
            for (int i = index; i < getSize(); i++) {
                set.add(getResource(i));
            }
            List subList = new ArrayList(set);
            Collections.sort(subList);
            return subList;
        }


        @Override
		public RDFResource pickResource() {
            return null;  // Never called
        }
    };

    /**
     * The index at which the most recent entry was added
     */
    private int index = -1;

    private Set listeners = new HashSet();

    private NavigationHistorySelectable navigationHistorySelectable;

    private OWLModel owlModel;

    private Set resourcesSet = new HashSet();

    private SelectionListener selectionListener = new SelectionListener() {
        public void selectionChanged(SelectionEvent event) {
            if (!(event instanceof ProgrammaticSelectionEvent)) {
                handleSelectionChanged();
            }
        }
    };


    /**
     * Constructs a new NavigationHistoryManager.  Immediately after construction
     * this should be supplied with an initial entry, using <CODE>add</CODE>.
     *
     * @see #add
     */
    public NavigationHistoryManager(NavigationHistorySelectable nhc, OWLModel owlModel) {
        this.navigationHistorySelectable = nhc;
        this.owlModel = owlModel;
        this.owlModel.addModelListener(deleteListener);
        updateActionStatus();
        navigationHistorySelectable.addSelectionListener(selectionListener);
    }


    public void add(RDFResource resource) {
        index++;
        add(index, resource);
        while (getSize() > index + 1) {
            removeResourceByIndex(index + 1);
        }
        if (!resourcesSet.contains(resource)) {
            resourcesSet.add(resource);
        }
        updateActionStatus();
    }


    public void addIndexListener(ActionListener listener) {
        listeners.add(listener);
    }


    public void back() {
        if (index > 0) {
            index--;
            setSelectedResourceByCurrentIndex();
            updateActionStatus();
        }
    }


    public void dispose() {
        navigationHistorySelectable.removeSelectionListener(selectionListener);
        navigationHistorySelectable.dispose();
        owlModel.removeModelListener(deleteListener);
    }


    public void forward() {
        if (index < getSize() - 1) {
            index++;
            setSelectedResourceByCurrentIndex();
            updateActionStatus();
        }
    }


    public ResourceSelectionAction getBackAction() {
        return backAction;
    }


    public RDFResource getResource(int index) {
        return (RDFResource) getElementAt(index);
    }


    public ResourceSelectionAction getForwardAction() {
        return forwardAction;
    }


    public int getSelectedIndex() {
        return index;
    }


    private void handleSelectionChanged() {
        Collection selection = navigationHistorySelectable.getSelection();
        if (selection.size() == 1) {
            Object object = selection.iterator().next();
            if (object instanceof RDFResource) {
                RDFResource resource = (RDFResource) object;
                add(resource);
                updateActionStatus();
            }
        }
    }


    private void removeResource(RDFResource resource) {
        resourcesSet.remove(resource);
        for (int i = getSize() - 1; i > index; i--) {
            RDFResource r = getResource(i);
            if (r.equals(resource)) {
                remove(i);
            }
        }
        for (int i = index; i >= 0; i--) {
            RDFResource r = getResource(i);
            if (r.equals(resource)) {
                remove(i);
                index--;
            }
        }
    }


    private void removeResourceByIndex(int index) {
        RDFResource resource = getResource(index);
        remove(index);
        resourcesSet.remove(resource);
        if (contains(resource)) {
            resourcesSet.add(resource);
        }
    }


    private void setSelectedResourceByCurrentIndex() {
        RDFResource resource = getResource(index);
        navigationHistorySelectable.navigateToResource(resource);
    }


    private void updateActionStatus() {
        backAction.setEnabled(index > 0);
        forwardAction.setEnabled(index < getSize() - 1);

        for (Iterator it = new ArrayList(listeners).iterator(); it.hasNext();) {
            ActionListener listener = (ActionListener) it.next();
            listener.actionPerformed(null);
        }
    }

    // ComboBoxModel stuff


    public Object getSelectedItem() {
        if (index >= 0 && index < getSize()) {
            return getResource(index);
        }
        else {
            return null;
        }
    }


    public void setSelectedItem(Object anItem) {
        if (!anItem.equals(getResource(index))) {
            index = 0;
            for (int i = getSize() - 1; i >= 0; i--) {
                RDFResource frame = getResource(i);
                if (frame.equals(anItem)) {
                    index = i;
                    setSelectedResourceByCurrentIndex();
                    break;
                }
            }
            updateActionStatus();
        }
    }
}
