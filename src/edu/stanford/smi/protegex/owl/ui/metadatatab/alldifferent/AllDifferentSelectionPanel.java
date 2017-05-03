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

import edu.stanford.smi.protege.resource.Icons;
import edu.stanford.smi.protege.util.Disposable;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLAllDifferent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.model.event.ClassAdapter;
import edu.stanford.smi.protegex.owl.model.event.ClassListener;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Daniel Stoeckli  <stoeckli@smi.stanford.edu>
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AllDifferentSelectionPanel extends JPanel
        implements AllDifferentMemberChangedListener, Disposable {

    /**
     * A listener to the OWLAllDifferent metaclass which detected creating
     * and deleting AllDifferentInstances
     */
    private ClassListener clsListener = new ClassAdapter() {
        public void instanceAdded(RDFSClass cls, RDFResource instance) {
            listModel.addElement(instance);
        }


        public void instanceRemoved(RDFSClass cls, RDFResource instance) {
            listModel.removeElement(instance);
        }
    };

    private Action createAction =
            new AbstractAction("Create owl:AllDifferent", Icons.getCreateIcon()) {

                public void actionPerformed(ActionEvent arg0) {

                    OWLAllDifferent adi = owlModel.createOWLAllDifferent();
                    list.setSelectedValue(adi, true);
                }
            };

    private Action deleteAction =
            new AbstractAction("Delete owl:AllDifferent", OWLIcons.getDeleteIcon()) {

                public void actionPerformed(ActionEvent actionEvent) {

                    OWLAllDifferent ali = (OWLAllDifferent) list.getSelectedValue();

                    // no item is selected
                    if (ali == null) {
                        editorPanel.setIsAllDifferentItemSelected(false);
                        ProtegeUI.getModalDialogFactory().showMessageDialog(owlModel,
                                "Select an All-Different Element first", "No Item Selected");
                        return;
                    }

                    ali.delete();

                    // remove from the jlist in editorPanel
                    editorPanel.setSelectedAllDifferentInstance(null);
                }
            };

    private AllDifferentEditorPanel editorPanel;

    /**
     * The JList displaying the AllDifferentInstances
     */
    private JList list;

    /**
     * A ListModel containing the AllDifferentInstances
     */
    private DefaultListModel listModel;

    private OWLModel owlModel;


    private ListSelectionListener selectionListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent e) {
            OWLAllDifferent adi = (OWLAllDifferent) list.getSelectedValue();

            // at least one item in the list
            if (adi != null) {
                editorPanel.setIsAllDifferentItemSelected(true);
                editorPanel.setSelectedAllDifferentInstance(adi);
                deleteAction.setEnabled(true);
            }
            else { // no items in the list
                editorPanel.setSelectedAllDifferentInstance(null);
                editorPanel.disableAddAction();
                deleteAction.setEnabled(false);
            }
        }
    };


    public AllDifferentSelectionPanel(OWLModel owlModel, AllDifferentEditorPanel editorPanel) {

        this.owlModel = owlModel;
        this.editorPanel = editorPanel;

        listModel = new DefaultListModel();
        Collection adis = owlModel.getOWLAllDifferents();
        for (Iterator it = adis.iterator(); it.hasNext();) {
            OWLAllDifferent OWLAllDifferent = (OWLAllDifferent) it.next();
            listModel.addElement(OWLAllDifferent);
        }
        owlModel.getOWLAllDifferentClass().addClassListener(clsListener);

        deleteAction.setEnabled(false);

        list = new JList(listModel);
        list.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list,
                                                          Object value,
                                                          int index,
                                                          boolean isSelected,
                                                          boolean cellHasFocus) {
                OWLAllDifferent adi = (OWLAllDifferent) value;
                return super.getListCellRendererComponent(list, adi.getBrowserText(), index, isSelected, cellHasFocus);
            }
        });
        list.addListSelectionListener(selectionListener);
        JScrollPane scrollPane = new JScrollPane(list);
        LabeledComponent lc = new LabeledComponent("Sets of \"all different\" Individuals", scrollPane);

        lc.addHeaderButton(createAction);
        lc.addHeaderButton(deleteAction);
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, lc);
    }


    public void dispose() {
        owlModel.getOWLAllDifferentClass().removeClassListener(clsListener);
    }


    protected void updateListUI() {
        list.updateUI();
    }


    public void allDifferentMemberChanged() {
        list.updateUI();
    }
}
