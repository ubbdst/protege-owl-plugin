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

import edu.stanford.smi.protege.model.Model;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A PropertyWidget to edit the values of annotation properties.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class TriplesComponent extends AbstractTriplesComponent {
    
    private Action deleteRowAction;
    private Action createObjectPropertyValueAction;
    private Action createDatatypePropertyValueAction;    
    private Action addResourceAction;

    public TriplesComponent(RDFProperty predicate) {
        this(predicate, "Triples", OWLIcons.getImageIcon(OWLIcons.TRIPLES));
    }

    public TriplesComponent(RDFProperty predicate, boolean isreadOnly) {
    	this(predicate, "Triples", OWLIcons.getImageIcon(OWLIcons.TRIPLES), isreadOnly);
    }

    public TriplesComponent(RDFProperty predicate, String label, Icon icon) {
        this(predicate, label, icon, false);
    }

    public TriplesComponent(RDFProperty predicate, String label, Icon icon, boolean isReadOnly) {
        super(predicate, label, icon, isReadOnly);
    }

        
    protected void addButtons(LabeledComponent lc) {
    	createDatatypePropertyValueAction = new CreateValueAction(getTable(), "Create datatype property value...", OWLIcons.getCreateIndividualIcon(OWLIcons.DATATYPE_TRIPLE)) {
            protected Collection getAllowedProperties(OWLModel owlModel) {
                Collection results = new ArrayList();
                Iterator it = owlModel.getRDFProperties().iterator();
                RDFSNamedClass type = (RDFSNamedClass) table.getTableModel().getSubject().getRDFType();
                while (it.hasNext()) {
                    RDFProperty property = (RDFProperty) it.next();
                    if (property.isVisible() &&
                            (property.hasDatatypeRange() || isDatatypeProperty(property, type))) {
                        results.add(property);
                    }
                }
                results.remove(owlModel.getSystemFrames().getPalDescriptionSlot());
                results.remove(owlModel.getSystemFrames().getPalNameSlot());
                results.remove(owlModel.getSystemFrames().getPalRangeSlot());
                results.remove(owlModel.getSystemFrames().getPalStatementSlot());
                return results;
            }
        };
        
        lc.addHeaderButton(createDatatypePropertyValueAction);
        
        createObjectPropertyValueAction = new CreateValueAction(getTable(), "Create object property value...", OWLIcons.getCreateIndividualIcon(OWLIcons.RDF_INDIVIDUAL)) {
            protected Collection getAllowedProperties(OWLModel owlModel) {
                Collection results = new ArrayList();
                Iterator it = super.getAllowedProperties(owlModel).iterator();
                RDFSNamedClass type = (RDFSNamedClass) table.getTableModel().getSubject().getRDFType();
                while (it.hasNext()) {
                    RDFProperty property = (RDFProperty) it.next();
                    if (!isDatatypeProperty(property, type) && (property.isAnnotationProperty() || !property.isSystem())) {
                        results.add(property);
                    }
                }
                return results;
            }
        };
        lc.addHeaderButton(createObjectPropertyValueAction);
        
        addResourceAction = new AddResourceAction(getTable());
        lc.addHeaderButton(addResourceAction);
        
        deleteRowAction = new DeleteTripleAction(getTable());
        lc.addHeaderButton(deleteRowAction);
        
    }


    private boolean isDatatypeProperty(RDFProperty property, RDFSNamedClass type) {
        if (!property.hasObjectRange()) {
            if (type instanceof OWLNamedClass) {
                RDFResource allValuesFrom = ((OWLNamedClass) type).getAllValuesFrom(property);
                if (allValuesFrom instanceof RDFSDatatype) {
                    return true;
                }
            }
            else {
                return true;
            }
        }
        return false;
    }

    protected void updateActions() {
        super.updateActions();
        final int row = getTable().getSelectedRow();
        TriplesTableModel tableModel = getTable().getTableModel();
        boolean deleteRowEnabled = false;
        if (row >= 0) {
            deleteRowEnabled = tableModel.isDeleteEnabled(row);
        }
        deleteRowAction.setEnabled(isEnabled() && deleteRowEnabled);
    }
    
    public void setEnabled(boolean enabled) {    	
    	createDatatypePropertyValueAction.setEnabled(enabled);
    	createObjectPropertyValueAction.setEnabled(enabled);
    	addResourceAction.setEnabled(enabled);
    	deleteRowAction.setEnabled(enabled);
    	getTable().setEnabled(enabled);
    	super.setEnabled(enabled);
    };
}
