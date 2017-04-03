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

package edu.stanford.smi.protegex.owl.ui.clsproperties;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;
import edu.stanford.smi.protegex.owl.ui.widget.PropertyWidget;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * An Action to add a selected property to the domain of the currently
 * edited Cls.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class AddPropertyAction extends ResourceSelectionAction {

    private PropertyWidget propertyWidget;


    public AddPropertyAction(PropertyWidget propertyWidget) {
        super("Add this class to the domain of properties...",
                OWLIcons.getAddIcon(OWLIcons.RDF_PROPERTY), true);
        this.propertyWidget = propertyWidget;
    }


    public void resourceSelected(RDFResource resource) {
        RDFSClass cls = (RDFSClass) propertyWidget.getEditedResource();
        final RDFProperty property = (RDFProperty) resource;
        RDFSClass rootCls = cls.getOWLModel().getOWLThingClass();
        OWLModel owlModel = resource.getOWLModel();
        try {
            owlModel.beginTransaction("Add " + cls.getBrowserText() + " to the domain of " + property.getBrowserText(), property.getName());
            if (property.getUnionDomain(false).contains(rootCls)) {
                property.removeUnionDomainClass(rootCls);
            }
            property.addUnionDomainClass(cls);
            owlModel.commitTransaction();
        }
        catch (Exception ex) {
        	owlModel.rollbackTransaction();
            OWLUI.handleError(owlModel, ex);
        }
    }


    protected RDFSNamedClass getBasePropertyMetaclass() {
        OWLModel owlModel = (OWLModel) propertyWidget.getKnowledgeBase();
        return owlModel.getRDFPropertyClass();
    }


    public Collection getSelectableResources() {
        RDFSClass propertyMetaclass = getBasePropertyMetaclass();
        List properties = new ArrayList(propertyMetaclass.getInstances(true));
        RDFSClass cls = (RDFSClass) propertyWidget.getEditedResource();
        properties.removeAll(cls.getUnionDomainProperties());
        Collection choice = new ArrayList();
        for (Iterator it = properties.iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (!(property instanceof OWLProperty) || !property.isAnnotationProperty()) {
                if (!property.isSystem()) {
                    choice.add(property);
                }
            }
        }
        return choice;
    }


    public Collection pickResources() {
        String label = "Select " + propertyWidget.getLabel();
        OWLModel owlModel = (OWLModel) propertyWidget.getKnowledgeBase();
        return ProtegeUI.getSelectionDialogFactory().selectResourcesFromCollection((Component) propertyWidget,
                owlModel, getSelectableResources(), label);
    }
}
