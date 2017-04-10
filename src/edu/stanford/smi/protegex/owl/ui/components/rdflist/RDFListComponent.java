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

package edu.stanford.smi.protegex.owl.ui.components.rdflist;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Frame;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.components.AbstractPropertyValuesComponent;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.util.UUIDInstanceURI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

/**
 * The default widget for properties of type rdf:List (or a subclass of rdf:List).
 * It looks similar to a normal InstanceListWidget but operates on a linked RDF list.
 * <p/>
 * It is possible to subclass this widget class to operate on simulated lists with first
 * and rest properties.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RDFListComponent extends AbstractPropertyValuesComponent implements Disposable {

    private ResourceSelectionAction addAction = new ResourceSelectionAction("Add existing value...",
            OWLIcons.getAddIcon(OWLIcons.RDF_INDIVIDUAL)) {

        public void resourceSelected(RDFResource resource) {
            addItem(resource);
        }


        public Collection getSelectableResources() {
            OWLModel owlModel = getOWLModel();
            Collection values;
            if (getSubject() instanceof OWLNamedClass) {
                values = owlModel.getUserDefinedOWLNamedClasses();
            }
            else if (getSubject() instanceof RDFSNamedClass) {
                values = owlModel.getUserDefinedRDFSNamedClasses();
            }
            else if (getSubject() instanceof OWLProperty) {
                values = owlModel.getVisibleUserDefinedOWLProperties();
            }
            else {
                Collection clses = getListClass().getUnionRangeClasses(getFirstProperty());
                if (clses.size() > 0) {
                    values = new HashSet();
                    for (Iterator it = clses.iterator(); it.hasNext();) {
                        Cls cls = (Cls) it.next();
                        values.addAll(cls.getInstances());
                    }
                }
                else {
                    values = new ArrayList(owlModel.getOWLIndividuals());
                }
                // values.addAll(owlModel.getRDFExternalResourceClass().getInstances(false));
            }
            return values;
        }


        public RDFResource pickResource() {
            if (getSubject() instanceof RDFSClass) {
                String title = "Select a class to add";
                return (RDFResource) ProtegeUI.getSelectionDialogFactory().selectClass(RDFListComponent.this,
                        getOWLModel(), title);
            }
            else if (getSubject() instanceof OWLProperty) {
                String title = "Select a property to add";
                Collection properties = getSelectableResources();
                return ProtegeUI.getSelectionDialogFactory().selectProperty(RDFListComponent.this, getSubject().getOWLModel(),
                        properties, title);
            }
            else {
                OWLModel owlModel = getOWLModel();
                owlModel.getRDFUntypedResourcesClass().setVisible(true);
                Collection classes = getListClass().getUnionRangeClasses(getFirstProperty());
                RDFResource result = ProtegeUI.getSelectionDialogFactory().selectResourceByType(RDFListComponent.this, owlModel, classes);
                owlModel.getRDFUntypedResourcesClass().setVisible(false);
                return result;
            }
        }
    };


    private Action createAction = new AbstractAction("Create new value...",
            OWLIcons.getCreateIndividualIcon(OWLIcons.RDF_INDIVIDUAL)) {
        public void actionPerformed(ActionEvent e) {
            handleCreateAction();
        }
    };

    private Action deleteAction = new AbstractAction("Delete selected values",
            OWLIcons.getDeleteIcon(OWLIcons.RDF_INDIVIDUAL)) {
        public void actionPerformed(ActionEvent e) {
            handleDelete();
        }
    };

    private JList list;

    private DefaultListModel listModel;

    private Action moveDownAction = new AbstractAction("Move selected value down",
            OWLIcons.getDownIcon()) {
        public void actionPerformed(ActionEvent e) {
            int selIndex = list.getSelectedIndex();
            swapValues(selIndex, selIndex + 1);
        }


        public void onSelectionChange() {
            boolean allowed = false;
            Collection sel = getSelection();
            if (sel.size() == 1) {
                int selIndex = list.getSelectedIndex();
                if (selIndex < list.getModel().getSize() - 1) {
                    allowed = true;
                }
            }
            //setAllowed(allowed);
        }
    };


    private Action moveUpAction = new AbstractAction("Move selected value up",
            OWLIcons.getUpIcon()) {
        public void actionPerformed(ActionEvent e) {
            int selIndex = list.getSelectedIndex();
            swapValues(selIndex, selIndex - 1);
        }


        public void onSelectionChange() {
            boolean allowed = false;
            Collection sel = getSelection();
            if (sel.size() == 1) {
                int selIndex = list.getSelectedIndex();
                if (selIndex > 0) {
                    allowed = true;
                }
            }
            //setAllowed(allowed);
        }
    };

    /**
     * The List of ListInstances where this is currently registered to
     */
    private List registeredTo = new ArrayList();

    private Action removeAction = new AbstractAction("Remove selected value",
            OWLIcons.getRemoveIcon(OWLIcons.RDF_INDIVIDUAL)) {
        public void actionPerformed(ActionEvent e) {
            int selIndex = list.getSelectedIndex();
            removeListValue(getSubject(), getPredicate(), getListResource(), selIndex);
        }


        public void onSelectionChange() {
            // setAllowed(getSelection().size() == 1);
        }
    };


    /**
     * A FrameListener to all nodes of the current list
     */
    private PropertyValueListener valueListener = new PropertyValueAdapter() {
        public void browserTextChanged(RDFResource resource) {
            repaint();
        }


        public void propertyValueChanged(RDFResource resource, RDFProperty property, Collection oldValues) {
            unregisterPropertyValueListener();
            registerPropertyValueListener();
            refill();
        }
    };


    private Action viewAction = new AbstractAction("View selected values", OWLIcons.getViewIcon()) {
        public void actionPerformed(ActionEvent e) {
            Collection sel = getSelection();
            for (Iterator it = sel.iterator(); it.hasNext();) {
                Object o = it.next();
                if (o instanceof RDFResource) {
                    getOWLModel().getProject().show((RDFResource) o);
                }
            }
        }
    };


    public RDFListComponent(RDFProperty predicate) {
    	this(predicate, null);
    }

    public RDFListComponent(RDFProperty predicate, String label) {
        this(predicate, label, false);
    }
    
    public RDFListComponent(RDFProperty predicate, String label, boolean isReadOnly) {
        super(predicate, label, isReadOnly);
        addAction.setEnabled(false);
        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.setCellRenderer(new ResourceRenderer());
        list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateActions();
            }
        });
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewAction.actionPerformed(null);
                }
            }
        });
        OWLLabeledComponent lc = new OWLLabeledComponent((label == null ? getLabel():label), new JScrollPane(list));
        lc.addHeaderButton(createAction);
        lc.addHeaderButton(addAction);
        lc.addHeaderButton(moveUpAction);
        lc.addHeaderButton(moveDownAction);
        lc.addHeaderButton(removeAction);
        lc.addHeaderButton(deleteAction);
        add(lc);
    }


    private void addItem(Frame frame) {
        if (getListResource() == null) {
            RDFSNamedClass type = (RDFSNamedClass) getSubject().getProtegeType();
            Cls metaCls = (Cls) type.getUnionRangeClasses(getPredicate()).iterator().next();
            Instance newList = (Instance) metaCls.createDirectInstance(null);
            getSubject().setPropertyValue(getPredicate(), newList);
        }
        appendListValue(getListResource(), (Instance) frame);
    }


    public void appendListValue(RDFResource li, Instance value) {
        if (getFirst(li) == null) {
            li.setPropertyValue(getFirstProperty(), value);
        }
        else if (getRest(li) == null || getNil().equals(getRest(li))) {
            RDFResource newRest = li.getProtegeType().createInstance(null);
            newRest.setPropertyValue(getFirstProperty(), value);
            li.setPropertyValue(getRestProperty(), newRest);
        }
        else {
            appendListValue(getRest(li), value);  // Recursion into tail
        }
    }


    public void dispose() {
        unregisterPropertyValueListener();
    }


    protected Instance getFirst(Instance li) {
        Slot restSlot = getFirstProperty();
        final Object value = li.getDirectOwnSlotValue(restSlot);
        if (value instanceof Instance) {
            return (Instance) value;
        }
        else {
            // TODO: Support other types of (primitive) values
            return null; // getKnowledgeBase().getRootCls();
        }
    }


    protected RDFProperty getFirstProperty() {
        return getOWLModel().getRDFFirstProperty();
    }


    protected RDFSNamedClass getListClass() {
        Collection clses = ((RDFSNamedClass) getSubjectType()).getUnionRangeClasses(getPredicate());
        return (RDFSNamedClass) clses.iterator().next();
    }


    private RDFResource getListElement(int index) {
        RDFResource element = getListResource();
        for (int i = 0; i < index; i++) {
            element = (RDFResource) element.getPropertyValue(getRestProperty());
        }
        return element;
    }


    public RDFResource getListResource() {
        return (RDFResource) getSubject().getPropertyValue(getPredicate());
    }


    protected RDFResource getNil() {
        return getOWLModel().getRDFNil();
    }


    protected RDFResource getRest(RDFResource li) {
        return (RDFResource) li.getPropertyValue(getRestProperty());
    }


    protected RDFProperty getRestProperty() {
        return getOWLModel().getRDFRestProperty();
    }


    public int getRowCount() {
        return list.getModel().getSize();
    }


    public Collection getSelection() {
        return Arrays.asList(list.getSelectedValues());
    }


    protected void handleCreateAction() {
        RDFSNamedClass listClass = getListClass();
        RDFProperty firstProperty = getOWLModel().getRDFProperty(RDFNames.Slot.FIRST);
        Collection clses = listClass.getUnionRangeClasses(firstProperty);
        RDFSNamedClass cls = ProtegeUI.getSelectionDialogFactory().selectClass(this, getOWLModel(), clses,
                "Select type of new list element");
        if (cls != null) {
            //Generate unique name for the instance
            final String name = new UUIDInstanceURI(getOWLModel()).generateUniqueInstanceName();
            RDFResource instance = cls.createInstance(name);
            if (instance instanceof RDFSClass) {
                RDFSClass newclass = (RDFSClass) instance;
                if (newclass.getSuperclassCount() == 0) {
                    newclass.addSuperclass(getOWLModel().getOWLThingClass());
                }
            }
            getOWLModel().getProject().show(instance);
            addItem(instance);
        }
    }


    private void handleDelete() {
        Collection sels = new HashSet(getSelection());
        for (Iterator it = sels.iterator(); it.hasNext();) {
            Instance instance = (Instance) it.next();
            instance.delete();
        }
    }


    public boolean isAddEnabled() {
        RDFResource list = getListResource();
        if (list == null) {
            return true;
        }
        else {
            return getOWLModel().getTripleStoreModel().
                    isActiveTriple(getSubject(), getPredicate(), list);
        }
    }


    public boolean isCreateEnabled() {
        return isAddEnabled();
    }


    public boolean isDeleteEnabled() {
        if (isRemoveEnabled()) {
            int[] sels = list.getSelectedIndices();
            for (int i = 0; i < sels.length; i++) {
                int sel = sels[i];
                Object value = listModel.get(sel);
                if (value instanceof RDFResource) {
                    RDFResource resource = (RDFResource) value;
                    if (!resource.isEditable()) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }


    public boolean isMoveDownEnabled() {
        if (isRemoveEnabled()) {
            int[] sels = list.getSelectedIndices();
            if (sels.length == 1) {
                return sels[0] < getRowCount() - 1;
            }
        }
        return false;
    }


    public boolean isMoveUpEnabled() {
        if (isRemoveEnabled()) {
            int[] sels = list.getSelectedIndices();
            if (sels.length == 1) {
                return sels[0] > 0;
            }
        }
        return false;
    }


    public boolean isRemoveEnabled() {
        RDFResource li = getListResource();
        if (li != null) {
            if (getOWLModel().getTripleStoreModel().
                    isEditableTriple(getSubject(), getPredicate(), li)) {
                int[] sels = list.getSelectedIndices();
                return sels.length > 0;
            }
        }
        return false;
    }


    private void refill() {
        listModel.removeAllElements();
        RDFResource li = getListResource();
        while (li != null && !li.equals(getNil())) {
            Object first = getFirst(li);
            if (first != null) {
                listModel.addElement(first);
            }
            li = getRest(li);
        }
    }


    /**
     * Registers the valueListener at all nodes in the list.
     */
    private void registerPropertyValueListener() {
        RDFResource li = getListResource();
        RDFResource nil = getNil();
        while (li != null && !nil.equals(li)) {
            li.addPropertyValueListener(valueListener);
            registeredTo.add(li);
            li = getRest(li);
        }
    }


    private void removeListValue(RDFResource resource, RDFProperty property, RDFResource li, int index) {
        if (index == 0) {
            RDFResource rest = getRest(li);
            li.setPropertyValue(getRestProperty(), null);
            resource.setPropertyValue(property, rest);
            li.delete();
        }
        else {
            RDFResource pred = null;
            while (index > 0) {
                index--;
                pred = li;
                li = getRest(li);
            }
            RDFResource rest = getRest(li);
            li.setPropertyValue(getRestProperty(), null);
            pred.setPropertyValue(getRestProperty(), rest);
            li.delete();
        }
    }


    public void setSelectedRow(int row) {
        list.setSelectedIndex(row);
    }


    public void setSubject(RDFResource subject) {
        unregisterPropertyValueListener();
        super.setSubject(subject);
        registerPropertyValueListener();
        updateActions();
    }


    private void swapValues(int a, int b) {
        Instance ia = getListElement(a);
        Instance ib = getListElement(b);
        Object ea = ia.getDirectOwnSlotValue(getFirstProperty());
        Object eb = ib.getDirectOwnSlotValue(getFirstProperty());
        ia.setDirectOwnSlotValue(getFirstProperty(), eb);
        ib.setDirectOwnSlotValue(getFirstProperty(), ea);
        list.setSelectedIndex(b);
    }


    private void updateActions() {
    	boolean editable = !isReadOnly();
    	
        addAction.setEnabled(editable && isAddEnabled());
        createAction.setEnabled(editable && isCreateEnabled());
        deleteAction.setEnabled(editable && isDeleteEnabled());
        moveDownAction.setEnabled(editable && isMoveDownEnabled());
        moveUpAction.setEnabled(editable && isMoveUpEnabled());
        removeAction.setEnabled(editable && isRemoveEnabled());
    }


    private void unregisterPropertyValueListener() {
        for (Iterator it = registeredTo.iterator(); it.hasNext();) {
            RDFResource listInstance = (RDFResource) it.next();
            listInstance.removePropertyValueListener(valueListener);
        }
        registeredTo.clear();
    }


    public void valuesChanged() {
        unregisterPropertyValueListener();
        registerPropertyValueListener();
        refill();
        updateActions();
    }
}
