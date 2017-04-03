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

package edu.stanford.smi.protegex.owl.ui.properties.range;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.ResourceRenderer;
import edu.stanford.smi.protegex.owl.ui.clsdesc.ClassDescriptionEditorComponent;
import edu.stanford.smi.protegex.owl.ui.code.SymbolEditorComponent;
import edu.stanford.smi.protegex.owl.ui.code.SymbolErrorDisplay;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTable;

import javax.swing.*;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class UnionRangeClassesTable extends OWLTable {

    private UnionRangeClassesTableModel tableModel;


    public UnionRangeClassesTable(UnionRangeClassesTableModel tableModel, OWLModel owlModel) {
        super(tableModel, owlModel, true);
        this.tableModel = tableModel;
        getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }


    void createAndEditRow() {
        int row = getOWLTableModel().addEmptyRow(-1);
        editExpression(row);
    }


    protected ResourceRenderer createOWLFrameRenderer() {
        return new ResourceRenderer() {
            protected Icon getClsIcon(Cls cls) {
                if (tableModel.isInherited((RDFSClass) cls)) {
                    ImageIcon icon = ((RDFSClass) cls).getImageIcon();
                    return OWLIcons.getInheritedIcon(icon, OWLIcons.CLASS_FRAME);
                }
                else {
                    return super.getClsIcon(cls);
                }
            }
        };
    }


    protected SymbolEditorComponent createSymbolEditorComponent(OWLModel model,
                                                                SymbolErrorDisplay errorDisplay) {
        return new ClassDescriptionEditorComponent(model, errorDisplay, false);
    }


    public void hideSymbolPanel() {
        super.hideSymbolPanel();
        getOWLTableModel().removeEmptyRow();
    }

    /*public Component prepareEditor(TableCellEditor editor, int row, int column) {
       String expression = (String) getModel().getValueAt(row, column);
       showSymbolPanel(expression.length() == 0);
       return super.prepareEditor(editor, row, column);
   } */
}
