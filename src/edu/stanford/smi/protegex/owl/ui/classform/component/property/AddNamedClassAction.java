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

import edu.stanford.smi.protege.Application;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

import java.awt.*;
import java.util.Collection;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AddNamedClassAction extends ResourceSelectionAction {

    private PropertyFormTable table;

    public AddNamedClassAction(PropertyFormTable table) {
        super("Add named class...", OWLIcons.getAddIcon(OWLIcons.PRIMITIVE_OWL_CLASS));
        this.table = table;
    }


    public Collection getSelectableResources() {
        OWLModel owlModel = table.getTableModel().getProperty().getOWLModel();
        Collection col = owlModel.getUserDefinedOWLNamedClasses();
        col.removeAll(table.getTableModel().getRDFResources());
        return col;
    }


    public void resourceSelected(RDFResource resource) {
        RDFProperty prop = table.getTableModel().getProperty();
        RDFSClass subject = (RDFSClass) table.getTableModel().getNamedClass();
        OWLSomeValuesFrom someRestr =
                subject.getOWLModel().createOWLSomeValuesFrom(prop, resource);
        subject.addSuperclass(someRestr);
        //@@TODO update closure if it exists
    }

    public RDFResource pickResource() {
        RDFProperty prop = table.getTableModel().getProperty();
        Collection ranges = prop.getRanges(true);
        OWLModel owlModel = prop.getOWLModel();
        Component mainWindow = Application.getMainWindow();

        return ProtegeUI.getSelectionDialogFactory().selectClass(mainWindow,
                                                                 owlModel,
                                                                 ranges);
    }
}
