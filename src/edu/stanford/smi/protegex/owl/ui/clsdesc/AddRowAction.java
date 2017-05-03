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
import edu.stanford.smi.protege.ui.FrameComparator;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.owltable.OWLTableModel;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;

/**
 * An Action that adds a class from a list into the table.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
class AddRowAction extends ResourceSelectionAction {

    private ClassDescriptionTable table;

    private boolean thingAllowed;


    AddRowAction(ClassDescriptionTable table, String name, boolean thingAllowed) {
        super(name, OWLIcons.getAddIcon(OWLIcons.PRIMITIVE_OWL_CLASS), true);
        this.table = table;
        this.thingAllowed = thingAllowed;
    }


    public void resourceSelected(RDFResource resource) {
        OWLModel owlModel = resource.getOWLModel();
        try {
            owlModel.beginTransaction(getValue(Action.NAME) + " " + resource.getBrowserText() +
                    " to " + table.getEditedCls().getBrowserText(), table.getEditedCls().getName());
            table.addCls((RDFSClass) resource);
            owlModel.commitTransaction();
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);
        }
    }


    public Collection getSelectableResources() {
        final OWLModel owlModel = table.getOWLModel();
        Collection<OWLNamedClass> clses = owlModel.getUserDefinedOWLNamedClasses();
        if (thingAllowed) {
            clses.add(owlModel.getOWLThingClass());
        }
        Cls editedCls = ((OWLTableModel) table.getModel()).getEditedCls();
        clses.remove(editedCls); // Can never add itself
        OWLNamedClass[] cs = clses.toArray(new OWLNamedClass[0]);
        Arrays.sort(cs, new FrameComparator());
        return Arrays.asList(cs);
    }


    public Collection pickResources() {
        return ProtegeUI.getSelectionDialogFactory().selectClasses(table, table.getOWLModel(),
                "Select named class(es) to add");
    }
}
