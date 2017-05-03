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

package edu.stanford.smi.protegex.owl.ui.components.triples;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class DeleteTripleAction extends AbstractAction {

    private TriplesTable table;


    public DeleteTripleAction(TriplesTable table) {
        this(table, "Delete selected value", OWLIcons.getDeleteIcon());
    }


    public DeleteTripleAction(TriplesTable table, String name, Icon icon) {
        super(name, icon);
        this.table = table;
    };


    public void actionPerformed(ActionEvent e) {    	
    	if (table.getCellEditor() != null)
			table.getCellEditor().stopCellEditing();
    	
        table.removeEditor();
        int row = table.getSelectedRow();
        if (row >= 0) {
        	table.getTableModel().deleteRow(row);
        }
    }
}
