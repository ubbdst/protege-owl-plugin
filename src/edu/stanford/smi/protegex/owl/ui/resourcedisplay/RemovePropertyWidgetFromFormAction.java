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

package edu.stanford.smi.protegex.owl.ui.resourcedisplay;

import edu.stanford.smi.protege.model.Project;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protege.widget.FormWidget;
import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.resourceselection.ResourceSelectionAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * An Action to remove a property widget of a currently visible slot from the
 * FormWidget of the direct type of a given resource.
 *
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class RemovePropertyWidgetFromFormAction extends ResourceSelectionAction {

    private RDFResource resource;

    private ResourceDisplay parent;


    public RemovePropertyWidgetFromFormAction(RDFResource resource, ResourceDisplay parent) {
        super("Remove property widget from form...", OWLIcons.getImageIcon("RemovePropertyWidgetFromForm"));
        this.resource = resource;
        this.parent = parent;
    }


    public void resourceSelected(RDFResource resource) {
        RDFSClass directType = this.resource.getProtegeType();
        Project project = this.resource.getProject();
        FormWidget formWidget = (FormWidget) project.getDesignTimeClsWidget(directType);
        Slot slot = (Slot) resource;
        formWidget.replaceWidget(slot, null);
        parent.setInstance(null);
        parent.setInstance(this.resource);
    }


    public Collection getSelectableResources() {
        RDFSClass directType = resource.getProtegeType();
        FormWidget formWidget = (FormWidget) resource.getProject().getDesignTimeClsWidget(directType);
        Collection properties = new ArrayList(directType.getUnionDomainProperties(true));
        for (Iterator it = directType.getUnionDomainProperties(true).iterator(); it.hasNext();) {
            RDFProperty property = (RDFProperty) it.next();
            if (property.isSystem() ||
                    !(property instanceof OWLProperty) ||
                    ((OWLProperty) property).isAnnotationProperty() ||
                    formWidget.getSlotWidget(property) == null) {
                properties.remove(property);
            }
        }
        return properties;
    }


    public RDFResource pickResource() {
        String label = "Select a property to remove the widget for";
        OWLModel owlModel = resource.getOWLModel();
        return ProtegeUI.getSelectionDialogFactory().selectProperty(parent, owlModel,
                getSelectableResources(), label);
    }
}
