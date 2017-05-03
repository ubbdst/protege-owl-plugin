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

package edu.stanford.smi.protegex.owl.ui.classform.component.property;

import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.OWLSomeValuesFrom;
import edu.stanford.smi.protegex.owl.ui.existential.Existential;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DeleteRowAction extends AbstractAction {

    private PropertyFormTable table;


    public DeleteRowAction(PropertyFormTable table) {
        super("Delete selected rows", OWLIcons.getDeleteIcon());
        this.table = table;
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                updateEnabled();
            }
        });
        updateEnabled();
    }


    public void actionPerformed(ActionEvent e) {
        OWLObjectProperty prop = (OWLObjectProperty) table.getTableModel().getProperty();
        OWLNamedClass subject = table.getTableModel().getNamedClass();
        OWLNamedClass filler = (OWLNamedClass) table.getSelectedResource();
        OWLSomeValuesFrom restr =
                Existential.getDirectExistentialRelation(subject, prop, filler);
        subject.removeSuperclass(restr);
        //@@TODO update closure if required
    }


    // Public for testing purposes
    public boolean isEnabledFor(int[] selectedRows) {
        return selectedRows.length > 0;
    }


    private void updateEnabled() {
        int[] selectedRows = table.getSelectedRows();
        setEnabled(isEnabledFor(selectedRows));
    }
}
