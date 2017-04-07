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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Action;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.util.LabeledComponent;
import edu.stanford.smi.protegex.owl.model.OWLModel;
import edu.stanford.smi.protegex.owl.model.OWLOntology;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSClass;
import edu.stanford.smi.protegex.owl.ui.components.triples.AbstractTriplesComponent;
import edu.stanford.smi.protegex.owl.ui.components.triples.AddResourceAction;
import edu.stanford.smi.protegex.owl.ui.components.triples.CreateValueAction;
import edu.stanford.smi.protegex.owl.ui.components.triples.DeleteTripleAction;
import edu.stanford.smi.protegex.owl.ui.components.triples.TriplesTable;
import edu.stanford.smi.protegex.owl.ui.components.triples.TriplesTableModel;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;

/**
 * A PropertyWidget to edit the values of annotation properties.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AnnotationsComponent extends AbstractTriplesComponent {
    private static final long serialVersionUID = -8853232024353780133L;
    private Action deleteRowAction;
    private Action createValueAction;
    private Action todoAction;
    private Action addResourceAction;


    public AnnotationsComponent(RDFProperty predicate) {
    	this(predicate, false);
    }
    
    public AnnotationsComponent(RDFProperty predicate, boolean isReadOnly) {
    	super(predicate, "Annotations", OWLIcons.getImageIcon(OWLIcons.ANNOTATIONS_TABLE), isReadOnly);
    }
    

    protected void addButtons(LabeledComponent lc) {
        createValueAction = new CreateValueAction(getTable(), "Create new annotation value", OWLIcons.getCreateIcon(OWLIcons.ANNOTATION)) {
            private static final long serialVersionUID = 2018303283100762805L;

            @Override
            public void actionPerformed(ActionEvent e) {
                getTableModel().setAllowReadOnlyEdit(true);
                try {
                    super.actionPerformed(e);
                }
                finally {
                    getTableModel().setAllowReadOnlyEdit(false);
                }
            }
            
            @SuppressWarnings("unchecked")
            public Collection getSelectableResources() {
                TriplesTableModel tableModel = table.getTableModel();
                OWLModel owlModel = tableModel.getOWLModel();
                Collection<RDFProperty> properties = new ArrayList();
                Collection<RDFProperty> annotationProperties = owlModel.getOWLAnnotationProperties();
                Collection ontologyProperties = owlModel.getOWLOntologyProperties();
                RDFResource resource = tableModel.getSubject();
                for (RDFProperty property : annotationProperties) {
                    if (ontologyProperties.contains(property)) {
                        if (resource instanceof OWLOntology) {
                            properties.add(property);
                        }
                    }
                    else {
                        RDFSClass type = resource.getProtegeType();
                        Collection domainProperties = type.getUnionDomainProperties(true);
                        if (domainProperties.contains(property)) {
                            boolean zero = resource.getPropertyValues(property, true).isEmpty();
                            if (!property.isFunctional() || zero) {
                                properties.add(property);
                            }
                        }
                    }
                }
                return properties;
            }
        };
        
        lc.addHeaderButton(createValueAction);
        
        todoAction = new AddTodoAction((AnnotationsTable) getTable());
        
        lc.addHeaderButton(todoAction);

        addResourceAction = new AddResourceAction(getTable()) {
            protected Collection getAllowedProperties(OWLModel owlModel) {
                Collection<RDFProperty> allowedProperties = new ArrayList<RDFProperty>();
                for (RDFProperty property : owlModel.getOWLAnnotationProperties()) {
                    allowedProperties.add(property);
                }
                return allowedProperties;
            }
        };
        
        lc.addHeaderButton(addResourceAction);
        deleteRowAction = new DeleteTripleAction(getTable(), "Delete selected annotation", OWLIcons.getDeleteIcon(OWLIcons.ANNOTATION));
        deleteRowAction.setEnabled(false);
        lc.addHeaderButton(deleteRowAction);
    }


    protected TriplesTable createTable(Project project) {
        return new AnnotationsTable(project, (AnnotationsTableModel) getTableModel());
    }


    protected TriplesTableModel createTableModel() {
        return new AnnotationsTableModel();
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
    	createValueAction.setEnabled(enabled);
    	addResourceAction.setEnabled(enabled);
    	todoAction.setEnabled(enabled);
    	deleteRowAction.setEnabled(enabled);
    	getTable().setEnabled(enabled);
    	super.setEnabled(enabled);
    };
}