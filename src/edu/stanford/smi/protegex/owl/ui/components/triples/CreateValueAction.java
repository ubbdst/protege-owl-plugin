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

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.model.impl.OWLUtil;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.metadata.AnnotationsWidgetPlugin;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class CreateValueAction extends ResourceSelectionAction {

    protected TriplesTable table;


    public CreateValueAction(TriplesTable table) {
        this(table, "Create new property value...", OWLIcons.getCreateIcon(OWLIcons.TRIPLE));
    }


    public CreateValueAction(TriplesTable table, String name, Icon icon) {
        super(name, icon);
        this.table = table;
    }


    protected Collection getAllowedProperties(OWLModel owlModel) {
        Collection allowedProperties = owlModel.getRDFProperties();
        List properties = new ArrayList();
        for (Iterator it = allowedProperties.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (property.isVisible() && property.hasObjectRange()) {
                properties.add(property);
            }
        }
        return properties;
    }


    public Collection getSelectableResources() {
        TriplesTableModel tableModel = table.getTableModel();
        OWLModel owlModel = tableModel.getOWLModel();
        Collection allowedProperties = getAllowedProperties(owlModel);
        return allowedProperties;
        /*Collection properties = new ArrayList();
        RDFResource resource = tableModel.getSubject();
        for (Iterator it = allowedProperties.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            RDFSClass type = resource.getProtegeType();
            Collection domainProperties = type.getUnionDomainProperties(true);
            if (domainProperties.contains(property)) {
                int count = resource.getPropertyValues(property, true).size();
                if (type instanceof OWLNamedClass) {
                    int max = ((OWLNamedClass) type).getMaxCardinality(property);
                    if (max < count) {
                        properties.add(property);
                    }
                }
                else {  // RDFSNamedClass only
                    if (count == 0 || !property.isFunctional()) {
                        properties.add(property);
                    }
                }
            }
        }
        return properties;*/
    }


    protected void handleCreateAction(RDFProperty property) {
        TriplesTableModel tableModel = table.getTableModel();
        OWLModel owlModel = tableModel.getOWLModel();
        RDFSClass cls = null;
        if (property.hasObjectRange()) {
            Collection classes = property.getUnionRangeClasses();
            if (OWLUI.isExternalResourcesSupported(owlModel)) {
                owlModel.getRDFUntypedResourcesClass().setVisible(true);
            }
            cls = ProtegeUI.getSelectionDialogFactory().selectClass(table, owlModel,
                    classes, "Select class of new value");
            owlModel.getRDFUntypedResourcesClass().setVisible(false);
        }
        if (cls != null) {
            RDFResource resource = cls.createInstance(null);
            if (resource instanceof RDFSClass) {
                RDFSClass newClass = (RDFSClass) resource;
                if (newClass.getSuperclassCount() == 0) {
                    newClass.addSuperclass(owlModel.getOWLThingClass());
                }
            }
            else if (resource instanceof OWLOntology) {
                String defaultNS = owlModel.getNamespaceManager().getDefaultNamespace();
                if (defaultNS.endsWith("#")) {
                    defaultNS = defaultNS.substring(0, defaultNS.length() - 1);
                }
                String prefixBase = "ontology";
                int index = 0;
                String prefix = prefixBase;
                while (owlModel.getNamespaceManager().getNamespaceForPrefix(prefixBase) != null) {
                    prefix = prefixBase + (index++);
                }
                String nsBase = ProtegeNames.DEFAULT_DEFAULT_BASE;
                String ns = nsBase + "#";
                while (owlModel.getNamespaceManager().getPrefix(ns) != null) {
                    ns = nsBase + index + "#";
                }
                owlModel.getNamespaceManager().setPrefix(ns, prefix);
                OWLOntology ontology = (OWLOntology) resource;
                ontology = (OWLOntology) ontology.rename(prefix + ":");
            }
            else if (resource instanceof RDFUntypedResource) {
                resource =  OWLUtil.assignUniqueURI((RDFUntypedResource) resource);
            }
            owlModel.getProject().show(resource);
            int row = tableModel.addRow(property, resource);
            table.getSelectionModel().setSelectionInterval(row, row);
            table.scrollRectToVisible(table.getCellRect(row, TriplesTableModel.COL_PROPERTY, true));
        }
    }


    public RDFResource pickResource() {
        TriplesTableModel tableModel = table.getTableModel();
        OWLModel owlModel = tableModel.getOWLModel();
        Collection resources = getSelectableResources();
        return ProtegeUI.getSelectionDialogFactory().selectProperty(table, owlModel, resources);
    }


    public void resourceSelected(RDFResource resource) {
        TriplesTableModel tableModel = table.getTableModel();
        RDFProperty property = (RDFProperty) resource;
        if (property instanceof OWLObjectProperty) {
            handleCreateAction(property);
        }
        else {
            int row = tableModel.addRow(property);
            if (row >= 0) {
                Object value = tableModel.getValue(row);
                Iterator it = TriplesComponent.plugins();
                while (it.hasNext()) {
                    AnnotationsWidgetPlugin plugin = (AnnotationsWidgetPlugin) it.next();
                    if (plugin.canEdit(tableModel.getSubject(), property, value)) {
                        Object newValue = plugin.editValue(null, tableModel.getSubject(), property, value);
                        if (newValue != null) {
                            tableModel.setValue(newValue, row);
                        }
                        return;
                    }
                }
                table.editCell(row);
            }
            else {
                ProtegeUI.getModalDialogFactory().showErrorMessageDialog(resource.getOWLModel(),
                        "Could not create default value for " + property.getBrowserText());
            }
        }
    }
}
