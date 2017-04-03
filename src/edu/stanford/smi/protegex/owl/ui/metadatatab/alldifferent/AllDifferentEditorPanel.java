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

package edu.stanford.smi.protegex.owl.ui.metadatatab.alldifferent;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.ui.FrameRenderer;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueAdapter;
import edu.stanford.smi.protegex.owl.model.event.PropertyValueListener;
import edu.stanford.smi.protegex.owl.ui.OWLLabeledComponent;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.ResourceComparator;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.WidgetUtilities;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.List;

/**
 * @author Daniel Stoeckli <stoeckli@smi.stanford.edu>
 */
public class AllDifferentEditorPanel extends JPanel implements Disposable {

    private Action addAction = new AbstractAction("Add Instance",
            OWLIcons.getAddIcon(OWLIcons.RDF_INDIVIDUAL)) {

        public void actionPerformed(ActionEvent arg0) {
            addInstance();
        }
    };

    private Action addInstancesOfClsAction = new AbstractAction("Add all Instances from a given class",
            OWLIcons.getAddIcon(OWLIcons.PRIMITIVE_OWL_CLASS)) {

        public void actionPerformed(ActionEvent arg0) {
            addInstancesOfCls();
        }
    };

    private AllDifferentMemberChangedListener allDifferentMemberChangedListener;

    private OWLAllDifferent currentOWLAllDifferent;

    private boolean isAllDifferentItemSelected;

    private JList list;

    private DefaultListModel listModel;

    private PropertyValueListener membersListener = new PropertyValueAdapter() {
        public void propertyValueChanged(RDFResource resource, RDFProperty property, Collection oldValues) {
            updateValues();
        }
    };

    private OWLModel owlModel;


    private Action removeAction = new AbstractAction("Remove Instance",
            OWLIcons.getRemoveIcon(OWLIcons.RDF_INDIVIDUAL)) {

        public void actionPerformed(ActionEvent arg0) {
            removeInstance();
        }
    };


    private Action viewAction = new AbstractAction("View Instance", OWLIcons.getViewIcon()) {

        public void actionPerformed(ActionEvent arg0) {
            Instance instance = (Instance) list.getSelectedValue();
            owlModel.getProject().show(instance);
        }
    };


    public AllDifferentEditorPanel(OWLModel owlModel) {

        this.owlModel = owlModel;
        isAllDifferentItemSelected = false;

        viewAction.setEnabled(false);
        addAction.setEnabled(false);
        addInstancesOfClsAction.setEnabled(false);
        removeAction.setEnabled(false);

        listModel = new DefaultListModel();

        list = new JList(listModel);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                enableViewAndRemoveAction();
            }
        });

        FrameRenderer renderer = FrameRenderer.createInstance();
        list.setCellRenderer(renderer);

        LabeledComponent lc = new OWLLabeledComponent("Distinct Members of Selected Set",
                new JScrollPane(list));
        WidgetUtilities.addViewButton(lc, viewAction);
        lc.addHeaderButton(addAction);
        lc.addHeaderButton(addInstancesOfClsAction);
        lc.addHeaderButton(removeAction);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
    }


    private void addInstance() {
        if (!isAllDifferentItemSelected) {
            ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                    "Select an All-Different Element first", "No Item Selected");
            return;
        }

        Collection instances = ProtegeUI.getSelectionDialogFactory().selectResourcesByType(this, owlModel,
                Collections.singleton(owlModel.getOWLThingClass()), "Add individuals");

        Collection members = new HashSet(currentOWLAllDifferent.getDistinctMembers());
        members.addAll(instances);
        List newValues = new ArrayList(members);
        Collections.sort(newValues, new ResourceComparator());
        currentOWLAllDifferent.setDistinctMembers(newValues);
        allDifferentMemberChangedListener.allDifferentMemberChanged();
        updateValues();
    }


    private void addInstancesOfCls() {
        Cls cls = ProtegeUI.getSelectionDialogFactory().selectClass(this, owlModel);
        if (cls != null) {
            for (Iterator it = cls.getDirectInstances().iterator(); it.hasNext();) {
                Instance instance = (Instance) it.next();
                if (instance instanceof RDFResource) {
                    currentOWLAllDifferent.addDistinctMember((RDFIndividual) instance);
                }
            }
            allDifferentMemberChangedListener.allDifferentMemberChanged();
            updateValues();
        }
    }


    private void enableViewAndRemoveAction() {
        boolean oneIsSelected = list.getSelectedIndex() >= 0;
        viewAction.setEnabled(oneIsSelected);
        removeAction.setEnabled(oneIsSelected);
    }


    void disableAddAction() {
        addAction.setEnabled(false);
        addInstancesOfClsAction.setEnabled(false);
    }


    public void dispose() {
        if (currentOWLAllDifferent != null) {
            currentOWLAllDifferent.removePropertyValueListener(membersListener);
        }
    }


    private void removeInstance() {
        RDFIndividual instance = (RDFIndividual) list.getSelectedValue();

        // no item is selected
        if (instance == null) {
            ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                    "Select an All-Different Member first", "No Item Selected");
            return;
        }

        currentOWLAllDifferent.removeDistinctMember(instance);
        updateValues();
        allDifferentMemberChangedListener.allDifferentMemberChanged();
    }


    protected void setIsAllDifferentItemSelected(boolean isAllDifferentItemSelected) {
        this.isAllDifferentItemSelected = isAllDifferentItemSelected;
        addAction.setEnabled(true);
        addInstancesOfClsAction.setEnabled(true);
    }


    public void setAllDifferentMemberChangedListener(AllDifferentMemberChangedListener memberListener) {
        this.allDifferentMemberChangedListener = memberListener;
    }


    void setSelectedAllDifferentInstance(OWLAllDifferent OWLAllDifferent) {
        if (currentOWLAllDifferent != null) {
            currentOWLAllDifferent.removePropertyValueListener(membersListener);
        }
        currentOWLAllDifferent = OWLAllDifferent;
        if (currentOWLAllDifferent != null) {
            currentOWLAllDifferent.addPropertyValueListener(membersListener);
        }
        updateValues();
    }


    private void updateValues() {
        listModel.removeAllElements();
        if (currentOWLAllDifferent != null) {
            Iterator it = currentOWLAllDifferent.listDistinctMembers();
            while (it.hasNext()) {
                RDFIndividual instance = (RDFIndividual) it.next();
                listModel.addElement(instance);
            }
        }
    }
}
