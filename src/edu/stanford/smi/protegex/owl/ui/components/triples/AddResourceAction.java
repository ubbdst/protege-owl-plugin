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

import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSNames;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AddResourceAction extends ResourceSelectionAction {

    private TriplesTable table;


    public AddResourceAction(TriplesTable table) {
        this(table, "Add existing resource as value...", OWLIcons.getAddIcon(OWLIcons.RDF_INDIVIDUAL));
    }


    public AddResourceAction(TriplesTable table, String name, Icon icon) {
        super(name, icon);
        this.table = table;
    }


    protected Collection getAllowedProperties(OWLModel owlModel) {
        return owlModel.getRDFProperties();
    }


    public Collection getSelectableResources() {
        TriplesTableModel tableModel = table.getTableModel();
        OWLModel owlModel = tableModel.getOWLModel();
        Collection<RDFProperty> properties = new ArrayList<RDFProperty>();
        Collection allowedProperties = getAllowedProperties(owlModel);
        for (Iterator it = allowedProperties.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (property.isVisible() && property.hasObjectRange() && !property.isSystem()) {
                properties.add(property);
            }
            else if (property.isAnnotationProperty() && !(property instanceof OWLDatatypeProperty)) {
                properties.add(property);
            }
        }
        properties.add(owlModel.getOWLDisjointWithProperty());
        properties.add(owlModel.getOWLDifferentFromProperty());
        properties.add(owlModel.getOWLEquivalentPropertyProperty());
        properties.add(owlModel.getOWLSameAsProperty());
        properties.add(owlModel.getRDFProperty(RDFSNames.Slot.IS_DEFINED_BY));
        properties.add(owlModel.getRDFProperty(RDFSNames.Slot.SEE_ALSO));
        return properties;
    }


    public void resourceSelected(RDFResource resource) {
        TriplesTableModel tableModel = table.getTableModel();
        OWLModel owlModel = tableModel.getOWLModel();
        RDFProperty property = (RDFProperty) resource;
        if (property.hasObjectRange() || 
                (property.isAnnotationProperty() && !(property instanceof OWLDatatypeProperty))) {
            owlModel.getRDFUntypedResourcesClass().setVisible(true);
            Collection unionRangeClasses = property.getUnionRangeClasses();
            if(unionRangeClasses.isEmpty()) {
                unionRangeClasses = Collections.singleton(owlModel.getOWLThingClass());
            }
            RDFResource value = ProtegeUI.getSelectionDialogFactory().selectResourceByType(table, owlModel, unionRangeClasses);
            owlModel.getRDFUntypedResourcesClass().setVisible(false);
            tryToAddValue(property, value);
        }
        else {
            int row = tableModel.addRow(property);
            table.editCell(row);
        }
    }


    public RDFResource pickResource() {
        TriplesTableModel tableModel = table.getTableModel();
        OWLModel owlModel = tableModel.getOWLModel();
        Collection properties = getSelectableResources();
        return ProtegeUI.getSelectionDialogFactory().selectProperty(table, owlModel, properties);
    }


    private void tryToAddValue(RDFProperty property, RDFResource value) {
        if (value != null) {
            TriplesTableModel tableModel = table.getTableModel();
            if (tableModel.getSubject().getPropertyValues(property).contains(value)) {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(table, "This value is already used.");
            }
            else {
                int row = tableModel.addRow(property, value);
                table.getSelectionModel().setSelectionInterval(row, row);
                table.scrollRectToVisible(table.getCellRect(row, 0, true));
            }
        }
    }
}
