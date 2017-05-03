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

package edu.stanford.smi.protegex.owl.ui.actions;

import edu.stanford.smi.protegex.owl.model.*;
import edu.stanford.smi.protegex.owl.swrl.model.SWRLIndividual;
import edu.stanford.smi.protegex.owl.ui.ProtegeUI;
import edu.stanford.smi.protegex.owl.ui.icons.OWLIcons;
import edu.stanford.smi.protegex.owl.ui.widget.OWLUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Holger Knublauch  <holger@knublauch.com>
 */
public class ConvertIndividualToClassAction extends ResourceAction {

    public ConvertIndividualToClassAction() {
        super("Convert individual to class",
              OWLIcons.getCreateIcon(OWLIcons.PRIMITIVE_OWL_CLASS), null, true);
    }


    public void actionPerformed(ActionEvent e) {
        RDFIndividual instance = (RDFIndividual) getResource();
        String clsName = getClsName(instance);
        if (ProtegeUI.getModalDialogFactory().showConfirmDialog(getComponent(),
                                                                "This will create a new class " + clsName +
                                                                " as a subclass of " + instance.getProtegeType().getName() + "\n" +
                                                                "with restrictions that represent the values of the individual\n" +
                                                                "and then make this individual an instance of the new class.",
                                                                "Confirm conversion")) {
            OWLModel owlModel = instance.getOWLModel();
            try {
                owlModel.beginTransaction("Convert individual " + instance.getBrowserText() + " to class", clsName);
                performAction(instance);
                owlModel.commitTransaction();
            }
            catch (Exception ex) {
            	owlModel.rollbackTransaction();
            	OWLUI.handleError(owlModel, ex);                
            }
        }
    }


    private static void createRestrictionsForValues(OWLNamedClass cls, RDFProperty property, Collection values) {
        OWLModel owlModel = cls.getOWLModel();
        for (Iterator it = values.iterator(); it.hasNext();) {
            Object value = (Object) it.next();
            cls.addSuperclass(owlModel.createOWLHasValue(property, value));
        }
    }


    public static String getClsName(RDFIndividual individual) {
        String baseName = individual.getName() + "Class";
        String name = baseName;
        for (int i = 1; individual.getOWLModel().getRDFResource(name) != null; i++) {
            name = baseName + i;
        }
        return name;
    }


    public boolean isSuitable(Component component, RDFResource resource) {
        if (resource instanceof RDFIndividual &&
            resource.isEditable() &&
            !(resource instanceof SWRLIndividual) &&
            !(resource instanceof OWLOntology)) {
            return !resource.isAnonymous();
        }
        else {
            return false;
        }
    }


    public static OWLNamedClass performAction(RDFIndividual individual) {
        OWLModel owlModel = individual.getOWLModel();
        String name = getClsName(individual);
        OWLNamedClass cls = (OWLNamedClass) owlModel.createOWLNamedSubclass(name, (OWLNamedClass) individual.getProtegeType());
        for (Iterator it = individual.getRDFProperties().iterator(); it.hasNext();) {
            RDFProperty rdfProperty = (RDFProperty) it.next();
            if (!rdfProperty.equals(owlModel.getRDFTypeProperty())) {
                Collection values = individual.getPropertyValues(rdfProperty);
                if (!values.isEmpty()) {
                    if (rdfProperty.isAnnotationProperty()) {
                        cls.setPropertyValues(rdfProperty, values);
                    }
                    else {
                        createRestrictionsForValues(cls, rdfProperty, values);
                    }
                }
            }
        }
        individual.setProtegeType(cls);
        return cls;
    }
}
