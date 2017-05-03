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

package edu.stanford.smi.protegex.owl.ui.components.annotations;

import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.ui.components.triples.TriplesTable;
import edu.stanford.smi.protegex.owl.ui.components.triples.TriplesTableModel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AddTodoAction extends AbstractAction {

    private TriplesTable table;


    public AddTodoAction(TriplesTable table) {
        super("Add TODO list item", OWLIcons.getCreateIcon("TodoAnnotation"));
        this.table = table;
    }


    public void actionPerformed(ActionEvent e) {
        TriplesTableModel tableModel = table.getTableModel();
        OWLModel owlModel = tableModel.getOWLModel();
        OWLDatatypeProperty todoProperty = owlModel.getTodoAnnotationProperty();
        String prefix = owlModel.getTodoAnnotationPrefix();
        String value = prefix + ": ";
        Collection existingValues = tableModel.getSubject().getPropertyValues(todoProperty);
        while (existingValues.contains(value)) {
            value += "-";
        }
        tableModel.getSubject().addPropertyValue(todoProperty, value);
        int row = tableModel.getPropertyValueRow(todoProperty, value);
        table.getSelectionModel().setSelectionInterval(row, row);
        table.editCell(row);
    }
}
