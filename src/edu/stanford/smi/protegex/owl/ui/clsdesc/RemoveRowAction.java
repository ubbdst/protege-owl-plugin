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

package edu.stanford.smi.protegex.owl.ui.clsdesc;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.owltable.AbstractOWLTableAction;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableModel;

import java.awt.event.ActionEvent;
import java.util.Arrays;

/**
 * A OWLTableAction that removes the selected row from the table.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class RemoveRowAction extends AbstractOWLTableAction {

    private ClassDescriptionTable table;


    RemoveRowAction(ClassDescriptionTable table) {
        super("Remove selected class from list", OWLIcons.getRemoveIcon(OWLIcons.PRIMITIVE_OWL_CLASS));
        this.table = table;
    }


    public void actionPerformed(ActionEvent e) {
        OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(table);
        int[] sels = table.getSelectedRows();
        Arrays.sort(sels);
        final OWLNamedClass editedClass = table.getEditedCls();
        for (int i = sels.length - 1; i >= 0; i--) {
            int selIndex = sels[i];
            OWLTableModel tableModel = (OWLTableModel) table.getModel();
            Cls cls = tableModel.getClass(selIndex);
            if (tableModel.isCellEditable(selIndex, tableModel.getSymbolColumnIndex()) ||
                    cls.equals(cls.getKnowledgeBase().getRootCls())) {
                tableModel.deleteRow(selIndex);
                table.getSelectionModel().setSelectionInterval(selIndex, selIndex);
            }
        }
        if (tab != null) {
            tab.ensureClsSelected(editedClass, -1);
        }
    }


    public boolean isEnabledFor(RDFSClass cls, int rowIndex) {
        ClassDescriptionTableModel tableModel = (ClassDescriptionTableModel) table.getModel();
        return cls != null && tableModel.isRemoveEnabledFor(cls);
    }
}
