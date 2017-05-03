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

package edu.stanford.smi.protegex.owl.ui.conditions;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.cls.OWLClassesTab;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableModel;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

import java.util.Arrays;
import java.util.Collection;

/**
 * An Action to add a named class as a superclass into the conditions table.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class AddNamedClassAction extends ResourceSelectionAction
        implements ConditionsTableConstants {

    private ConditionsTable table;


    AddNamedClassAction(ConditionsTable table) {
        super("Add named class...", OWLIcons.getAddIcon("PrimitiveClass"));
        this.table = table;
    }


    public void resourceSelected(RDFResource resource) {
        final Cls editedCls = table.getEditedCls();
        table.selectNecessaryIfNothingSelected();
        OWLClassesTab tab = OWLClassesTab.getOWLClassesTab(table);
        int row = table.addRow((Cls) resource);
        if (tab != null) {
            tab.ensureClsSelected(editedCls, row);
        }
    }


    public Collection getSelectableResources() {
        Collection<OWLNamedClass> clses = table.getOWLModel().getUserDefinedOWLNamedClasses();
        clses.add(table.getOWLModel().getOWLThingClass());
        clses.remove(((OWLTableModel) table.getModel()).getEditedCls());
        ConditionsTableModel tableModel = (ConditionsTableModel) table.getModel();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Cls cls = tableModel.getClass(i);
            if (cls instanceof OWLNamedClass) {
                clses.remove(cls);
            }
        }
        return clses;
    }


    public RDFResource pickResource() {
        return (RDFResource) ProtegeUI.getSelectionDialogFactory().selectClass(table, table.getOWLModel(),
                "Select a named class to add");
    }
}
